package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 16/4/20.
 */
public class ExpressMethod extends Label {
    public ExpressMethod(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.name = JSONUtil.getString(jsonObject, "type_name");
            this.keyWord = JSONUtil.getString(jsonObject, "type_code");
        }
    }
}
