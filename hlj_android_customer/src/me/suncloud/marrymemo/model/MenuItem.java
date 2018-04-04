package me.suncloud.marrymemo.model;

import android.view.Menu;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

public class MenuItem extends Label {

    public MenuItem(JSONObject json) {
        if (json != null) {
            if (!json.isNull("name")) {
                name = JSONUtil.getString(json, "name");
            }else if(!json.isNull("title")){
                name = JSONUtil.getString(json, "title");
            }else if(!json.isNull("area_name")){
                name = JSONUtil.getString(json, "area_name");
            }
            if (!json.isNull("id")) {
                id = json.optLong("id", 0);
            } else if (!json.isNull("pid")) {
                id = json.optLong("pid", 0);
            } else if (!json.isNull("key")) {
                id = json.optLong("key", 0);
            }
            keyWord = JSONUtil.getString(json, "value");
        }
    }

    public MenuItem(){

    }
}
