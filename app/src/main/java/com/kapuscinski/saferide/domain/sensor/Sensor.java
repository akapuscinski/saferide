/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.sensor;

public interface Sensor<Listener> {

    void addListener(Listener listener);

    void removeListener(Listener listener);

    void start();

    void stop();

    boolean isRunning();

    boolean isAvailable();
}
