/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.view.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.kapuscinski.saferide.R;
import com.kapuscinski.saferide.di.component.DaggerMapActivityComponent;
import com.kapuscinski.saferide.di.component.MapActivityComponent;
import com.kapuscinski.saferide.domain.Constants;
import com.kapuscinski.saferide.domain.Mapper;
import com.kapuscinski.saferide.domain.entity.Area;
import com.kapuscinski.saferide.domain.entity.Damage;
import com.kapuscinski.saferide.domain.entity.Region;
import com.kapuscinski.saferide.presentation.SafeRideApp;
import com.kapuscinski.saferide.presentation.contract.MapContract;
import com.kapuscinski.saferide.presentation.custom.BitmapsProvider;
import com.kapuscinski.saferide.presentation.custom.TouchableSupportMapFragment;
import com.kapuscinski.saferide.presentation.presenter.MapPresenter;
import com.kapuscinski.saferide.presentation.util.OnCameraChangeListenerAggregator;
import com.kapuscinski.survivormvp.presentation.activity.BaseComponentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This activity shows map with damage data and adds new damage data if damage was detected
 */
public class MapActivity extends BaseComponentActivity<MapContract.View, MapPresenter,
        MapActivityComponent> implements OnMapReadyCallback, MapContract.View {

    private static final int REQUEST_GPS_PERMISSIONS = 100;
    private static final int LOADER_ID = 1;

    @BindView(R.id.map_progress) ProgressBar progress;
    @BindView(R.id.map_toggleDetection) FloatingActionButton toggleDetectionBtn;

    @Inject BitmapsProvider bitmapCreator;
    private TouchableSupportMapFragment mapFragment;
    private GoogleMap map;
    private MaterialDialog adjustOrientationDialog, activateGpsDialog;
    private OnCameraChangeListenerAggregator onCameraChangeListeners;
    private HashMap<LatLng, Marker> damageMap = new HashMap<>();
    private List<GroundOverlay> overlays = new ArrayList<>();
    private boolean touchingMap;

    @Override
    public int getLoaderId() {
        return LOADER_ID;
    }

    @Override
    public void injectDependencies(MapActivityComponent mapActivityComponent) {
        mapActivityComponent.inject(this);
    }

    @Override
    public MapActivityComponent provideComponent() {
        SafeRideApp app = (SafeRideApp) getApplicationContext();
        return DaggerMapActivityComponent.builder()
                .applicationComponent(app.getApplicationComponent())
                .build();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        mapFragment = (TouchableSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id
                .map_mapFragment);
        onCameraChangeListeners = new OnCameraChangeListenerAggregator();
        buildDialogs();

        mapFragment.setMapTouchListener(new TouchableSupportMapFragment.MapTouchListener() {
            @Override
            public void onMapTouch() {
                touchingMap = true;
                presenter.onMapTouch();
            }

            @Override
            public void onTouchEnd() {
                presenter.onMapTouchEnd();
                touchingMap = false;
            }
        });
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                presenter.onDestroy(); //call it explicitly cause if we use finish() the activity
                // won't go through onDestroy() method
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_map_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return presenter.onMyLocationClick();
            }
        });
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);

        googleMap.setOnCameraChangeListener(onCameraChangeListeners);
        onCameraChangeListeners.addListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                presenter.onCameraChange(cameraPosition.zoom);
            }
        });

        presenter.onMapReady();
    }

    @OnClick(R.id.map_toggleDetection)
    public void onToggleDetectionClick() {
        presenter.onToggleDetectionClick();
    }

    @Override
    public boolean isTouchingMap() {
        return touchingMap;
    }

    @Override
    public void showDamage(Damage damage) {
        LatLng latLng = new LatLng(damage.getLatitude(), damage.getLongitude());

        //check if damage is already added
        if (damageMap.containsKey(latLng))
            return;

        Bitmap icon = bitmapCreator.createMarkerBitmap(damage.getDamageValue(), damage
                .getEntriesCount());

        Marker m = map.addMarker(new MarkerOptions()
                .position(new LatLng(damage.getLatitude(), damage.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(icon)));
        damageMap.put(latLng, m);
    }

    @Override
    public void showDamage(List<Damage> damageList) {
        clearOverlays();
        showMarkers();
        for (Damage damage : damageList) {
            showDamage(damage);
        }
    }

    @Override
    public void showAreas(List<Area> areas) {
        clearOverlays();
        hideMarkers();
        for (Area area : areas) {
            showArea(area);
        }
    }

    @Override
    public void showArea(Area area) {
        if (area.getEntriesCount() == 0)
            return;

        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmapCreator
                .createGroundOverlayBitmap(area.getEntriesCount()));

        GroundOverlay overlay = map.addGroundOverlay(new GroundOverlayOptions()
                .positionFromBounds(Mapper.mapRegionToLatLngBounds(area.getRegion()))
                .anchor(0.5f, 0.5f)
                .image(icon));
        animateOverlay(overlay);
        overlays.add(overlay);
    }

    @Override
    public void requestMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void moveCamera(double latitude, double longitude, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), map
                .getCameraPosition().zoom));
    }

    @Override
    public void moveMarker(double latitude, double longitude) {
        //for future if we use custom marker for our position
    }

    @Override
    public VisibleRegion getVisibleRegion() {
        return map.getProjection().getVisibleRegion();
    }

    @Override
    public void checkGPSPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_GPS_PERMISSIONS);
        }
    }

    @Override
    public void onDamageDetectionStarted() {
        toggleDetectionBtn.setImageResource(R.drawable.ic_pause_dark);
    }

    @Override
    public void onDamageDetectionPaused() {
        toggleDetectionBtn.setImageResource(R.drawable.ic_play_dark);
    }

    @Override
    public void showRequestGpsDialog() {
        activateGpsDialog.show();
    }

    @Override
    public void hideRequestGpsDialog() {
        if (activateGpsDialog.isShowing())
            activateGpsDialog.hide();
    }

    @Override
    public void showAdjustOrientationDialog() {
        adjustOrientationDialog.show();
    }

    @Override
    public void hideAdjustOrientationDialog() {
        adjustOrientationDialog.hide();
    }

    @Override
    public void showSensorsNotAvailableDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.map_warning)
                .content(R.string.map_sensors_not_available)
                .positiveText(R.string.ok)
                .theme(Theme.LIGHT)
                .show();
    }

    @Override
    public void showProgress() {
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override
    public List<Region> getAreaRegions() {
        List<Region> regions = new ArrayList<>();
        float displayHeight = mapFragment.getView().getHeight();
        float displayWidth = mapFragment.getView().getWidth();

        Projection p = map.getProjection();

        double size = displayHeight <= displayWidth ?
                displayHeight / Constants.Damage.DAMAGE_AREA_IN_ROW :
                displayWidth / Constants.Damage.DAMAGE_AREA_IN_ROW;

        bitmapCreator.setAreaSize((int) size);

        for (double i = 0; i < displayHeight; i += size) {
            double top = p.fromScreenLocation(new Point(0, (int) i)).latitude;
            double bottom = p.fromScreenLocation(new Point(0, (int) (i + size))).latitude;
            for (double j = 0; j < displayWidth; j += size) {
                double left = p.fromScreenLocation(new Point((int) j, 0)).longitude;
                double right = p.fromScreenLocation(new Point((int) (j + size), 0)).longitude;
                regions.add(new Region(top, right, bottom, left));
            }
        }
        return regions;
    }

    private void clearOverlays() {
        for (GroundOverlay overlay : overlays) {
            overlay.remove();
        }
        overlays.clear();
    }

    private void hideMarkers() {
        Iterator<Map.Entry<LatLng, Marker>> iterator = damageMap.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().setVisible(false);
        }
    }

    private void showMarkers() {
        Iterator<Map.Entry<LatLng, Marker>> iterator = damageMap.entrySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().getValue().setVisible(true);
        }
    }

    private void buildDialogs() {
        adjustOrientationDialog = new MaterialDialog.Builder(this)
                .theme(Theme.LIGHT)
                .title(R.string.map_warning)
                .content(R.string.map_incorrect_orientation_content)
                .negativeText(R.string.map_dismiss)
                .build();

        activateGpsDialog = new MaterialDialog.Builder(this)
                .title(R.string.map_warning)
                .content(R.string.map_no_gps_content)
                .positiveText(R.string.ok)
                .theme(Theme.LIGHT)
                .build();
    }

    private BitmapDescriptor getMarkerIcon(Damage damage) {
        switch (damage.getDamageValue()) {
            case Damage.BIG_DAMAGE_VALUE:
                return BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_RED);
            case Damage.MEDIUM_DAMAGE_VALUE:
                return BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_YELLOW);
            case Damage.SMALL_DAMAGE_VALUE:
                return BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_BLUE);
            default:
                return null;
        }
    }

    private void animateOverlay(final GroundOverlay overlay) {
        final float baseWidth = overlay.getWidth();
        ValueAnimator animator = ValueAnimator.ofFloat(0, baseWidth);
        animator.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                overlay.setDimensions(animation.getAnimatedFraction() * baseWidth);
            }
        });
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private MarkerOptions createPositionMarker(LatLng position) {
        return new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.defaultMarker
                        (BitmapDescriptorFactory.HUE_BLUE));
    }
}
