package com.hunliji.hljcommonlibrary.view_tracker.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by wangtao on 2018/2/22.
 */

public class TrackerValue {

    @SerializedName("default")
    private String defaultValue;
    @SerializedName("object_path")
    private TracerObjectPath objectPath;
    @SerializedName("map")
    private Map<String,String> map;

    public Object getValue(Object classObject){
        Object value=null;
        if(objectPath!=null){
            value=objectPath.getObject(classObject);
        }
        if(value==null){
            value=defaultValue;
        }
        if(map!=null&&value!=null){
            value=map.get(value.toString());
        }
        return value;
    }
}
