package me.suncloud.marrymemo.model.finder;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Label;

/**
 * 备婚类型
 * Created by chen_bin on 2017/10/17 0017.
 */
public class UserPrepareCategory extends Label {
    @SerializedName(value = "image_path")
    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.imagePath);
    }

    public UserPrepareCategory() {}

    protected UserPrepareCategory(Parcel in) {
        super(in);
        this.imagePath = in.readString();
    }

    public static final Creator<UserPrepareCategory> CREATOR = new Creator<UserPrepareCategory>() {
        @Override
        public UserPrepareCategory createFromParcel(Parcel source) {
            return new UserPrepareCategory(source);
        }

        @Override
        public UserPrepareCategory[] newArray(int size) {return new UserPrepareCategory[size];}
    };
}
