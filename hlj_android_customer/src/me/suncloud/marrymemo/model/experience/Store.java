package me.suncloud.marrymemo.model.experience;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jinxin on 2017/3/29 0029.
 */

public class Store implements Parcelable {

    @SerializedName(value = "comment_tag")
    List<String> commentTag;//印象
    @SerializedName(value = "contact_phone")
    String contactPhone;//电话
    @SerializedName("location2")
    String location;//位置
    @SerializedName(value = "store_video")
    List<StoreVideo> storeVideo;//视频
    @SerializedName(value = "activity_count")
    int activityCount;//活动数目
    String  panorama;//全景
    @SerializedName(value = "media_album_id")
    long mediaAlbumId;//图集Id
    String address;//地址
    String name;//名字

    private long storeId; //本地设置

    public List<String> getCommentTag() {
        return commentTag;
    }

    public void setCommentTag(List<String> commentTag) {
        this.commentTag = commentTag;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<StoreVideo> getStoreVideo() {
        return storeVideo;
    }

    public void setStoreVideo(List<StoreVideo> storeVideo) {
        this.storeVideo = storeVideo;
    }

    public int getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(int activityCount) {
        this.activityCount = activityCount;
    }

    public String getPanorama() {
        return panorama;
    }

    public void setPanorama(String panorama) {
        this.panorama = panorama;
    }

    public long getMediaAlbumId() {
        return mediaAlbumId;
    }

    public void setMediaAlbumId(long mediaAlbumId) {
        this.mediaAlbumId = mediaAlbumId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStoreId(long storeId) {
        this.storeId = storeId;
    }

    public long getStoreId() {
        return storeId;
    }

    public Store() {
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.commentTag);
        dest.writeString(this.contactPhone);
        dest.writeString(this.location);
        dest.writeTypedList(this.storeVideo);
        dest.writeInt(this.activityCount);
        dest.writeString(this.panorama);
        dest.writeLong(this.mediaAlbumId);
        dest.writeString(this.address);
        dest.writeString(this.name);
        dest.writeLong(this.storeId);
    }

    protected Store(Parcel in) {
        this.commentTag = in.createStringArrayList();
        this.contactPhone = in.readString();
        this.location = in.readString();
        this.storeVideo = in.createTypedArrayList(StoreVideo.CREATOR);
        this.activityCount = in.readInt();
        this.panorama = in.readString();
        this.mediaAlbumId = in.readLong();
        this.address = in.readString();
        this.name = in.readString();
        this.storeId = in.readLong();
    }

    public static final Creator<Store> CREATOR = new Creator<Store>() {
        @Override
        public Store createFromParcel(Parcel source) {return new Store(source);}

        @Override
        public Store[] newArray(int size) {return new Store[size];}
    };
}
