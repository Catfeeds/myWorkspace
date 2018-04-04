package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.io.Serializable;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.util.JSONUtil;

public class Persistent implements Serializable {

    private String iphone;
    private String streamingPhone;
    private String screenShot;
    private String domain;

    public Persistent(JSONObject jsonObject) {
        if (jsonObject != null) {
            domain = JSONUtil.getString(jsonObject, "domain");
            iphone = JSONUtil.getString(jsonObject, "iphone");
            streamingPhone = JSONUtil.getString(jsonObject, "m3u8_640_480");
            screenShot = JSONUtil.getString(jsonObject, "vframe");
        }
    }

    public String getDomain() {
        return JSONUtil.isEmpty(domain) ? Constants.QINIU_HOST : domain;
    }

    public String getScreenShot() {
        return JSONUtil.isEmpty(screenShot) ? null : getDomain() + screenShot;
    }

    public String getStreamingPhone() {
        return JSONUtil.isEmpty(streamingPhone) ? null : getDomain() + streamingPhone;
    }

    public String getIphone() {
        return JSONUtil.isEmpty(iphone) ? null : getDomain() + iphone;
    }
}