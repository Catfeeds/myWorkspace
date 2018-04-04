package me.suncloud.marrymemo.model.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2016/12/7.
 */

public class FeedList implements Parcelable {

    @SerializedName("page_count")
    private int pageCount;
    @SerializedName("new_count")
    private int newCount;
    @SerializedName("last_time")
    private long lastTime;

    public int getPageCount() {
        return pageCount;
    }

    public int getNewCount() {
        return newCount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pageCount);
        dest.writeInt(this.newCount);
        dest.writeLong(this.lastTime);
    }

    public FeedList() {}

    protected FeedList(Parcel in) {
        this.pageCount = in.readInt();
        this.newCount = in.readInt();
        this.lastTime = in.readLong();
    }

    public static final Creator<FeedList> CREATOR = new Creator<FeedList>() {
        @Override
        public FeedList createFromParcel(Parcel source) {return new FeedList(source);}

        @Override
        public FeedList[] newArray(int size) {return new FeedList[size];}
    };
}
