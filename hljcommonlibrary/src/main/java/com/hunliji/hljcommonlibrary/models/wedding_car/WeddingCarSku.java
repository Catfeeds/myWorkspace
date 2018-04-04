package com.hunliji.hljcommonlibrary.models.wedding_car;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jinxin on 2017/12/27 0027.
 */

public class WeddingCarSku implements Parcelable {

    @SerializedName(value = "actual_price")
    double actualPrice;
    long id;
    @SerializedName(value = "limit_num")
    int limitNum;
    @SerializedName(value = "limit_sold_out")
    int limitSoldOut;
    @SerializedName(value = "market_price")
    double marketPrice;
    @SerializedName(value = "sale_price")
    double salePrice;
    @SerializedName(value = "sku_item")
    List<WeddingCarSkuItem> skuItem;

    private transient String skuNames;

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    public int getLimitSoldOut() {
        return limitSoldOut;
    }

    public void setLimitSoldOut(int limitSoldOut) {
        this.limitSoldOut = limitSoldOut;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public List<WeddingCarSkuItem> getSkuItem() {
        return skuItem;
    }

    public void setSkuItem(List<WeddingCarSkuItem> skuItem) {
        this.skuItem = skuItem;
    }

    public String getSkuNames() {
        if (!TextUtils.isEmpty(skuNames)) {
            return skuNames;
        }
        StringBuffer stringBuffer = new StringBuffer("");
        if (skuItem != null && !skuItem.isEmpty()) {
            int size = skuItem.size();
            for (int i = 0; i < size; i++) {
                WeddingCarSkuItem item = skuItem.get(i);
                if (i > 0) {
                    stringBuffer.append(";");
                }
                stringBuffer.append(item.getValue());
            }
        }
        skuNames = stringBuffer.toString();
        return skuNames;
    }
    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.actualPrice);
        dest.writeLong(this.id);
        dest.writeInt(this.limitNum);
        dest.writeInt(this.limitSoldOut);
        dest.writeDouble(this.marketPrice);
        dest.writeDouble(this.salePrice);
        dest.writeTypedList(this.skuItem);
    }

    public WeddingCarSku() {}

    protected WeddingCarSku(Parcel in) {
        this.actualPrice = in.readDouble();
        this.id = in.readLong();
        this.limitNum = in.readInt();
        this.limitSoldOut = in.readInt();
        this.marketPrice = in.readDouble();
        this.salePrice = in.readDouble();
        this.skuItem = in.createTypedArrayList(WeddingCarSkuItem.CREATOR);
    }

    public static final Creator<WeddingCarSku> CREATOR = new Creator<WeddingCarSku>() {
        @Override
        public WeddingCarSku createFromParcel(Parcel source) {return new WeddingCarSku(source);}

        @Override
        public WeddingCarSku[] newArray(int size) {return new WeddingCarSku[size];}
    };
}
