package com.hunliji.hljcommonlibrary.models.wallet;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/4/27.
 */

public class Wallet implements Parcelable {

    @SerializedName("pack_num")
    private int packetCount; //红包数
    @SerializedName("coupon_num")
    private int couponCount; //优惠券数
    private double balance; //余额
    @SerializedName("insurance_url")
    private String insuranceUrl; //我的保单地址
    @SerializedName("pending_insurance_num")
    private int pendingInsuranceNum;//待领取保单个数

    public int getPacketCount() {
        return packetCount;
    }

    public int getCouponCount() {
        return couponCount;
    }

    public double getBalance() {
        return balance;
    }

    public String getInsuranceUrl() {
        return insuranceUrl;
    }

    public int getPendingInsuranceNum() {
        return pendingInsuranceNum;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.packetCount);
        dest.writeInt(this.couponCount);
        dest.writeDouble(this.balance);
        dest.writeString(this.insuranceUrl);
        dest.writeInt(this.pendingInsuranceNum);
    }

    public Wallet() {}

    protected Wallet(Parcel in) {
        this.packetCount = in.readInt();
        this.couponCount = in.readInt();
        this.balance = in.readDouble();
        this.insuranceUrl = in.readString();
        this.pendingInsuranceNum = in.readInt();
    }

    public static final Creator<Wallet> CREATOR = new Creator<Wallet>() {
        @Override
        public Wallet createFromParcel(Parcel source) {return new Wallet(source);}

        @Override
        public Wallet[] newArray(int size) {return new Wallet[size];}
    };
}
