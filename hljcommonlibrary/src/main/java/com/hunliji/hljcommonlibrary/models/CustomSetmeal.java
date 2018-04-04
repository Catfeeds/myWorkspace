package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/7/27.
 */
public class CustomSetmeal implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "merchant_id")
    long merchantId;
    @SerializedName(value = "title")
    String title;
    @SerializedName(value = "cover_path")
    String coverPath;
    @SerializedName(value = "reason")
    String reason;
    @SerializedName(value = "is_published")
    boolean isPublished;
    @SerializedName(value = "actual_price")
    double actualPrice;
    @SerializedName(value = "status")
    int status;
    @SerializedName(value = "sale_count")
    int salesCount;
    @SerializedName(value = "collectors_count")
    int collectorsCount;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.merchantId);
        dest.writeString(this.title);
        dest.writeString(this.coverPath);
        dest.writeString(this.reason);
        dest.writeByte(this.isPublished ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.actualPrice);
        dest.writeInt(this.status);
        dest.writeInt(this.salesCount);
        dest.writeInt(this.collectorsCount);
    }

    public CustomSetmeal() {}

    protected CustomSetmeal(Parcel in) {
        this.id = in.readLong();
        this.merchantId = in.readLong();
        this.title = in.readString();
        this.coverPath = in.readString();
        this.reason = in.readString();
        this.isPublished = in.readByte() != 0;
        this.actualPrice = in.readDouble();
        this.status = in.readInt();
        this.salesCount = in.readInt();
        this.collectorsCount = in.readInt();
    }

    public static final Creator<CustomSetmeal> CREATOR = new Creator<CustomSetmeal>() {
        @Override
        public CustomSetmeal createFromParcel(Parcel source) {return new CustomSetmeal(source);}

        @Override
        public CustomSetmeal[] newArray(int size) {return new CustomSetmeal[size];}
    };

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }

    public int getCollectorsCount() {
        return collectorsCount;
    }

    public void setCollectorsCount(int collectorsCount) {
        this.collectorsCount = collectorsCount;
    }
}