package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/5/15.post请求后返回相对应的id参数
 */

public class BasePostResult implements Parcelable {

    private long id;
    private String action;
    @SerializedName(value = "gold")
    private int gold;

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

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.action);
        dest.writeInt(this.gold);
    }

    public BasePostResult() {}

    protected BasePostResult(Parcel in) {
        this.id = in.readLong();
        this.action = in.readString();
        this.gold = in.readInt();
    }

    public static final Parcelable.Creator<BasePostResult> CREATOR = new Parcelable
            .Creator<BasePostResult>() {
        @Override
        public BasePostResult createFromParcel(Parcel source) {return new BasePostResult(source);}

        @Override
        public BasePostResult[] newArray(int size) {return new BasePostResult[size];}
    };
}
