package me.suncloud.marrymemo.model.wallet;

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
    @SerializedName("fund_total")
    private double fundTotal;//理财总余额
    @SerializedName("fund_user_flag")
    private boolean fundUserFlag;//1转入过理财 0没有

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

    public double getFundTotal() {
        return fundTotal;
    }

    public boolean isFundUserFlag() {
        return fundUserFlag;
    }

    public Wallet() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.packetCount);
        dest.writeInt(this.couponCount);
        dest.writeDouble(this.balance);
        dest.writeString(this.insuranceUrl);
        dest.writeInt(this.pendingInsuranceNum);
        dest.writeDouble(this.fundTotal);
        dest.writeByte(this.fundUserFlag ? (byte) 1 : (byte) 0);
    }

    protected Wallet(Parcel in) {
        this.packetCount = in.readInt();
        this.couponCount = in.readInt();
        this.balance = in.readDouble();
        this.insuranceUrl = in.readString();
        this.pendingInsuranceNum = in.readInt();
        this.fundTotal = in.readDouble();
        this.fundUserFlag = in.readByte() != 0;
    }

    public static final Creator<Wallet> CREATOR = new Creator<Wallet>() {
        @Override
        public Wallet createFromParcel(Parcel source) {return new Wallet(source);}

        @Override
        public Wallet[] newArray(int size) {return new Wallet[size];}
    };
}
