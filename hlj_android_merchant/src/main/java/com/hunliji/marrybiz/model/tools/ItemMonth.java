package com.hunliji.marrybiz.model.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2016/9/16.
 */
public class ItemMonth implements Parcelable {
    @SerializedName(value = "date")
    String date;
    @SerializedName(value = "full_status")
    boolean fullStatus;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeByte(this.fullStatus ? (byte) 1 : (byte) 0);
    }

    public ItemMonth() {}

    protected ItemMonth(Parcel in) {
        this.date = in.readString();
        this.fullStatus = in.readByte() != 0;
    }

    public static final Creator<ItemMonth> CREATOR = new Creator<ItemMonth>() {
        @Override
        public ItemMonth createFromParcel(Parcel source) {return new ItemMonth(source);}

        @Override
        public ItemMonth[] newArray(int size) {return new ItemMonth[size];}
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFullStatus() {
        return fullStatus;
    }

    public void setFullStatus(boolean fullStatus) {
        this.fullStatus = fullStatus;
    }
}