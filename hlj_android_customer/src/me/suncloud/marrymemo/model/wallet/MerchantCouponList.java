package me.suncloud.marrymemo.model.wallet;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 2017/11/8.婚品商家可用优惠券列表
 */

public class MerchantCouponList implements Parcelable {

    @SerializedName("merchant_id")
    private long merchantId;
    @SerializedName("coupon_list")
    private ArrayList<CouponRecord> couponList;

    public long getMerchantId() {
        return merchantId;
    }

    public ArrayList<CouponRecord> getCouponList() {
        return couponList;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.merchantId);
        dest.writeTypedList(this.couponList);
    }

    public MerchantCouponList() {}

    protected MerchantCouponList(Parcel in) {
        this.merchantId = in.readLong();
        this.couponList = in.createTypedArrayList(CouponRecord.CREATOR);
    }

    public static final Parcelable.Creator<MerchantCouponList> CREATOR = new Parcelable
            .Creator<MerchantCouponList>() {
        @Override
        public MerchantCouponList createFromParcel(Parcel source) {
            return new MerchantCouponList(source);
        }

        @Override
        public MerchantCouponList[] newArray(int size) {return new MerchantCouponList[size];}
    };
}
