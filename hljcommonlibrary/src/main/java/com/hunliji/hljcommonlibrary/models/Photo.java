package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/7/27.
 * 婚礼纪通用的图片model
 */
public class Photo implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "image_path", alternate = {"path", "photo_path", "img", "url",
            "media_path", "cover"})
    private String imagePath;
    @SerializedName(value = "width")
    private int width;
    @SerializedName(value = "height")
    private int height;
    @SerializedName(value = "describe", alternate = {"description"})
    private String describe;
    private long bucketId;
    private long duration;
    private boolean isVideo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {this.width = width;}

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {this.height = height;}

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public long getBucketId() {
        return bucketId;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Photo() {}

    public Photo(String imagePath) {
        this.imagePath = imagePath;
    }

    public Photo(long id, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.imagePath);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.describe);
        dest.writeLong(this.bucketId);
        dest.writeLong(this.duration);
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
    }

    protected Photo(Parcel in) {
        this.id = in.readLong();
        this.imagePath = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.describe = in.readString();
        this.bucketId = in.readLong();
        this.duration = in.readLong();
        this.isVideo = in.readByte() != 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel source) {return new Photo(source);}

        @Override
        public Photo[] newArray(int size) {return new Photo[size];}
    };
}