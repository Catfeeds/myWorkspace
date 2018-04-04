package com.hunliji.hljnotelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/5/10.
 * 笔记特殊的标签
 */
public class NoteCategoryMark implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName("mark_id")
    private long markId;
    @SerializedName("name")
    private String name;

    public long getId() {
        return id;
    }

    public long getMarkId() {
        return markId;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.markId);
        dest.writeString(this.name);
    }

    public NoteCategoryMark() {}

    protected NoteCategoryMark(Parcel in) {
        this.id = in.readLong();
        this.markId = in.readLong();
        this.name = in.readString();
    }

    public static final Creator<NoteCategoryMark> CREATOR = new Creator<NoteCategoryMark>() {
        @Override
        public NoteCategoryMark createFromParcel(Parcel source) {
            return new NoteCategoryMark(source);
        }

        @Override
        public NoteCategoryMark[] newArray(int size) {return new NoteCategoryMark[size];}
    };
}