package me.suncloud.marrymemo.model.community;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2018/3/20.poster点击量
 */

public class PosterWatchFeed implements Parcelable {
    public static final String SAME_CITY_TYPE = "same_city";
    public static final String QA_TYPE = "qa";

    @SerializedName("today_watch_count")
    int todayWatchCount;
    String type;

    public int getTodayWatchCount() {
        return todayWatchCount;
    }

    public String getType() {
        return type;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.todayWatchCount);
        dest.writeString(this.type);
    }

    public PosterWatchFeed() {}

    protected PosterWatchFeed(Parcel in) {
        this.todayWatchCount = in.readInt();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<PosterWatchFeed> CREATOR = new Parcelable
            .Creator<PosterWatchFeed>() {
        @Override
        public PosterWatchFeed createFromParcel(Parcel source) {return new PosterWatchFeed(source);}

        @Override
        public PosterWatchFeed[] newArray(int size) {return new PosterWatchFeed[size];}
    };
}
