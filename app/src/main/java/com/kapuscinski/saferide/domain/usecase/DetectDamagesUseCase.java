/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.usecase;

import com.kapuscinski.saferide.domain.damage.DamageDetector;
import com.kapuscinski.saferide.domain.damage.DamageUploader;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

/**
 * This use case detects damage and sends it to server. If damage couldn't be uploaded to the
 * server saves it to local database
 *
 * <p>
 *     This class uses {@link DamageDetector} for damage detection and {@link DamageUploader} for
 *     uploading damage data
 * </p>
 */
public class DetectDamagesUseCase extends BaseAsyncUseCase<DamageDetector.Listener> implements
        DamageDetector.Listener {

    private DamageDetector detector;
    private DamageUploader uploader;

    @Inject
    public DetectDamagesUseCase(ExecutorService executorService, MainThreadExecutor
            mainThreadExecutor, DamageDetector damageDetector, DamageUploader uploader) {
        this.executor = executorService;
        this.mainThreadExecutor = mainThreadExecutor;
        this.detector = damageDetector;
        this.uploader = uploader;
        this.detector.setListener(this);
    }

    @Override
    public void run() {
        this.detector.start();
    }

    public void stop() {
        this.detector.stop();
    }

    @Override
    public void onDamageDetected(final Damage damage) {
        post(new Runnable() {
            @Override
            public void run() {
                if (listener != null)
                    listener.onDamageDetected(damage);
            }
        });

        executor.submit(new Runnable() {
            @Override
            public void run() {
                if (!uploader.uploadDamage(damage)) {
                    damage.save();
                }
            }
        });
    }

    @Override
    public void onIncorrectOrientation(float[] orientation) {
        listener.onIncorrectOrientation(orientation);
    }

    @Override
    public void onCorrectOrientation() {
        listener.onCorrectOrientation();
    }

    @Override
    public void onSensorsNotAvailable() {
        listener.onSensorsNotAvailable();
    }
}
