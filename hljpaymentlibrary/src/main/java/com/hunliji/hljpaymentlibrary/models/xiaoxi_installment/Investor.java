package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 出借人model
 * Created by chen_bin on 2017/12/13 0013.
 */
public class Investor implements Parcelable {
    @SerializedName(value = "investorId")
    private long investorId;
    @SerializedName(value = "investorName")
    private String investorName;
    @SerializedName(value = "investorIdCard")
    private String investorIdCard;
    @SerializedName(value = "investorAmount")
    private double investorAmount;

    public long getInvestorId() {
        return investorId;
    }

    public String getInvestorName() {
        return investorName;
    }

    public String getInvestorIdCard() {
        return investorIdCard;
    }

    public double getInvestorAmount() {
        return investorAmount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.investorId);
        dest.writeString(this.investorName);
        dest.writeString(this.investorIdCard);
        dest.writeDouble(this.investorAmount);
    }

    public Investor() {}

    protected Investor(Parcel in) {
        this.investorId = in.readLong();
        this.investorName = in.readString();
        this.investorIdCard = in.readString();
        this.investorAmount = in.readDouble();
    }

    public static final Creator<Investor> CREATOR = new Creator<Investor>() {
        @Override
        public Investor createFromParcel(Parcel source) {return new Investor(source);}

        @Override
        public Investor[] newArray(int size) {return new Investor[size];}
    };
}
