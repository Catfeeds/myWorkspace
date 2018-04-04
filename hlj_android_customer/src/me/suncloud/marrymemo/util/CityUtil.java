package me.suncloud.marrymemo.util;

import android.content.Context;
import android.os.AsyncTask;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hunliji.hljcommonlibrary.models.Location;
import com.hunliji.hljcommonlibrary.utils.DeviceUuidFactory;
import com.hunliji.hljcommonlibrary.utils.EmptySubscriber;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.LocationSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.api.CustomCommonApi;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.task.HttpPostTask;
import me.suncloud.marrymemo.task.OnHttpRequestListener;

/**
 * Created by mo_yu on 2017/6/5.城市相关工具类
 */

public class CityUtil {

    private Context context;
    private OnGetCityResultListener onGetCityResultListener;
    private OnLocationListener onLocationListener;
    private AMapLocationClient client;

    public CityUtil(Context context, OnGetCityResultListener onGetCityResultListener) {
        this.context = context;
        this.onGetCityResultListener = onGetCityResultListener;
    }

    public interface OnGetCityResultListener {
        void onResult(City city);
    }

    public interface OnLocationListener {
        void onLocation(AMapLocation location);
    }

    public void setOnLocationListener(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }

    public void startLocation() {
        if (client != null) {
            if (!client.isStarted() && LocationSession.getInstance()
                    .getLocation(context) == null) {
                client.startLocation();
            }
        }
    }

    public void stopLocation() {
        if (client != null) {
            if (client.isStarted()) {
                client.stopLocation();
            }
        }
    }

    //    public void location() {
    //        LocationClientOption option = new LocationClientOption();
    //        option.setOpenGps(true);
    //        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
    //        option.setCoorType("bd09ll");
    //        option.setScanSpan(3000);
    //        option.setIsNeedAddress(true);
    //        client = new LocationClient(context);
    //        client.setLocOption(option);
    //        client.registerLocationListener(new BDLocationListener() {
    //            @Override
    //            public void onReceiveLocation(BDLocation location) {
    //                if (onLocationListener != null) {
    //                    onLocationListener.onLocation(location);
    //                }
    //                if (location.getLocType() == 61 || location.getLocType() == 65 || location
    //                        .getLocType() == 161) {
    //                    Location loc = LocationSession.getInstance()
    //                            .getLocation(context);
    //                    if (loc == null) {
    //                        loc = new Location();
    //                    }
    //                    getMyCity(loc,
    //                            location.getProvince(),
    //                            location.getCity(),
    //                            location.getDistrict(),
    //                            location.getLatitude(),
    //                            location.getLongitude());
    //                    loc.setDistrict(location.getDistrict());
    //                    loc.setLatitude(location.getLatitude());
    //                    loc.setLongitude(location.getLongitude());
    //                    loc.setAddress(location.getAddrStr());
    //                    loc.setCity(location.getCity());
    //                    loc.setProvince(location.getProvince());
    //                    LocationSession.getInstance()
    //                            .saveLocation(context, loc);
    //                    sendToken(loc);
    //                    client.stop();
    //                }
    //            }
    //
    //            @Override
    //            public void onConnectHotSpotMessage(String s, int i) {
    //
    //            }
    //        });
    //        client.start();
    //    }

    public void location() {
        client = new AMapLocationClient(context);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setGpsFirst(true);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setInterval(3000);
        option.setNeedAddress(true);
        client.setLocationOption(option);
        client.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (onLocationListener != null) {
                    onLocationListener.onLocation(aMapLocation);
                }
                if (null != aMapLocation && aMapLocation.getErrorCode() == 0) {
                    Location sessionLoc = LocationSession.getInstance()
                            .getLocation(context);
                    if (sessionLoc == null) {
                        sessionLoc = new Location();
                    }
                    getMyCity(sessionLoc,
                            aMapLocation.getProvince(),
                            aMapLocation.getCity(),
                            aMapLocation.getDistrict(),
                            aMapLocation.getLatitude(),
                            aMapLocation.getLongitude());
                    sessionLoc.setDistrict(aMapLocation.getDistrict());
                    sessionLoc.setLatitude(aMapLocation.getLatitude());
                    sessionLoc.setLongitude(aMapLocation.getLongitude());
                    sessionLoc.setAddress(aMapLocation.getAddress());
                    sessionLoc.setCity(aMapLocation.getCity());
                    sessionLoc.setProvince(aMapLocation.getProvince());
                    LocationSession.getInstance()
                            .saveLocation(context, sessionLoc);
                    CustomCommonApi.createPhone(context, sessionLoc)
                            .subscribe(new EmptySubscriber<Long>());
                    client.stopLocation();
                }
            }
        });
        client.startLocation();
    }

    public void getMyCity(
            Location res,
            String province,
            String city,
            String district,
            double latitude,
            double longitude) {
        if (JSONUtil.isEmpty(city) && JSONUtil.isEmpty(province)) {
            return;
        }
        City locationCity = Session.getInstance()
                .getLocationCity(context);
        //1.当定位城市与上一次保存的定位城市相同时，不重新获取城市。
        //2.当定位城市与选择的城市相同时，不重新获取城市


        if (res != null && (JSONUtil.isEmpty(city) || city.equals(res.getCity())) && (JSONUtil
                .isEmpty(
                province) || province.equals(res.getProvince())) && (JSONUtil.isEmpty(district)
                || district.equals(
                res.getDistrict()))) {
            if (locationCity != null) {
                return;
            }
        } else {
            try {
                Session.getInstance()
                        .setLocationCity(context, new City());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new GetMyCityTask().execute(Constants.getAbsUrl(String.format(Constants.HttpPath
                        .GET_MY_CITY_URL,
                !JSONUtil.isEmpty(city) ? city : "",
                !JSONUtil.isEmpty(district) ? district : "",
                !JSONUtil.isEmpty(province) ? province : "",
                latitude,
                longitude)));
    }

    private class GetMyCityTask extends AsyncTask<String, Object, City> {
        @Override
        protected City doInBackground(String... params) {
            try {
                String json = JSONUtil.getStringFromUrl(params[0]);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                JSONObject object = new JSONObject(json).optJSONObject("data");
                if (object != null && object.optLong("cid") > 0) {
                    return new City(object.optLong("cid"),
                            JSONUtil.getString(object, "short_name"));
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final City result) {
            if (result != null && result.getId() > 0) {
                //保存定位的城市
                try {
                    Session.getInstance()
                            .setLocationCity(context, result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (onGetCityResultListener != null) {
                onGetCityResultListener.onResult(result);
            }
            super.onPostExecute(result);
        }

    }
}
