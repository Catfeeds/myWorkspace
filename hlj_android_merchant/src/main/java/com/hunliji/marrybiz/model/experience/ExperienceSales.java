package com.hunliji.marrybiz.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 体验店销售
 * Created by jinxin on 2017/12/19 0019.
 */

public class ExperienceSales implements Parcelable {

    long id;
    @SerializedName(value = "fullname")
    String fullName;
    String phone;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.fullName);
        dest.writeString(this.phone);
    }

    public ExperienceSales() {}

    protected ExperienceSales(Parcel in) {
        this.id = in.readLong();
        this.fullName = in.readString();
        this.phone = in.readString();
    }

    public static final Parcelable.Creator<ExperienceSales> CREATOR = new Parcelable
            .Creator<ExperienceSales>() {
        @Override
        public ExperienceSales createFromParcel(Parcel source) {return new ExperienceSales(source);}

        @Override
        public ExperienceSales[] newArray(int size) {return new ExperienceSales[size];}
    };
}
