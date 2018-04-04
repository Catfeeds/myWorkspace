package com.hunliji.hljmaplibrary.views.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljmaplibrary.R;
import com.hunliji.hljmaplibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends HljBaseActivity implements AMap.InfoWindowAdapter {

    @BindView(R2.id.map_view)
    MapView mapView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private AMap aMap;

    public static final String ARG_ADDRESS = "address";
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";
    private String address;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_map);
        ButterKnife.bind(this);

        initValues();
        initMap(savedInstanceState);
    }

    private void initValues() {
        address = getIntent().getStringExtra(ARG_ADDRESS);
        latitude = getIntent().getDoubleExtra(ARG_LATITUDE, 0);
        longitude = getIntent().getDoubleExtra(ARG_LONGITUDE, 0);
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        if (latitude > 0 && longitude > 0) {
            addMarker();
        }
    }

    private void addMarker() {
        aMap.setInfoWindowAdapter(this);
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_map_point))
                .position(latLng)
                .title(address)
                .snippet(address)
                .draggable(false);
        mapView.getMap()
                .clear();
        Marker marker = mapView.getMap()
                .addMarker(markerOption);
        marker.showInfoWindow();
        moveMapCenter(latLng);
    }

    /**
     * 重新设置地图中心
     *
     * @param latLng
     */
    private void moveMapCenter(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition
                .fromLatLngZoom(
                latLng,
                14));
        aMap.moveCamera(cameraUpdate);
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onFinish() {
        super.onFinish();
        mapView.onDestroy();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.map_info_window, null);
        TextView tvAddress =  view.findViewById(R.id.tv_address);
        tvAddress.setText(marker.getTitle());
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

}
