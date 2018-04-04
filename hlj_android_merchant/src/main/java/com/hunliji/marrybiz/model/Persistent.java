package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.util.JSONUtil;

import java.io.Serializable;


import org.json.JSONObject;

public class Persistent implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 5035388241234144995L;
    /**
     *
     */
    private String iphone;
    private String ipad;
    private String flash;
    private String streamingPhone;
    private String streamingPad;
    private String screenShot;

    public Persistent(JSONObject jsonObject) {
        if (jsonObject != null) {
            iphone = JSONUtil.getString(jsonObject, "iphone");
            ipad = JSONUtil.getString(jsonObject, "ipad");
            flash = JSONUtil.getString(jsonObject, "flash");
            streamingPhone = JSONUtil.getString(jsonObject, "m3u8_640_480");
            streamingPad = JSONUtil.getString(jsonObject, "m3u8_1024_768");
            screenShot = JSONUtil.getString(jsonObject, "vframe");
        }
    }

    public String getScreenShot() {
        return JSONUtil.isEmpty(screenShot) ? null : Constants.QINIU_HOST + screenShot;
    }

    public String getStreamingPad() {
        return JSONUtil.isEmpty(streamingPad) || !streamingPhone.endsWith(".m3u8") ? null :
                Constants.QINIU_HOST + streamingPad;
    }

    public String getStreamingPhone() {
        return JSONUtil.isEmpty(streamingPhone) || !streamingPhone.endsWith(".m3u8") ? null :
                Constants.QINIU_HOST + streamingPhone;
    }

    public String getIphone() {
        return JSONUtil.isEmpty(iphone) ? null : Constants.QINIU_HOST + iphone;
    }

    public String getIpad() {
        return JSONUtil.isEmpty(ipad) ? null : Constants.QINIU_HOST + ipad;
    }

    public String getFlash() {
        return JSONUtil.isEmpty(flash) ? null : Constants.QINIU_HOST + flash;
    }
}