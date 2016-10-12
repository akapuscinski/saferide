/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.sensor;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSensor<Listener> implements Sensor<Listener> {

    private List<Listener> listeners = new ArrayList<>();
    private boolean running;

    protected List<Listener> getListeners() {
        return listeners;
    }

    /**
     * Adds listener to listeners list if listener isn't already added.
     * @param listener
     */
    @Override
    public void addListener(Listener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    /**
     * Removes listener from listeners list, if listeners list is empty this method calls
     * {@link BaseSensor#stop()}
     * @param listener
     */
    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
        if (listeners.size() == 0) {
            //don't drain battery when no one is listening
            stop();
        }
    }

    /**
     * Starts sensor if it's not running already
     */
    @Override
    public void start() {
        if (isRunning())
            return;
        this.running = true;
    }


    /**
     * Stops sensor if it's running
     */
    @Override
    public void stop() {
        if (!isRunning())
            return;
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
