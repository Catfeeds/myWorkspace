package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/8/22.
 */

public class TrackMerchant implements Parcelable{
    @SerializedName("id")
    private long id;
    @SerializedName("user_id")
    private long userId;
    @SerializedName("name")
    private String name;
    @SerializedName("logo_path")
    private String logoPath;
    @SerializedName(value = "shop_type")
    private int shopType;

    public TrackMerchant(long id, long userId, String name, String logoPath, int shopType) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.logoPath = logoPath;
        this.shopType = shopType;
    }

    public long getId() {
        return id;
    }

    public int getShopType() {
        return shopType;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.logoPath);
        dest.writeInt(this.shopType);
    }

    protected TrackMerchant(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.name = in.readString();
        this.logoPath = in.readString();
        this.shopType = in.readInt();
    }

    public static final Creator<TrackMerchant> CREATOR = new Creator<TrackMerchant>() {
        @Override
        public TrackMerchant createFromParcel(Parcel source) {return new TrackMerchant(source);}

        @Override
        public TrackMerchant[] newArray(int size) {return new TrackMerchant[size];}
    };
}
