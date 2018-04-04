package com.hunliji.marrybiz.model.leaflets;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;

import java.util.List;

/**
 * 活动报名model
 * Created by jinxin on 2017/5/24 0024.
 */

public class EventSignUp implements Parcelable {
    @SerializedName(value = "activity")
    EventInfo eventInfo;
    @SerializedName(value = "list")
    List<EventSource> eventSources;
    @SerializedName(value = "page_count")
    int pageCount;
    @SerializedName(value = "total_count")
    int totalCount;

    public EventInfo getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public List<EventSource> getEventSources() {
        return eventSources;
    }

    public void setEventSources(List<EventSource> eventSources) {
        this.eventSources = eventSources;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.eventInfo, flags);
        dest.writeTypedList(this.eventSources);
        dest.writeInt(this.pageCount);
        dest.writeInt(this.totalCount);
    }

    public EventSignUp() {}

    protected EventSignUp(Parcel in) {
        this.eventInfo = in.readParcelable(EventInfo.class.getClassLoader());
        this.eventSources = in.createTypedArrayList(EventSource.CREATOR);
        this.pageCount = in.readInt();
        this.totalCount = in.readInt();
    }

    public static final Parcelable.Creator<EventSignUp> CREATOR = new Parcelable
            .Creator<EventSignUp>() {
        @Override
        public EventSignUp createFromParcel(Parcel source) {return new EventSignUp(source);}

        @Override
        public EventSignUp[] newArray(int size) {return new EventSignUp[size];}
    };
}
