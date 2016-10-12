/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.contract;

import com.google.android.gms.maps.model.VisibleRegion;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Area;
import com.kapuscinski.saferide.domain.entity.Region;

import java.util.List;

public interface MapContract {

    interface View {

        boolean isTouchingMap();

        void showDamage(Damage damage);

        void showDamage(List<Damage> damageList);

        void showArea(Area area);

        void showAreas(List<Area> areas);

        void requestMap();

        void moveCamera(double latitude, double longitude, float zoom);

        void moveMarker(double latitude, double longitude);

        VisibleRegion getVisibleRegion();

        void checkGPSPermissions();

        void onDamageDetectionStarted();

        void onDamageDetectionPaused();

        void showRequestGpsDialog();

        void hideRequestGpsDialog();

        void showAdjustOrientationDialog();

        void hideAdjustOrientationDialog();

        void showSensorsNotAvailableDialog();

        void showProgress();

        void hideProgress();

        List<Region> getAreaRegions();

    }

    interface Presenter {
        void onMapReady();

        void onCameraChange(float zoom);

        void onMapTouch();

        void onMapTouchEnd();

        boolean onMyLocationClick();

        void onToggleDetectionClick();
    }
}
