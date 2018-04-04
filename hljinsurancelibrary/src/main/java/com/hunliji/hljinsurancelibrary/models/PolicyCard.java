package com.hunliji.hljinsurancelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2017/11/27.蜜月保所用到的简单请帖信息
 */

public class PolicyCard implements Parcelable {
    private long id;
    @SerializedName("bride_name")
    private String brideName;
    @SerializedName("groom_name")
    private String groomName;
    private DateTime time;

    public long getId() {
        return id;
    }

    public String getBrideName() {
        return brideName;
    }

    public String getGroomName() {
        return groomName;
    }

    public DateTime getTime() {
        return time;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.brideName);
        dest.writeString(this.groomName);
        dest.writeSerializable(this.time);
    }

    public PolicyCard() {}

    protected PolicyCard(Parcel in) {
        this.id = in.readLong();
        this.brideName = in.readString();
        this.groomName = in.readString();
        this.time = (DateTime) in.readSerializable();
    }

    public static final Parcelable.Creator<PolicyCard> CREATOR = new Parcelable
            .Creator<PolicyCard>() {
        @Override
        public PolicyCard createFromParcel(Parcel source) {return new PolicyCard(source);}

        @Override
        public PolicyCard[] newArray(int size) {return new PolicyCard[size];}
    };
}
