package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/10/19.提现手续费相关信息
 */

public class WithdrawParam implements Parcelable {

    @SerializedName("withdraw_max")
    private double withdrawMax;
    @SerializedName("withdraw_min")
    private double withdrawMin;
    @SerializedName("withdraw_rate")
    private double withdrawRate;

    public double getWithdrawMax() {
        return withdrawMax;
    }

    public double getWithdrawMin() {
        return withdrawMin;
    }

    public double getWithdrawRate() {
        return withdrawRate;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.withdrawMax);
        dest.writeDouble(this.withdrawMin);
        dest.writeDouble(this.withdrawRate);
    }

    public WithdrawParam() {}

    protected WithdrawParam(Parcel in) {
        this.withdrawMax = in.readDouble();
        this.withdrawMin = in.readDouble();
        this.withdrawRate = in.readDouble();
    }

    public static final Creator<WithdrawParam> CREATOR = new Creator<WithdrawParam>() {
        @Override
        public WithdrawParam createFromParcel(Parcel source) {return new WithdrawParam(source);}

        @Override
        public WithdrawParam[] newArray(int size) {return new WithdrawParam[size];}
    };
}
