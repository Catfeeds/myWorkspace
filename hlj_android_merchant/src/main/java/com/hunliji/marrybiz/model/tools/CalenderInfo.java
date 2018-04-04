package com.hunliji.marrybiz.model.tools;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * 日程model
 * Created by chen_bin on 2016/10/27 0027.
 */

public class CalenderInfo implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    @SerializedName(value = "date")
    String date;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeString(this.date);
    }

    public CalenderInfo() {}

    protected CalenderInfo(Parcel in) {
        this.id = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
        this.date = in.readString();
    }

    public static final Creator<CalenderInfo> CREATOR = new Creator<CalenderInfo>() {
        @Override
        public CalenderInfo createFromParcel(Parcel source) {return new CalenderInfo(source);}

        @Override
        public CalenderInfo[] newArray(int size) {return new CalenderInfo[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}