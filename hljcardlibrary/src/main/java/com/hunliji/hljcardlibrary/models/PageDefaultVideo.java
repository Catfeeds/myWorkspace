package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/6/22.
 */

public class PageDefaultVideo implements Parcelable {

    @SerializedName("video_path")
    private String path;
    @SerializedName("video_width")
    private int width;
    @SerializedName("video_height")
    private int height;
    @SerializedName("persistent_id")
    private String persistentId;

    public String getPath() {
        return path;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPersistentId() {
        return persistentId;
    }

    protected PageDefaultVideo(Parcel in) {
        path = in.readString();
        width = in.readInt();
        height = in.readInt();
        persistentId = in.readString();
    }

    public static final Creator<PageDefaultVideo> CREATOR = new Creator<PageDefaultVideo>() {
        @Override
        public PageDefaultVideo createFromParcel(Parcel in) {
            return new PageDefaultVideo(in);
        }

        @Override
        public PageDefaultVideo[] newArray(int size) {
            return new PageDefaultVideo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeString(persistentId);
    }
}
