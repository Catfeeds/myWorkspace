package com.hunliji.hljhttplibrary.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/7/18.
 * 婚礼纪Http返回结果中的status结构
 * 自顶一个状态码和响应信息
 * RetCode=0时表示正常状态
 * 非正常状态时,msg中含有错误信息
 */
public class HljHttpStatus implements Parcelable {
    @SerializedName(value = "RetCode",alternate = "code")
    int retCode;
    String msg;
    @SerializedName(value = "current_time")
    long currentTimeLong;

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getCurrentTimeLong() {
        return currentTimeLong;
    }

    public void setCurrentTimeLong(long currentTimeLong) {
        this.currentTimeLong = currentTimeLong;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.retCode);
        dest.writeString(this.msg);
        dest.writeLong(this.currentTimeLong);
    }

    public HljHttpStatus() {}

    protected HljHttpStatus(Parcel in) {
        this.retCode = in.readInt();
        this.msg = in.readString();
        this.currentTimeLong = in.readLong();
    }

    public static final Parcelable.Creator<HljHttpStatus> CREATOR = new Parcelable
            .Creator<HljHttpStatus>() {
        @Override
        public HljHttpStatus createFromParcel(Parcel source) {return new HljHttpStatus(source);}

        @Override
        public HljHttpStatus[] newArray(int size) {return new HljHttpStatus[size];}
    };
}
