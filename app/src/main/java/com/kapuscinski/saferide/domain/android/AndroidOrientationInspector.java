/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.android;

import com.kapuscinski.saferide.domain.sensor.OrientationListener;
import com.kapuscinski.saferide.domain.sensor.OrientationSensor;
import com.kapuscinski.saferide.domain.sensor.OrientationInspector;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class AndroidOrientationInspector implements OrientationInspector, OrientationListener {

    public static final float MAX_DEVICE_ROTATION = 15; //in degrees calculated against Y axis
    public static final long MAX_MILLIS_INCORRECT_ROTATION = 3000l;

    private ScheduledExecutorService scheduledExecutorService;
    private OrientationSensor orientationSensor;

    private boolean inCorrectOrientation = true;
    private ScheduledFuture rotationFuture;
    private float[] currentRotation = new float[3]; //in degrees

    @Inject
    public AndroidOrientationInspector(ScheduledExecutorService scheduledExecutorService,
                                       OrientationSensor orientationSensor) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.orientationSensor = orientationSensor;
    }

    @Override
    public boolean isInCorrectOrientation() {
        return inCorrectOrientation;
    }

    @Override
    public float[] getCurrentRotation() {
        return currentRotation;
    }

    @Override
    public void start() {
        if(!orientationSensor.isRunning()){
            orientationSensor.start();
            orientationSensor.addListener(this);
        }
    }

    @Override
    public void stop() {
        orientationSensor.removeListener(this);
    }

    /**
     * Checks if device is in correct orientation based on it's pitch angle (along vertical axis).
     * If not starts runnable which will after MAX_MILLIS_INCORRECT_ROTATION changed
     * inCorrectRotation variable to false and will notify listener about incorrect device
     * attitude, it will notify listener about incorrect rotation all the time after initial call
     * when (this method is called inside onAccelerationChanged) until device orientation will be
     * corrected and if that happens it would notify listener about corrected orientation. If before
     * MAX_MILLIS_INCORRECT_ROTATION elapsed device attitude will be
     * corrected runnable will be cancelled and listener won't get any notifications.
     */
    private void checkOrientation(float[] degreesValues) {
        if (90 - Math.abs(degreesValues[1]) > MAX_DEVICE_ROTATION && inCorrectOrientation) {
            //device is in incorrect orientation start a runnable if it's not already started to
            // watch device's orientation for MAX_MILLIS_INCORRECT_ROTATION
            // if till this time elapsed device won't correct it's rotation change
            // inCorrectOrientation variable value to false
            if (rotationFuture == null) {
                rotationFuture = scheduledExecutorService
                        .schedule(new Runnable() {
                            @Override
                            public void run() {
                                inCorrectOrientation = false;
                            }
                        }, MAX_MILLIS_INCORRECT_ROTATION, TimeUnit.MILLISECONDS);
            }
        } else if (90 - Math.abs(degreesValues[1]) > MAX_DEVICE_ROTATION && !inCorrectOrientation) {
            //we have already changed inCorrectOrientation value to false do nothing
        } else {
            //device has corrected it's orientation
            if (rotationFuture != null) {
                rotationFuture.cancel(false);
                rotationFuture = null;
            }

            inCorrectOrientation = true;
        }
    }

    @Override
    public void onOrientationChanged(float[] degreesValues) {
        currentRotation = degreesValues;
        checkOrientation(degreesValues);
    }
}
