package com.hunliji.marrybiz.model.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Work;

/**
 * Created by mo_yu on 2017/2/4.推荐套餐
 */

public class RecommendWork implements Parcelable {

    private long id;
    private int seat;//套餐位置，只允许1,2,3
    @SerializedName(value = "set_meal")
    private Work work;

    public long getId() {
        return id;
    }

    public int getSeat() {
        return seat;
    }

    public Work getWork() {
        return work;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.seat);
        dest.writeParcelable(this.work, flags);
    }

    public RecommendWork() {}

    protected RecommendWork(Parcel in) {
        this.id = in.readLong();
        this.seat = in.readInt();
        this.work = in.readParcelable(Work.class.getClassLoader());
    }

    public static final Creator<RecommendWork> CREATOR = new Creator<RecommendWork>() {
        @Override
        public RecommendWork createFromParcel(Parcel source) {return new RecommendWork(source);}

        @Override
        public RecommendWork[] newArray(int size) {return new RecommendWork[size];}
    };
}
