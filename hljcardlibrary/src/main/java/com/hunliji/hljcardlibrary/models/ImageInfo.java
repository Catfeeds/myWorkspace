package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.HljCommon;

/**
 * Created by wangtao on 2017/6/10.
 */

public class ImageInfo implements Parcelable {

    @SerializedName("image_hole_id")
    private long holeId;
    @SerializedName("h5_hole_image_path")
    private String h5ImagePath;
    @SerializedName("image_path")
    private String path;
    @SerializedName("video_height")
    private int videoHeight;
    @SerializedName("video_width")
    private int videoWidth;
    @SerializedName("is_video")
    private boolean isVideo;
    @SerializedName("persistent_id")
    private String persistentId;

    public long getHoleId() {
        return holeId;
    }

    public String getH5ImagePath() {
        return h5ImagePath;
    }

    public String getPath() {
        return path;
    }

    public ImageInfo(ImageHole hole) {
        this.holeId = hole.getId();
        if (!hole.isSupportVideo()) {
            this.h5ImagePath = hole.getDefaultH5ImagePath();
        } else {
            this.isVideo = true;
            if (hole.getDefaultH5HoleVideo() != null) {
                this.h5ImagePath = hole.getDefaultH5HoleVideo()
                        .getPath();
                this.path = hole.getDefaultH5HoleVideo()
                        .getPath();
                this.videoHeight = hole.getDefaultH5HoleVideo()
                        .getHeight();
                this.videoWidth = hole.getDefaultH5HoleVideo()
                        .getWidth();
                this.persistentId = hole.getDefaultH5HoleVideo()
                        .getPersistentId();
            }
        }
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public void setH5ImagePath(String h5ImagePath) {
        this.h5ImagePath = h5ImagePath;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.holeId);
        dest.writeString(this.h5ImagePath);
        dest.writeString(this.path);
        dest.writeInt(this.videoHeight);
        dest.writeInt(this.videoWidth);
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
        dest.writeString(this.persistentId);
    }

    protected ImageInfo(Parcel in) {
        this.holeId = in.readLong();
        this.h5ImagePath = in.readString();
        this.path = in.readString();
        this.videoHeight = in.readInt();
        this.videoWidth = in.readInt();
        this.isVideo = in.readByte() != 0;
        this.persistentId = in.readString();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel source) {return new ImageInfo(source);}

        @Override
        public ImageInfo[] newArray(int size) {return new ImageInfo[size];}
    };
}
