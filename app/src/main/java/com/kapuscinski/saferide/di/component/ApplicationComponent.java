/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.di.component;

import android.content.Context;

import com.google.gson.Gson;
import com.kapuscinski.saferide.domain.communication.CommunicationManager;
import com.kapuscinski.saferide.domain.net.DamageServerApi;
import com.kapuscinski.saferide.domain.persistence.PreferencesManager;
import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;
import com.kapuscinski.saferide.presentation.SafeRideApp;
import com.kapuscinski.saferide.di.module.ApplicationModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SafeRideApp app);

    Context context();
    ExecutorService executorService();
    ScheduledExecutorService scheduledExecutorService();
    MainThreadExecutor mainThreadExecutor();
    Gson gson();
    DamageServerApi api();
    PreferencesManager preferencesManager();
    CommunicationManager communicationManager();

}
