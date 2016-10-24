/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.di.component;

import com.kapuscinski.saferide.di.module.MapActivityModule;
import com.kapuscinski.saferide.presentation.view.activity.MapActivity;
import com.kapuscinski.saferide.di.module.ActivityModule;
import com.kapuscinski.saferide.di.module.LocationModule;
import com.kapuscinski.saferide.di.scope.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = {MapActivityModule.class,
        ActivityModule.class, LocationModule.class})
public interface MapActivityComponent {

    void inject(MapActivity mapActivity);
}