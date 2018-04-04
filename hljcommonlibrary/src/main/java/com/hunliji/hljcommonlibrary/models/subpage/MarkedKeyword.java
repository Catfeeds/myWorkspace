package com.hunliji.hljcommonlibrary.models.subpage;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 *发现页推荐标签跟keyword组合的model
 * Created by chen_bin on 2017/5/10 0010.
 */
public class MarkedKeyword implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "mark_id")
    private long markId;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "color")
    private String color;

    public long getId() {
        return id;
    }

    public long getMarkId() {
        return markId;
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.markId);
        dest.writeString(this.title);
        dest.writeString(this.color);
    }

    public MarkedKeyword() {}

    protected MarkedKeyword(Parcel in) {
        this.id = in.readLong();
        this.markId = in.readLong();
        this.title = in.readString();
        this.color = in.readString();
    }

    public static final Creator<MarkedKeyword> CREATOR = new Creator<MarkedKeyword>() {
        @Override
        public MarkedKeyword createFromParcel(Parcel source) {return new MarkedKeyword(source);}

        @Override
        public MarkedKeyword[] newArray(int size) {return new MarkedKeyword[size];}
    };
}
