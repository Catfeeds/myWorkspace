package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/2/10.提现
 */

public class WithDraw implements Parcelable {
    @SerializedName(value = "err_code")
    private String errCode;
    @SerializedName(value = "result_code")
    private String resultCode;//FAIL失败，SUCCESS 提现成功
    @SerializedName(value = "err_code_des")
    private String errCodeDes;//	失败原因
    @SerializedName(value = "insurance")


    public String getErrCode() {
        return errCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public String getErrCodeDes() {
        return errCodeDes;
    }

    public WithDraw() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.errCode);
        dest.writeString(this.resultCode);
        dest.writeString(this.errCodeDes);
    }

    protected WithDraw(Parcel in) {
        this.errCode = in.readString();
        this.resultCode = in.readString();
        this.errCodeDes = in.readString();
    }

    public static final Creator<WithDraw> CREATOR = new Creator<WithDraw>() {
        @Override
        public WithDraw createFromParcel(Parcel source) {return new WithDraw(source);}

        @Override
        public WithDraw[] newArray(int size) {return new WithDraw[size];}
    };
}
