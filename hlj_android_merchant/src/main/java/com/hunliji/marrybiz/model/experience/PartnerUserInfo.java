package com.hunliji.marrybiz.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/12/19.另一半用户信息 伴侣
 */

public class PartnerUserInfo implements Parcelable {

    String name;//另一半称呼
    @SerializedName(value = "phonev2")
    String phone;//另一半电话
    @SerializedName(value = "winxin")
    String weixin;//微信
    String qq;//qq

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getWeixin() {
        return weixin;
    }

    public String getQq() {
        return qq;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.weixin);
        dest.writeString(this.qq);
    }

    public PartnerUserInfo() {}

    protected PartnerUserInfo(Parcel in) {
        this.name = in.readString();
        this.phone = in.readString();
        this.weixin = in.readString();
        this.qq = in.readString();
    }

    public static final Parcelable.Creator<PartnerUserInfo> CREATOR = new Parcelable
            .Creator<PartnerUserInfo>() {
        @Override
        public PartnerUserInfo createFromParcel(Parcel source) {return new PartnerUserInfo(source);}

        @Override
        public PartnerUserInfo[] newArray(int size) {return new PartnerUserInfo[size];}
    };
}
