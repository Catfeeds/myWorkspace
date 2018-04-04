package com.hunliji.hljcommonlibrary.models.chat_ext_object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangtao on 2017/11/8.
 */

public class WSLocation implements Parcelable {

    private String address;
    private String title;
    private double latitude;
    private double longitude;


    public WSLocation(String title,String address, double latitude, double longitude) {
        this.title=title;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;

    }

    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    protected WSLocation(Parcel in) {
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<WSLocation> CREATOR = new Creator<WSLocation>() {
        @Override
        public WSLocation createFromParcel(Parcel in) {
            return new WSLocation(in);
        }

        @Override
        public WSLocation[] newArray(int size) {
            return new WSLocation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}
