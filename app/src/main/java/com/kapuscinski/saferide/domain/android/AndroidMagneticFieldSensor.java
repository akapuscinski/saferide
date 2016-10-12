/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.android;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.kapuscinski.saferide.domain.sensor.MagneticFieldListener;
import com.kapuscinski.saferide.domain.sensor.BaseSensor;
import com.kapuscinski.saferide.domain.sensor.MagneticFieldSensor;

import javax.inject.Inject;

public class AndroidMagneticFieldSensor extends BaseSensor<MagneticFieldListener>
        implements MagneticFieldSensor, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor magneticField;

    @Inject
    public AndroidMagneticFieldSensor(SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void start() {
        super.start();
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void stop() {
        super.stop();

        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean isAvailable() {
        return magneticField!=null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        for (MagneticFieldListener listener : getListeners()) {
            listener.onMagneticFieldChanged(event.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
