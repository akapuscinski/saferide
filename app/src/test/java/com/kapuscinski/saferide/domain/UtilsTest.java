/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class UtilsTest {

    private double delta = 0.0000001;

    @Test
    public void shouldCalculatePointsDistance() {
        LatLng l1 = new LatLng(0, 0);
        LatLng l2 = new LatLng(0, 2);
        LatLng l3 = new LatLng(4, 0);
        LatLng l4 = new LatLng(1, 1);

        assertEquals(Utils.getDistanceBetweenPoints(l1, l2), 2, delta);
        assertEquals(Utils.getDistanceBetweenPoints(l1, l3), 4, delta);
        assertEquals(Utils.getDistanceBetweenPoints(l1, l4), Math.sqrt(2), delta);
    }

    //see online implementation to compare results http://www.movable-type.co.uk/scripts/latlong.html
    @Test
    public void testDegreesToMetersConversion() {
        double m = Utils.convertDegreesToMeters(10);
        assertEquals(1112000, m, 0.9);
    }
}
