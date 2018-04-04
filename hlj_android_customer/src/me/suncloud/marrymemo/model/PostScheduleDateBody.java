package me.suncloud.marrymemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 16/8/22.
 */
public class PostScheduleDateBody implements Parcelable {
    @SerializedName(value = "schedule_date")
    private List<String> scheduleDate;
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName(value = "hall_id")
    private Long hallId;
    @SerializedName(value = "phone")
    private String phone;

    public List<String> getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(ArrayList<String> scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.scheduleDate);
        dest.writeLong(this.merchantId);
        dest.writeValue(this.hallId);
        dest.writeString(this.phone);
    }

    public PostScheduleDateBody() {}

    protected PostScheduleDateBody(Parcel in) {
        this.scheduleDate = in.createStringArrayList();
        this.merchantId = in.readLong();
        this.hallId = (Long) in.readValue(Long.class.getClassLoader());
        this.phone = in.readString();
    }

    public static final Creator<PostScheduleDateBody> CREATOR = new Creator<PostScheduleDateBody>
            () {
        @Override
        public PostScheduleDateBody createFromParcel(Parcel source) {
            return new PostScheduleDateBody(source);
        }

        @Override
        public PostScheduleDateBody[] newArray(int size) {return new PostScheduleDateBody[size];}
    };
}
