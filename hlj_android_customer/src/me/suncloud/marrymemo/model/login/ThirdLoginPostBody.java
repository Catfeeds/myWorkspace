package me.suncloud.marrymemo.model.login;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 三方注册postBody
 * Created by jinxin on 2017/1/10 0010.
 */

public class ThirdLoginPostBody implements Parcelable {
    String phone;
    String sms_code;
    String type;//qq sina weixin
    String openid;//QQ WeChat 用openid Sina 用id
    String user_info;//三方登录返回的user_info

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSms_code() {
        return sms_code;
    }

    public void setSms_code(String sms_code) {
        this.sms_code = sms_code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUser_info() {
        return user_info;
    }

    public void setUser_info(String user_info) {
        this.user_info = user_info;
    }

    public ThirdLoginPostBody() {}

    public ThirdLoginPostBody(String sms_code) {
        this.sms_code = sms_code;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.sms_code);
        dest.writeString(this.type);
        dest.writeString(this.openid);
        dest.writeString(this.user_info);
    }

    protected ThirdLoginPostBody(Parcel in) {
        this.phone = in.readString();
        this.sms_code = in.readString();
        this.type = in.readString();
        this.openid = in.readString();
        this.user_info = in.readString();
    }

    public static final Creator<ThirdLoginPostBody> CREATOR = new Creator<ThirdLoginPostBody>() {
        @Override
        public ThirdLoginPostBody createFromParcel(Parcel source) {
            return new ThirdLoginPostBody(source);
        }

        @Override
        public ThirdLoginPostBody[] newArray(int size) {return new ThirdLoginPostBody[size];}
    };
}
