package com.hunliji.hljpaymentlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suncloud on 2016/8/4.
 */
public class PayResult implements Parcelable {

    private double fee;
    @SerializedName("free_order_link")
    private String freeOrderLink;
    @SerializedName("pay_params")
    private String payParams;
    @SerializedName("pay_agent")
    private String payAgent;

    public double getFee() {
        return fee;
    }

    public String getFreeOrderLink() {
        return freeOrderLink;
    }

    public String getPayParams() {
        return payParams;
    }

    public String getPayAgent() {
        return payAgent;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.fee);
        dest.writeString(this.freeOrderLink);
        dest.writeString(this.payParams);
        dest.writeString(this.payAgent);
    }

    protected PayResult(Parcel in) {
        this.fee = in.readDouble();
        this.freeOrderLink = in.readString();
        this.payParams = in.readString();
        this.payAgent = in.readString();
    }

    public static final Creator<PayResult> CREATOR = new Creator<PayResult>() {
        @Override
        public PayResult createFromParcel(Parcel source) {return new PayResult(source);}

        @Override
        public PayResult[] newArray(int size) {return new PayResult[size];}
    };
}

