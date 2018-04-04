package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;

/**
 * Created by mo_yu on 2017/10/8.桌数使用的简单label
 */

public class DeskPriceLabel extends Label {
    String describe;
    String minPrice;
    String maxPrice;

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.describe);
        dest.writeString(this.minPrice);
        dest.writeString(this.maxPrice);
    }

    public DeskPriceLabel() {}

    protected DeskPriceLabel(Parcel in) {
        super(in);
        this.describe = in.readString();
        this.minPrice = in.readString();
        this.maxPrice = in.readString();
    }

    public static final Creator<DeskPriceLabel> CREATOR = new Creator<DeskPriceLabel>() {
        @Override
        public DeskPriceLabel createFromParcel(Parcel source) {return new DeskPriceLabel(source);}

        @Override
        public DeskPriceLabel[] newArray(int size) {return new DeskPriceLabel[size];}
    };
}
