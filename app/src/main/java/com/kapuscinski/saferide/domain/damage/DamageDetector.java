/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.damage;

import android.hardware.SensorManager;
import android.support.annotation.Nullable;

import com.kapuscinski.saferide.domain.Constants;
import com.kapuscinski.saferide.domain.sensor.AccelerometerListener;
import com.kapuscinski.saferide.domain.sensor.OrientationInspector;
import com.kapuscinski.saferide.domain.sensor.PositionListener;
import com.kapuscinski.saferide.domain.sensor.AccelerometerSensor;
import com.kapuscinski.saferide.domain.sensor.PositionSensor;
import com.kapuscinski.saferide.domain.entity.AccelerometerEvent;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Position;
import com.kapuscinski.saferide.domain.persistence.PreferencesManager;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Based on callbacks received from {@link AccelerometerSensor} and {@link PositionSensor}
 * implementations this class detects if damage occurred and notifies listener if set.
 * <p>
 * Damage detection is based on acceleration along y axis, if acceleration exceeds one of the
 * damage thresholds, damage occurred. Then when device position callbacks are received this
 * class checks if these positions are accurate enough and based on this decides if damage can
 * be precisely localized if yes listener is notified.
 * </p>
 */
public class DamageDetector implements PositionListener, AccelerometerListener,
        PreferencesManager.Listener {

    /**
     * Listener interface to receive callbacks if damage occurred or if damage detection stopped
     * due to incorrect device orientation
     */
    public interface Listener {

        void onDamageDetected(Damage damage);

        void onIncorrectOrientation(float[] orientation);

        void onCorrectOrientation();

        void onSensorsNotAvailable();

    }

    //default values
    public static final float DESIRED_GPS_ACCURACY = 15; //in meters
    public static final float MAX_DISTANCE_COVERED_BETWEEN_DAMAGE = 50; //in meters
    public static final float SMALL_DAMAGE_THRESHOLD = Constants.Damage.SMALL_DAMAGE_THRESHOLD;
    public static final float MEDIUM_DAMAGE_THRESHOLD = Constants.Damage.MEDIUM_DAMAGE_THRESHOLD;
    public static final float BIG_DAMAGE_THRESHOLD = Constants.Damage.BIG_DAMAGE_THRESHOLD;

    private OrientationInspector orientationInspector;
    private AccelerometerSensor accelerometerSensor;
    private PositionSensor positionSensor;
    private PreferencesManager preferences;

    private Position lastPosition;
    private Listener listener;
    private TreeSet<AccelerometerEvent> eventSet = new TreeSet<>(new EventTimestampComparator());
    private boolean correctOrientationCalled = false; //we don't want to notify listener all the
    // time about correct orientation, we want to do it only it device recovers from incorrect
    // orientation
    private float desiredGPSAccuracy = DESIRED_GPS_ACCURACY;

    private float maxDistanceCoveredBetweenDamage = MAX_DISTANCE_COVERED_BETWEEN_DAMAGE;
    private float smallDamageThreshold;
    private float mediumDamageThreshold;
    private float bigDamageThreshold;

    @Inject
    public DamageDetector(OrientationInspector orientationInspector,
                          AccelerometerSensor accelerometerSensor,
                          PositionSensor positionSensor,
                          PreferencesManager preferences) {
        this.orientationInspector = orientationInspector;
        this.accelerometerSensor = accelerometerSensor;
        this.positionSensor = positionSensor;
        this.preferences = preferences;

        this.preferences.addListener(this);
        readPreferences();
    }

    @Override
    public void onAccelerationChanged(AccelerometerEvent event) {
        if (!orientationInspector.isInCorrectOrientation()) {
            listener.onIncorrectOrientation(orientationInspector.getCurrentRotation());
            correctOrientationCalled = false;
            return;
        } else if (!correctOrientationCalled) {
            listener.onCorrectOrientation();
            correctOrientationCalled = true;
        }

        if (!damageDetected(event))
            return;

        eventSet.add(event);
        Timber.d("Detected big enough acceleration: %.2f", event.getYAcceleration());
    }

    @Override
    public void onPositionChanged(Position position) {
        if (lastPosition == null) {
            lastPosition = position;

            //there are no new damage events discovered - return
            if (eventSet.size() == 0)
                return;

            //there are already detected damages that occurred before first position notification
            //check if any of these damages is accurate enough
            AccelerometerEvent event = findLargestAccurateEvent(eventSet, lastPosition);

            if (event != null) {
                Damage d = Damage.create(event, lastPosition, determineDamageValue(event));
                if (listener != null)
                    listener.onDamageDetected(d);
            }
            eventSet.clear();

            return;
        }

        //nothing is detected just save position
        if (eventSet.size() == 0) {
            lastPosition = position;
            return;
        }

        //split currently stored damage events to parts which are more likely to be accurate to
        // first or second position
        double middle = (position.getTimestamp() - lastPosition.getTimestamp()) / 2 + lastPosition.getTimestamp();
        AccelerometerEvent middleEvent = new AccelerometerEvent();
        middleEvent.setNanos((long) middle * 1000000);
        AccelerometerEvent firstPositionEvent = findLargestAccurateEvent(eventSet.headSet
                (middleEvent), lastPosition);
        AccelerometerEvent secondPositionEvent = findLargestAccurateEvent(eventSet.tailSet
                (middleEvent), position);

        if (firstPositionEvent != null) {
            Damage d = Damage.create(firstPositionEvent, lastPosition, determineDamageValue(firstPositionEvent));

            if (listener != null)
                listener.onDamageDetected(d);
        }
        if (secondPositionEvent != null) {
            Damage d = Damage.create(secondPositionEvent, position, determineDamageValue
                    (secondPositionEvent));

            if (listener != null)
                listener.onDamageDetected(d);
        }
        lastPosition = position;
        eventSet.clear();
    }

    @Override
    public void onPreferenceChanged(String key, PreferencesManager manager) {
        switch (key) {
            case PreferencesManager.SMALL_DAMAGE_THRESHOLD:
                smallDamageThreshold = manager.getFloat(key);
                break;
            case PreferencesManager.MEDIUM_DAMAGE_THRESHOLD:
                mediumDamageThreshold = manager.getFloat(key);
                break;
            case PreferencesManager.BIG_DAMAGE_THRESHOLD:
                bigDamageThreshold = manager.getFloat(key);
                break;
        }
    }

    public void start() {
        positionSensor.start();
        orientationInspector.start();
        accelerometerSensor.start();
        accelerometerSensor.addListener(this);
        positionSensor.addListener(this);
    }

    public void stop() {
        orientationInspector.stop();
        accelerometerSensor.removeListener(this);
        positionSensor.removeListener(this);
    }

    /**
     * Reads preferences and stores values in appropriate variables
     */
    private void readPreferences() {
        smallDamageThreshold = preferences.getFloat(PreferencesManager.SMALL_DAMAGE_THRESHOLD,
                SMALL_DAMAGE_THRESHOLD);
        mediumDamageThreshold = preferences.getFloat(PreferencesManager.MEDIUM_DAMAGE_THRESHOLD,
                MEDIUM_DAMAGE_THRESHOLD);
        bigDamageThreshold = preferences.getFloat(PreferencesManager.BIG_DAMAGE_THRESHOLD, BIG_DAMAGE_THRESHOLD);
    }

    /**
     * Method compares to positions and determines which is more accurate based on their timestamps
     * according to an accelerometer event timestamp
     *
     * @param firstPosition
     * @param secondPosition
     * @param event
     * @return
     */
    private Position getBetterPosition(Position firstPosition, Position secondPosition,
                                       AccelerometerEvent event) {
        //compare position timestamps with accelerometer event timestamp
        if (Math.abs(firstPosition.getTimestamp() - event.getNanos()) <= Math.abs(secondPosition
                .getTimestamp() - event.getNanos()))
            return firstPosition;
        else
            return secondPosition;
    }

    /**
     * Checks whether position is accurate based on desiredGPSAccuracy if yes than checks if
     * covered distance between damage detection and position change isn't too long based on
     * maxDistanceCoveredBetweenDamage
     *
     * @param position
     * @param event
     * @return
     */
    public boolean isPositionAccurate(Position position, AccelerometerEvent event) {
        if (position.getAccuracy() > desiredGPSAccuracy)
            return false;

        //estimate covered time between damage detection and position update
        if (position.getSpeed() == 0)
            return false;

        float time = Math.abs(position.getTimestamp() - event.getNanos() / 1000000);
        time /= 1000; //convert to seconds
        float distance = position.getSpeed() * time;

        return distance <= maxDistanceCoveredBetweenDamage;
    }

    /**
     * Based on acceleration values along y axis checks if damage occurred, the force of gravity
     * is subtracted from y axis before check
     *
     * @param event AccelerometerEvent to check if damage occurred
     * @return <code>true</code> if damage occurred <code>false</code> otherwise
     */
    public boolean damageDetected(AccelerometerEvent event) {
        eliminateGravityForce(event);
        return Math.abs(event.getYAcceleration()) >= smallDamageThreshold;
    }

    /**
     * As we are constantly monitoring device pitch rotation (along vertical axis), simply
     * extract force of gravity from vertical acceleration from accelerometer event. It's not
     * most accurate way but it enabled devices without linear accelerometer to still use damage
     * detection
     *
     * @param event
     */
    public void eliminateGravityForce(AccelerometerEvent event) {
        event.setYAcceleration(event.getYAcceleration() - SensorManager.GRAVITY_EARTH);
    }

    /**
     * Finds accelerometer event with the biggest damage which is accurate enough for given position
     *
     * @param events   Collection of accelerometer events where damages has occurred
     * @param position Position to check if currently checked accelerometer event is accurate
     *                 enough
     * @return AccelerometerEvent which has the biggest damage and is accurate enough
     */
    @Nullable
    public AccelerometerEvent findLargestAccurateEvent(Collection<AccelerometerEvent> events, Position
            position) {
        TreeSet<AccelerometerEvent> eventSet = new TreeSet<>(new EventDamageComparator());
        eventSet.addAll(events);

        AccelerometerEvent largestEvent = null;
        for (AccelerometerEvent event : eventSet) {
            if (isPositionAccurate(position, event)) {
                largestEvent = event;
                break;
            }
        }

        return largestEvent;
    }

    /**
     * Based on current damage thresholds determine damage value
     *
     * @param damageEvent AccelerometerEvent based on which this function determines damage value
     * @return <code>int</code> determined damage value
     */
    public int determineDamageValue(AccelerometerEvent damageEvent) {
        float absY = Math.abs(damageEvent.getYAcceleration());
        if (absY < smallDamageThreshold)
            throw new IllegalArgumentException("yAcceleration isn't greater than " +
                    "SMALL_DAMAGE_THRESHOLD - damage didn't occur");

        if (absY >= smallDamageThreshold && absY < mediumDamageThreshold)
            return Damage.SMALL_DAMAGE_VALUE;
        else if (absY >= mediumDamageThreshold && absY < bigDamageThreshold)
            return Damage.MEDIUM_DAMAGE_VALUE;
        else
            return Damage.BIG_DAMAGE_VALUE;
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public float getDesiredGPSAccuracy() {
        return desiredGPSAccuracy;
    }

    public void setDesiredGPSAccuracy(float desiredGPSAccuracy) {
        this.desiredGPSAccuracy = desiredGPSAccuracy;
    }

    public float getMaxDistanceCoveredBetweenDamage() {
        return maxDistanceCoveredBetweenDamage;
    }

    public void setMaxDistanceCoveredBetweenDamage(float maxDistanceCoveredBetweenDamage) {
        this.maxDistanceCoveredBetweenDamage = maxDistanceCoveredBetweenDamage;
    }

    public float getSmallDamageThreshold() {
        return smallDamageThreshold;
    }

    public void setSmallDamageThreshold(float smallDamageThreshold) {
        this.smallDamageThreshold = smallDamageThreshold;
    }

    public float getMediumDamageThreshold() {
        return mediumDamageThreshold;
    }

    public void setMediumDamageThreshold(float mediumDamageThreshold) {
        this.mediumDamageThreshold = mediumDamageThreshold;
    }

    public float getBigDamageThreshold() {
        return bigDamageThreshold;
    }

    public void setBigDamageThreshold(float bigDamageThreshold) {
        this.bigDamageThreshold = bigDamageThreshold;
    }

    /**
     * Compares AccelerometerEvents based on their acc values, descending
     */
    private class EventDamageComparator implements Comparator<AccelerometerEvent> {

        @Override
        public int compare(AccelerometerEvent event, AccelerometerEvent t1) {
            return Float.compare(t1.getYAcceleration(), event.getYAcceleration());
        }
    }

    /**
     * Compares AccelerometerEvents based on their timestamps, ascending
     */
    private class EventTimestampComparator implements Comparator<AccelerometerEvent> {

        @Override
        public int compare(AccelerometerEvent event, AccelerometerEvent t1) {
            long result = event.getNanos() - t1.getNanos();
            if (result > 0)
                return 1;
            if (result == 0)
                return 0;
            else
                return -1;
        }
    }

    @Override
    public String toString() {
        return "DamageDetector{" +
                "orientationInspector=" + orientationInspector +
                ", accelerometerSensor=" + accelerometerSensor +
                ", positionSensor=" + positionSensor +
                ", preferences=" + preferences +
                ", lastPosition=" + lastPosition +
                ", listener=" + listener +
                ", eventSet=" + eventSet +
                ", correctOrientationCalled=" + correctOrientationCalled +
                ", desiredGPSAccuracy=" + desiredGPSAccuracy +
                ", maxDistanceCoveredBetweenDamage=" + maxDistanceCoveredBetweenDamage +
                ", smallDamageThreshold=" + smallDamageThreshold +
                ", mediumDamageThreshold=" + mediumDamageThreshold +
                ", bigDamageThreshold=" + bigDamageThreshold +
                '}';
    }
}
