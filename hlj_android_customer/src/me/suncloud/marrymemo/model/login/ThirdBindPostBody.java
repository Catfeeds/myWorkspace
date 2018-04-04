package me.suncloud.marrymemo.model.login;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 三方绑定用到的postBody
 * Created by jixnin on 2017/1/12 0012.
 */

public class ThirdBindPostBody implements Parcelable {
    String openid;
    String type;
    String user_info;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_info() {
        return user_info;
    }

    public void setUser_info(String user_info) {
        this.user_info = user_info;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.openid);
        dest.writeString(this.type);
        dest.writeString(this.user_info);
    }

    public ThirdBindPostBody() {}

    protected ThirdBindPostBody(Parcel in) {
        this.openid = in.readString();
        this.type = in.readString();
        this.user_info = in.readString();
    }

    public static final Parcelable.Creator<ThirdBindPostBody> CREATOR = new Parcelable
            .Creator<ThirdBindPostBody>() {
        @Override
        public ThirdBindPostBody createFromParcel(Parcel source) {
            return new ThirdBindPostBody(source);
        }

        @Override
        public ThirdBindPostBody[] newArray(int size) {return new ThirdBindPostBody[size];}
    };
}
