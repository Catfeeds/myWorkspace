package com.hunliji.hljcommonlibrary.models.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/11/16.单个商家下所有婚品订单的运费
 */
public class MerchantShippingFee implements Parcelable {
    @SerializedName("merchant_id")
    long merchantId;
    @SerializedName("user_id")
    long userId;
    @SerializedName("shipping_fee")
    double shippingFee;

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.merchantId);
        dest.writeLong(this.userId);
        dest.writeDouble(this.shippingFee);
    }

    public MerchantShippingFee() {}

    protected MerchantShippingFee(Parcel in) {
        this.merchantId = in.readLong();
        this.userId = in.readLong();
        this.shippingFee = in.readDouble();
    }

    public static final Parcelable.Creator<MerchantShippingFee> CREATOR = new Parcelable
            .Creator<MerchantShippingFee>() {
        @Override
        public MerchantShippingFee createFromParcel(Parcel source) {
            return new MerchantShippingFee(source);
        }

        @Override
        public MerchantShippingFee[] newArray(int size) {return new MerchantShippingFee[size];}
    };
}
