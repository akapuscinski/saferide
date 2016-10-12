package com.kapuscinski.survivormvp.presentation.activity;

import android.support.v7.app.AppCompatActivity;

import com.kapuscinski.survivormvp.presentation.presenter.BasePresenter;

import javax.inject.Inject;

/**
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com on 16.06.2016.
 */
public abstract class BaseActivity<V, Presenter extends BasePresenter<V>> extends
        AppCompatActivity {

    @Inject
    protected Presenter presenter;

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView((V) this);
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        presenter.detachView();
    }

}
