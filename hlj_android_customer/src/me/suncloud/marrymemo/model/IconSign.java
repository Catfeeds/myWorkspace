package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.io.Serializable;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/7/8.
 */
public class IconSign implements Serializable {

    private String iconUrl;
    private int width;
    private int height;

    public IconSign(JSONObject jsonObject){
        if(jsonObject!=null){
            iconUrl= JSONUtil.getString(jsonObject,"url");
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
