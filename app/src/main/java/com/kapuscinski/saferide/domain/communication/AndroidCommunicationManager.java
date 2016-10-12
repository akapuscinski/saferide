/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.presentation.view.service.DamageDetectionService;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows different android application components to communicate and to check theirs
 * current state. Communication is based on {@link Intent} and {@link BroadcastReceiver}
 */
public class AndroidCommunicationManager implements CommunicationManager {

    public static final String DAMAGE_DETECTED_ACTION = "damage_detected";
    public static final String ORIENTATION_CHANGED_ACTION = "orientation_changed";
    public static final String SENSORS_NOT_AVAILABLE_ACTION = "sensors_not_available";
    public static final String ORIENTATION_KEY = "orientation";
    public static final String DAMAGE_KEY = "damage";

    private Context context;
    private List<CommunicationListener> listeners = new ArrayList<>();

    private DamageReceiver damageReceiver = new DamageReceiver();
    private OrientationReceiver orientationReceiver = new OrientationReceiver();
    private SensorsNotAvailableReceiver sensorsNotAvailableReceiver = new SensorsNotAvailableReceiver();

    private boolean damageDetectionServiceActive;

    public AndroidCommunicationManager(Context context) {
        this.context = context;
    }

    @Override
    public void broadcastDamageDetected(Damage damage) {
        Intent i = new Intent();
        i.setAction(DAMAGE_DETECTED_ACTION);
        i.putExtra(DAMAGE_KEY, damage);
        context.sendBroadcast(i);
    }

    @Override
    public void broadcastOrientationChange(boolean correctOrientation) {
        Intent i = new Intent();
        i.setAction(ORIENTATION_CHANGED_ACTION);
        i.putExtra(ORIENTATION_KEY, correctOrientation);
        context.sendBroadcast(i);
    }

    @Override
    public void broadcastSensorsNotAvailable() {
        Intent i = new Intent();
        i.setAction(SENSORS_NOT_AVAILABLE_ACTION);
        context.sendBroadcast(i);
    }

    @Override
    public void startDamageDetectionService() {
        context.startService(new Intent().setClass(context, DamageDetectionService.class));
    }

    @Override
    public void stopDamageDetectionService() {
        context.stopService(new Intent().setClass(context, DamageDetectionService.class));
    }

    @Override
    public void setDamageDetectionServiceActive(boolean active) {
        this.damageDetectionServiceActive = active;
    }

    @Override
    public boolean isDamageDetectionServiceActive() {
        return damageDetectionServiceActive;
    }

    @Override
    public void addListener(CommunicationListener listener) {
        if(listeners.size()==0)
            registerReceivers();

        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    @Override
    public void removeListener(CommunicationListener listener) {
        listeners.remove(listener);

        if(listeners.size()==0)
            unregisterReceivers();
    }

    private void registerReceivers() {
        context.registerReceiver(damageReceiver, new IntentFilter(DAMAGE_DETECTED_ACTION));
        context.registerReceiver(orientationReceiver, new IntentFilter(ORIENTATION_CHANGED_ACTION));
        context.registerReceiver(sensorsNotAvailableReceiver, new IntentFilter
                (SENSORS_NOT_AVAILABLE_ACTION));
    }

    private void unregisterReceivers() {
        context.registerReceiver(new DamageReceiver(), new IntentFilter(DAMAGE_DETECTED_ACTION));
        context.registerReceiver(new OrientationReceiver(), new IntentFilter(ORIENTATION_CHANGED_ACTION));
        context.registerReceiver(new SensorsNotAvailableReceiver(), new IntentFilter
                (SENSORS_NOT_AVAILABLE_ACTION));
        context.unregisterReceiver(damageReceiver);
        context.unregisterReceiver(sensorsNotAvailableReceiver);
        context.unregisterReceiver(orientationReceiver);
    }

    private class DamageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Damage damage = (Damage) intent.getSerializableExtra(AndroidCommunicationManager
                    .DAMAGE_KEY);
            for (CommunicationListener listener : listeners) {
                listener.onDamageDetected(damage);
            }
        }
    }

    private class OrientationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean inCorrectOrientation = intent.getBooleanExtra(AndroidCommunicationManager
                    .ORIENTATION_KEY, true);
            for (CommunicationListener listener : listeners) {
                listener.onOrientationChange(inCorrectOrientation);
            }
        }
    }

    private class SensorsNotAvailableReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            for (CommunicationListener listener : listeners) {
                listener.onSensorsNotAvailable();
            }
        }
    }
}
