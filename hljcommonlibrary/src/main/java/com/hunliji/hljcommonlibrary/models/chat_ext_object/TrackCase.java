package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/8/22.
 */

public class TrackCase implements Parcelable{
    @SerializedName("id")
    private long id;
    @SerializedName("title")
    private String title;
    @SerializedName("cover_path")
    private String coverPath;

    public TrackCase(
            long id, String title, String coverPath) {
        this.id = id;
        this.title = title;
        this.coverPath = coverPath;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverPath() {
        return coverPath;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.coverPath);
    }

    protected TrackCase(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.coverPath = in.readString();
    }

    public static final Creator<TrackCase> CREATOR = new Creator<TrackCase>() {
        @Override
        public TrackCase createFromParcel(Parcel source) {return new TrackCase(source);}

        @Override
        public TrackCase[] newArray(int size) {return new TrackCase[size];}
    };
}
