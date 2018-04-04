package com.hunliji.marrybiz.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/12/19.派单员
 */

public class Admin implements Parcelable {

    @SerializedName("admin_id")
    long adminId;
    String nickname;

    public long getAdminId() {
        return adminId;
    }

    public String getNickname() {
        return nickname;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.adminId);
        dest.writeString(this.nickname);
    }

    public Admin() {}

    protected Admin(Parcel in) {
        this.adminId = in.readLong();
        this.nickname = in.readString();
    }

    public static final Parcelable.Creator<Admin> CREATOR = new Parcelable.Creator<Admin>() {
        @Override
        public Admin createFromParcel(Parcel source) {return new Admin(source);}

        @Override
        public Admin[] newArray(int size) {return new Admin[size];}
    };
}
