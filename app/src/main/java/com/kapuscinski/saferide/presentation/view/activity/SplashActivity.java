/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.view.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent().setClass(this, MapActivity.class));
        finish();
    }
}
