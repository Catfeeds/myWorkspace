package me.suncloud.marrymemo.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 体验店活动
 * Created by jinxin on 2016/10/28.
 */

public class ExperienceEvent implements Parcelable {
    long id;
    @SerializedName(value = "activity_id")
    long eventId;
    @SerializedName(value = "start_time")
    DateTime startTime;
    int position;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    boolean deleted;
    @SerializedName(value = "activity")
    EventInfo eventInfo;//活动具体内容
    String week;
    String time;
    String title;
    @SerializedName(value = "is_time_limit")
    boolean isTimeLimit;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public EventInfo getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTimeLimit() {
        return isTimeLimit;
    }

    public void setTimeLimit(boolean timeLimit) {
        isTimeLimit = timeLimit;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.eventId);
        HljTimeUtils.writeDateTimeToParcel(dest,this.startTime);
        dest.writeInt(this.position);
        HljTimeUtils.writeDateTimeToParcel(dest,this.updatedAt);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.eventInfo, flags);
        dest.writeString(this.week);
        dest.writeString(this.time);
        dest.writeString(this.title);
        dest.writeByte(this.isTimeLimit ? (byte) 1 : (byte) 0);
    }

    public ExperienceEvent() {}

    protected ExperienceEvent(Parcel in) {
        this.id = in.readLong();
        this.eventId = in.readLong();
        this.startTime = HljTimeUtils.readDateTimeToParcel(in);
        this.position = in.readInt();
        this.updatedAt = HljTimeUtils.readDateTimeToParcel(in);
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.deleted = in.readByte() != 0;
        this.eventInfo = in.readParcelable(EventInfo.class.getClassLoader());
        this.week = in.readString();
        this.time = in.readString();
        this.title = in.readString();
        this.isTimeLimit = in.readByte() != 0;
    }

    public static final Creator<ExperienceEvent> CREATOR = new Creator<ExperienceEvent>() {
        @Override
        public ExperienceEvent createFromParcel(Parcel source) {return new ExperienceEvent(source);}

        @Override
        public ExperienceEvent[] newArray(int size) {return new ExperienceEvent[size];}
    };
}
