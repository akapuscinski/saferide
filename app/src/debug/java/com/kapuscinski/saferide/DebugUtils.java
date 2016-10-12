/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide;

import com.google.android.gms.maps.model.VisibleRegion;

public class DebugUtils {

    public static String getRegionFromVisibleRegion(VisibleRegion region){
        StringBuilder builder = new StringBuilder();
        builder.append("Top: ");
        builder.append(defaultFormat(region.farLeft.latitude));
        builder.append("\n");
        builder.append("Right: ");
        builder.append(defaultFormat(region.farRight.longitude));
        builder.append("\n");
        builder.append("Bottom: ");
        builder.append(defaultFormat(region.nearLeft.latitude));
        builder.append("\n");
        builder.append("Left: ");
        builder.append(defaultFormat(region.nearLeft.longitude));
        builder.append("\n");
        builder.append("Height: ");
        builder.append(defaultFormat(region.farLeft.latitude-region.nearLeft.latitude));
        builder.append("\n");
        builder.append("Width: ");
        builder.append(defaultFormat(region.farRight.longitude-region.farLeft.longitude));
        return builder.toString();
    }

    public static String defaultFormat(double number){
        return String.format("%.6f", number);
    }
}
