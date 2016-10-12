/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.di.module;

import android.app.Activity;

import com.kapuscinski.saferide.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    Activity provideActivity(){return activity;}

}
