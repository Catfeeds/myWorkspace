package me.suncloud.marrymemo.model.wallet;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;

import org.joda.time.DateTime;

/**
 * 用户端优惠记录，用户领取一张优惠券（coupon info作为优惠券model）后，
 * 就有一个属于这个用户的优惠券记录coupon record，还是成为优惠券，
 * 但其实本身是一个包含原券信息的记录实体
 * 在用户使用优惠券的时候是使用这个记录，即CouponRecord，使用CouponRecord的id
 * Created by chen_bin on 2016/10/18 0018.
 */

public class CouponRecord implements Parcelable {
    @SerializedName(value = "id")
    long id;
    // coupon_id 是coupon info的id
    @SerializedName(value = "coupon_id")
    long couponId;
    @SerializedName(value = "merchant_id")
    long merchantId;
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "code")
    String code;
    @SerializedName(value = "valid_start")
    DateTime validStart;
    @SerializedName(value = "valid_end")
    DateTime validEnd;
    @SerializedName(value = "used")
    boolean isUsed;
    @SerializedName(value = "money_sill")
    double moneySill;
    @SerializedName(value = "value")
    double value;
    @SerializedName(value = "coupon")
    CouponInfo coupon;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.couponId);
        dest.writeLong(this.merchantId);
        dest.writeLong(this.userId);
        dest.writeSerializable(this.createdAt);
        dest.writeString(this.code);
        dest.writeSerializable(this.validStart);
        dest.writeSerializable(this.validEnd);
        dest.writeByte(this.isUsed ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.moneySill);
        dest.writeDouble(this.value);
        dest.writeParcelable(this.coupon, flags);
    }

    public CouponRecord() {}

    protected CouponRecord(Parcel in) {
        this.id = in.readLong();
        this.couponId = in.readLong();
        this.merchantId = in.readLong();
        this.userId = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.code = in.readString();
        this.validStart = (DateTime) in.readSerializable();
        this.validEnd = (DateTime) in.readSerializable();
        this.isUsed = in.readByte() != 0;
        this.moneySill = in.readDouble();
        this.value = in.readDouble();
        this.coupon = in.readParcelable(CouponInfo.class.getClassLoader());
    }

    public static final Creator<CouponRecord> CREATOR = new Creator<CouponRecord>() {
        @Override
        public CouponRecord createFromParcel(Parcel source) {return new CouponRecord(source);}

        @Override
        public CouponRecord[] newArray(int size) {return new CouponRecord[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCouponId() {
        return couponId;
    }

    public void setCouponId(long couponId) {
        this.couponId = couponId;
    }

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

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DateTime getValidStart() {
        return validStart;
    }

    public void setValidStart(DateTime validStart) {
        this.validStart = validStart;
    }

    public DateTime getValidEnd() {
        return validEnd;
    }

    public void setValidEnd(DateTime validEnd) {
        this.validEnd = validEnd;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public double getMoneySill() {
        return moneySill;
    }

    public void setMoneySill(double moneySill) {
        this.moneySill = moneySill;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public CouponInfo getCoupon() {
        if (coupon == null) {
            coupon = new CouponInfo();
        }
        return coupon;
    }

    public void setCoupon(CouponInfo coupon) {
        this.coupon = coupon;
    }
}