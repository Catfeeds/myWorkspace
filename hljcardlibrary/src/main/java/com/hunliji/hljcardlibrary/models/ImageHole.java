package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/6/10.
 */

public class ImageHole implements Parcelable {

    private long id;

    @SerializedName("default_h5_hole_image_path")
    private String defaultH5ImagePath;
    @SerializedName("default_h5_hole_video")
    private PageDefaultVideo defaultH5HoleVideo;
    @SerializedName("mask_image_path")
    private String maskImagePath;

    @SerializedName("frame")
    private String frameStr;
    @SerializedName("support_video")
    private boolean isSupportVideo;
    @SerializedName("z_index")
    private int zIndex;

    public String getMaskImagePath() {
        return maskImagePath;
    }

    public HoleFrame getHoleFrame() {
        return new HoleFrame(frameStr);
    }

    public long getId() {
        return id;
    }

    public PageDefaultVideo getDefaultH5HoleVideo() {
        return defaultH5HoleVideo;
    }

    public String getDefaultH5ImagePath() {
        return defaultH5ImagePath;
    }

    public boolean isSupportVideo() {
        return isSupportVideo;
    }

    public int getzIndex() {
        return zIndex;
    }

    public ImageHole() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.defaultH5ImagePath);
        dest.writeParcelable(this.defaultH5HoleVideo, flags);
        dest.writeString(this.maskImagePath);
        dest.writeString(this.frameStr);
        dest.writeByte(this.isSupportVideo ? (byte) 1 : (byte) 0);
        dest.writeInt(this.zIndex);
    }

    protected ImageHole(Parcel in) {
        this.id = in.readLong();
        this.defaultH5ImagePath = in.readString();
        this.defaultH5HoleVideo = in.readParcelable(PageDefaultVideo.class.getClassLoader());
        this.maskImagePath = in.readString();
        this.frameStr = in.readString();
        this.isSupportVideo = in.readByte() != 0;
        this.zIndex = in.readInt();
    }

    public static final Creator<ImageHole> CREATOR = new Creator<ImageHole>() {
        @Override
        public ImageHole createFromParcel(Parcel source) {return new ImageHole(source);}

        @Override
        public ImageHole[] newArray(int size) {return new ImageHole[size];}
    };
}
