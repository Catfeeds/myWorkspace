package com.hunliji.marrybiz.model;

import org.json.JSONObject;

/**
 * author:Bezier
 * 2015/4/22 15:36
 */
public class WorkDescribe implements Identifiable {

    private static final long serialVersionUID = 6468645596736353380L;

    private long id;
    private String name;
    private String describe;

    @Override
    public Long getId() {
        return id;
    }

    public WorkDescribe(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id", 0);
            this.name = json.optString("name");
            this.describe = json.optString("describe");

        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
