package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by hua_rong on 2017/4/19.
 * 订单信息
 */

public class CommonOrderInfo implements Parcelable {

    @SerializedName(value = "wedding_time")
    DateTime weddingTime;
    long id;

    public DateTime getWeddingTime() {
        return weddingTime;
    }

    public void setWeddingTime(DateTime weddingTime) {
        this.weddingTime = weddingTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.weddingTime);
        dest.writeLong(this.id);
    }

    public CommonOrderInfo() {
    }

    protected CommonOrderInfo(Parcel in) {
        this.weddingTime = (DateTime) in.readSerializable();
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<CommonOrderInfo> CREATOR = new Parcelable.Creator<CommonOrderInfo>() {
        @Override
        public CommonOrderInfo createFromParcel(Parcel source) {
            return new CommonOrderInfo(source);
        }

        @Override
        public CommonOrderInfo[] newArray(int size) {
            return new CommonOrderInfo[size];
        }
    };
}
