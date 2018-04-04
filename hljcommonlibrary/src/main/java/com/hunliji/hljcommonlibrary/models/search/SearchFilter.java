package com.hunliji.hljcommonlibrary.models.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/12/1.
 * 搜索筛选参数
 */

public class SearchFilter implements Parcelable {

    @SerializedName("price_max")
    double priceMax;
    @SerializedName("price_min")
    double priceMin;
    @SerializedName("property_id")
    long propertyId;
    @SerializedName("table_max")
    int tableMax;
    @SerializedName("table_min")
    int tableMin;
    @SerializedName("category_id")
    long categoryId;
    @SerializedName("service")
    long productService; // 婚品搜索筛选选项：1包邮 2消费保障
    @SerializedName("tags")
    String tags;

    public long getProductService() {
        return productService;
    }

    public void setProductService(long productService) {
        this.productService = productService;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public int getTableMax() {
        return tableMax;
    }

    public void setTableMax(int tableMax) {
        this.tableMax = tableMax;
    }

    public int getTableMin() {
        return tableMin;
    }

    public void setTableMin(int tableMin) {
        this.tableMin = tableMin;
    }

    public double getPriceMax() {
        return priceMax;
    }

    public double getPriceMin() {
        return priceMin;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPriceMax(double priceMax) {
        this.priceMax = priceMax;
    }

    public void setPriceMin(double priceMin) {
        this.priceMin = priceMin;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public SearchFilter() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.priceMax);
        dest.writeDouble(this.priceMin);
        dest.writeLong(this.propertyId);
        dest.writeInt(this.tableMax);
        dest.writeInt(this.tableMin);
        dest.writeLong(this.categoryId);
        dest.writeLong(this.productService);
        dest.writeString(this.tags);
    }

    protected SearchFilter(Parcel in) {
        this.priceMax = in.readDouble();
        this.priceMin = in.readDouble();
        this.propertyId = in.readLong();
        this.tableMax = in.readInt();
        this.tableMin = in.readInt();
        this.categoryId = in.readLong();
        this.productService = in.readLong();
        this.tags = in.readString();
    }

    public static final Creator<SearchFilter> CREATOR = new Creator<SearchFilter>() {
        @Override
        public SearchFilter createFromParcel(Parcel source) {return new SearchFilter(source);}

        @Override
        public SearchFilter[] newArray(int size) {return new SearchFilter[size];}
    };
}
