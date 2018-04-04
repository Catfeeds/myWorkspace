package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Suncloud on 2015/9/10.
 */
public class ShareInfo implements Serializable {

    private String icon;
    private String title;
    private String desc;
    private String url;
    private String desc2;

    public ShareInfo(JSONObject jsonObject) {
        if (jsonObject != null) {
            icon = JSONUtil.getString(jsonObject, "icon");
            title = JSONUtil.getString(jsonObject, "title");
            desc = JSONUtil.getString(jsonObject, "desc");
            url = JSONUtil.getString(jsonObject, "url");
            desc2 = JSONUtil.getString(jsonObject, "desc2");
        }
    }

    public String getDesc() {
        return desc;
    }

    public String getDesc2() {
        return JSONUtil.isEmpty(desc2) ? desc : desc2;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
