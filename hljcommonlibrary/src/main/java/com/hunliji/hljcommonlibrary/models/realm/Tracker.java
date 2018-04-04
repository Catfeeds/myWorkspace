package com.hunliji.hljcommonlibrary.models.realm;

import io.realm.RealmObject;

/**
 * Created by Suncloud on 2016/9/9.
 */
public class Tracker extends RealmObject {

    private String trackerString;
    private int version; //1 老版本点击统计，2 新版展示统计

    public Tracker(String trackerString,int version){
        this.trackerString=trackerString;
        this.version=version;
    }

    public Tracker(){}

    public String getTrackerString() {
        return trackerString;
    }

    public void setTrackerString(String trackerString) {
        this.trackerString = trackerString;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
