package com.kapuscinski.survivormvp.presentation.presenter;

import java.util.ArrayList;

/**
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com on 16.05.2016.
 */
public abstract class BasePresenter<View> {

    protected View view;
    protected ArrayList<ViewCommand<View>> storedCommands = new ArrayList<>();
    private boolean viewDestroyed = false; //used for presenters that survive orientation changes

    public void attachView(View view){
        this.view = view;
    }

    public void onResume(){viewDestroyed = false;}

    public void onPause(){}

    public void onDestroy(){viewDestroyed = true;}

    public void detachView(){
        this.view = null;
    }

    protected boolean wasViewDestroyed(){
        return viewDestroyed;
    }

}
