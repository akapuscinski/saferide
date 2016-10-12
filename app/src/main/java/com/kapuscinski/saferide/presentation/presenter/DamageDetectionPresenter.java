/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.presenter;

import com.kapuscinski.saferide.domain.communication.CommunicationManager;
import com.kapuscinski.saferide.domain.damage.DamageDetector;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.usecase.DetectDamagesUseCase;
import com.kapuscinski.saferide.presentation.contract.DamageDetectionContract;

import javax.inject.Inject;

public class DamageDetectionPresenter implements DamageDetectionContract.Presenter,
        DamageDetector.Listener {

    private DetectDamagesUseCase damagesUseCase;
    private CommunicationManager communicationManager;
    private DamageDetectionContract.View view;
    private boolean inCorrectOrientation = true; //helper boolean to call orientation changes
    // only when they changed from incorrect to correct, without that onIncorrectOrientation
    // would be called until correctOrientation method would be called

    @Inject
    public DamageDetectionPresenter(DetectDamagesUseCase damagesUseCase,
                                    CommunicationManager communicationManager) {
        this.damagesUseCase = damagesUseCase;
        this.communicationManager = communicationManager;

        this.damagesUseCase.setListener(this);
    }

    @Override
    public void onCreate(){
        communicationManager.setDamageDetectionServiceActive(true);
        damagesUseCase.execute();
    }

    @Override
    public void onDestroy(){
        communicationManager.setDamageDetectionServiceActive(false);
        damagesUseCase.stop();
    }

    @Override
    public void onDamageDetected(Damage damage) {
        communicationManager.broadcastDamageDetected(damage);
    }

    @Override
    public void onIncorrectOrientation(float[] orientation) {
        if(inCorrectOrientation){
            inCorrectOrientation = false;
            view.showIncorrectOrientationMessage();
            communicationManager.broadcastOrientationChange(false);
        }
    }

    @Override
    public void onCorrectOrientation() {
        if(!inCorrectOrientation){
            inCorrectOrientation = true;
            view.hideIncorrectOrientationMessage();
            communicationManager.broadcastOrientationChange(true);
        }
    }

    @Override
    public void onSensorsNotAvailable() {
        communicationManager.broadcastSensorsNotAvailable();
    }

    public void setView(DamageDetectionContract.View view) {
        this.view = view;
    }
}
