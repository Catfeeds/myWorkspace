package com.hunliji.hljcommonlibrary.models.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2016/10/28.
 */

public class Sku implements Parcelable{
    private long id;
    private int quantity; //库存
    @SerializedName("show_num")
    private int showNum; //实时库存
    @SerializedName("actual_price")
    private double actualPrice; //原价
    @SerializedName("market_price")
    private double marketPrice;
    @SerializedName("sale_price")
    private double salePrice; //活动价
    @SerializedName("show_price")
    private double showPrice; //实时价格
    @SerializedName("limit_num")
    private int limitNum; //限量活动库存
    @SerializedName("limit_sold_out")
    private int limitSoldOut; //限量活动已购买数

    private String name;

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getShowNum() {
        return showNum;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public int getLimitSoldOut() {
        return limitSoldOut;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    public void setShowPrice(double showPrice) {
        this.showPrice = showPrice;
    }

    public Sku() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.quantity);
        dest.writeInt(this.showNum);
        dest.writeDouble(this.actualPrice);
        dest.writeDouble(this.marketPrice);
        dest.writeDouble(this.salePrice);
        dest.writeDouble(this.showPrice);
        dest.writeInt(this.limitNum);
        dest.writeInt(this.limitSoldOut);
        dest.writeString(this.name);
    }

    protected Sku(Parcel in) {
        this.id = in.readLong();
        this.quantity = in.readInt();
        this.showNum = in.readInt();
        this.actualPrice = in.readDouble();
        this.marketPrice = in.readDouble();
        this.salePrice = in.readDouble();
        this.showPrice = in.readDouble();
        this.limitNum = in.readInt();
        this.limitSoldOut = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<Sku> CREATOR = new Creator<Sku>() {
        @Override
        public Sku createFromParcel(Parcel source) {return new Sku(source);}

        @Override
        public Sku[] newArray(int size) {return new Sku[size];}
    };
}
