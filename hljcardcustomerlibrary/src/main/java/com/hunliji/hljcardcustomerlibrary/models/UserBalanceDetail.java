package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2017/6/20.旧版余额明细
 */

public class UserBalanceDetail implements Parcelable {

    @SerializedName("balance")
    private double balance;
    @SerializedName("cash_balance")
    private double cashBalance;
    @SerializedName("gift_balance")
    private double giftBalance;
    @SerializedName("last_withdraw_at")
    private DateTime lastWithdrawAt;

    public double getBalance() {
        return balance;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public double getGiftBalance() {
        return giftBalance;
    }

    public DateTime getLastWithdrawAt() {
        return lastWithdrawAt;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.balance);
        dest.writeDouble(this.cashBalance);
        dest.writeDouble(this.giftBalance);
        dest.writeSerializable(this.lastWithdrawAt);
    }

    public UserBalanceDetail() {}

    protected UserBalanceDetail(Parcel in) {
        this.balance = in.readDouble();
        this.cashBalance = in.readDouble();
        this.giftBalance = in.readDouble();
        this.lastWithdrawAt = (DateTime) in.readSerializable();
    }

    public static final Creator<UserBalanceDetail> CREATOR = new Creator<UserBalanceDetail>() {
        @Override
        public UserBalanceDetail createFromParcel(Parcel source) {
            return new UserBalanceDetail(source);
        }

        @Override
        public UserBalanceDetail[] newArray(int size) {return new UserBalanceDetail[size];}
    };
}
