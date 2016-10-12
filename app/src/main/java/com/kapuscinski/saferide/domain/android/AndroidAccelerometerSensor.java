/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.android;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.kapuscinski.saferide.domain.sensor.AccelerometerListener;
import com.kapuscinski.saferide.domain.Mapper;
import com.kapuscinski.saferide.domain.sensor.AccelerometerSensor;
import com.kapuscinski.saferide.domain.sensor.BaseSensor;

import javax.inject.Inject;

public class AndroidAccelerometerSensor extends BaseSensor<AccelerometerListener> implements
        AccelerometerSensor, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Inject
    public AndroidAccelerometerSensor(SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void start() {
        super.start();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void stop() {
        super.stop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean isAvailable() {
        return accelerometer!=null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        for (AccelerometerListener accelerometerListener : getListeners()) {
            accelerometerListener.onAccelerationChanged(
                    Mapper.mapSensorEventToAccelerometerEvent(event));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
