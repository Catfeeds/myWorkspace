package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;

/**
 * media
 * Created by chen_bin on 2017/6/27 0027.
 */
public class Media implements Parcelable {
    @SerializedName(value = "photo")
    private Photo photo;
    @SerializedName(value = "video")
    private Video video;
    @SerializedName(value = "type")
    private int type;

    public transient static final int TYPE_PHOTO = 1; //图片类
    public transient static final int TYPE_VIDEO = 2; //视频类

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Photo getPhoto() {
        if (photo == null) {
            photo = new Photo();
        }
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Video getVideo() {
        if (video == null) {
            video = new Video();
        }
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.photo, flags);
        dest.writeParcelable(this.video, flags);
        dest.writeInt(this.type);
    }

    public Media() {}

    protected Media(Parcel in) {
        this.photo = in.readParcelable(Photo.class.getClassLoader());
        this.video = in.readParcelable(Video.class.getClassLoader());
        this.type = in.readInt();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {return new Media(source);}

        @Override
        public Media[] newArray(int size) {return new Media[size];}
    };
}
