package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.BindInfo;

/**
 * Created by mo_yu on 2017/6/20.新版请帖余额明细
 */

public class CardBalanceDetail implements Parcelable {

    @SerializedName("balance")
    private double balance;
    @SerializedName("bank")
    private BindInfo bindInfo;
    @SerializedName("customize")
    private int customize;//1可自定义修改提现金额 0不支持自定义

    protected CardBalanceDetail(Parcel in) {
        balance = in.readDouble();
        bindInfo = in.readParcelable(BindInfo.class.getClassLoader());
        customize = in.readInt();
    }

    public static final Creator<CardBalanceDetail> CREATOR = new Creator<CardBalanceDetail>() {
        @Override
        public CardBalanceDetail createFromParcel(Parcel in) {
            return new CardBalanceDetail(in);
        }

        @Override
        public CardBalanceDetail[] newArray(int size) {
            return new CardBalanceDetail[size];
        }
    };

    public double getBalance() {
        return balance;
    }

    public BindInfo getBindInfo() {
        return bindInfo;
    }

    public int getCustomize() {
        return customize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(balance);
        parcel.writeParcelable(bindInfo, i);
        parcel.writeInt(customize);
    }
}
