package me.suncloud.marrymemo.model.marry;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2017/12/13
 * 礼金数量
 */

public class GuestCount implements Parcelable {

    @SerializedName(value = "guest_count")
    private int guestCount;

    public int getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeInt(this.guestCount);}

    public GuestCount() {}

    protected GuestCount(Parcel in) {this.guestCount = in.readInt();}

    public static final Parcelable.Creator<GuestCount> CREATOR = new Parcelable
            .Creator<GuestCount>() {
        @Override
        public GuestCount createFromParcel(Parcel source) {return new GuestCount(source);}

        @Override
        public GuestCount[] newArray(int size) {return new GuestCount[size];}
    };
}
