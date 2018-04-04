package com.hunliji.hljcommonlibrary.models.coupon;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 优惠券的model
 * Created by chen_bin on 2016/10/15 0015.
 */
public class CouponInfo implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "provide_start")
    private DateTime provideStart;
    @SerializedName(value = "provide_end")
    private DateTime provideEnd;
    @SerializedName(value = "valid_start")
    private DateTime validStart;
    @SerializedName(value = "valid_end")
    private DateTime validEnd;
    @SerializedName(value = "used")
    private boolean isUsed;
    @SerializedName(value = "money_sill")
    private double moneySill;
    @SerializedName(value = "value")
    private double value;
    @SerializedName(value = "hidden")
    private boolean hidden;
    @SerializedName(value = "total_count")
    private int totalCount;
    @SerializedName(value = "provided_count")
    private int providedCount;
    @SerializedName(value = "used_count")
    private int usedCount;
    @SerializedName(value = "online_used_count")
    private int onlineUsedCount;
    @SerializedName(value = "offline_used_count")
    private int offlineUsedCount;
    @SerializedName(value = "type")
    private int type;
    @SerializedName(value = "get_status")
    private int getStatus;
    @SerializedName(value = "merchant")
    private Merchant merchant;

    public transient static final int GET_STATUS_WAITING_RECEIVE = 1; //待领取
    public transient static final int GET_STATUS_CAN_RECEIVE = 2; //可领取
    public transient static final int GET_STATUS_LOOT_ALL = 3; //抢光
    public transient static final int GET_STATUS_RECEIVED = 4; //已领取

    public CouponInfo() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getProvideStart() {
        return provideStart;
    }

    public void setProvideStart(DateTime provideStart) {
        this.provideStart = provideStart;
    }

    public DateTime getProvideEnd() {
        return provideEnd;

    }

    public void setProvideEnd(DateTime provideEnd) {
        this.provideEnd = provideEnd;
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

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public double getMoneySill() {
        return moneySill;
    }

    public void setMoneySill(int moneySill) {
        this.moneySill = moneySill;
    }

    public double getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getProvidedCount() {
        return providedCount;
    }

    public void setProvidedCount(int providedCount) {
        this.providedCount = providedCount;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public int getOnlineUsedCount() {
        return onlineUsedCount;
    }

    public void setOnlineUsedCount(int onlineUsedCount) {
        this.onlineUsedCount = onlineUsedCount;
    }

    public int getOfflineUsedCount() {
        return offlineUsedCount;
    }

    public void setOfflineUsedCount(int offlineUsedCount) {
        this.offlineUsedCount = offlineUsedCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGetStatus() {
        return getStatus;
    }

    public void setGetStatus(int getStatus) {
        this.getStatus = getStatus;
    }

    public Merchant getMerchant() {
        if (merchant == null) {
            merchant = new Merchant();
        }
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.merchantId);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        HljTimeUtils.writeDateTimeToParcel(dest, this.updatedAt);
        dest.writeString(this.title);
        HljTimeUtils.writeDateTimeToParcel(dest, this.provideStart);
        HljTimeUtils.writeDateTimeToParcel(dest, this.provideEnd);
        HljTimeUtils.writeDateTimeToParcel(dest, this.validStart);
        HljTimeUtils.writeDateTimeToParcel(dest, this.validEnd);
        dest.writeByte(this.isUsed ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.moneySill);
        dest.writeDouble(this.value);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
        dest.writeInt(this.totalCount);
        dest.writeInt(this.providedCount);
        dest.writeInt(this.usedCount);
        dest.writeInt(this.onlineUsedCount);
        dest.writeInt(this.offlineUsedCount);
        dest.writeInt(this.type);
        dest.writeInt(this.getStatus);
        dest.writeParcelable(this.merchant, flags);
    }

    protected CouponInfo(Parcel in) {
        this.id = in.readLong();
        this.merchantId = in.readLong();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.updatedAt = HljTimeUtils.readDateTimeToParcel(in);
        this.title = in.readString();
        this.provideStart = HljTimeUtils.readDateTimeToParcel(in);
        this.provideEnd = HljTimeUtils.readDateTimeToParcel(in);
        this.validStart = HljTimeUtils.readDateTimeToParcel(in);
        this.validEnd = HljTimeUtils.readDateTimeToParcel(in);
        this.isUsed = in.readByte() != 0;
        this.moneySill = in.readDouble();
        this.value = in.readDouble();
        this.hidden = in.readByte() != 0;
        this.totalCount = in.readInt();
        this.providedCount = in.readInt();
        this.usedCount = in.readInt();
        this.onlineUsedCount = in.readInt();
        this.offlineUsedCount = in.readInt();
        this.type = in.readInt();
        this.getStatus = in.readInt();
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
    }

    public static final Creator<CouponInfo> CREATOR = new Creator<CouponInfo>() {
        @Override
        public CouponInfo createFromParcel(Parcel source) {return new CouponInfo(source);}

        @Override
        public CouponInfo[] newArray(int size) {return new CouponInfo[size];}
    };
}