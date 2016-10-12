/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain;

import com.kapuscinski.saferide.domain.sensor.AccelerometerSensor;
import com.kapuscinski.saferide.domain.sensor.PositionSensor;
import com.kapuscinski.saferide.domain.damage.DamageDetector;
import com.kapuscinski.saferide.domain.sensor.OrientationInspector;
import com.kapuscinski.saferide.domain.entity.AccelerometerEvent;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Position;
import com.kapuscinski.saferide.domain.persistence.PreferencesManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Tests DamageDetector class with it's default settings (after initialization)
 */
//todo tests with orientation
public class DamageDetectorTest {

    private DamageDetector detector;

    @Mock
    private DamageDetector.Listener listener;
    @Mock
    private OrientationInspector inspector;
    @Mock
    private AccelerometerSensor accelerometerSensor;
    @Mock
    private PositionSensor positionSensor;
    @Mock
    private PreferencesManager preferencesManager;

    private Position position1, position2, position3;
    private long baseMillis = 1467807644268L, baseNanos = baseMillis * 1000000;
    private double baseLat = 50.4353, baseLon = 19.5651;
    private float baseSpeed = 17, baseAcc = 10;
    private float gravity = 9.81f;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(preferencesManager.getFloat(PreferencesManager.SMALL_DAMAGE_THRESHOLD,
                DamageDetector.SMALL_DAMAGE_THRESHOLD)).thenReturn(DamageDetector
                .SMALL_DAMAGE_THRESHOLD);
        Mockito.when(preferencesManager.getFloat(PreferencesManager.MEDIUM_DAMAGE_THRESHOLD,
                DamageDetector.MEDIUM_DAMAGE_THRESHOLD)).thenReturn(DamageDetector
                .MEDIUM_DAMAGE_THRESHOLD);
        Mockito.when(preferencesManager.getFloat(PreferencesManager.BIG_DAMAGE_THRESHOLD,
                DamageDetector.BIG_DAMAGE_THRESHOLD)).thenReturn(DamageDetector
                .BIG_DAMAGE_THRESHOLD);
        Mockito.when(inspector.isInCorrectOrientation()).thenReturn(true);
        detector = new DamageDetector(inspector, accelerometerSensor, positionSensor,
                preferencesManager);
        detector.setListener(listener);

