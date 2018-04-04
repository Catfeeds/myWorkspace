package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/7/27.
 * 一个相对比较通用的类,拥有id和key, value, name几个较为普遍的属性,比如property
 */
public class Label implements Parcelable {
    @SerializedName(value = "id", alternate = {"key"})
    private long id;
    @SerializedName(value = "name", alternate = {"title", "area_name","label"})
    private String name;
    private String value;
    private transient boolean isSelected;

    public Label() {}

    public Label(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        dest.writeString(this.value);
    }

    protected Label(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.value = in.readString();
    }

    public static final Creator<Label> CREATOR = new Creator<Label>() {
        @Override
        public Label createFromParcel(Parcel source) {return new Label(source);}

        @Override
        public Label[] newArray(int size) {return new Label[size];}
    };
}