/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.net;

import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Area;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DamageServerApi {

    String API_URL = "http://saferiderhc-akapa.rhcloud.com/";
    String TEST_API_URL = "http://localhost:8080/";

    @GET("damages/{coordinates}")
    Call<List<Damage>> getDamage(@Path("coordinates") String coordinates);

    @GET("area/{coordinates}")
    Call<Area> getArea(@Path("coordinates") String coordinates);

    @POST("entries")
    Call<ResponseBody> addDamage(@Body Damage damage);
}
