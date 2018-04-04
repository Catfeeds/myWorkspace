package me.suncloud.marrymemo.model.main;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Work;

import java.util.List;

/**
 * 猜你喜欢model
 * Created by jinxin on 2016/11/28 0028.
 */

public class YouLike implements Parcelable {
    @SerializedName(value = "cover_path")
    String coverPath;
    @SerializedName(value = "property_id")
    long propertyId;
    @SerializedName(value = "property_name")
    String propertyName;
    List<Work> recommend;

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(long propertyId) {
        this.propertyId = propertyId;
    }

    public List<Work> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<Work> recommend) {
        this.recommend = recommend;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.coverPath);
        dest.writeLong(this.propertyId);
        dest.writeString(this.propertyName);
        dest.writeTypedList(this.recommend);
    }

    public YouLike() {}

    protected YouLike(Parcel in) {
        this.coverPath = in.readString();
        this.propertyId = in.readLong();
        this.propertyName = in.readString();
        this.recommend = in.createTypedArrayList(Work.CREATOR);
    }

    public static final Parcelable.Creator<YouLike> CREATOR = new Parcelable.Creator<YouLike>() {
        @Override
        public YouLike createFromParcel(Parcel source) {return new YouLike(source);}

        @Override
        public YouLike[] newArray(int size) {return new YouLike[size];}
    };
}
