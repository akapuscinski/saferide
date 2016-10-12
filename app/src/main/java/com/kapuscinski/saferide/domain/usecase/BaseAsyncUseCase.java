/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.domain.usecase;


import com.kapuscinski.saferide.domain.threading.MainThreadExecutor;

import java.util.concurrent.ExecutorService;

/**
 * The base class for async use cases. This classes uses {@link ExecutorService} to do it's
 * work defined inside <code>run()</code> method in background. This class may use <code>post
 * (Runnable task)</code> method to do operations on main thread e.g. to notify listener.
 * <p>
 * <b>Note</b> Don't use <code>run()</code> method directly, use <code>execute()</code> method
 * instead, the current thread will be used to do this class job otherwise
 * </p>
 *
 * @param <Listener> Listener interface for listening on operation results
 */
public abstract class BaseAsyncUseCase<Listener> implements Runnable, UseCase<Listener> {

    protected ExecutorService executor;
    protected MainThreadExecutor mainThreadExecutor;
    protected Listener listener;

    public void execute() {
        executor.submit(this);
    }

    ;

    protected void post(Runnable task) {
        mainThreadExecutor.post(task);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public String toString() {
        return "AsyncUseCase{" +
                "listener=" + listener +
                ", executor=" + executor +
                ", mainThreadExecutor=" + mainThreadExecutor +
                '}';
    }
}
