/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain;

public class Constants {

    public static final class Net {
        public static final String API_URL = "http://saferiderhc-akapa.rhcloud.com/";
        public static final int RETRY_REQUEST_COUNT = 2;
    }

    public static final class Damage {
        public static final float MINIMAL_CLUSTER_SIZE = 0.005f * 0.005f;
        public static final int DAMAGE_AREA_IN_ROW = 5;
        public static final float SMALL_DAMAGE_THRESHOLD = 4;
        public static final float MEDIUM_DAMAGE_THRESHOLD = 8;
        public static final float BIG_DAMAGE_THRESHOLD = 12;
    }
}
