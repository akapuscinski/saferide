/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.contract;

public interface DamageDetectionContract {

    interface View{
        void showIncorrectOrientationMessage();

        void hideIncorrectOrientationMessage();
    }

    interface Presenter{
        void onCreate();

        void onDestroy();
    }
}
