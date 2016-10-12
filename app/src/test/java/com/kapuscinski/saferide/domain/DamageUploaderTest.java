/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kapuscinski.saferide.domain.damage.DamageUploader;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.net.DamageServerApi;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//todo whole class need testing
public class DamageUploaderTest {

    private DamageUploader uploader;

    @Before
    public void setup(){
        DamageServerApi api = provideDamageServerApi(provideRetrofit(provideGson()));
        uploader = new DamageUploader(api);
    }

    @Test
    public void testUpload(){
        Damage d = new Damage(1, 13.5f, 0.5f, 2, 47.05467, 11.09871, 10, 1470340062000l, 17);
        uploader.uploadDamage(d);
    }

    @Test
    public void testBigUpload(){
        for (int i = 0; i < 10; i++) {
            Damage d = new Damage(1, 15.5f, 0.5f, 2, 40, 20+0.0001*i, 10,
                    1470340062000l+i, 17);
            uploader.uploadDamage(d);
        }
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
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC); //todo based on build variant

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        return new Retrofit.Builder()
                .baseUrl(Constants.Net.API_URL)
//                .baseUrl("http://localhost:8080")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
    }

    public DamageServerApi provideDamageServerApi(Retrofit retrofit) {
        return retrofit.create(DamageServerApi.class);
    }
}
