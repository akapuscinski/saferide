/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.di.component;

import com.kapuscinski.saferide.presentation.view.activity.SettingsActivity;
import com.kapuscinski.saferide.di.module.ActivityModule;
import com.kapuscinski.saferide.di.scope.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface SettingsActivityComponent {

    void inject(SettingsActivity.SettingsFragment settingsFragment);
}
