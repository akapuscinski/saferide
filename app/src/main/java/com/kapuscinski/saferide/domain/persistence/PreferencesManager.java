/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.persistence;

public interface PreferencesManager {

    String SMALL_DAMAGE_THRESHOLD = "small_damage";
    String MEDIUM_DAMAGE_THRESHOLD = "medium_damage";
    String BIG_DAMAGE_THRESHOLD = "big_damage";

    interface Listener{
        void onPreferenceChanged(String key, PreferencesManager manager);
    }

    void addListener(Listener listener);

    void removeListener(Listener listener);

    int getInt(String key, int defaultValue);

    long getLong(String key, long defaultValue);

    float getFloat(String key, float defaultValue);

    String getString(String key, String defaultValue);

    boolean getBoolean (String key, boolean defaultValue);

    int getInt(String key);

    long getLong(String key);

    float getFloat(String key);

    String getString(String key);

    boolean getBoolean (String key);

    void saveInt(String key, int value);

    void saveLong(String key, long value);

    void saveFloat(String key, float value);

    void saveString(String key, String value);

    void saveBoolean(String key, boolean value);
}
