package com.hunliji.hljcarlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2018/1/3.是否历史最低
 */

public class SecKillParams implements Parcelable {

    @SerializedName("is_lowest")
    String isLowest;//0 或者null就是true

    public boolean isLowest() {
        return !TextUtils.isEmpty(isLowest) && !isLowest.equalsIgnoreCase("0");
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeString(this.isLowest);}

    public SecKillParams() {}

    protected SecKillParams(Parcel in) {this.isLowest = in.readString();}

    public static final Creator<SecKillParams> CREATOR = new Creator<SecKillParams>() {
        @Override
        public SecKillParams createFromParcel(Parcel source) {return new SecKillParams(source);}

        @Override
        public SecKillParams[] newArray(int size) {return new SecKillParams[size];}
    };
}
