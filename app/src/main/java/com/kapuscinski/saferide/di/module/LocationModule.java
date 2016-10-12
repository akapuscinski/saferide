/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.di.module;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;

import com.kapuscinski.saferide.domain.android.AndroidPositionSensor;
import com.kapuscinski.saferide.domain.sensor.PositionSensor;
import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationModule {

    @Provides
    public LocationManager provideLocationManager(Context context) {
        return (LocationManager) context
                .getSystemService(Activity.LOCATION_SERVICE);
    }

    @Provides
    public PositionSensor providePositionSensor(LocationManager locationManager, MainThreadExecutor
            mainThreadExecutor) {
        return new AndroidPositionSensor(locationManager, mainThreadExecutor);
    }
}
