package com.hunliji.hljsharelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/7/14.
 */

public class ThirdLoginParameter implements Parcelable{

    @SerializedName("openid")
    private String thirdId;
    @SerializedName("user_info")
    private String loginInfo;
    private String type; //第三方登陆类型

    public String getThirdId() {
        return thirdId;
    }

    public String getLoginInfo() {
        return loginInfo;
    }

    public String getType() {
        return type;
    }

    public ThirdLoginParameter(String thirdId, String loginInfo, String type) {
        this.thirdId = thirdId;
        this.loginInfo = loginInfo;
        this.type = type;
    }

    protected ThirdLoginParameter(Parcel in) {
        thirdId = in.readString();
        loginInfo = in.readString();
        type = in.readString();
    }

    public static final Creator<ThirdLoginParameter> CREATOR = new Creator<ThirdLoginParameter>() {
        @Override
        public ThirdLoginParameter createFromParcel(Parcel in) {
            return new ThirdLoginParameter(in);
        }

        @Override
        public ThirdLoginParameter[] newArray(int size) {
            return new ThirdLoginParameter[size];
        }
    };

    public void setLoginInfo(String loginInfo) {
        this.loginInfo = loginInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thirdId);
        dest.writeString(loginInfo);
        dest.writeString(type);
    }
}
