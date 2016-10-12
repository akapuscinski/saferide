/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.di.module;

import android.content.Context;

import com.kapuscinski.saferide.di.scope.ActivityScope;
import com.kapuscinski.saferide.presentation.custom.BitmapsProvider;

import dagger.Module;
import dagger.Provides;

@Module
public class MapActivityModule {

    @ActivityScope
    @Provides
    public BitmapsProvider provideMapBitmapsCreator(Context context){
        return new BitmapsProvider(context);
    }
}
