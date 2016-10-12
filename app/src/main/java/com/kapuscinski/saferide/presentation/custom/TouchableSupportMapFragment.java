/*
 * Created by Adrian Kapuscinski kapuscinskiadrian@gmail.com
 */

package com.kapuscinski.saferide.presentation.custom;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

public class TouchableSupportMapFragment extends SupportMapFragment {

    public interface MapTouchListener{
        void onMapTouch();

        void onTouchEnd();
    }

    private View mOriginalContentView;
    private TouchableWrapper mTouchView;
    private MapTouchListener mapTouchListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent, savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }

    @Override
    public View getView() {
        return mOriginalContentView;
    }

    public void setMapTouchListener(MapTouchListener listener) {
        this.mapTouchListener = listener;
    }

    private class TouchableWrapper extends FrameLayout {

        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    if(mapTouchListener !=null)
                        mapTouchListener.onMapTouch();
                    break;

                case MotionEvent.ACTION_UP:
                    if(mapTouchListener !=null)
                        mapTouchListener.onTouchEnd();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}