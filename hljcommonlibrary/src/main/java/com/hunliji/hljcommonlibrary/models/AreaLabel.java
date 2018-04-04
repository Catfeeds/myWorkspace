package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/8/1.通过cid获取所在市，省
 */

public class AreaLabel extends Label {
    @SerializedName("cid")
    private long cid;
    @SerializedName("level")
    private int level;
    @SerializedName("parent_id")
    private long parentId;

    public long getCid() {
        return cid;
    }

    public int getLevel() {
        return level;
    }

    public long getParentId() {
        return parentId;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.cid);
        dest.writeInt(this.level);
        dest.writeLong(this.parentId);
    }

    public AreaLabel() {}

    protected AreaLabel(Parcel in) {
        this.cid = in.readLong();
        this.level = in.readInt();
        this.parentId = in.readLong();
    }

    public static final Parcelable.Creator<AreaLabel> CREATOR = new Parcelable.Creator<AreaLabel>
            () {
        @Override
        public AreaLabel createFromParcel(Parcel source) {return new AreaLabel(source);}

        @Override
        public AreaLabel[] newArray(int size) {return new AreaLabel[size];}
    };
}
