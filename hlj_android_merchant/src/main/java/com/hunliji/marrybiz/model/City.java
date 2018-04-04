package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;


public class City implements Identifiable {

    /**
     *
     */

    private static final long serialVersionUID = -6729701457326788459L;

    private long cid;
    private String name;
    private String pinying;
    private String short_py;

    public City(JSONObject json) {
        if(json!=null) {
            this.cid = json.optLong("cid",0);
            this.name = JSONUtil.getString(json, "name");
            this.pinying = JSONUtil.getString(json, "pinying");
            this.short_py = JSONUtil.getString(json, "short_py");
        }
    }

    public Long getId() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPingying() {
        return pinying;
    }

    public void setPinYing(String pinying) {
        this.pinying = pinying;
    }

    public String getShortPy() {
        return short_py;
    }

}