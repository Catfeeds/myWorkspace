package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 小犀分期-授信结果
 * Created by chen_bin on 2017/8/23 0023.
 */
public class CreditLimit implements Parcelable {
    @SerializedName(value = "availableLimit")
    private double availableLimit; //可用额度
    @SerializedName(value = "usedLimit")
    private double usedLimit; //已用额度
    @SerializedName(value = "totalLimit")
    private double totalLimit; //总额度
    @SerializedName(value = "riskCheckNotReady")
    private boolean riskCheckNotReady; //风控计算进度标识

    public double getAvailableLimit() {
        return availableLimit;
    }

    public double getUsedLimit() {
        return usedLimit;
    }

    public double getTotalLimit() {
        return totalLimit;
    }

    public boolean isRiskCheckNotReady() {
        return riskCheckNotReady;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.availableLimit);
        dest.writeDouble(this.usedLimit);
        dest.writeDouble(this.totalLimit);
        dest.writeByte(this.riskCheckNotReady ? (byte) 1 : (byte) 0);
    }

    public CreditLimit() {}

    protected CreditLimit(Parcel in) {
        this.availableLimit = in.readDouble();
        this.usedLimit = in.readDouble();
        this.totalLimit = in.readDouble();
        this.riskCheckNotReady = in.readByte() != 0;
    }

    public static final Creator<CreditLimit> CREATOR = new Creator<CreditLimit>() {
        @Override
        public CreditLimit createFromParcel(Parcel source) {return new CreditLimit(source);}

        @Override
        public CreditLimit[] newArray(int size) {return new CreditLimit[size];}
    };
}
