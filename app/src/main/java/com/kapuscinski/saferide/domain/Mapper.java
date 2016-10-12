/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain;

import android.hardware.SensorEvent;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.VisibleRegion;
import com.kapuscinski.saferide.domain.entity.AccelerometerEvent;
import com.kapuscinski.saferide.domain.entity.Position;
import com.kapuscinski.saferide.domain.entity.Region;
import com.raizlabs.android.dbflow.annotation.NotNull;

public class Mapper {

    public static Position mapLocationToPosition(Location location) {
        return new Position(location.getLatitude(), location.getLongitude(), location.getSpeed(),
                location.getAccuracy(), location.getTime());
    }

    public static AccelerometerEvent mapSensorEventToAccelerometerEvent(SensorEvent event) {
        return new AccelerometerEvent(event.values[0], event.values[1], event.values[2],
                event.timestamp, event.accuracy);
    }

    public static Region mapVisibleRegionToRegion(VisibleRegion visibleRegion) {
        return new Region(visibleRegion.farLeft.latitude, visibleRegion.farRight.longitude,
                visibleRegion.nearLeft.latitude, visibleRegion.nearLeft.longitude);
    }

    public static Position mapLatLngToPosition(LatLng latLng) {
        return new Position(latLng.latitude, latLng.longitude, 0, 0, 0);
    }

    public static LatLngBounds mapRegionToLatLngBounds(@NotNull Region region) {
        LatLng farLeft = new LatLng(region.getTopLat(), region.getLeftLon());
        LatLng farRight = new LatLng(region.getTopLat(), region.getRightLon());
        LatLng nearLeft = new LatLng(region.getBottomLat(), region.getLeftLon());
        LatLng nearRight = new LatLng(region.getBottomLat(), region.getRightLon());
        return new LatLngBounds.Builder()
                .include(farLeft)
                .include(farRight)
                .include(nearLeft)
                .include(nearRight)
                .build();
    }
}
