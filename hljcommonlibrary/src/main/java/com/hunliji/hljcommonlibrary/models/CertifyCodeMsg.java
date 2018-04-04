package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 登录验证码
 * Created by jinxin on 2016/8/30.
 */
public class CertifyCodeMsg implements Parcelable {
    @SerializedName(value = "msg")
    String msg;
    @SerializedName(value = "status")
    int status;


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(
            Parcel dest,
            int flags) {
        dest.writeString(this.msg);
        dest.writeInt(this.status);
    }

    public CertifyCodeMsg() {}

    protected CertifyCodeMsg(Parcel in) {
        this.msg = in.readString();
        this.status = in.readInt();
    }

    public static final Creator<CertifyCodeMsg> CREATOR = new
            Creator<CertifyCodeMsg>() {
        @Override
        public CertifyCodeMsg createFromParcel(Parcel source) {
            return new CertifyCodeMsg(source);
        }

        @Override
        public CertifyCodeMsg[] newArray(int size) {return new CertifyCodeMsg[size];}
    };

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
