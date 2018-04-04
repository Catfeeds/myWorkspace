package com.hunliji.marrybiz.model.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 活动范例
 * Created by chen_bin on 2017/2/8 0008.
 */
public class EventExample implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = " finder_activity_id")
    private long finderActivityId;
    @SerializedName(value = "activity")
    private String title;
    @SerializedName(value = "url")
    private String url;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.finderActivityId);
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    public EventExample() {}

    protected EventExample(Parcel in) {
        this.id = in.readLong();
        this.finderActivityId = in.readLong();
        this.title = in.readString();
        this.url = in.readString();
    }

    public static final Creator<EventExample> CREATOR = new Creator<EventExample>() {
        @Override
        public EventExample createFromParcel(Parcel source) {return new EventExample(source);}

        @Override
        public EventExample[] newArray(int size) {return new EventExample[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getFinderActivityId() {
        return finderActivityId;
    }

    public void setFinderActivityId(long finderActivityId) {
        this.finderActivityId = finderActivityId;
    }
}
