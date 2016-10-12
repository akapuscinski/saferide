/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain;

import com.google.android.gms.maps.model.LatLng;

public class Utils {

    public static void convertRadiansToDegrees(float[] radians) {
        if (radians == null)
            return;
        for (int i = 0; i < radians.length; i++) {
            radians[i] = (float) Math.toDegrees(radians[i]);
        }
    }

    public static double getDistanceBetweenPoints(LatLng p1, LatLng p2) {
        return Math.sqrt(
                Math.pow(p1.latitude - p2.latitude, 2) + Math.pow(p1.longitude - p2.longitude, 2)
        );
    }

    public static double convertDegreesToMeters(double degrees) {
        return calculateDistanceBetweenPoints(0, 0, 0, degrees);
    }

    public static double calculateDistanceBetweenPoints(double lat1,
                                                        double lon1,
                                                        double lat2,
                                                        double lon2) {
        double R = 6372.8;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c * 1000;
    }
}
