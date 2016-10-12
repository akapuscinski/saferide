/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.presenter;

import com.kapuscinski.saferide.domain.Mapper;
import com.kapuscinski.saferide.domain.communication.CommunicationListener;
import com.kapuscinski.saferide.domain.communication.CommunicationManager;
import com.kapuscinski.saferide.domain.entity.Area;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Position;
import com.kapuscinski.saferide.domain.sensor.PositionListener;
import com.kapuscinski.saferide.domain.sensor.PositionSensor;
import com.kapuscinski.saferide.domain.usecase.GetDamageUseCase;
import com.kapuscinski.saferide.presentation.contract.MapContract;
import com.kapuscinski.survivormvp.presentation.presenter.BasePresenter;

import java.util.List;

import javax.inject.Inject;

/**
 * Presenter made for MapActivity. NOTE: this presenter survives orientation changes, view is
 * available after onResume has been called.
 */
public class MapPresenter extends BasePresenter<MapContract.View> implements
        MapContract.Presenter, CommunicationListener, PositionListener, PositionSensor.StatusListener {

    private GetDamageUseCase getDamageUseCase;

    private CommunicationManager communicationManager;
    private PositionSensor positionSensor;
    private float zoom;
    private boolean shouldRequestDamageData = false;
    private boolean followPosition = true; //helper flag to indicate whether map should or
    // shouldn't move to current position, follow again if location button was clicked, if user
    // manually moved the map don't follow

    @Inject
    public MapPresenter(GetDamageUseCase getDamageUseCase,
                        CommunicationManager communicationManager,
                        PositionSensor positionSensor) {
        this.getDamageUseCase = getDamageUseCase;
        this.communicationManager = communicationManager;
        this.positionSensor = positionSensor;

        this.positionSensor.addListener(this);
        this.positionSensor.addStatusListener(this);
    }

    @Override
    public void onResume() {
        communicationManager.addListener(this);
        view.checkGPSPermissions();
        view.requestMap();

        if (communicationManager.isDamageDetectionServiceActive()) {
            view.onDamageDetectionStarted();
            positionSensor.start();
        } else
            view.onDamageDetectionPaused();
    }

    @Override
    public void onPause() {
        positionSensor.stop();
    }

    @Override
    public void onDestroy() {
        positionSensor.removeListener(this);
        communicationManager.removeListener(this);
    }

    @Override
    public void onMapReady() {
        //todo add damage points for currently visible region
    }

    @Override
    public void onCameraChange(float zoom) {
        this.zoom = zoom;
        if(!view.isTouchingMap())
            requestDamageData();
        else
            shouldRequestDamageData = true;
    }

    @Override
    public void onMapTouch() {
        followPosition = false;
    }

    @Override
    public void onMapTouchEnd() {
        if(shouldRequestDamageData){
            requestDamageData();
            shouldRequestDamageData = false;
        }
    }

    @Override
    public boolean onMyLocationClick() {
        followPosition = true;
        return false; //use default behaviour
    }

    @Override
    public void onToggleDetectionClick() {
        if (communicationManager.isDamageDetectionServiceActive()) {
            view.onDamageDetectionPaused();
            communicationManager.stopDamageDetectionService();
            positionSensor.stop();
        } else {
            if (!positionSensor.isAvailable()) {
                view.showRequestGpsDialog();
            }
            view.onDamageDetectionStarted();
            communicationManager.startDamageDetectionService();
            positionSensor.start();
            followPosition = true;
        }
    }

    private void requestDamageData() {
        if (view.isTouchingMap())
            return;

        view.showProgress();

        getDamageUseCase.setListener(new GetDamageUseCase.Listener() {
            @Override
            public void onDamageReceived(List<Damage> damageList) {
                view.hideProgress();

                if (damageList != null)
                    view.showDamage(damageList);
            }

            @Override
            public void onClusterReceived(List<Area> areaList) {
                view.hideProgress();
                view.showAreas(areaList);
            }
        });
        getDamageUseCase.setAreaRegions(view.getAreaRegions());
        getDamageUseCase.setRegion(Mapper.mapVisibleRegionToRegion(view.getVisibleRegion()));
        getDamageUseCase.execute();
    }

    @Override
    public void onPositionChanged(Position position) {
        view.moveMarker(position.getLatitude(), position.getLongitude());

        if (followPosition)
            view.moveCamera(position.getLatitude(), position.getLongitude(), zoom);
    }

    @Override
    public void onDamageDetected(Damage damage) {
        view.showDamage(damage);
    }

    @Override
    public void onSensorsNotAvailable() {
        view.onDamageDetectionPaused();
        view.showSensorsNotAvailableDialog();
    }

    @Override
    public void onOrientationChange(boolean correctOrientation) {
        if (!correctOrientation)
            view.showAdjustOrientationDialog();
        else
            view.hideAdjustOrientationDialog();
    }

    //position sensor status listener
    @Override
    public void onTurnedOn() {
        view.hideRequestGpsDialog();
    }

    @Override
    public void onTurnedOff() {
        if(communicationManager.isDamageDetectionServiceActive())
            view.showRequestGpsDialog();
    }
}
