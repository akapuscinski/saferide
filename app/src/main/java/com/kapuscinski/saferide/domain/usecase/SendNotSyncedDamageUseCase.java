/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.usecase;

import com.kapuscinski.saferide.domain.damage.DamageUploader;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Damage_Table;
import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * This use case sends data retrieved from database which isn't already synced with server and
 * sends this data to server. If operation was successful this class marks data as synced, this
 * class restarts infinitely in periods defined by constant {@link #RETRY_DELAY}
 */
public class SendNotSyncedDamageUseCase extends BaseAsyncUseCase {

    public static final long RETRY_DELAY = 4 * 60 * 1000;

    private DamageUploader uploader;
    private ScheduledExecutorService scheduledExecutor;

    @Inject
    public SendNotSyncedDamageUseCase(ScheduledExecutorService scheduledExecutorService, MainThreadExecutor
            mainThreadExecutor, DamageUploader uploader) {
        this.scheduledExecutor = scheduledExecutorService;
        this.mainThreadExecutor = mainThreadExecutor;
        this.uploader = uploader;
    }

    @Override
    public void run() {
        List<Damage> notSyncedDamage = retrieveNotSyncedDamage();
        if (notSyncedDamage.size() > 0) {
            for (Damage damage : notSyncedDamage) {
                if (uploader.uploadDamage(damage)) {
                    damage.setSynced(true);
                    damage.save();
                }
            }
        }
    }

    @Override
    public void execute() {
        scheduledExecutor.scheduleWithFixedDelay(this, 0, RETRY_DELAY, TimeUnit
                .MILLISECONDS);
    }

    private List<Damage> retrieveNotSyncedDamage() {
        return SQLite.select().from(Damage.class)
                .where(Damage_Table.synced.is(false))
                .queryList();
    }
}
