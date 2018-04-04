package com.hunliji.hljkefulibrary.moudles;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/10/19.
 */

public class EMTrack implements Parcelable{

    public static final String TRACK_TYPE_WORK = "trackTypeWork";
    public static final String TRACK_TYPE_SHOP_PRODUCT = "trackTypeProduct";
    public static final String TRACK_TYPE_CAR_PRODUCT = "trackTypeCar";
    public static final String TRACK_TYPE_HOTEL_V2 = "trackTypeHotelV2";
    public static final String TRACK_TYPE_PRODUCT_ORDER = "trackTypeProductOrder";
    public static final String TRACK_TYPE_CAR_ORDER = "trackTypeCarOrder";
    public static final String TRACK_TYPE_HOTEL = "trackTypeHotel";

    @SerializedName("title")
    private String title;
    @SerializedName("desc")
    private String desc;
    @SerializedName("price")
    private String priceStr;
    @SerializedName("img_url")
    private String imagePath;
    @SerializedName("item_url")
    private String linkUrl;
    @SerializedName("trackType")
    private String trackType;
    @SerializedName("trackImageWidth")
    private int trackImageWidth;
    @SerializedName("trackImageHeight")
    private int trackImageHeight;
    @SerializedName("trackId")
    private long trackId;

    public EMTrack() {
    }

    protected EMTrack(Parcel in) {
        title = in.readString();
        desc = in.readString();
        priceStr = in.readString();
        imagePath = in.readString();
        linkUrl = in.readString();
        trackType = in.readString();
        trackImageWidth = in.readInt();
        trackImageHeight = in.readInt();
        trackId = in.readLong();
    }

    public static final Creator<EMTrack> CREATOR = new Creator<EMTrack>() {
        @Override
        public EMTrack createFromParcel(Parcel in) {
            return new EMTrack(in);
        }

        @Override
        public EMTrack[] newArray(int size) {
            return new EMTrack[size];
        }
    };

    public String getTrackType() {
        return trackType;
    }

    public long getTrackId() {
        return trackId;
    }

    public String getTitle() {
        return title;
    }

    public String getPriceStr() {
        return priceStr;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getTrackImageWidth() {
        return trackImageWidth;
    }

    public int getTrackImageHeight() {
        return trackImageHeight;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPriceStr(String priceStr) {
        this.priceStr = priceStr;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public void setTrackType(String trackType) {
        this.trackType = trackType;
    }

    public void setTrackImageWidth(int trackImageWidth) {
        this.trackImageWidth = trackImageWidth;
    }

    public void setTrackImageHeight(int trackImageHeight) {
        this.trackImageHeight = trackImageHeight;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(priceStr);
        dest.writeString(imagePath);
        dest.writeString(linkUrl);
        dest.writeString(trackType);
        dest.writeInt(trackImageWidth);
        dest.writeInt(trackImageHeight);
        dest.writeLong(trackId);
    }
}
