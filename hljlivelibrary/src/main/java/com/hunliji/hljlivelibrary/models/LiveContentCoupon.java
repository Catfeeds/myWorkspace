package com.hunliji.hljlivelibrary.models;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

/**
 * Created by luohanlin on 2017/11/6.
 */

public class LiveContentCoupon extends LiveContent {
    private long id;
    private String title;
    @SerializedName("money_sill")
    private double moneySill;
    private double value;
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("provided_count")
    private int providedCount;
    private int type;

    public static LiveContent parseLiveContentCoupon(String contentStr) {
        LiveContentCoupon liveContent = GsonUtil.getGsonInstance()
                .fromJson(contentStr, LiveContentCoupon.class);

        return liveContent;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCashCoupon() {
        if (type == 1) {
            return false;
        }

        return true;
    }

    public double getMoneySill() {
        return moneySill;
    }

    public double getValue() {
        return value;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getProvidedCount() {
        return providedCount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeDouble(this.moneySill);
        dest.writeDouble(this.value);
        dest.writeInt(this.totalCount);
        dest.writeInt(this.providedCount);
        dest.writeInt(this.type);
    }

    public LiveContentCoupon() {}

    protected LiveContentCoupon(Parcel in) {
        super(in);
        this.id = in.readLong();
        this.title = in.readString();
        this.moneySill = in.readDouble();
        this.value = in.readDouble();
        this.totalCount = in.readInt();
        this.providedCount = in.readInt();
        this.type = in.readInt();
    }

    public static final Creator<LiveContentCoupon> CREATOR = new Creator<LiveContentCoupon>() {
        @Override
        public LiveContentCoupon createFromParcel(Parcel source) {
            return new LiveContentCoupon(source);
        }

        @Override
        public LiveContentCoupon[] newArray(int size) {return new LiveContentCoupon[size];}
    };
}
