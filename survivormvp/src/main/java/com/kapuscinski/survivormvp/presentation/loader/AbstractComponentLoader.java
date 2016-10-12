package com.kapuscinski.survivormvp.presentation.loader;

import android.content.Context;
import android.support.v4.content.Loader;

/**
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com on 28.06.2016.
 */
public abstract class AbstractComponentLoader<T> extends Loader<T> {

    private T component;

    public AbstractComponentLoader(Context context) {
        super(context);
    }

    protected abstract T getComponent();

    @Override
    protected void onStartLoading() {
        if(component!=null)
            deliverResult(component);
        else
            forceLoad();
    }

    @Override
    protected void onForceLoad() {
        component = getComponent();
        deliverResult(component);
    }

    @Override
    protected void onReset() {
        component = null;
    }
}
