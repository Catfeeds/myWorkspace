package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.HljCommon;

/**
 * 视频
 * Created by chen_bin on 2017/3/14 0014.
 */
public class Video implements Parcelable {
    @SerializedName(value = "persistent_id")
    private String persistentId;
    @SerializedName(value = "origin_path")
    private String originPath;
    @SerializedName(value = "persistent_path")
    private Persistent persistent;
    @SerializedName(value = "width")
    private int width;
    @SerializedName(value = "height")
    private int height;
    @SerializedName(value = "duration")
    private Float duration;

    public Video(Photo photo) {
        this.height = photo.getHeight();
        this.width = photo.getWidth();
        this.duration = (float) photo.getDuration() / 1000;
        this.localPath = photo.getImagePath();
    }

    private transient String localPath;

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public Persistent getPersistent() {
        return persistent;
    }

    public void setPersistent(Persistent persistent) {
        this.persistent = persistent;
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

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public float getDuration() {
        if (duration == null) {
            return 0;
        }
        return duration;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Video() {}

    public String getScreenShot() {
        if (persistent != null && !TextUtils.isEmpty(persistent.getScreenShot())) {
            return persistent.getScreenShot();
        } else if (!TextUtils.isEmpty(originPath)) {
            return originPath + HljCommon.QINIU.SCREEN_SHOT_URL_1_SECONDS;
        }
        return null;
    }

    public String getVideoPath() {
        if (persistent == null) {
            return originPath;
        } else if (!TextUtils.isEmpty(persistent.getStreamingPhone())) { //m3u8_640_480
            return persistent.getStreamingPhone();
        } else if (!TextUtils.isEmpty(persistent.getIphone())) {
            return persistent.getIphone();
        } else {
            return originPath;
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.persistentId);
        dest.writeString(this.originPath);
        dest.writeParcelable(this.persistent, flags);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeValue(this.duration);
    }

    protected Video(Parcel in) {
        this.persistentId = in.readString();
        this.originPath = in.readString();
        this.persistent = in.readParcelable(Persistent.class.getClassLoader());
        this.width = in.readInt();
        this.height = in.readInt();
        this.duration = (Float) in.readValue(Float.class.getClassLoader());
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {return new Video(source);}

        @Override
        public Video[] newArray(int size) {return new Video[size];}
    };
}
