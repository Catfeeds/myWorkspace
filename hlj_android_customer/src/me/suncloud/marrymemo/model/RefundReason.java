package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 16/4/7.
 */
public class RefundReason implements Identifiable {
    private long id;
    private String name;
    private String desc;
    private boolean isShow;

    public RefundReason(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            name = JSONUtil.getString(jsonObject, "name");
            desc = JSONUtil.getString(jsonObject, "desc");
            isShow = jsonObject.optInt("show", 0) > 0;
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isShow() {
        return isShow;
    }

    public String getDesc() {
        return desc;
    }
}
