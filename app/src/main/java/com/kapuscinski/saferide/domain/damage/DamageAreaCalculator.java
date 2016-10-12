/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.damage;

import com.kapuscinski.saferide.domain.Constants;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Region;

import java.util.ArrayList;
import java.util.List;

public class DamageAreaCalculator {

    public static List<Region> calculateDamageAreaRegions(Region region) {
        List<Region> regions = new ArrayList<>();
        double width = region.getRightLon() - region.getLeftLon();
        double height = region.getTopLat() - region.getBottomLat();
        double size = width <= height ?
                width / Constants.Damage.DAMAGE_AREA_IN_ROW :
                height / Constants.Damage.DAMAGE_AREA_IN_ROW;

        for (double i = region.getBottomLat(); i < region.getTopLat(); i += size) {
            for (double j = region.getLeftLon(); j < region.getRightLon(); j += size) {
                regions.add(new Region(i + size, j + size, i, j));
            }
        }

        return regions;
    }

    public static List<Region> calculateDamageAreaRegions(double displayWidth, double
            displayHeight, Region visibleRegion) {
        List<Region> regions = new ArrayList<>();
        double size = displayHeight <= displayWidth ?
                displayHeight / Constants.Damage.DAMAGE_AREA_IN_ROW :
                displayWidth / Constants.Damage.DAMAGE_AREA_IN_ROW;

        for (double i = 0; i < displayHeight; i += size) {
            for (double j = 0; j < displayWidth; j += size) {
                regions.add(new Region(i + size, j + size, i, j));
            }
        }

        return regions;
    }

    public static Region calculateDamageAreaBounds(List<Damage> damageList) {
        Region r = new Region(0, 0, 200, 200);

        for (Damage damage : damageList) {
            if (damage.getLongitude() < r.getLeftLon())
                r.setLeftLon(damage.getLongitude());

            if (damage.getLongitude() > r.getRightLon())
                r.setRightLon(damage.getLongitude());

            if (damage.getLatitude() < r.getBottomLat())
                r.setBottomLat(damage.getLatitude());

            if (damage.getLatitude() > r.getTopLat())
                r.setTopLat(damage.getLatitude());
        }

        return r;
    }
}
