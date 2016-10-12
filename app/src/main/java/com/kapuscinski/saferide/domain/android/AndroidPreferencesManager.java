/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kapuscinski.saferide.domain.persistence.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Android implementation of {@link PreferencesManager} used to store primitive values locally
 * for app configuration purposes. This class uses {@link SharedPreferences} internally.
 */
public class AndroidPreferencesManager implements PreferencesManager, SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private List<Listener> listeners;

    public AndroidPreferencesManager(Context context) {
        listeners = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void addListener(Listener listener) {
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }


    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    @Override
    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    @Override
    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }

    @Override
    public float getFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    @Override
    public String getString(String key) {
        return preferences.getString(key, null);
    }

    @Override
    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    @Override
    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    @Override
    public void saveLong(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    @Override
    public void saveFloat(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    @Override
    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    @Override
    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        for (Listener listener : listeners) {
            listener.onPreferenceChanged(s, this);
        }
    }
}
