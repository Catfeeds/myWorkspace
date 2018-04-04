package com.hunliji.marrybiz.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 预约到店的model
 * Created by chen_bin on 2016/10/26 0026.
 */

public class Reservation implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "date")
    String date;
    @SerializedName(value = "fullname")
    String fullName;
    @SerializedName(value = "phone_num")
    String phoneNum;
    @SerializedName(value = "status")
    int status;
    transient DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeSerializable(this.createdAt);
        dest.writeString(this.date);
        dest.writeString(this.fullName);
        dest.writeString(this.phoneNum);
        dest.writeInt(this.status);
    }

    public Reservation() {}

    public Reservation(long id, int status) {
        this.id = id;
        this.status = status;
    }

    public Reservation(long id, String date, String fullName) {
        this.id = id;
        this.date = date;
        this.fullName = fullName;
    }

    protected Reservation(Parcel in) {
        this.id = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.date = in.readString();
        this.fullName = in.readString();
        this.phoneNum = in.readString();
        this.status = in.readInt();
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel source) {return new Reservation(source);}

        @Override
        public Reservation[] newArray(int size) {return new Reservation[size];}
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

    public DateTime getDate() {
        try {
            return DateTime.parse(date, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
