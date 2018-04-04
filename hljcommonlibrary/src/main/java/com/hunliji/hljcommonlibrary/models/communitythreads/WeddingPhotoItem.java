package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

import java.util.ArrayList;

/**
 * Created by mo_yu on 17/5/4.晒婚纱照小组
 */
public class WeddingPhotoItem implements Parcelable {
    long id;
    @SerializedName("community_thread_id")
    private long communityThreadId;
    private String description;//组描述
    @SerializedName("likes_count")
    private int likesCount;
    @SerializedName("is_praised")
    private boolean isPraised;
    private ArrayList<Photo> photos;
    transient boolean isCollapseViewOpened; // 列表显示使用的特殊的本地标志位

    public long getCommunityThreadId() {
        return communityThreadId;
    }

    public void setCommunityThreadId(long communityThreadId) {
        this.communityThreadId = communityThreadId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setPraised(boolean praised) {
        isPraised = praised;
    }

    public boolean isCollapseViewOpened() {
        return isCollapseViewOpened;
    }

    public void setCollapseViewOpened(boolean collapseViewOpened) {
        isCollapseViewOpened = collapseViewOpened;
    }

    public WeddingPhotoItem() {}

    public ArrayList<Photo> getPhotos() {
        if (photos == null) {
            return new ArrayList<>();
        }
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public long getId() {
        return id;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.communityThreadId);
        dest.writeString(this.description);
        dest.writeInt(this.likesCount);
        dest.writeByte(this.isPraised ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.photos);
    }

    protected WeddingPhotoItem(Parcel in) {
        this.id = in.readLong();
        this.communityThreadId = in.readLong();
        this.description = in.readString();
        this.likesCount = in.readInt();
        this.isPraised = in.readByte() != 0;
        this.photos = in.createTypedArrayList(Photo.CREATOR);
    }

    public static final Creator<WeddingPhotoItem> CREATOR = new Creator<WeddingPhotoItem>() {
        @Override
        public WeddingPhotoItem createFromParcel(Parcel source) {return new WeddingPhotoItem(source);}

        @Override
        public WeddingPhotoItem[] newArray(int size) {return new WeddingPhotoItem[size];}
    };
}
