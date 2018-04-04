package com.hunliji.hljcommonlibrary.models.realm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.annotations.Ignore;

/**
 * Created by Suncloud on 2016/10/11.
 */

public class WSProduct implements Parcelable {
    private long id;
    @SerializedName("cover_path")
    private String coverPath;
    private String title;
    @SerializedName("actual_price")
    private double actualPrice;
    @SerializedName("cover_height")
    private int height;
    @SerializedName("cover_width")
    private int width;
    @Ignore
    private String kind;

    public WSProduct() {}

    public WSProduct(String kind) {this.kind = kind;}

    protected WSProduct(Parcel in) {
        id = in.readLong();
        coverPath = in.readString();
        title = in.readString();
        actualPrice = in.readDouble();
        height = in.readInt();
        width = in.readInt();
        kind = in.readString();
    }

    public static final Creator<WSProduct> CREATOR = new Creator<WSProduct>() {
        @Override
        public WSProduct createFromParcel(Parcel in) {
            return new WSProduct(in);
        }

        @Override
        public WSProduct[] newArray(int size) {
            return new WSProduct[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(coverPath);
        dest.writeString(title);
        dest.writeDouble(actualPrice);
        dest.writeInt(height);
        dest.writeInt(width);
        dest.writeString(kind);
    }
}
