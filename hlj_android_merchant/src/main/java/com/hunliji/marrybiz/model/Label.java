package com.hunliji.marrybiz.model;


import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

public class Label implements Identifiable {

    private static final long serialVersionUID = -4081307101409363350L;
    private String name;
    private String keyWord;
    private String order;
    private long id;
    private long pid;

    public Label(JSONObject json) {
        if (json != null) {
            name = JSONUtil.getString(json, "name");
            if (!json.isNull("id")) {
                id = json.optLong("id", 0);
            }else if (!json.isNull("key")) {
                id = json.optLong("key", 0);
            }
            pid = json.optLong("pid", 0);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public long getPid() {
        return pid;
    }
}
