package com.hunliji.hljcommonlibrary.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by werther on 16/7/23.
 * 用于Http请求header中添加的城市信息特殊model
 * 商家端与用户端的格式会有所不同
 */
public class HttpHeaderCity {
    @SerializedName(value = "gps_longitude")
    private double longitude;
    @SerializedName(value = "gps_latitude")
    private double latitude;
    @SerializedName(value = "gps_city")
    private String cityName;
    @SerializedName(value = "gps_province")
    private String province;
    @SerializedName(value = "expo_cid")
    private long expoCid;
    @SerializedName(value = "community_cid")
    private long communityCid;

    /**
     * 商家端Http请求头的城市信息初始化方法
     * 三个定位参数都是属于商家用户信息,一般来说不会轻易更改,除非商家修改商家的服务区域
     *
     * @param longitude
     * @param latitude
     * @param cityName
     */
    public HttpHeaderCity(double longitude, double latitude, String cityName) {
        this.longitude = longitude;
        this.latitude = latitude;
        try {
            if(!TextUtils.isEmpty(cityName)) {
                this.cityName = URLEncoder.encode(cityName, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 用户端Http请求头的城市信息初始化方法
     * 分为定位信息和所选城市信息,定位信息随硬件定位而得,所选城市则是用户的选择
     * 用户端改变所选城市后,http请求头的信息要随之改变
     *
     * @param longitude 定位信息
     * @param latitude  定位信息
     * @param cityName  定位信息
     * @param province  定位信息
     * @param cid       所选城市
     */
    public HttpHeaderCity(
            double longitude, double latitude, String cityName, String province, long cid) {
        this.longitude = longitude;
        this.latitude = latitude;
        try {
            if(!TextUtils.isEmpty(cityName)) {
                this.cityName = URLEncoder.encode(cityName, "UTF-8");
            }
            if(!TextUtils.isEmpty(province)) {
                this.province = URLEncoder.encode(province, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.communityCid = this.expoCid = cid;
    }
}
