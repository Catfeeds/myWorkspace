package me.suncloud.marrymemo.model.login;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 微信登录userinfo
 * Created by jinxin on 2016/12/13 0013.
 */

public class WechatUserInfo implements Parcelable {
    String avatar;
    String nick;
    String birthday;
    String user_id;
    String last_phone_token;
    int  bind_type;
    String info;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLast_phone_token() {
        return last_phone_token;
    }

    public void setLast_phone_token(String last_phone_token) {
        this.last_phone_token = last_phone_token;
    }

    public int getBind_type() {
        return bind_type;
    }

    public void setBind_type(int bind_type) {
        this.bind_type = bind_type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public WechatUserInfo() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.nick);
        dest.writeString(this.birthday);
        dest.writeString(this.user_id);
        dest.writeString(this.last_phone_token);
        dest.writeInt(this.bind_type);
        dest.writeString(this.info);
    }

    protected WechatUserInfo(Parcel in) {
        this.avatar = in.readString();
        this.nick = in.readString();
        this.birthday = in.readString();
        this.user_id = in.readString();
        this.last_phone_token = in.readString();
        this.bind_type = in.readInt();
        this.info = in.readString();
    }

    public static final Creator<WechatUserInfo> CREATOR = new Creator<WechatUserInfo>() {
        @Override
        public WechatUserInfo createFromParcel(Parcel source) {return new WechatUserInfo(source);}

        @Override
        public WechatUserInfo[] newArray(int size) {return new WechatUserInfo[size];}
    };
}
