/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.damage;

import android.support.annotation.Nullable;

import com.kapuscinski.saferide.domain.Constants;
import com.kapuscinski.saferide.domain.entity.Area;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Region;
import com.kapuscinski.saferide.domain.net.DamageServerApi;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

public class DamageDownloader {

    private DamageServerApi api;
    private final int retryCount = Constants.Net.RETRY_REQUEST_COUNT;

    @Inject
    public DamageDownloader(DamageServerApi api) {
        this.api = api;
    }

    @Nullable
    public List<Damage> downloadDamage(Region region) {
        try {
            Response<List<Damage>> damageResponse = api.getDamage(region.toCoordinatesString()).execute();
            if (damageResponse.code() != 200)
                return null;
            else
                return damageResponse.body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public Area downloadArea(Region region){
        return downloadArea(region, retryCount);
    }

    @Nullable
    public Area downloadArea(Region region, int retryCount){
        if(retryCount<1)
            return null;
        try {
            Response<Area> damageResponse = api.getArea(region.toCoordinatesString())
                    .execute();
            if (damageResponse.code() != 200){
                return downloadArea(region, --retryCount);
            }
            else
                return damageResponse.body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}