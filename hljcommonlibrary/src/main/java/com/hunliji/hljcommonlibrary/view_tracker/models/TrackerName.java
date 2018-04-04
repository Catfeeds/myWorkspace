package com.hunliji.hljcommonlibrary.view_tracker.models;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
/**
 * Created by wangtao on 2018/2/12.
 */

public class TrackerName {

    @SerializedName("default")
    private String defaultName;
    @SerializedName("value")
    private TrackerValue value;

    public String getName(Object classObject) {
        String name=null;
        if(value!=null){
            Object object=value.getValue(classObject);
            if(object!=null&&object instanceof String){
                name= (String) object;
            }
        }
        if(TextUtils.isEmpty(name)){
            name=defaultName;
        }
        return name;
    }
}
