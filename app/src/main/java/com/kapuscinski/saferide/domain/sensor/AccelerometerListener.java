/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.sensor;

import com.kapuscinski.saferide.domain.entity.AccelerometerEvent;

/**
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */
public interface AccelerometerListener {

    void onAccelerationChanged(AccelerometerEvent event);

}
