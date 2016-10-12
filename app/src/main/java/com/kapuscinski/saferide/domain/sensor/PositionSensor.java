/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.sensor;

public interface PositionSensor extends Sensor<PositionListener> {

    interface StatusListener {
        void onTurnedOn();

        void onTurnedOff();
    }

    void addStatusListener(StatusListener listener);

    void removeStatusListener(StatusListener listener);
}
