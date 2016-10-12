/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.damage;

import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.net.DamageServerApi;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

public class DamageUploader {

    private DamageServerApi api;

    @Inject
    public DamageUploader(DamageServerApi api) {
        this.api = api;
    }

    public boolean uploadDamage(Damage damage){
        try {
            Response<ResponseBody> response = api.addDamage(damage).execute();

            if(response.code()!=200)
                return false;
            else{
                Timber.d("Damage uploaded successfully: %s", damage);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
