package com.hunliji.marrybiz.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.User;

/**
 * Created by mo_yu on 2017/12/19.体验店客户
 */

public class Lead implements Parcelable {

    long id;
    String name;//用户姓名
    String phone;//用户电话
    @SerializedName("nick_name")
    String nickName;
    @SerializedName("user_info")
    PartnerUserInfo partnerUserInfo;
    @SerializedName(value = "user_id")
    long userId;
    User user;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public PartnerUserInfo getPartnerUserInfo() {
        return partnerUserInfo;
    }

    public long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public String getNickName() {
        return nickName;
    }

    public Lead() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.nickName);
        dest.writeParcelable(this.partnerUserInfo, flags);
        dest.writeLong(this.userId);
        dest.writeParcelable(this.user, flags);
    }

    protected Lead(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.phone = in.readString();
        this.nickName = in.readString();
        this.partnerUserInfo = in.readParcelable(PartnerUserInfo.class.getClassLoader());
        this.userId = in.readLong();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Lead> CREATOR = new Creator<Lead>() {
        @Override
        public Lead createFromParcel(Parcel source) {return new Lead(source);}

        @Override
        public Lead[] newArray(int size) {return new Lead[size];}
    };
}
