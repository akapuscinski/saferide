/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.view.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.kapuscinski.saferide.R;
import com.kapuscinski.saferide.presentation.SafeRideApp;
import com.kapuscinski.saferide.presentation.contract.DamageDetectionContract;
import com.kapuscinski.saferide.presentation.view.activity.MapActivity;
import com.kapuscinski.saferide.di.component.DaggerDamageDetectionComponent;
import com.kapuscinski.saferide.presentation.presenter.DamageDetectionPresenter;

import javax.inject.Inject;

/**
 * Service that is constantly listening for new damage detection and sends broadcast if damage
 * occurred. Note: this service should be started in foreground as it drains battery fast, user
 * should know it it's running.
 */
public class DamageDetectionService extends Service implements DamageDetectionContract.View {

    private static final int NOTIFICATION_ID = 123;

    @Inject
    DamageDetectionPresenter presenter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SafeRideApp app = (SafeRideApp) getApplicationContext();
        DaggerDamageDetectionComponent.builder()
                .applicationComponent(app.getApplicationComponent())
                .build().inject(this);


        presenter.setView(this);
        presenter.onCreate();

        showNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        stopForeground(true);
    }

    private void showNotification() {
        startForeground(NOTIFICATION_ID, createDefaultNotification());
    }

    private Notification createDefaultNotification() {
        Intent i = new Intent(this, MapActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                i, 0);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(false);
        builder.setTicker(getResources().getString(R.string.service_ticker));
        builder.setContentTitle(getResources().getString(R.string.service_title));
        builder.setContentText(getResources().getString(R.string.service_content));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setOngoing(true);
        builder.setContentIntent(pi);
        return builder.build();
    }

    private Notification createDefaultNotification(String tickerText, String title, String message) {
        Intent i = new Intent(this, MapActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                i, 0);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(false);
        builder.setTicker(tickerText);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setOngoing(true);
        builder.setContentIntent(pi);
        return builder.build();
    }

    @Override
    public void showIncorrectOrientationMessage() {
        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification n = createDefaultNotification(
                getResources().getString(R.string.service_incorrect_ticker),
                getResources().getString(R.string.service_incorrect_title),
                getResources().getString(R.string.service_incorrect_message));

        nm.notify(NOTIFICATION_ID, n);
    }

    @Override
    public void hideIncorrectOrientationMessage() {
        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification n = createDefaultNotification();
        n.tickerText = getResources().getString(R.string.service_correct_ticker);

        nm.notify(NOTIFICATION_ID, n);
    }
}