        //speed 17m/s ~ 61km/h
        position1 = new Position(baseLat, baseLon, baseSpeed, baseAcc, baseMillis);
        position2 = new Position(baseLat + 0.001, baseLon + 0.005, baseSpeed, baseAcc + 1,
                baseMillis +
                        1000);
        position3 = new Position(baseLat + 0.009, baseLon + 0.01, baseSpeed, baseAcc + 2, baseMillis +
                10 * 1000);
    }

    @Test
    public void shouldNotDetectDamageTest() {
        AccelerometerEvent toLowAccEvent = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD-1+gravity, 5},
                baseNanos, 1);

        detector.onAccelerationChanged(toLowAccEvent);
        detector.onPositionChanged(position1);
        toLowAccEvent.setAccValues(new float[]{1, 11f, 5});
        detector.onAccelerationChanged(toLowAccEvent);
        toLowAccEvent.setAccValues(new float[]{1, 11.9f, 10});
        detector.onAccelerationChanged(toLowAccEvent);
        detector.onPositionChanged(position2);

        verify(listener, times(0)).onDamageDetected
                (any(Damage.class));

    }

    //tests scenario that takes place after start of listening, when no previous position and
    // accelerometer event is available, accelerometer event comes before first position is received
    @Test
    public void shouldDetectAfterStartedListening() {
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, -Constants.Damage
                .SMALL_DAMAGE_THRESHOLD-1+gravity, 4}, baseNanos -
                500 * 1000000, 1);
        detector.onAccelerationChanged(event);
        detector.onPositionChanged(position1);
        detector.onPositionChanged(position2);

        Damage expected = Damage.create(event, position1, detector.determineDamageValue(event));

        verify(listener, times(1)).onDamageDetected(eq(expected));
    }

    //tests scenario when to "big" enough accelerometer events occurred between single position
    // change, should notify about bigger
    @Test
    public void shouldDetectLaterEventWithFirstPosition() {
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .MEDIUM_DAMAGE_THRESHOLD+gravity, 4}, baseNanos -
                500 * 1000000, 1);
        AccelerometerEvent event2 = new AccelerometerEvent(new float[]{1, Constants.Damage
                .BIG_DAMAGE_THRESHOLD+gravity, 4},
                baseNanos - 100 * 1000000,
                1);

        detector.onAccelerationChanged(event);
        detector.onAccelerationChanged(event2);
        detector.onPositionChanged(position1);
        detector.onPositionChanged(position2);

        Damage expected = Damage.create(event2, position1, detector.determineDamageValue(event2));

        verify(listener, times(1)).onDamageDetected(eq(expected));
    }

    //as above but second event comes just after first position occurred
    @Test
    public void shouldDetectLaterEventWithFirstPosition2() {
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .MEDIUM_DAMAGE_THRESHOLD+gravity, 4}, baseNanos -
                500 * 1000000, 1);
        AccelerometerEvent event2 = new AccelerometerEvent(new float[]{1, Constants.Damage
                .BIG_DAMAGE_THRESHOLD+gravity, 4},
                baseNanos + 100 * 1000000,
                1);

        detector.onAccelerationChanged(event);
        detector.onPositionChanged(position1);
        detector.onAccelerationChanged(event2);
        detector.onPositionChanged(position2);

        Damage expected = Damage.create(event2, position1, detector.determineDamageValue(event2));

        verify(listener, times(1)).onDamageDetected(eq(expected));
    }

    @Test
    public void shouldDetectEarlierEventWithFirstPosition() {
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .BIG_DAMAGE_THRESHOLD+gravity, 4},
                baseNanos -
                500 * 1000000, 1);
        AccelerometerEvent event2 = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD+gravity, 4}, baseNanos +
                100 * 1000000,
                1);

        detector.onAccelerationChanged(event);
        detector.onPositionChanged(position1);
        detector.onAccelerationChanged(event2);
        detector.onPositionChanged(position2);

        Damage expected = Damage.create(event, position1, detector.determineDamageValue(event));

        verify(listener, times(1)).onDamageDetected(eq(expected));
    }

    @Test
    public void shouldDetectEventWithSecondPosition() {
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD+gravity, 4}, baseNanos
                + 900 * 1000000, 1);

        detector.onPositionChanged(position1);
        detector.onAccelerationChanged(event);
        detector.onPositionChanged(position2);

        Damage expected = Damage.create(event, position2, detector.determineDamageValue(event));

        verify(listener, times(1)).onDamageDetected(eq(expected));
    }

    @Test
    public void tooLowGPSAccuracyTest() {
        Position tooLowAcc = new Position(baseLat, baseLon, baseSpeed, DamageDetector
                .DESIRED_GPS_ACCURACY + 1, baseMillis);
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD+gravity, 4}, baseNanos
                + 100 * 1000000, 1);

        detector.onPositionChanged(tooLowAcc);
        detector.onAccelerationChanged(event);
        detector.onPositionChanged(position2);

        verify(listener, times(0)).onDamageDetected(any(Damage.class));
    }

    //First accelerometer event occurred then gps position changed but too long distance was
    // covered based on speed from gps position and timestamps from position and acc event
    @Test
    public void tooLongDistanceCoveredTest() {
        Position tooFast = new Position(baseLat, baseLon, baseSpeed + 20, DamageDetector
                .DESIRED_GPS_ACCURACY + 1, baseMillis);
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD+gravity, 4}, baseNanos
                + 100 * 1000000, 1);

        detector.onPositionChanged(tooFast);
        detector.onAccelerationChanged(event);
        detector.onPositionChanged(position2);

        verify(listener, times(0)).onDamageDetected(any(Damage.class));
    }

    @Test
    public void shouldDetectTwoEvents() {
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD+gravity, 4}, baseNanos + 100 * 1000000, 1);
        AccelerometerEvent event2 = new AccelerometerEvent(new float[]{1, Constants.Damage
                .MEDIUM_DAMAGE_THRESHOLD+gravity, 4}, baseNanos + 1100 * 1000000, 1);

        detector.onPositionChanged(position1);
        detector.onAccelerationChanged(event);
        detector.onPositionChanged(position2);
        detector.onAccelerationChanged(event2);
        detector.onPositionChanged(position3);

        Damage expected = Damage.create(event, position1, detector.determineDamageValue(event));
        Damage expected2 = Damage.create(event2, position2, detector.determineDamageValue(event2));

        verify(listener, times(1)).onDamageDetected(eq(expected));
        verify(listener, times(1)).onDamageDetected(eq(expected2));
    }

    @Test
    public void shouldDetectThreeDifferentDamages() {
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD+gravity, 4},
                baseNanos + 100 * 1000000, 1);
        AccelerometerEvent event2 = new AccelerometerEvent(new float[]{2, Constants.Damage
                .MEDIUM_DAMAGE_THRESHOLD+gravity, 4}, baseNanos
                + 1100 * 1000000, 1);
        AccelerometerEvent event3 = new AccelerometerEvent(new float[]{3, Constants.Damage
                .BIG_DAMAGE_THRESHOLD+gravity, 4},
                baseNanos + 9900L * 1000000, 1);

        detector.onPositionChanged(position1);
        detector.onAccelerationChanged(event);
        detector.onPositionChanged(position2);
        detector.onAccelerationChanged(event2);
        detector.onAccelerationChanged(event3);
        detector.onPositionChanged(position3);

        Damage expected = Damage.create(event, position1, detector.determineDamageValue(event));
        Damage expected2 = Damage.create(event2, position2, detector.determineDamageValue(event2));
        Damage expected3 = Damage.create(event3, position3, detector.determineDamageValue(event3));

        verify(listener, times(1)).onDamageDetected(eq(expected));
        verify(listener, times(1)).onDamageDetected(eq(expected2));
        verify(listener, times(1)).onDamageDetected(eq(expected3));
    }

    @Test
    public void shouldCallOnIncorrectOrientation(){
        Mockito.when(inspector.isInCorrectOrientation()).thenReturn(false);
        Mockito.when(inspector.getCurrentRotation()).thenReturn(new float[3]);

        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD+gravity, 4},
                baseNanos + 100 * 1000000, 1);

        detector.onAccelerationChanged(event);

        verify(listener, Mockito.times(1)).onIncorrectOrientation(new float[3]);
    }

    @Test
    public void shouldCallOnCorrectOrientationOnlyOnce(){
        Mockito.when(inspector.isInCorrectOrientation()).thenReturn(false);
        Mockito.when(inspector.getCurrentRotation()).thenReturn(new float[3]);

        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD+gravity, 4},
                baseNanos + 100 * 1000000, 1);
        detector.onAccelerationChanged(event);

        Mockito.when(inspector.isInCorrectOrientation()).thenReturn(true);

        detector.onAccelerationChanged(event);
        detector.onAccelerationChanged(event);
        detector.onAccelerationChanged(event);
        detector.onAccelerationChanged(event);

        verify(listener, Mockito.times(1)).onIncorrectOrientation(new float[3]);
        verify(listener, Mockito.times(1)).onCorrectOrientation();
    }

    @Test
    public void shouldNotDetectWhenInIncorrectOrientation(){
        Mockito.when(inspector.isInCorrectOrientation()).thenReturn(false);
        AccelerometerEvent event = new AccelerometerEvent(new float[]{1, Constants.Damage
                .SMALL_DAMAGE_THRESHOLD+gravity, 4},
                baseNanos + 100 * 1000000, 1);
        AccelerometerEvent event2 = new AccelerometerEvent(new float[]{2, Constants.Damage
                .MEDIUM_DAMAGE_THRESHOLD+gravity, 4}, baseNanos
                + 1100 * 1000000, 1);
        AccelerometerEvent event3 = new AccelerometerEvent(new float[]{3, Constants.Damage
                .BIG_DAMAGE_THRESHOLD+gravity, 4},
                baseNanos + 9900L * 1000000, 1);

        detector.onPositionChanged(position1);
        detector.onAccelerationChanged(event);
        detector.onPositionChanged(position2);
        detector.onAccelerationChanged(event2);
        detector.onAccelerationChanged(event3);
        detector.onPositionChanged(position3);

        verify(listener, times(0)).onDamageDetected(any(Damage.class));
    }
}
