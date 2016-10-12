/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kapuscinski.saferide.domain.damage.DamageDownloader;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Region;
import com.kapuscinski.saferide.domain.net.DamageServerApi;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DamageDownloaderTest {

    private DamageDownloader downloader;

    @Before
    public void setup(){
        DamageServerApi api = provideDamageServerApi(provideRetrofit(provideGson()));
        downloader = new DamageDownloader(api);
    }

    @Test
    public void testDownload(){
        Region r = new Region(60.22,44.99,20.10,10.1);
        List<Damage> d = downloader.downloadDamage(r);
        d.size();
    }

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

    public DamageServerApi provideDamageServerApi(Retrofit retrofit) {
        return retrofit.create(DamageServerApi.class);
    }
}
