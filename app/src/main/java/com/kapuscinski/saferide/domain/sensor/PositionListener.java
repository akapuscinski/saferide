/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.sensor;

import com.kapuscinski.saferide.domain.entity.Position;

public interface PositionListener {

    void onPositionChanged(Position position);
}
