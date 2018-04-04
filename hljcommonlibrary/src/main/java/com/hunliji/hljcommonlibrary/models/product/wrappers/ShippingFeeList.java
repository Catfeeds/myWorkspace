package com.hunliji.hljcommonlibrary.models.product.wrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.product.MerchantShippingFee;

import java.util.List;

/**
 * Created by mo_yu on 2017/11/16.单个商家下所有婚品订单的运费列表
 */

public class ShippingFeeList implements Parcelable {
    @SerializedName("shipping_fee")
    List<MerchantShippingFee> shippingFees;

    public List<MerchantShippingFee> getShippingFees() {
        return shippingFees;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeTypedList(this.shippingFees);}

    public ShippingFeeList() {}

    protected ShippingFeeList(Parcel in) {
        this.shippingFees = in.createTypedArrayList(MerchantShippingFee.CREATOR);
    }

    public static final Parcelable.Creator<ShippingFeeList> CREATOR = new Parcelable
            .Creator<ShippingFeeList>() {
        @Override
        public ShippingFeeList createFromParcel(Parcel source) {return new ShippingFeeList(source);}

        @Override
        public ShippingFeeList[] newArray(int size) {return new ShippingFeeList[size];}
    };
}
