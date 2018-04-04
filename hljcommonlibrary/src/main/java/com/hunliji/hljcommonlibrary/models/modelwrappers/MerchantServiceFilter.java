package com.hunliji.hljcommonlibrary.models.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chen_bin on 2017/8/2 0002.
 */
public class MerchantServiceFilter implements Parcelable {
    @SerializedName(value = "category_tag_id")
    private long categoryMarkId;
    @SerializedName(value = "price_max")
    private double priceMax;
    @SerializedName(value = "price_min")
    private double priceMin;
    @SerializedName(value = "sale_way")
    private int saleWay; //227租赁 228出售
    @SerializedName(value = "shop_area")
    private long shopAreaId;
    @SerializedName(value = "tags")
    private List<String> tags;
    //婚纱摄影二级分类 1婚纱摄影 2儿童摄影 3写真摄影 4旅拍
    @SerializedName(value = "filter_second_category")
    long filterSecondCategory;

    public long getCategoryMarkId() {
        return categoryMarkId;
    }

    public void setCategoryMarkId(long categoryMarkId) {
        this.categoryMarkId = categoryMarkId;
    }

    public double getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(double priceMax) {
        this.priceMax = priceMax;
    }

    public double getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(double priceMin) {
        this.priceMin = priceMin;
    }

    public int getSaleWay() {
        return saleWay;
    }

    public void setSaleWay(int saleWay) {
        this.saleWay = saleWay;
    }

    public long getShopAreaId() {
        return shopAreaId;
    }

    public void setShopAreaId(long shopAreaId) {
        this.shopAreaId = shopAreaId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public long getFilterSecondCategory() {
        return filterSecondCategory;
    }

    public void setFilterSecondCategory(long filterSecondCategory) {
        this.filterSecondCategory = filterSecondCategory;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.categoryMarkId);
        dest.writeDouble(this.priceMax);
        dest.writeDouble(this.priceMin);
        dest.writeInt(this.saleWay);
        dest.writeLong(this.shopAreaId);
        dest.writeStringList(this.tags);
        dest.writeLong(filterSecondCategory);
    }

    public MerchantServiceFilter() {}

    protected MerchantServiceFilter(Parcel in) {
        this.categoryMarkId = in.readLong();
        this.priceMax = in.readDouble();
        this.priceMin = in.readDouble();
        this.saleWay = in.readInt();
        this.shopAreaId = in.readLong();
        this.tags = in.createStringArrayList();
        this.filterSecondCategory = in.readLong();
    }

    public static final Creator<MerchantServiceFilter> CREATOR = new Creator<MerchantServiceFilter>() {
        @Override
        public MerchantServiceFilter createFromParcel(Parcel source) {return new MerchantServiceFilter(source);}

        @Override
        public MerchantServiceFilter[] newArray(int size) {return new MerchantServiceFilter[size];}
    };
}