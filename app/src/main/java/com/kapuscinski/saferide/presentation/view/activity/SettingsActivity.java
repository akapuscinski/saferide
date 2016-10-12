/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.view.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.kapuscinski.saferide.R;
import com.kapuscinski.saferide.domain.damage.DamageDetector;
import com.kapuscinski.saferide.domain.persistence.PreferencesManager;
import com.kapuscinski.saferide.domain.usecase.ClearDatabaseUseCase;
import com.kapuscinski.saferide.presentation.SafeRideApp;
import com.kapuscinski.saferide.presentation.custom.FloatEditTextPreference;
import com.kapuscinski.saferide.di.component.DaggerSettingsActivityComponent;
import com.kapuscinski.saferide.di.component.SettingsActivityComponent;

import javax.inject.Inject;

public class SettingsActivity extends AppCompatActivity {

    private SettingsActivityComponent component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SafeRideApp app = (SafeRideApp) getApplicationContext();
        component = DaggerSettingsActivityComponent.builder()
                .applicationComponent(app.getApplicationComponent())
                .build();

        //we are not using preference activity directly cause it's hiding actionbar and
        // addPreferenceFromResource method is deprecated for PreferenceActivity
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public SettingsActivityComponent getComponent() {
        return component;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences
            .OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

        @Inject ClearDatabaseUseCase clearDatabaseUseCase;

        private SharedPreferences preferences;
        private FloatEditTextPreference.WarningDialog warningDialog;

        private FloatEditTextPreference smallDamage, mediumDamage, bigDamage;
        private Preference clearDb;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            preferences.registerOnSharedPreferenceChangeListener(this);

            smallDamage = (FloatEditTextPreference) findPreference(PreferencesManager.SMALL_DAMAGE_THRESHOLD);
            mediumDamage = (FloatEditTextPreference) findPreference(PreferencesManager
                    .MEDIUM_DAMAGE_THRESHOLD);
            bigDamage = (FloatEditTextPreference) findPreference(PreferencesManager
                    .BIG_DAMAGE_THRESHOLD);
            warningDialog = new FloatEditTextPreference.WarningDialog() {
                @Override
                public void showNumberFormatException(Context context) {
                    new MaterialDialog.Builder(context)
                            .title("Warning")
                            .content("Number format exception occurred. Remember to separate decimal values " +
                                    "with dot '.' not comma")
                            .theme(Theme.LIGHT)
                            .positiveText("OK")
                            .show();
                }
            };
            smallDamage.setWarningDialog(warningDialog);
            mediumDamage.setWarningDialog(warningDialog);
            bigDamage.setWarningDialog(warningDialog);

            clearDb = findPreference("clear_db");
            clearDb.setOnPreferenceClickListener(this);

            setupDamageThresholds();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            ((SettingsActivity) getActivity()).getComponent().inject(this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            preferences.unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case PreferencesManager.SMALL_DAMAGE_THRESHOLD:
                case PreferencesManager.MEDIUM_DAMAGE_THRESHOLD:
                case PreferencesManager.BIG_DAMAGE_THRESHOLD:
                    Preference p = findPreference(key);
                    p.setSummary(String.format("%.1f", preferences.getFloat(key, -1f)));
            }
        }

        private void setupDamageThresholds() {
            smallDamage.setDefaultValue(DamageDetector.SMALL_DAMAGE_THRESHOLD);
            mediumDamage.setDefaultValue(DamageDetector.MEDIUM_DAMAGE_THRESHOLD);
            bigDamage.setDefaultValue(DamageDetector.BIG_DAMAGE_THRESHOLD);

            smallDamage.setSummary(String.format("%.1f", preferences.getFloat(PreferencesManager
                    .SMALL_DAMAGE_THRESHOLD, DamageDetector.SMALL_DAMAGE_THRESHOLD)));
            mediumDamage.setSummary(String.format("%.1f", preferences.getFloat(PreferencesManager
                    .MEDIUM_DAMAGE_THRESHOLD, DamageDetector.MEDIUM_DAMAGE_THRESHOLD)));
            bigDamage.setSummary(String.format("%.1f", preferences.getFloat(PreferencesManager
                    .BIG_DAMAGE_THRESHOLD, DamageDetector.BIG_DAMAGE_THRESHOLD)));
        }

        @Override
        public boolean onPreferenceClick(final Preference preference) {
            switch (preference.getKey()) {
                case "clear_db":
                    preference.setTitle(getResources().getString(R.string.preferences_clearing_db));
                    clearDatabaseUseCase.setListener(new ClearDatabaseUseCase.Listener() {
                        @Override
                        public void onDatabaseCleared() {
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    preference.setTitle(getResources().getString(R.string.preferences_clear_db));
                                }
                            }, 1000);

                            preference.setTitle(getResources().getString(R.string
                                    .preferences_cleared));
                        }
                    });
                    clearDatabaseUseCase.execute();
                    return true;
            }
            return false;
        }
    }

}
