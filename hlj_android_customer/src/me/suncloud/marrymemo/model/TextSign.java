package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.io.Serializable;

import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by Suncloud on 2015/7/8.
 */
public class TextSign implements Serializable {

    private String name;
    private int color;
    private int fillMode;

    public TextSign(JSONObject jsonObject){
        if(jsonObject!=null){
            name = JSONUtil.getString(jsonObject,"name");
            color = Util.parseColor(JSONUtil.getString(jsonObject,"color"));
            fillMode = jsonObject.optInt("fill_mode");
        }
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getFillMode() {
        return fillMode;
    }
}
