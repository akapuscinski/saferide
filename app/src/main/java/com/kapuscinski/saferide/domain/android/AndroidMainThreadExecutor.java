/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.android;

import android.os.Handler;
import android.os.Looper;

import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;

/**
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com on 15.05.2016.
 */
public class AndroidMainThreadExecutor implements MainThreadExecutor {

    private Handler Handler;

    public AndroidMainThreadExecutor() {
        Handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void post(Runnable task) {
        Handler.post(task);
    }
}
