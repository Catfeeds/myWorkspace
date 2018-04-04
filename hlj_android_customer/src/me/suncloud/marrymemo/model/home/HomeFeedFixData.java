package me.suncloud.marrymemo.model.home;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by luohanlin on 2017/4/20.
 */

public class HomeFeedFixData implements Parcelable {
    private List<HomeFeed> list;

    public List<HomeFeed> getList() {
        return list;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeTypedList(this.list);}

    public HomeFeedFixData() {}

    protected HomeFeedFixData(Parcel in) {this.list = in.createTypedArrayList(HomeFeed.CREATOR);}

    public static final Parcelable.Creator<HomeFeedFixData> CREATOR = new Parcelable
            .Creator<HomeFeedFixData>() {
        @Override
        public HomeFeedFixData createFromParcel(Parcel source) {return new HomeFeedFixData(source);}

        @Override
        public HomeFeedFixData[] newArray(int size) {return new HomeFeedFixData[size];}
    };
}
