package com.kapuscinski.survivormvp.presentation.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.kapuscinski.survivormvp.presentation.loader.AbstractComponentLoader;
import com.kapuscinski.survivormvp.presentation.presenter.BasePresenter;

/**
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */
public abstract class BaseComponentActivity<V, Presenter extends
        BasePresenter<V>, Component> extends BaseActivity<V, Presenter> implements LoaderManager
        .LoaderCallbacks<Component>{

    private Component component;

    public abstract int getLoaderId();

    public abstract void injectDependencies(Component component);

    public abstract Component provideComponent();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(getLoaderId(), null, this);
    }

    @Override
    public Loader<Component> onCreateLoader(int id, Bundle args) {
        return new AbstractComponentLoader<Component>(this) {
            @Override
            protected Component getComponent() {
                return provideComponent();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Component> loader, Component data) {
        component = data;
        injectDependencies(component);
    }

    @Override
    public void onLoaderReset(Loader<Component> loader) {
        component = null;
    }
}
