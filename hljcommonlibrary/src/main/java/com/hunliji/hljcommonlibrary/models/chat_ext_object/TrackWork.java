package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/8/22.
 */

public class TrackWork implements Parcelable{
    @SerializedName("id")
    private long id;
    @SerializedName("title")
    private String title;
    @SerializedName("cover_path")
    private String coverPath;
    @SerializedName("market_price")
    private double marketPrice;
    @SerializedName("show_price")
    private double showPrice;

    public TrackWork(
            long id, String title, String coverPath, double showPrice, double marketPrice) {
        this.id = id;
        this.title = title;
        this.coverPath = coverPath;
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
        return coverPath;
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
        dest.writeString(this.coverPath);
        dest.writeDouble(this.marketPrice);
        dest.writeDouble(this.showPrice);
    }

    protected TrackWork(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.coverPath = in.readString();
        this.marketPrice = in.readDouble();
        this.showPrice = in.readDouble();
    }

    public static final Creator<TrackWork> CREATOR = new Creator<TrackWork>() {
        @Override
        public TrackWork createFromParcel(Parcel source) {return new TrackWork(source);}

        @Override
        public TrackWork[] newArray(int size) {return new TrackWork[size];}
    };
}
