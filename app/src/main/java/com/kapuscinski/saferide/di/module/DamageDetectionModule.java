/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.di.module;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;

import com.kapuscinski.saferide.domain.android.AndroidAccelerometerSensor;
import com.kapuscinski.saferide.domain.android.AndroidMagneticFieldSensor;
import com.kapuscinski.saferide.domain.android.AndroidOrientationInspector;
import com.kapuscinski.saferide.domain.android.AndroidOrientationSensor;
import com.kapuscinski.saferide.domain.android.AndroidPositionSensor;
import com.kapuscinski.saferide.domain.sensor.AccelerometerSensor;
import com.kapuscinski.saferide.domain.sensor.MagneticFieldSensor;
import com.kapuscinski.saferide.domain.sensor.OrientationSensor;
import com.kapuscinski.saferide.domain.sensor.PositionSensor;
import com.kapuscinski.saferide.domain.sensor.OrientationInspector;
import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;
import com.kapuscinski.saferide.di.scope.ServiceScope;

import java.util.concurrent.ScheduledExecutorService;

import dagger.Module;
import dagger.Provides;

@Module
public class DamageDetectionModule {

    @ServiceScope
    @Provides
    public OrientationInspector provideOrientationInspector(
            ScheduledExecutorService scheduledExecutorService,
            OrientationSensor orientationSensor) {
        return new AndroidOrientationInspector(scheduledExecutorService, orientationSensor);
    }

    @ServiceScope
    @Provides
    public OrientationSensor provideOrientationSensor(SensorManager sensorManager) {
        return new AndroidOrientationSensor(sensorManager);
    }

    @ServiceScope
    @Provides
    public LocationManager provideLocationManager(Context context) {
        return (LocationManager) context
                .getSystemService(Activity.LOCATION_SERVICE);
    }

    @ServiceScope
    @Provides
    public SensorManager provideSensorManager(Context context) {
        return (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
    }

    @ServiceScope
    @Provides
    public AccelerometerSensor provideAccelerometer(SensorManager sensorManager){
        return new AndroidAccelerometerSensor(sensorManager);
    }


    @ServiceScope
    @Provides
    public MagneticFieldSensor provideMagneticField(SensorManager sensorManager){
        return new AndroidMagneticFieldSensor(sensorManager);
    }

    @ServiceScope
    @Provides
    public PositionSensor providePositionSensor(LocationManager locationManager,
                                                MainThreadExecutor mainThreadExecutor){
        return new AndroidPositionSensor(locationManager, mainThreadExecutor);
    }

}
