package me.suncloud.marrymemo.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 体验店photo
 * Created by jinxin on 2016/10/31.
 */

public class ExperiencePhoto implements Parcelable {
    long id;
    String title;
    @SerializedName(value = "items_count")
    int itemsCount;
    @SerializedName(value = "items_cover_id")
    long itemsCoverId;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    boolean deleted;
    @SerializedName(value = "cover_image")
    Photo cover;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public long getItemsCoverId() {
        return itemsCoverId;
    }

    public void setItemsCoverId(long itemsCoverId) {
        this.itemsCoverId = itemsCoverId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Photo getCover() {
        return cover;
    }

    public void setCover(Photo cover) {
        this.cover = cover;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.itemsCount);
        dest.writeLong(this.itemsCoverId);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        HljTimeUtils.writeDateTimeToParcel(dest,this.updatedAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.cover, flags);
    }

    public ExperiencePhoto() {
    }

    protected ExperiencePhoto(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.itemsCount = in.readInt();
        this.itemsCoverId = in.readLong();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.updatedAt = HljTimeUtils.readDateTimeToParcel(in);
        this.deleted = in.readByte() != 0;
        this.cover = in.readParcelable(Photo.class.getClassLoader());
    }

    public static final Parcelable.Creator<ExperiencePhoto> CREATOR = new Parcelable.Creator<ExperiencePhoto>() {
        @Override
        public ExperiencePhoto createFromParcel(Parcel source) {
            return new ExperiencePhoto(source);
        }

        @Override
        public ExperiencePhoto[] newArray(int size) {
            return new ExperiencePhoto[size];
        }
    };
}
