package com.hunliji.hljimagelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Suncloud on 2016/8/22.
 */
public class Size implements Parcelable {
    private int width;
    private int height;
    private long duration;

    public Size(int width, int height) {
        super();
        this.width = width;
        this.height = height;
    }

    public Size(int width, int height, long duration) {
        this.width = width;
        this.height = height;
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected Size(Parcel in) {
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<Size> CREATOR = new Creator<Size>() {
        @Override
        public Size createFromParcel(Parcel in) {
            return new Size(in);
        }

        @Override
        public Size[] newArray(int size) {
            return new Size[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
    }
}
