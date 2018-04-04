package com.hunliji.marrybiz.model.revenue;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by hua_rong on 2017/8/15 0015.
 * 收入管理界面 model
 */

public class RevenueManager implements Parcelable {

    @SerializedName(value = "withdraw_ability")
    private double withdrawAbility;//可提现收入
    private double withdrawing;//提现中资金
    @SerializedName(value = "bond_fee")
    private double bondFee; // 保证金
    @SerializedName(value = "pedding")
    private double unclearAmount; // 待结算金额
    @SerializedName(value = "bond_money")
    private double bondMoney; // 担保中的金额
    @SerializedName(value = "bond_enough")
    private boolean bondEnough; // 保证金余额是否足够0：不够 1：足够
    @SerializedName(value = "withdraw_account")
    private String withdrawAccount;
    @SerializedName(value = "finance_date")
    private DateTime hintTime;
    @SerializedName(value = "has_old")
    private boolean hasOld;

    public double getWithdrawAbility() {
        return withdrawAbility;
    }

    public void setWithdrawAbility(double withdrawAbility) {
        this.withdrawAbility = withdrawAbility;
    }

    public double getWithdrawing() {
        return withdrawing;
    }

    public void setWithdrawing(double withdrawing) {
        this.withdrawing = withdrawing;
    }

    public double getBondFee() {
        return bondFee;
    }

    public void setBondFee(double bondFee) {
        this.bondFee = bondFee;
    }

    public double getUnclearAmount() {
        return unclearAmount;
    }

    public void setUnclearAmount(double unclearAmount) {
        this.unclearAmount = unclearAmount;
    }

    public double getBondMoney() {
        return bondMoney;
    }

    public void setBondMoney(double bondMoney) {
        this.bondMoney = bondMoney;
    }

    public boolean isBondEnough() {
        return bondEnough;
    }

    public void setBondEnough(boolean bondEnough) {
        this.bondEnough = bondEnough;
    }

    public String getWithdrawAccount() {
        return withdrawAccount;
    }

    public void setWithdrawAccount(String withdrawAccount) {
        this.withdrawAccount = withdrawAccount;
    }

    public DateTime getHintTime() {
        return hintTime;
    }

    public void setHintTime(DateTime hintTime) {
        this.hintTime = hintTime;
    }

    public boolean isHasOld() {
        return hasOld;
    }

    public void setHasOld(boolean hasOld) {
        this.hasOld = hasOld;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.withdrawAbility);
        dest.writeDouble(this.withdrawing);
        dest.writeDouble(this.bondFee);
        dest.writeDouble(this.unclearAmount);
        dest.writeDouble(this.bondMoney);
        dest.writeByte(this.bondEnough ? (byte) 1 : (byte) 0);
        dest.writeString(this.withdrawAccount);
        dest.writeSerializable(this.hintTime);
        dest.writeByte(this.hasOld ? (byte) 1 : (byte) 0);
    }

    public RevenueManager() {}

    protected RevenueManager(Parcel in) {
        this.withdrawAbility = in.readDouble();
        this.withdrawing = in.readDouble();
        this.bondFee = in.readDouble();
        this.unclearAmount = in.readDouble();
        this.bondMoney = in.readDouble();
        this.bondEnough = in.readByte() != 0;
        this.withdrawAccount = in.readString();
        this.hintTime = (DateTime) in.readSerializable();
        this.hasOld = in.readByte() != 0;
    }

    public static final Parcelable.Creator<RevenueManager> CREATOR = new Parcelable.Creator<RevenueManager>() {
        @Override
        public RevenueManager createFromParcel(Parcel source) {return new RevenueManager(source);}

        @Override
        public RevenueManager[] newArray(int size) {return new RevenueManager[size];}
    };
}
