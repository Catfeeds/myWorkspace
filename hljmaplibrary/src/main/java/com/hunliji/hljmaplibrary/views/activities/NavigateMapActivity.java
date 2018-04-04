package com.hunliji.hljmaplibrary.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljmaplibrary.R;
import com.hunliji.hljmaplibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = RouterPath.IntentPath.Map.NAVIGATE_MAP)
public class NavigateMapActivity extends HljBaseActivity implements AMap.InfoWindowAdapter {

    @BindView(R2.id.map_view)
    MapView mapView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private AMap aMap;

    public static final String ARG_TITLE = "title";
    public static final String ARG_ADDRESS = "address";
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";
    private String title;
    private String address;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_map);
        ButterKnife.bind(this);

        setOkText("其他地图");
        initValues();
        initMap(savedInstanceState);
    }

    private void initValues() {
        title = getIntent().getStringExtra(ARG_TITLE);
        address = getIntent().getStringExtra(ARG_ADDRESS);
        latitude = getIntent().getDoubleExtra(ARG_LATITUDE, 0);
        longitude = getIntent().getDoubleExtra(ARG_LONGITUDE, 0);
    }

    private void initMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
        aMap.getUiSettings()
                .setZoomControlsEnabled(false);
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
                .title(title)
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
        if (mapView != null) {
            mapView.onPause();
        }
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
    public void onOkButtonClick() {
        super.onOkButtonClick();
        onNavigate();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.map_info_navigate_window, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
        tvTitle.setText(marker.getTitle());
        tvContent.setText(marker.getSnippet());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigate();
            }
        });

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void onNavigate() {
        try {
            Uri url = Uri.parse("geo:" + "0,0" + "?q=" + address);
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(NavigateMapActivity.this, R.string.label_no_map, Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
