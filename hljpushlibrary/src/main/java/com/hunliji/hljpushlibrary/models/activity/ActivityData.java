package com.hunliji.hljpushlibrary.models.activity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;

/**
 * Created by luohanlin on 2018/3/16.
 * 首页活动弹窗使用的数据Model
 */

public class ActivityData implements Parcelable {
    @SerializedName("log_id")
    private long logId;
    @SerializedName(value = "finder_activity")
    EventInfo finderActivity;

    public long getLogId() {
        return logId;
    }

    public EventInfo getFinderActivity() {
        return finderActivity;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.logId);
        dest.writeParcelable(this.finderActivity, flags);
    }

    public ActivityData() {}

    protected ActivityData(Parcel in) {
        this.logId = in.readLong();
        this.finderActivity = in.readParcelable(EventInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<ActivityData> CREATOR = new Parcelable
            .Creator<ActivityData>() {
        @Override
        public ActivityData createFromParcel(Parcel source) {return new ActivityData(source);}

        @Override
        public ActivityData[] newArray(int size) {return new ActivityData[size];}
    };
}
