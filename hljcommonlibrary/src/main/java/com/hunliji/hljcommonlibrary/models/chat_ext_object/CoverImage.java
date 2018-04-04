package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

/**
 * Created by wangtao on 2017/8/25.
 */

public class CoverImage implements Parcelable {

    @SerializedName("image_path")
    private String imagePath;
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;


    public CoverImage(Photo photo) {
        this.imagePath = photo.getImagePath();
        this.width = photo.getWidth();
        this.height = photo.getHeight();
    }
    public String getImagePath() {
        return imagePath;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imagePath);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public CoverImage() {}

    protected CoverImage(Parcel in) {
        this.imagePath = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<CoverImage> CREATOR = new Creator<CoverImage>() {
        @Override
        public CoverImage createFromParcel(Parcel source) {return new CoverImage(source);}

        @Override
        public CoverImage[] newArray(int size) {return new CoverImage[size];}
    };
}
