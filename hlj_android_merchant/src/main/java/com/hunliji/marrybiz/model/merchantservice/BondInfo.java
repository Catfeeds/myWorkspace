package com.hunliji.marrybiz.model.merchantservice;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2018/2/1.保证金信息
 */

public class BondInfo implements Parcelable {

    @SerializedName("withdraw_ability")
    double withdrawAbility;
    @SerializedName("pedding")
    double pedding;
    @SerializedName("bond_money")
    double bondMoney;
    @SerializedName("bond_fee")
    double bondFee;
    @SerializedName("bond_enough")
    boolean bondEnough;//保证金是否充足
    @SerializedName("withdraw_account")
    double withdrawAccount;
    @SerializedName("withdraw_name")
    String withdrawName;
    @SerializedName("has_old")
    boolean hasOld;
    @SerializedName("pedding_count")
    double peddingCount;
    @SerializedName("bond_count")
    double bondCount;
    @SerializedName("withdrawing")
    double withdrawing;

    public double getWithdrawAbility() {
        return withdrawAbility;
    }

    public double getPedding() {
        return pedding;
    }

    public double getBondMoney() {
        return bondMoney;
    }

    public double getBondFee() {
        return bondFee;
    }

    public boolean isBondEnough() {
        return bondEnough;
    }

    public double getWithdrawAccount() {
        return withdrawAccount;
    }

    public String getWithdrawName() {
        return withdrawName;
    }

    public boolean isHasOld() {
        return hasOld;
    }

    public double getPeddingCount() {
        return peddingCount;
    }

    public double getBondCount() {
        return bondCount;
    }

    public double getWithdrawing() {
        return withdrawing;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.withdrawAbility);
        dest.writeDouble(this.pedding);
        dest.writeDouble(this.bondMoney);
        dest.writeDouble(this.bondFee);
        dest.writeByte(this.bondEnough ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.withdrawAccount);
        dest.writeString(this.withdrawName);
        dest.writeByte(this.hasOld ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.peddingCount);
        dest.writeDouble(this.bondCount);
        dest.writeDouble(this.withdrawing);
    }

    public BondInfo() {}

    protected BondInfo(Parcel in) {
        this.withdrawAbility = in.readDouble();
        this.pedding = in.readDouble();
        this.bondMoney = in.readDouble();
        this.bondFee = in.readDouble();
        this.bondEnough = in.readByte() != 0;
        this.withdrawAccount = in.readDouble();
        this.withdrawName = in.readString();
        this.hasOld = in.readByte() != 0;
        this.peddingCount = in.readDouble();
        this.bondCount = in.readDouble();
        this.withdrawing = in.readDouble();
    }

    public static final Parcelable.Creator<BondInfo> CREATOR = new Parcelable.Creator<BondInfo>() {
        @Override
        public BondInfo createFromParcel(Parcel source) {return new BondInfo(source);}

        @Override
        public BondInfo[] newArray(int size) {return new BondInfo[size];}
    };
}
