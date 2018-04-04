package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Suncloud on 2016/8/19.
 */
public class City implements Parcelable {


    private long cid;
    private String name;
    @SerializedName("pinyin")
    private String pinYin;
    @SerializedName("short_py")
    private String shortPY;
    private String letter;


    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    public String getShortPY() {
        return shortPY;
    }

    public void setShortPY(String shortPY) {
        this.shortPY = shortPY;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.cid);
        dest.writeString(this.name);
        dest.writeString(this.pinYin);
        dest.writeString(this.shortPY);
        dest.writeString(this.letter);
    }

    public City() {}

    protected City(Parcel in) {
        this.cid = in.readLong();
        this.name = in.readString();
        this.pinYin = in.readString();
        this.shortPY = in.readString();
        this.letter = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {return new City(source);}

        @Override
        public City[] newArray(int size) {return new City[size];}
    };
}
