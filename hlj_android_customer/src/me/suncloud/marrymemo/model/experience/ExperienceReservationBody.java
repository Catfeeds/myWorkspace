package me.suncloud.marrymemo.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jinxin on 2016/10/31.
 */

public class ExperienceReservationBody implements Parcelable {

    @SerializedName("store_id")
    private long storeId;
    private String mobile;
    private String name;

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mobile);
        dest.writeString(this.name);
    }

    public ExperienceReservationBody() {
    }

    protected ExperienceReservationBody(Parcel in) {
        this.mobile = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<ExperienceReservationBody> CREATOR = new Parcelable.Creator<ExperienceReservationBody>() {
        @Override
        public ExperienceReservationBody createFromParcel(Parcel source) {
            return new ExperienceReservationBody(source);
        }

        @Override
        public ExperienceReservationBody[] newArray(int size) {
            return new ExperienceReservationBody[size];
        }
    };
}
