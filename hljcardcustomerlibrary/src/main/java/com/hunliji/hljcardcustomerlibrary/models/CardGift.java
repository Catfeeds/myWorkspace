package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/2/7 请帖礼物
 */

public class CardGift implements Parcelable {

    @SerializedName(value = "desc")
    String desc;
    @SerializedName(value = "icon")
    String icon;
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "price")
    double price;
    @SerializedName(value = "title")
    String title;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.desc);
        dest.writeString(this.icon);
        dest.writeLong(this.id);
        dest.writeDouble(this.price);
        dest.writeString(this.title);
    }

    public CardGift() {}

    protected CardGift(Parcel in) {
        this.desc = in.readString();
        this.icon = in.readString();
        this.id = in.readLong();
        this.price = in.readDouble();
        this.title = in.readString();
    }

    public static final Creator<CardGift> CREATOR = new Creator<CardGift>() {
        @Override
        public CardGift createFromParcel(Parcel source) {return new CardGift(source);}

        @Override
        public CardGift[] newArray(int size) {return new CardGift[size];}
    };
}
