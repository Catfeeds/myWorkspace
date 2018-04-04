package com.hunliji.hljcarlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mo_yu on 2017/12/27.婚车品牌
 */

public class Brand implements Parcelable {

    private long id;
    private String logo;
    private String title;

    public long getId() {
        return id;
    }

    public String getLogo() {
        return logo;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.logo);
        dest.writeString(this.title);
    }

    public Brand() {}

    protected Brand(Parcel in) {
        this.id = in.readLong();
        this.logo = in.readString();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<Brand> CREATOR = new Parcelable.Creator<Brand>() {
        @Override
        public Brand createFromParcel(Parcel source) {return new Brand(source);}

        @Override
        public Brand[] newArray(int size) {return new Brand[size];}
    };
}
