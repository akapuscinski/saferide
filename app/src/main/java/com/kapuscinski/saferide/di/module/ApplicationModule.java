/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.di.module;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kapuscinski.saferide.domain.Constants;
import com.kapuscinski.saferide.domain.android.AndroidMainThreadExecutor;
import com.kapuscinski.saferide.domain.android.AndroidPreferencesManager;
import com.kapuscinski.saferide.domain.net.DamageServerApi;
import com.kapuscinski.saferide.domain.persistence.PreferencesManager;
import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;
import com.kapuscinski.saferide.domain.communication.AndroidCommunicationManager;
import com.kapuscinski.saferide.domain.communication.CommunicationManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApplicationModule {

    private Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public Context provideAppContext() {
        return context;
    }

    @Singleton
    @Provides
    ExecutorService provideThreadExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Singleton
    @Provides
    ScheduledExecutorService provideScheduledThreadExecutor() {
        return Executors.newScheduledThreadPool(5);
    }

    @Singleton
    @Provides
    MainThreadExecutor provideMainThreadExecutor() {
        return new AndroidMainThreadExecutor();
    }

    /**
     * Return gson that will skip ModelAdapter field from BaseModel class. That's necessary cause
     * we're using BaseModel as super class for our entities to save them using dbflow
     * @return Gson
     */
    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaredClass().equals(ModelAdapter.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    @Singleton
    @Provides
    Retrofit provideRetrofit(Gson gson) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); //todo based on build variant

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        return new Retrofit.Builder()
                .baseUrl(Constants.Net.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }

    @Singleton
    @Provides
    public DamageServerApi provideDamageServerApi(Retrofit retrofit) {
        return retrofit.create(DamageServerApi.class);
    }

    @Singleton
    @Provides
    public PreferencesManager providePreferencesManager(Context context) {
        return new AndroidPreferencesManager(context);
    }

    @Provides
    @Singleton
    public CommunicationManager provideCommunicationManager(Context context){
        return new AndroidCommunicationManager(context);
    }
}
