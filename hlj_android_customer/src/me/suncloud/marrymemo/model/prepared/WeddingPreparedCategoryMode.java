package me.suncloud.marrymemo.model.prepared;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 备婚清单分类数据是id和name,但由于服务器数据是写死的，没有查表，用一个内部类
 * Created by jinxin on 2017/9/27 0027.
 */

public class WeddingPreparedCategoryMode implements Parcelable {
    long id;
    String name;

    public WeddingPreparedCategoryMode(long id, String name) {
        this.id = id;
        this.name = name;
    }

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

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
    }

    protected WeddingPreparedCategoryMode(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<WeddingPreparedCategoryMode> CREATOR = new Parcelable
            .Creator<WeddingPreparedCategoryMode>() {
        @Override
        public WeddingPreparedCategoryMode createFromParcel(Parcel source) {
            return new WeddingPreparedCategoryMode(source);
        }

        @Override
        public WeddingPreparedCategoryMode[] newArray(int size) {return new WeddingPreparedCategoryMode[size];}
    };
}
