package com.hunliji.hljcommonlibrary.models.product.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.product.FreeShipping;

/**
 * Created by mo_yu on 2017/11/9.邮费模版和邮费集合
 */

public class FreeShippingFeeWrapper implements Parcelable {

    @SerializedName("free_shipping")
    FreeShipping freeShipping;
    @SerializedName("shipping_fee")
    double shippingFee;

    public FreeShipping getFreeShipping() {
        return freeShipping;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.freeShipping,
                flags);
        dest.writeDouble(this.shippingFee);
    }

    public FreeShippingFeeWrapper() {}

    protected FreeShippingFeeWrapper(Parcel in) {
        this.freeShipping = in.readParcelable(FreeShipping.class.getClassLoader());
        this.shippingFee = in.readDouble();
    }

    public static final Parcelable.Creator<FreeShippingFeeWrapper> CREATOR = new Parcelable
            .Creator<FreeShippingFeeWrapper>() {
        @Override
        public FreeShippingFeeWrapper createFromParcel(Parcel source) {
            return new FreeShippingFeeWrapper(source);
        }

        @Override
        public FreeShippingFeeWrapper[] newArray(int size) {return new FreeShippingFeeWrapper[size];}
    };
}
