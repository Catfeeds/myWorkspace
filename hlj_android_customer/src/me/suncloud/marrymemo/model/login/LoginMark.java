package me.suncloud.marrymemo.model.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jinxin on 2016/9/1.
 */
public class LoginMark implements Parcelable {
    Long id;
    String name;
    @SerializedName(value = "image_path")
    String imagePath;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imagePath);
    }

    public LoginMark() {}

    protected LoginMark(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.imagePath = in.readString();
    }

    public static final Parcelable.Creator<LoginMark> CREATOR = new
            Parcelable.Creator<LoginMark>() {
        @Override
        public LoginMark createFromParcel(Parcel source) {
            return new LoginMark(source);
        }

        @Override
        public LoginMark[] newArray(int size) {return new LoginMark[size];}
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof LoginMark) {
            LoginMark mark = (LoginMark) o;
            return mark.getId().equals(this.id);
        }
        return super.equals(o);
    }
}
