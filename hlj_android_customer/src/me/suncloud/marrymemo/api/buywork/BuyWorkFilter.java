package me.suncloud.marrymemo.api.buywork;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 筛选fliter
 * Created by jinxin on 2016/12/6 0006.
 */

public class BuyWorkFilter implements Parcelable {
    double priceMax;
    double priceMin;
    List<String> tags;//分开

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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.priceMax);
        dest.writeDouble(this.priceMin);
        dest.writeStringList(this.tags);
    }

    public BuyWorkFilter() {}

    protected BuyWorkFilter(Parcel in) {
        this.priceMax = in.readDouble();
        this.priceMin = in.readDouble();
        this.tags = in.createStringArrayList();
    }

    public static final Creator<BuyWorkFilter> CREATOR = new Creator<BuyWorkFilter>() {
        @Override
        public BuyWorkFilter createFromParcel(Parcel source) {return new BuyWorkFilter(source);}

        @Override
        public BuyWorkFilter[] newArray(int size) {return new BuyWorkFilter[size];}
    };
}
