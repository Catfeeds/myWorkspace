package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by werther on 16/7/27.
 * 动态配置的图片model,可以用于商家保证金图标获取等等
 */
public class IconSign implements Parcelable {
    String url;
    int width;
    int height;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public IconSign() {}

    protected IconSign(Parcel in) {
        this.url = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<IconSign> CREATOR = new Parcelable.Creator<IconSign>() {
        @Override
        public IconSign createFromParcel(Parcel source) {return new IconSign(source);}

        @Override
        public IconSign[] newArray(int size) {return new IconSign[size];}
    };
}
