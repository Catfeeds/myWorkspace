package com.hunliji.hljcommonlibrary.models.userprofile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/9/2.
 * 用户和伴侣的简略信息包装类
 * 只包含四个字段
 * avatar
 * id
 * nick
 * weddingday
 */
public class UserPartnerWrapper2 implements Parcelable {
    @SerializedName(value = "my")
    UserProfile user;
    @SerializedName(value = "partner")
    UserProfile partner;

    public UserProfile getUser() {
        return user;
    }

    public UserProfile getPartner() {
        return partner;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.partner, flags);
    }

    public UserPartnerWrapper2() {}

    protected UserPartnerWrapper2(Parcel in) {
        this.user = in.readParcelable(UserProfile.class.getClassLoader());
        this.partner = in.readParcelable(UserProfile.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserPartnerWrapper2> CREATOR = new Parcelable
            .Creator<UserPartnerWrapper2>() {
        @Override
        public UserPartnerWrapper2 createFromParcel(Parcel source) {
            return new UserPartnerWrapper2(source);
        }

        @Override
        public UserPartnerWrapper2[] newArray(int size) {return new UserPartnerWrapper2[size];}
    };
}
