package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 七牛图片上传本地服务器的Photo
 * Created by jinxin on 2018/3/13 0013.
 */

public class JsonPhoto implements Parcelable {
    @SerializedName("image_path")
    private String imagePath;
    private int width;
    private int height;

    public JsonPhoto(Photo photo) {
        if (photo != null) {
            this.imagePath = photo.getImagePath();
            this.width = photo.getWidth();
            this.height = photo.getHeight();
        }
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imagePath);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    public JsonPhoto() {}

    protected JsonPhoto(Parcel in) {
        this.imagePath = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Parcelable.Creator<JsonPhoto> CREATOR = new Parcelable.Creator<JsonPhoto>
            () {
        @Override
        public JsonPhoto createFromParcel(Parcel source) {return new JsonPhoto(source);}

        @Override
        public JsonPhoto[] newArray(int size) {return new JsonPhoto[size];}
    };
}
