package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.HljCommon;

import org.joda.time.DateTime;

/**
 * Created by luohanlin on 2017/4/26.
 * 套餐案例中的图片/视频多媒体model
 */

public class WorkMediaItem implements Parcelable {
    long id;
    @SerializedName("set_meal_id")
    long setMealId;
    @SerializedName("media_path")
    String mediaPath;
    String describe;
    int width;
    int height;
    @SerializedName("created_at")
    DateTime createdAt;
    @SerializedName("updated_at")
    DateTime updatedAt;
    int kind;  // 1： 图片， 2： 视频
    @SerializedName("persistent_id")
    String persistentId;
    @SerializedName("persistent_path")
    Persistent persistent;
    int position;
    @SerializedName("collectors_count")
    int collectorsCount;
    String status;
    private String localPath;

    public static final int MEDIA_KIND_PHOTO = 1;
    public static final int MEDIA_KIND_VIDEO = 2;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSetMealId() {
        return setMealId;
    }

    public void setSetMealId(long setMealId) {
        this.setMealId = setMealId;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public Persistent getPersistent() {
        return persistent;
    }

    public void setPersistent(Persistent persistent) {
        this.persistent = persistent;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCollectorsCount() {
        return collectorsCount;
    }

    public void setCollectorsCount(int collectorsCount) {
        this.collectorsCount = collectorsCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取这个多媒体的封面图，如果是图片，直接取media_path
     * 如果是视频，取视频封面截图vframe
     * 如果没有vframe，则获取视频截图链接
     *
     * @return
     */
    public String getItemCover() {
        if (MEDIA_KIND_VIDEO == kind) {
            if (persistent != null && !TextUtils.isEmpty(persistent.getScreenShot())) {
                return persistent.getScreenShot();
            } else {
                return mediaPath + HljCommon.QINIU.SCREEN_SHOT_URL_3_SECONDS;
            }
        } else {
            return mediaPath;
        }
    }

    public String getVideoPath() {
        if (persistent != null) {
            if (!TextUtils.isEmpty(persistent.getStreamingPhone())) { //m3u8_640_480
                return persistent.getStreamingPhone();
            } else if (!TextUtils.isEmpty(persistent.getIphone())) {
                return persistent.getIphone();
            }
        }
        return null;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.setMealId);
        dest.writeString(this.mediaPath);
        dest.writeString(this.describe);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeInt(this.kind);
        dest.writeString(this.persistentId);
        dest.writeParcelable(this.persistent, flags);
        dest.writeInt(this.position);
        dest.writeInt(this.collectorsCount);
        dest.writeString(this.status);
        dest.writeString(this.localPath);
    }

    public WorkMediaItem() {}

    protected WorkMediaItem(Parcel in) {
        this.id = in.readLong();
        this.setMealId = in.readLong();
        this.mediaPath = in.readString();
        this.describe = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
        this.kind = in.readInt();
        this.persistentId = in.readString();
        this.persistent = in.readParcelable(Persistent.class.getClassLoader());
        this.position = in.readInt();
        this.collectorsCount = in.readInt();
        this.status = in.readString();
        this.localPath = in.readString();
    }

    public static final Parcelable.Creator<WorkMediaItem> CREATOR = new Parcelable
            .Creator<WorkMediaItem>() {
        @Override
        public WorkMediaItem createFromParcel(Parcel source) {return new WorkMediaItem(source);}

        @Override
        public WorkMediaItem[] newArray(int size) {return new WorkMediaItem[size];}
    };
}
