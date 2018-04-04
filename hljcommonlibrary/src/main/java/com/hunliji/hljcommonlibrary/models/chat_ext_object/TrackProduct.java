package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

/**
 * Created by wangtao on 2017/8/22.
 */

public class TrackProduct implements Parcelable{
    @SerializedName("id")
    private long id;
    @SerializedName("title")
    private String title;
    @SerializedName("cover_image")
    private CoverImage coverImage;
    @SerializedName("market_price")
    private double marketPrice;
    @SerializedName("show_price")
    private double showPrice;

    public TrackProduct(
            long id, String title, Photo photo, double showPrice, double marketPrice) {
        this.id = id;
        this.title = title;
        this.coverImage = new CoverImage(photo);
        this.marketPrice = marketPrice;
        this.showPrice = showPrice;
    }

    public long getId() {

        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverPath() {
        return coverImage==null?null:coverImage.getImagePath();
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getShowPrice() {
        return showPrice;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeParcelable(this.coverImage, flags);
        dest.writeDouble(this.marketPrice);
        dest.writeDouble(this.showPrice);
    }

    protected TrackProduct(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.coverImage = in.readParcelable(CoverImage.class.getClassLoader());
        this.marketPrice = in.readDouble();
        this.showPrice = in.readDouble();
    }

    public static final Creator<TrackProduct> CREATOR = new Creator<TrackProduct>() {
        @Override
        public TrackProduct createFromParcel(Parcel source) {return new TrackProduct(source);}

        @Override
        public TrackProduct[] newArray(int size) {return new TrackProduct[size];}
    };
}
