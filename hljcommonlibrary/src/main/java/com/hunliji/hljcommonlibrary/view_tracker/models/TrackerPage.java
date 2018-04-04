package com.hunliji.hljcommonlibrary.view_tracker.models;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;

import java.util.List;

/**
 * {
 * "class": "me.suncloud.marrymemo.view.WorkActivity",
 * "object_path":{
 * "intent":{
 * "asg_key": "isSnapshot",
 * "default_value":"false"
 * },
 * "field":"work"
 * },
 * "value_path":{
 * "field":"id",
 * "method":"getId",
 * "child_path":{
 * "field":"id",
 * "method":"getId",
 * "child_path":...
 * }
 * },
 * "name": {
 * "default":"套餐详情页",
 * "map":{
 * "false":"套餐详情页"
 * }
 * }
 * }
 * Created by wangtao on 2018/2/11.
 */

public class TrackerPage {

    @SerializedName("class")
    private String className;
    @SerializedName("name")
    private TrackerName name;
    @SerializedName("data")
    private List<TrackerData> data;

    public String getClassName() {
        return className;
    }

    public String getTrackerName(Object classObject) {
        if (name == null) {
            return null;
        }
        return name.getName(classObject);
    }

    public VTMetaData getPageData(Object classObject) {
        if (CommonUtil.isCollectionEmpty(data)) {
            return null;
        }
        VTMetaData metaData = new VTMetaData();
        for (TrackerData trackerData : data) {
            Object value = trackerData.getValue(classObject);
            if (value != null) {
                metaData.addExtraData(trackerData.getKey(), value);
            }
        }
        return metaData;
    }
}
