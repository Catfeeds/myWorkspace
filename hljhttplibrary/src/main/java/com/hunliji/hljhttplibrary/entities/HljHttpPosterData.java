package com.hunliji.hljhttplibrary.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.hunliji.hljcommonlibrary.models.Poster;

/**
 * Created by hua_rong on 2017/6/1.
 * 用于返回婚礼保 空数据时的poster节点--->去购买
 */

public class HljHttpPosterData<T> extends HljHttpData<T> implements Parcelable {

    Poster poster;

    public Poster getPoster() {
        return poster;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeParcelable(this.poster, flags);}

    public HljHttpPosterData() {}

    protected HljHttpPosterData(Parcel in) {
        this.poster = in.readParcelable(Poster.class.getClassLoader());
    }

    public static final Parcelable.Creator<HljHttpPosterData> CREATOR = new Parcelable
            .Creator<HljHttpPosterData>() {
        @Override
        public HljHttpPosterData createFromParcel(Parcel source) {
            return new HljHttpPosterData(source);
        }

        @Override
        public HljHttpPosterData[] newArray(int size) {return new HljHttpPosterData[size];}
    };
}
