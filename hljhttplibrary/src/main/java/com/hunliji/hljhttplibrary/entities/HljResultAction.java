package com.hunliji.hljhttplibrary.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 一些网络请求的结果model
 * Created by jinxin on 2016/11/7.
 */

public class HljResultAction implements Parcelable {

    long id;
    String action;

    @Override
    public int describeContents() { return 0; }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.action);
    }

    public HljResultAction() {}

    protected HljResultAction(Parcel in) {
        this.id = in.readLong();
        this.action = in.readString();
    }

    public static final Parcelable.Creator<HljResultAction> CREATOR = new
            Parcelable.Creator<HljResultAction>() {
        @Override
        public HljResultAction createFromParcel(Parcel source) {
            return new HljResultAction(source);
        }

        @Override
        public HljResultAction[] newArray(int size) {return new HljResultAction[size];}
    };
}
