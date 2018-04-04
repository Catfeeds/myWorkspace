package com.hunliji.hljmaplibrary.views.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.hunliji.hljcommonlibrary.models.City;
import com.hunliji.hljcommonlibrary.utils.LocationSession;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljmaplibrary.R;
import com.hunliji.hljmaplibrary.R2;
import com.hunliji.hljmaplibrary.views.fragments.PoiResultFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by luohanlin on 2017/7/24.
 * 定位，poi查询，地点标注
 */
public class LocationMapActivity extends HljBaseNoBarActivity implements PoiResultFragment
        .OnPoiSelectListener {

    public static final String ARG_ADDRESS = "address";
    public static final String ARG_LATITUDE = "latitude";
    public static final String ARG_LONGITUDE = "longitude";

    @BindView(R2.id.btn_back)
    ImageButton btnBack;
    @BindView(R2.id.et_search)
    EditText etSearch;
    @BindView(R2.id.btn_clear)
    ImageButton btnClear;
    @BindView(R2.id.btn_search)
    Button btnSearch;
    @BindView(R2.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R2.id.map_view)
    MapView mapView;
    @BindView(R2.id.btn_save)
    Button btnSave;
    @BindView(R2.id.content_layout)
    FrameLayout contentLayout;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.img_search)
    ImageView imgSearch;

    private String cityStr;
    private String addressStr;
    private LatLng latLng;

    public static final String POI_RESULT_FRAGMENT_TAG = "poiResultFragmentTag";
    private InputMethodManager imm;
    private AMap aMap;
    private final float DEFAULT_ZOOM = 16;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            showPoiResultFragment();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();

        init(savedInstanceState);
    }

    public void init(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        initValues();
        initViews();
        initMap();
        setStatusBarPaddingColor(ContextCompat.getColor(this, R.color.colorWhite));
    }

    private void initValues() {
        addressStr = getIntent().getStringExtra(ARG_ADDRESS);
        double longitude = getIntent().getDoubleExtra(ARG_LONGITUDE, 0);
        double latitude = getIntent().getDoubleExtra(ARG_LATITUDE, 0);
        if (latitude > 0 && longitude > 0) {
            latLng = new LatLng(latitude, longitude);
        }
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    private void initViews() {
        etSearch.setText(addressStr);
        if (!TextUtils.isEmpty(addressStr)) {
            etSearch.setSelection(addressStr.length());
        }

        etSearch.addTextChangedListener(textWatcher);
    }

    private void initMap() {
        aMap = mapView.getMap();
        aMap.getUiSettings()
                .setZoomControlsEnabled(false);

        final boolean firstSearch;
        if (latLng != null) {
            // 如果有传入位置数据，显示mark并且设置为地图中心
            markOnMap(latLng, true);
            firstSearch = false;
        } else {
            // 没有传入位置数据，使用其他方式设置位置
            firstSearch = true;
        }

        setLocation();

        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                markOnMap(latLng, false);
            }
        });
        aMap.setOnPOIClickListener(new AMap.OnPOIClickListener() {
            @Override
            public void onPOIClick(Poi poi) {
                // 优先进入poi，没有poi再进入上面的click事件
                if (TextUtils.isEmpty(addressStr)) {
                    addressStr = poi.getName();
                }
                markOnMap(poi.getCoordinate(), false);
            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                if (firstSearch && etSearch.length() > 0) {
                    showPoiResultFragment();
                }
            }
        });
    }

    private void setLocation() {
        com.hunliji.hljcommonlibrary.models.Location hljLocation = LocationSession.getInstance()
                .getLocation(this);
        if (hljLocation != null) {
            LatLng hljLocationLatLng = new LatLng(hljLocation.getLatitude(),
                    hljLocation.getLongitude());
            cityStr = hljLocation.getCity();
            // 没有传入定位数据，则使用存有的用户定位数据
            if (latLng == null) {
                moveMapCenter(hljLocationLatLng, true);
            }
        }
        if (TextUtils.isEmpty(cityStr)) {
            City c = LocationSession.getInstance()
                    .getCity(this);
            if (c.getCid() > 0) {
                cityStr = c.getName();
            }
        }
        if (latLng == null || TextUtils.isEmpty(cityStr)) {
            // 没有现有数据，也没有传入数据，启用地图定位
            onLocation();
        }
    }

    /**
     * 启用地图定位
     */
    private void onLocation() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.showMyLocation(false);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                getLocationDetail(location);
                LatLng locationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (latLng == null) {
                    markOnMap(locationLatLng, true);
                }
            }
        });
        // 设置为true表示显示定位层并可触发定位
        aMap.setMyLocationEnabled(true);
    }

    // 根据定位信息，逆地理编码（坐标转地址），获取该地点的位置详细信息
    private void getLocationDetail(Location location) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(location.getLatitude(),
                location.getLongitude()), 200, GeocodeSearch.AMAP);
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(
                    RegeocodeResult regeocodeResult, int i) {
                cityStr = regeocodeResult.getRegeocodeAddress()
                        .getCity();
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        geocoderSearch.getFromLocationAsyn(query);
    }

    @OnClick(R2.id.btn_save)
    void onSave() {
        if (latLng == null) {
            return;
        }
        Intent intent = getIntent();
        intent.putExtra(ARG_ADDRESS, addressStr);
        intent.putExtra(ARG_LATITUDE, latLng.latitude);
        intent.putExtra(ARG_LONGITUDE, latLng.longitude);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @OnClick(R2.id.btn_back)
    @Override
    public void onBackPressed() {
        if (hidePoiResultFragment()) {
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R2.id.btn_search)
    void onSearchButton() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PoiResultFragment poiResultFragment = (PoiResultFragment) fragmentManager.findFragmentByTag(
                POI_RESULT_FRAGMENT_TAG);
        if (poiResultFragment != null && !poiResultFragment.isHidden()) {
            hidePoiResultFragment();
        } else {
            showPoiResultFragment();
        }
    }

    @OnClick(R2.id.btn_clear)
    void onEditTextClear() {
        etSearch.setText(null);
        imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    private boolean hidePoiResultFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PoiResultFragment poiResultFragment = (PoiResultFragment) fragmentManager.findFragmentByTag(
                POI_RESULT_FRAGMENT_TAG);
        if (poiResultFragment != null && !poiResultFragment.isHidden()) {
            btnSearch.setText("搜索");
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.hide(poiResultFragment);
            ft.commitAllowingStateLoss();
            return true;
        }

        return false;
    }

    private void showPoiResultFragment() {
        this.addressStr = etSearch.getText()
                .toString()
                .trim();
        btnSearch.setText("取消");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        PoiResultFragment poiResultFragment = (PoiResultFragment) fragmentManager.findFragmentByTag(
                POI_RESULT_FRAGMENT_TAG);
        if (poiResultFragment == null) {
            poiResultFragment = PoiResultFragment.newInstance(cityStr, addressStr);
            ft.add(R.id.content_layout, poiResultFragment, POI_RESULT_FRAGMENT_TAG);
            ft.commitAllowingStateLoss();
        } else {
            if (poiResultFragment.isHidden()) {
                ft.show(poiResultFragment);
                ft.commitAllowingStateLoss();
            }
            poiResultFragment.refresh(cityStr, addressStr);
        }
        poiResultFragment.setOnPoiSelectListener(this);
    }

    /**
     * 设置标记
     *
     * @param latLng
     * @param resetZoom
     */
    private void markOnMap(LatLng latLng, boolean resetZoom) {
        MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_map_point))
                .position(latLng)
                .draggable(false);
        this.latLng = latLng;
        mapView.getMap()
                .clear();
        mapView.getMap()
                .addMarker(markerOption);
        moveMapCenter(latLng, resetZoom);
    }


    /**
     * 重新设置地图中心
     *
     * @param latLng
     * @param resetZoom
     */
    private void moveMapCenter(LatLng latLng, boolean resetZoom) {
        float zoom;
        if (resetZoom) {
            zoom = DEFAULT_ZOOM;
        } else {
            zoom = aMap.getCameraPosition().zoom;
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition
                .fromLatLngZoom(
                latLng,
                zoom));
        aMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onPoiSelect(PoiItem poiItem) {
        etSearch.removeTextChangedListener(textWatcher);
        etSearch.setText(poiItem.getTitle());
        etSearch.addTextChangedListener(textWatcher);
        if (poiItem.getLatLonPoint() != null) {
            markOnMap(new LatLng(poiItem.getLatLonPoint()
                    .getLatitude(),
                    poiItem.getLatLonPoint()
                            .getLongitude()), true);
        } else {

        }
        hidePoiResultFragment();
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
}
