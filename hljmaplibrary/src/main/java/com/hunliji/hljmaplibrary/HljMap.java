package com.hunliji.hljmaplibrary;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * Created by wangtao on 2017/6/14.
 */

public class HljMap {

    private static final String AMAP_URL = "http://restapi.amap" + "" + "" + "" + "" + "" + "" +
            ".com/v3/staticmap?size=%s*%s&zoom=%s&scale=1";
    private static final String AMAP_URL2 = "http://restapi.amap" + "" + "" + "" + "" + "" + "" +
            ".com/v3/staticmap?size=%s*%s&scale=1";

    private static final String AMAP_MARKER = "&markers=-1,%s,0:%s,%s";
    public static final String AMAP_KEY = "&key=662219c1cb7bc688dd71a9c578f9236a";

    /**
     * 获取高德地图静态图URL
     *
     * @param longitude      经度
     * @param latitude       纬度
     * @param width          宽
     * @param height         高
     * @param zoom           放大倍数
     * @param markerIconPath marker链接地址
     * @return
     */
    public static String getAMapUrl(
            double longitude,
            double latitude,
            int width,
            int height,
            int zoom,
            String markerIconPath) {
        if (width > 1024) {
            float scale = (float) 1024 / width;
            width = 1024;
            height = (int) (height * scale);
        }

        StringBuilder mapUrl = new StringBuilder(String.format(AMAP_URL, width, height, zoom));
        if (longitude > 0 && latitude > 0) {
            mapUrl.append("&")
                    .append(String.format("location=%s,%s", longitude, latitude));
            if (!TextUtils.isEmpty(markerIconPath)) {
                mapUrl.append(String.format(AMAP_MARKER, markerIconPath, longitude, latitude));
            }
        }

        mapUrl.append(AMAP_KEY);

        Log.d("HLJMAP", mapUrl.toString());
        return mapUrl.toString();
    }

    public static String getAmapUrlWithMultiMarkers(
            List<LatLng> latLngs, int width, int height) {
        if (width > 1024) {
            float scale = (float) 1024 / width;
            width = 1024;
            height = (int) (height * scale);
        }

        StringBuilder mapUrl = new StringBuilder(String.format(AMAP_URL2, width, height));

        mapUrl.append(String.format("&markers=mid,0xF83244,:%s,%s",
                latLngs.get(0).longitude,
                latLngs.get(0).latitude));
        if (latLngs.size() > 1) {
            for (int i = 1; i < latLngs.size(); i++) {
                mapUrl.append(String.format(";%s,%s",
                        latLngs.get(i).longitude,
                        latLngs.get(i).latitude));
            }
        }


        mapUrl.append(AMAP_KEY);

        Log.d("HLJMAP", mapUrl.toString());
        return mapUrl.toString();
    }

    /**
     * 将百度坐标系转换为高德坐标系
     *
     * @param context
     * @param latitude  纬度
     * @param longitude 经度
     * @return
     */
    public static LatLng convertBDPointToAMap(Context context, double latitude, double longitude) {
        CoordinateConverter converter = new CoordinateConverter(context);
        converter.from(CoordinateConverter.CoordType.BAIDU);
        LatLng latLng = new LatLng(latitude, longitude);
        LatLng desLatLng = new LatLng(latitude, longitude);
        try {
            converter.coord(latLng);
            desLatLng = converter.convert();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return desLatLng;
    }
}
