/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.kapuscinski.saferide.domain.usecase.SendNotSyncedDamageUseCase;
import com.kapuscinski.saferide.di.component.ApplicationComponent;
import com.kapuscinski.saferide.di.component.DaggerApplicationComponent;
import com.kapuscinski.saferide.di.module.ApplicationModule;
import com.raizlabs.android.dbflow.BuildConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class SafeRideApp extends Application {

    private ApplicationComponent applicationComponent;

    @Inject
    SendNotSyncedDamageUseCase sendNotSynced;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Timber.plant(new Timber.DebugTree());

        FlowManager.init(new FlowConfig.Builder(this).build());
        if (BuildConfig.DEBUG) FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        applicationComponent.inject(this);

        sendNotSynced.execute();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
