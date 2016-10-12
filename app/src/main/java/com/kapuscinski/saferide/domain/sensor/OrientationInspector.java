/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.sensor;

public interface OrientationInspector {

    boolean isInCorrectOrientation();

    float[] getCurrentRotation();

    void start();

    void stop();

}
