package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/2/10.礼金提现
 */

public class WithDrawCash implements Parcelable {
    @SerializedName(value = "fee")
    private String fee;
    @SerializedName(value = "money")
    private String money;
    @SerializedName(value = "trade_no")
    private String tradeNo;
    @SerializedName(value = "errmsg")
    private String errMsg;
    @SerializedName(value = "status")
    private int status;


    public String getFee() {
        return fee;
    }

    public String getMoney() {
        return money;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public int getStatus() {
        return status;
    }

    public WithDrawCash() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fee);
        dest.writeString(this.money);
        dest.writeString(this.tradeNo);
        dest.writeString(this.errMsg);
        dest.writeInt(this.status);
    }

    protected WithDrawCash(Parcel in) {
        this.fee = in.readString();
        this.money = in.readString();
        this.tradeNo = in.readString();
        this.errMsg = in.readString();
        this.status = in.readInt();
    }

    public static final Creator<WithDrawCash> CREATOR = new Creator<WithDrawCash>() {
        @Override
        public WithDrawCash createFromParcel(Parcel source) {return new WithDrawCash(source);}

        @Override
        public WithDrawCash[] newArray(int size) {return new WithDrawCash[size];}
    };
}
