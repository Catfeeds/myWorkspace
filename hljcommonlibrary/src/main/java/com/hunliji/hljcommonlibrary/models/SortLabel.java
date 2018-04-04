package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mo_yu on 2017/8/1.排序使用的简单label
 */

public class SortLabel extends Label {
    String order;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.order);
    }

    public SortLabel() {}

    protected SortLabel(Parcel in) {
        this.order = in.readString();
    }

    public static final Parcelable.Creator<SortLabel> CREATOR = new Parcelable.Creator<SortLabel>
            () {
        @Override
        public SortLabel createFromParcel(Parcel source) {return new SortLabel(source);}

        @Override
        public SortLabel[] newArray(int size) {return new SortLabel[size];}
    };
}
