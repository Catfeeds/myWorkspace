package me.suncloud.marrymemo.model.login;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 微信登录post body
 * Created by jinxin on 2016/12/13 0013.
 */

public class WeChatLoginBody implements Parcelable {
    private String unionid;
    private WechatUserInfo user_info;

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public WechatUserInfo getUser_info() {
        return user_info;
    }

    public void setUser_info(WechatUserInfo user_info) {
        this.user_info = user_info;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.unionid);
        dest.writeParcelable(this.user_info, flags);
    }

    public WeChatLoginBody() {}

    protected WeChatLoginBody(Parcel in) {
        this.unionid = in.readString();
        this.user_info = in.readParcelable(WechatUserInfo.class.getClassLoader());
    }

    public static final Creator<WeChatLoginBody> CREATOR = new Creator<WeChatLoginBody>() {
        @Override
        public WeChatLoginBody createFromParcel(Parcel source) {return new WeChatLoginBody(source);}

        @Override
        public WeChatLoginBody[] newArray(int size) {return new WeChatLoginBody[size];}
    };
}
