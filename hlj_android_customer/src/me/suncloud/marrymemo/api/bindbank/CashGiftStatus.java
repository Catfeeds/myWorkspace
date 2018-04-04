package me.suncloud.marrymemo.api.bindbank;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.BankCard;

/**
 * 礼金功能开关状态
 * Created by jinxin on 2017/4/5 0005.
 */

public class CashGiftStatus implements Parcelable {
    @SerializedName(value = "current_cash_gift_on")
    boolean currentCaseGiftOn;//当前请帖开关状态
    @SerializedName(value = "is_cash_gift_on")
    boolean isCashGiftOn;//全局开关状态
    @SerializedName(value = "bank_card")
    BankCard bankCard;

    public boolean isCurrentCaseGiftOn() {
        return currentCaseGiftOn;
    }

    public void setCurrentCaseGiftOn(boolean currentCaseGiftOn) {
        this.currentCaseGiftOn = currentCaseGiftOn;
    }

    public boolean isCashGiftOn() {
        return isCashGiftOn;
    }

    public void setCashGiftOn(boolean cashGiftOn) {
        isCashGiftOn = cashGiftOn;
    }

    public BankCard getBankCard() {
        return bankCard;
    }

    public void setBankCard(BankCard bankCard) {
        this.bankCard = bankCard;
    }

    public CashGiftStatus() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(
            Parcel dest,
            int flags) {
        dest.writeByte(this.currentCaseGiftOn ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCashGiftOn ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.bankCard, flags);
    }

    protected CashGiftStatus(Parcel in) {
        this.currentCaseGiftOn = in.readByte() != 0;
        this.isCashGiftOn = in.readByte() != 0;
        this.bankCard = in.readParcelable(BankCard.class.getClassLoader());
    }

    public static final Creator<CashGiftStatus> CREATOR = new Creator<CashGiftStatus>() {
        @Override
        public CashGiftStatus createFromParcel(Parcel source) {return new CashGiftStatus(source);}

        @Override
        public CashGiftStatus[] newArray(int size) {return new CashGiftStatus[size];}
    };
}
