package com.kapuscinski.survivormvp.presentation.presenter;

/**
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */
public interface ViewCommand<View> {
    void execute(View view);
}
