package com.hunliji.hljmaplibrary.views.activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljmaplibrary.R;
import com.hunliji.hljmaplibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by luohanlin on 2017/7/27.
 */

public class LocationInfoActivity extends HljBaseActivity implements RadioGroup
        .OnCheckedChangeListener {
    @BindView(R2.id.rb_batterySaving)
    RadioButton rbBatterySaving;
    @BindView(R2.id.rb_deviceSensors)
    RadioButton rbDeviceSensors;
    @BindView(R2.id.rb_hightAccuracy)
    RadioButton rbHightAccuracy;
    @BindView(R2.id.rg_locationMode)
    RadioGroup rgLocationMode;
    @BindView(R2.id.tv_info)
    TextView tvInfo;
    @BindView(R2.id.et_interval)
    EditText etInterval;
    @BindView(R2.id.layout_interval)
    LinearLayout layoutInterval;
    @BindView(R2.id.et_httpTimeout)
    EditText etHttpTimeout;
    @BindView(R2.id.cb_onceLocation)
    CheckBox cbOnceLocation;
    @BindView(R2.id.cb_gpsFirst)
    CheckBox cbGpsFirst;
    @BindView(R2.id.cb_needAddress)
    CheckBox cbNeedAddress;
    @BindView(R2.id.cb_cacheAble)
    CheckBox cbCacheAble;
    @BindView(R2.id.cb_onceLastest)
    CheckBox cbOnceLastest;
    @BindView(R2.id.cb_sensorAble)
    CheckBox cbSensorAble;
    @BindView(R2.id.bt_location)
    Button btLocation;
    @BindView(R2.id.tv_result)
    TextView tvResult;

    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            onLocationResult(aMapLocation);
        }
    };
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;
    private boolean isLocationIng = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);
        ButterKnife.bind(this);

        initView();
        initLocation();
    }

    private void initView() {
        rgLocationMode.setOnCheckedChangeListener(this);
    }

    /**
     * init location client and options
     */
    private void initLocation() {
        locationClient = new AMapLocationClient(getApplicationContext());
        locationClientOption = new AMapLocationClientOption();
        locationClient.setLocationOption(locationClientOption);
        locationClient.setLocationListener(locationListener);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (null == locationClientOption) {
            locationClientOption = new AMapLocationClientOption();
        }
        if (checkedId == R.id.rb_batterySaving) {
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode
                    .Battery_Saving);
        } else if (checkedId == R.id.rb_deviceSensors) {
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode
                    .Device_Sensors);
        } else if (checkedId == R.id.rb_hightAccuracy) {
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode
                    .Hight_Accuracy);
        }
    }

    private void setViewEnable(boolean isEnable) {
        for (int i = 0; i < rgLocationMode.getChildCount(); i++) {
            rgLocationMode.getChildAt(i)
                    .setEnabled(isEnable);
        }
        etInterval.setEnabled(isEnable);
        etHttpTimeout.setEnabled(isEnable);
        cbOnceLocation.setEnabled(isEnable);
        cbGpsFirst.setEnabled(isEnable);
        cbNeedAddress.setEnabled(isEnable);
        cbCacheAble.setEnabled(isEnable);
        cbOnceLastest.setEnabled(isEnable);
        cbSensorAble.setEnabled(isEnable);
    }

    @OnClick(R2.id.bt_location)
    void onLocation() {
        if (!isLocationIng) {
            setViewEnable(false);
            btLocation.setText("停止定位");
            tvResult.setText("正在定位。。。");
            startLocation();
        } else {
            setViewEnable(true);
            btLocation.setText("开始定位");
            stopLocation();
            tvResult.setText("定位停止");
        }
        isLocationIng = !isLocationIng;
    }

    /**
     * reset all options according new setting args
     */
    private void resetLocationClientOption() {
        // 设置是否需要显示地址信息
        locationClientOption.setNeedAddress(cbNeedAddress.isChecked());
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationClientOption.setGpsFirst(cbGpsFirst.isChecked());
        locationClientOption.setGpsFirst(cbGpsFirst.isChecked());
        // 设置是否开启缓存
        locationClientOption.setLocationCacheEnable(cbCacheAble.isChecked());
        // 设置是否单次定位
        locationClientOption.setOnceLocation(cbOnceLocation.isChecked());
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        locationClientOption.setOnceLocationLatest(cbOnceLastest.isChecked());
        //设置是否使用传感器
        locationClientOption.setSensorEnable(cbSensorAble.isChecked());
        //设置是否开启wifi扫描，如果设置为false时同时会停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        String strInterval = etInterval.getText()
                .toString();
        if (!TextUtils.isEmpty(strInterval)) {
            try {
                // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
                locationClientOption.setInterval(Long.valueOf(strInterval));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        String strTimeout = etHttpTimeout.getText()
                .toString();
        if (!TextUtils.isEmpty(strTimeout)) {
            try {
                // 设置网络请求超时时间
                locationClientOption.setHttpTimeOut(Long.valueOf(strTimeout));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * start location processing
     */
    private void startLocation() {
        resetLocationClientOption();
        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();
    }

    /**
     * stop location processing
     */
    private void stopLocation() {
        locationClient.stopLocation();
    }

    /**
     * Must called at activity onDestroy
     */
    private void destroyLocationClient() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationClientOption = null;
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        destroyLocationClient();
    }

    /**
     * received location result
     *
     * @param location
     */
    private void onLocationResult(AMapLocation location) {
        if (null != location) {

            StringBuffer sb = new StringBuffer();
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if (location.getErrorCode() == 0) {
                sb.append("定位成功" + "\n");
                sb.append("定位类型: " + location.getLocationType() + "\n");
                sb.append("经    度    : " + location.getLongitude() + "\n");
                sb.append("纬    度    : " + location.getLatitude() + "\n");
                sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                sb.append("提供者    : " + location.getProvider() + "\n");

                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append("星    数    : " + location.getSatellites() + "\n");
                sb.append("国    家    : " + location.getCountry() + "\n");
                sb.append("省            : " + location.getProvince() + "\n");
                sb.append("市            : " + location.getCity() + "\n");
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                sb.append("区            : " + location.getDistrict() + "\n");
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
                //定位完成的时间
                sb.append("定位时间: " + location.getTime() + "\n");
            } else {
                //定位失败
                sb.append("定位失败" + "\n");
                sb.append("错误码:" + location.getErrorCode() + "\n");
                sb.append("错误信息:" + location.getErrorInfo() + "\n");
                sb.append("错误描述:" + location.getLocationDetail() + "\n");
            }
            //定位之后的回调时间
            sb.append("回调时间: " + System.currentTimeMillis() + "\n");

            //解析定位结果，
            String result = sb.toString();
            tvResult.setText(result);
        } else {
            tvResult.setText("定位失败，loc is null");
        }
    }
}
