package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.io.Serializable;


public class Splash implements Serializable {

    int version;
    String splash;
    //启动页新增参数
    private String coverImage;
    private Poster poster;

    public Splash(JSONObject json) {
        if(json!=null) {
            version = json.optInt("splash_version", 0);
            splash = JSONUtil.getString(json, "splash");
            coverImage = JSONUtil.getString(json, "cover_image");
            poster = new Poster(json.optJSONObject("poster"));
        }
    }


    public String getSplash() {
        return splash;
    }

    public int getVersion() {
        return version ;
    }

    public Poster getPoster() {
        return poster;
    }

    public String getCoverImage() {
        return coverImage;
    }
}
