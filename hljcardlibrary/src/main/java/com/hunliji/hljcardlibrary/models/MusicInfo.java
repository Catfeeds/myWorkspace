package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class MusicInfo implements Parcelable {

    private Long id;
    private String name;
    private long duration;
    private String url;
    private float size;

    public MusicInfo(Long id, String name, String path, long duration, int size) {
        super();
        this.id = id;
        this.name = name;
        this.url = path;
        this.duration = duration;
        float mSize = (float) size / (1024 * 1024);
        BigDecimal b = new BigDecimal(mSize);
        this.size = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public Long getId() {
        return id;
    }

    public long getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public float getSize() {
        return size;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.duration);
        dest.writeString(this.url);
        dest.writeFloat(this.size);
    }

    protected MusicInfo(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.duration = in.readLong();
        this.url = in.readString();
        this.size = in.readFloat();
    }

    public static final Parcelable.Creator<MusicInfo> CREATOR = new Parcelable.Creator<MusicInfo>
            () {
        @Override
        public MusicInfo createFromParcel(Parcel source) {return new MusicInfo(source);}

        @Override
        public MusicInfo[] newArray(int size) {return new MusicInfo[size];}
    };
}