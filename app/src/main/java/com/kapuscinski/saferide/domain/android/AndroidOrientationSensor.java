/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.android;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.kapuscinski.saferide.domain.sensor.OrientationListener;
import com.kapuscinski.saferide.domain.sensor.BaseSensor;
import com.kapuscinski.saferide.domain.sensor.OrientationSensor;

import javax.inject.Inject;

public class AndroidOrientationSensor extends BaseSensor<OrientationListener>
        implements OrientationSensor, SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accelerometer, magneticField;
    private float[] accValues, magValues, orientation;

    @Inject
    public AndroidOrientationSensor(SensorManager sensorManager) {
        this.sensorManager = sensorManager;

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void start() {
        super.start();
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void stop() {
        super.stop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean isAvailable() {
        return accelerometer!=null && magneticField!=null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            accValues = event.values;
        } else {
            magValues = event.values;
        }

        orientation = calculateOrientation();
        com.kapuscinski.saferide.domain.Utils.convertRadiansToDegrees(orientation);

        if(orientation!=null){
            for (OrientationListener listener : getListeners()) {
                listener.onOrientationChanged(orientation);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private float[] calculateOrientation() {
        if (accValues != null && magValues != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, accValues, magValues);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                return orientation;
            }
            return null;
        }
        return null;
    }
}
