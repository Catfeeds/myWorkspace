package me.suncloud.marrymemo.model;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Suncloud on 2016/3/17.
 */
public class AdvItem {

    @SerializedName("property")
    private long property;
    @SerializedName("type")
    private int type;

    public int getType() {
        return type;
    }

    public long getProperty() {
        return property;
    }
}
