/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.usecase;

import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

/**
 * This use case clears local database from damage detection data
 */
public class ClearDatabaseUseCase extends BaseAsyncUseCase<ClearDatabaseUseCase.Listener> {

    public interface Listener {

        void onDatabaseCleared();
    }

    @Inject
    public ClearDatabaseUseCase(ExecutorService executorService,
                                MainThreadExecutor mainThreadExecutor) {
        this.executor = executorService;
        this.mainThreadExecutor = mainThreadExecutor;
    }

    @Override
    public void run() {
        Delete.table(Damage.class);
        if (listener != null)
            post(new Runnable() {
                @Override
                public void run() {
                    listener.onDatabaseCleared();
                }
            });
    }


}
