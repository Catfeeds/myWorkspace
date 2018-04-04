package com.hunliji.hljcommonlibrary.view_tracker.models;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2018/2/11.
 */

public class TrackerData {

    @SerializedName("key")
    private String key;
    @SerializedName("value")
    private TrackerValue value;

    public String getKey() {
        return key;
    }

    public Object getValue(Object classObject) {
        if(value==null){
            return null;
        }
        return value.getValue(classObject);
    }
}
