/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.util;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * As GoogleMap allows only to set (not add) one listener to be notified about camera change,
 * this class allows to notify multiple listeners, set it as a listener for onCameraChange() method
 */
public class OnCameraChangeListenerAggregator implements GoogleMap.OnCameraChangeListener {

    private List<GoogleMap.OnCameraChangeListener> listeners = new ArrayList<>();

    public OnCameraChangeListenerAggregator() {
    }

    public void addListener(GoogleMap.OnCameraChangeListener listener){
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeListener(GoogleMap.OnCameraChangeListener listener){
        listeners.remove(listener);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        for (GoogleMap.OnCameraChangeListener listener : listeners) {
            listener.onCameraChange(cameraPosition);
        }
    }
}
