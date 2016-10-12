/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.android;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.kapuscinski.saferide.domain.Mapper;
import com.kapuscinski.saferide.domain.sensor.BaseSensor;
import com.kapuscinski.saferide.domain.sensor.PositionListener;
import com.kapuscinski.saferide.domain.sensor.PositionSensor;
import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AndroidPositionSensor extends BaseSensor<PositionListener>
        implements PositionSensor, LocationListener, GpsStatus.Listener {

    private LocationManager locationManager;
    private MainThreadExecutor mainThreadExecutor;
    private List<StatusListener> statusListeners = new ArrayList<>();

    @Inject
    public AndroidPositionSensor(LocationManager locationManager, MainThreadExecutor mainThreadExecutor) {
        this.locationManager = locationManager;
        this.mainThreadExecutor = mainThreadExecutor;
    }

    @Override
    public void start() {
        super.start();

        locationManager.addGpsStatusListener(this);

        //location manager has to be started on a main thread it won't work otherwise
        mainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                        AndroidPositionSensor.this);
            }
        });
    }

    @Override
    public void stop() {
        super.stop();

        locationManager.removeGpsStatusListener(this);

        mainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                locationManager.removeUpdates(AndroidPositionSensor.this);
            }
        });
    }

    @Override
    public boolean isAvailable() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        for (PositionListener listener : getListeners()) {
            listener.onPositionChanged(Mapper.mapLocationToPosition(location));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void addStatusListener(StatusListener listener) {
        if (!statusListeners.contains(listener))
            statusListeners.add(listener);
    }

    @Override
    public void removeStatusListener(StatusListener listener) {
        statusListeners.remove(listener);
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (event == GpsStatus.GPS_EVENT_STARTED) {
            for (StatusListener statusListener : statusListeners) {
                statusListener.onTurnedOn();
            }
        } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
            for (StatusListener statusListener : statusListeners) {
                statusListener.onTurnedOff();
            }
        }
    }
}
