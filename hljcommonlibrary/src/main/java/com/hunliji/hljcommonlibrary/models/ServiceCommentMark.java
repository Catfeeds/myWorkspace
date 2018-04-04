package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 套餐评价标签列表（新）
 * Created by chen_bin on 2017/9/26 0026.
 */
public class ServiceCommentMark implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "comments_count")
    private int commentsCount;

    public transient final static long ID_ALL = 0; //全部
    public transient final static long ID_HAS_PHOTOS = -1; //有图
    public transient final static long ID_GOOD_REPUTATION = -2; //好评
    public transient final static long ID_BAD_REPUTATION = -3;//待改善

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public String getCommentsCountStr() {
        if (id == ID_ALL) {
            return "";
        }
        return commentsCount > 999 ? "999+" : String.valueOf(commentsCount);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.commentsCount);
    }

    public ServiceCommentMark() {}

    protected ServiceCommentMark(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.commentsCount = in.readInt();
    }

    public static final Creator<ServiceCommentMark> CREATOR = new
            Creator<ServiceCommentMark>() {
        @Override
        public ServiceCommentMark createFromParcel(Parcel source) {
            return new ServiceCommentMark(source);
        }

        @Override
        public ServiceCommentMark[] newArray(int size) {
            return new ServiceCommentMark[size];
        }
    };

}
