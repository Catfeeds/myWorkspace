package me.suncloud.marrymemo.model.finder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.Photo;

/**
 * 发现页tab
 * Created by chen_bin on 2016/11/28 0028.
 */
public class SubPageLabel extends Label {
    @SerializedName(value = "image")
    private Photo image;

    public Photo getImage() {
        return image;
    }

    public void setImage(Photo image) {
        this.image = image;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeParcelable(this.image, flags);}

    public SubPageLabel() {}

    protected SubPageLabel(Parcel in) {
        this.image = in.readParcelable(Photo.class.getClassLoader());
    }

    public static final Parcelable.Creator<SubPageLabel> CREATOR = new Parcelable
            .Creator<SubPageLabel>() {
        @Override
        public SubPageLabel createFromParcel(Parcel source) {return new SubPageLabel(source);}

        @Override
        public SubPageLabel[] newArray(int size) {return new SubPageLabel[size];}
    };
}
