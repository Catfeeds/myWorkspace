package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinxin on 2016/9/21.
 */

public class PosterFloor implements Parcelable {
    long id;
    String name;
    @SerializedName(value = "style_name")
    String styleName;
    @SerializedName(value = "style_image")
    String styleImage;
    String version;
    boolean deleted;
    @SerializedName(value = "created_at")
    DateTime createAt;
    @SerializedName(value = "updated_at")
    DateTime updatedAd;
    @SerializedName(value = "site_type")
    int siteType;
    @SerializedName(value = "posters")
    Poster poster;
    List<PosterFloor> holes;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.styleName);
        dest.writeString(this.styleImage);
        dest.writeString(this.version);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.createAt);
        dest.writeSerializable(this.updatedAd);
        dest.writeInt(this.siteType);
        dest.writeParcelable(this.poster, flags);
        dest.writeList(this.holes);
    }

    public PosterFloor() {
    }

    protected PosterFloor(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.styleName = in.readString();
        this.styleImage = in.readString();
        this.version = in.readString();
        this.deleted = in.readByte() != 0;
        this.createAt = (DateTime) in.readSerializable();
        this.updatedAd = (DateTime) in.readSerializable();
        this.siteType = in.readInt();
        this.poster = in.readParcelable(Poster.class.getClassLoader());
        this.holes = new ArrayList<PosterFloor>();
        in.readList(this.holes, PosterFloor.class.getClassLoader());
    }

    public static final Creator<PosterFloor> CREATOR = new Creator<PosterFloor>() {
        @Override
        public PosterFloor createFromParcel(Parcel source) {
            return new PosterFloor(source);
        }

        @Override
        public PosterFloor[] newArray(int size) {
            return new PosterFloor[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<PosterFloor> getHoles() {
        return holes;
    }

    public void setHoles(List<PosterFloor> holes) {
        this.holes = holes;
    }

    public Poster getPoster() {
        return poster;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }

    public int getSiteType() {
        return siteType;
    }

    public void setSiteType(int siteType) {
        this.siteType = siteType;
    }

    public DateTime getUpdatedAd() {
        return updatedAd;
    }

    public void setUpdatedAd(DateTime updatedAd) {
        this.updatedAd = updatedAd;
    }

    public DateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(DateTime createAt) {
        this.createAt = createAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStyleImage() {
        return styleImage;
    }

    public void setStyleImage(String styleImage) {
        this.styleImage = styleImage;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
