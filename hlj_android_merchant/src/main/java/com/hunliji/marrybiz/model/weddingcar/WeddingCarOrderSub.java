package com.hunliji.marrybiz.model.weddingcar;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarProduct;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarSku;

/**
 * Created by jinxin on 2018/1/4 0004.
 */

public class WeddingCarOrderSub implements Parcelable {

    @SerializedName("activity_status")
    String activityStatus;
    @SerializedName("actual_money")
    double actualMoney;
    long id;
    @SerializedName("original_money")
    double originalMoney;
    @SerializedName("product")
    WeddingCarProduct product;
    int quantity;//数量
    WeddingCarSku sku;

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public void setActualMoney(double actualMoney) {
        this.actualMoney = actualMoney;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getOriginalMoney() {
        return originalMoney;
    }

    public void setOriginalMoney(double originalMoney) {
        this.originalMoney = originalMoney;
    }

    public WeddingCarProduct getProduct() {
        return product;
    }

    public void setProduct(WeddingCarProduct product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public WeddingCarSku getSku() {
        return sku;
    }

    public void setSku(WeddingCarSku sku) {
        this.sku = sku;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.activityStatus);
        dest.writeDouble(this.actualMoney);
        dest.writeLong(this.id);
        dest.writeDouble(this.originalMoney);
        dest.writeParcelable(this.product, flags);
        dest.writeInt(this.quantity);
        dest.writeParcelable(this.sku, flags);
    }

    public WeddingCarOrderSub() {}

    protected WeddingCarOrderSub(Parcel in) {
        this.activityStatus = in.readString();
        this.actualMoney = in.readDouble();
        this.id = in.readLong();
        this.originalMoney = in.readDouble();
        this.product = in.readParcelable(WeddingCarProduct.class.getClassLoader());
        this.quantity = in.readInt();
        this.sku = in.readParcelable(WeddingCarSku.class.getClassLoader());
    }

    public static final Parcelable.Creator<WeddingCarOrderSub> CREATOR = new Parcelable
            .Creator<WeddingCarOrderSub>() {
        @Override
        public WeddingCarOrderSub createFromParcel(Parcel source) {
            return new WeddingCarOrderSub(source);
        }

        @Override
        public WeddingCarOrderSub[] newArray(int size) {return new WeddingCarOrderSub[size];}
    };
}
