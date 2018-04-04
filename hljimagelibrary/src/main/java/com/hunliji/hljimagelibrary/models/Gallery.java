package com.hunliji.hljimagelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Suncloud on 2016/8/22.
 */
public class Gallery implements Parcelable {

    public static final int ALL_VIDEO_ID=-1;

    private long id;
    private String name;
    private int photoCount;
    private String path;
    private boolean isSelected;

    public Gallery(long id, String name, int photoCount, String path) {
        super();
        this.id = id;
        this.name = name;
        this.photoCount = photoCount;
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public String getPath() {
        return path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.photoCount);
        dest.writeString(this.path);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected Gallery(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.photoCount = in.readInt();
        this.path = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<Gallery> CREATOR = new Creator<Gallery>() {
        @Override
        public Gallery createFromParcel(Parcel source) {return new Gallery(source);}

        @Override
        public Gallery[] newArray(int size) {return new Gallery[size];}
    };
}
