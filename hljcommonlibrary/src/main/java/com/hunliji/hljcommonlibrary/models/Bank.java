package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 银行model
 * Created by chen_bin on 2017/8/16 0016.
 */
public class Bank implements Parcelable {
    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "code")
    private String code;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.code);
    }

    public Bank() {}

    protected Bank(Parcel in) {
        this.name = in.readString();
        this.code = in.readString();
    }

    public static final Creator<Bank> CREATOR = new Creator<Bank>() {
        @Override
        public Bank createFromParcel(Parcel source) {return new Bank(source);}

        @Override
        public Bank[] newArray(int size) {return new Bank[size];}
    };
}
