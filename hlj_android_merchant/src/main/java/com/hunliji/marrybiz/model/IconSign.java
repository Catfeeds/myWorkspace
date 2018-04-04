package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Suncloud on 2015/9/8.
 */
public class IconSign implements Serializable {

    private String iconUrl;
    private int width;
    private int height;

    public IconSign(JSONObject jsonObject){
        if(jsonObject!=null){
            iconUrl= JSONUtil.getString(jsonObject, "url");
            width=jsonObject.optInt("width");
            height=jsonObject.optInt("height");
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
