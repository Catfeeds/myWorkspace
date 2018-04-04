package com.hunliji.hljcommonlibrary.models.realm;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suncloud on 2016/10/11.
 */

public class WSMedia implements Parcelable {
    private String path;
    private int height;
    private int width;
    @SerializedName("voice_duration")
    private double voiceDuration;

    public WSMedia(){}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public double getVoiceDuration() {
        return voiceDuration;
    }

    public void setVoiceDuration(double voiceDuration) {
        this.voiceDuration = voiceDuration;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeInt(this.height);
        dest.writeInt(this.width);
        dest.writeDouble(this.voiceDuration);
    }

    protected WSMedia(Parcel in) {
        this.path = in.readString();
        this.height = in.readInt();
        this.width = in.readInt();
        this.voiceDuration = in.readDouble();
    }

    public static final Creator<WSMedia> CREATOR = new Creator<WSMedia>() {
        @Override
        public WSMedia createFromParcel(Parcel source) {return new WSMedia(source);}

        @Override
        public WSMedia[] newArray(int size) {return new WSMedia[size];}
    };
}
