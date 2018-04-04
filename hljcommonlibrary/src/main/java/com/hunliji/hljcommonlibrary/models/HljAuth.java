package com.hunliji.hljcommonlibrary.models;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.HljCommon;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by werther on 16/7/23.
 * 婚礼纪客户端使用的授权管理信息model
 */
public class HljAuth {
    private String appVersion;
    private String appName;
    private String phoneNumber;
    private String httpAccessToken;
    private String token;
    private String secret;
    private String cityStr;

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setHttpAccessToken(String httpAccessToken) {
        this.httpAccessToken = httpAccessToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setCityStr(String cityStr) {
        this.cityStr = cityStr;
    }

    /**
     * 获取授权信息key value的map数据,用于设置http header的授权信息
     *
     * @return
     */
    public Map<String, String> getAuthMap() {
        Map<String, String> map = new HashMap<>();

        map.put("devicekind", "android");
        if (!TextUtils.isEmpty(appVersion)) {
            map.put("appver", appVersion);
        }
        if (!TextUtils.isEmpty(appName)) {
            map.put("appName", appName);
        }
        if (HljCommon.debug) {
            map.put("test", "1");
        }
        if (!TextUtils.isEmpty(phoneNumber)) {
            map.put("phone", phoneNumber);
        }
        if (!TextUtils.isEmpty(httpAccessToken)) {
            map.put("Http-Access-Token", httpAccessToken);
        }
        if (!TextUtils.isEmpty(token)) {
            map.put("token", token);
        }
        if (!TextUtils.isEmpty(secret)) {
            map.put("secret", secret);
        }
        if (!TextUtils.isEmpty(cityStr)) {
            map.put("city", cityStr);
        }

        return map;
    }

}
