package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

public class ColorMenuItem extends MenuItem {

    public ColorMenuItem(JSONObject json) {
        super(json);
        if (json != null) {
            if (!json.isNull("rgb")) {
                keyWord = JSONUtil.getString(json, "rgb");
            }
        }
    }
}
