package com.hunliji.hljcommonlibrary.models.realm;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by wangtao on 2018/1/5.
 */

public class WSCity extends RealmObject {

    @SerializedName("cid")
    private long id;
    private String name;

    public WSCity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public WSCity() {

    }

    public long getId() {
        return id;
    }

    public String getName() {
        if(TextUtils.isEmpty(name)){
            return "";
        }
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
