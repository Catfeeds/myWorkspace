package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/8/26.
 * 话题的频道
 */
public class CommunityChannel implements Parcelable {
    long id;
    String title;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    @SerializedName(value = "fans_count")
    int fansCount;//粉丝数
    @SerializedName(value = "watch_count")
    int watchCount;//阅读数
    @SerializedName(value = "threads_count")
    int threadsCount;//话题数
    @SerializedName(value = "posts_count")
    int postsCount;//回帖数
    @SerializedName(value = "today_watch_count")
    int todayWatchCount;//今日数
    @SerializedName(value = "recent_threads_count")
    int recentThreadsCount;
    @SerializedName(value = "community_group_id")
    long communityGroupId;
    @SerializedName(value = "mark_id")
    long markId;
    @SerializedName(value = "weight")
    int weight;
    @SerializedName(value = "cover_path")
    String coverPath;
    @SerializedName(value = "desc")
    String desc;
    boolean hidden;
    boolean deleted;
    @SerializedName(value = "is_followed")
    boolean isFollowed;//关注标志
    boolean selected;
    @SerializedName(value = "is_same_city")
    boolean isSameCity;//同城标志
    @SerializedName(value = "is_default")
    boolean isDefault;//默认频道标志
    @SerializedName(value = "type")//特殊频道
    int type; //type=0 是以前那种 type=1 是晒婚纱照

    public transient final static long ID_SIMILAR_WEDDING = 256L;
    transient int targetType;

    public int getTargetType() {
        return targetType;
    }
    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CommunityChannel() {}

    public int getTodayWatchCount() {
        return todayWatchCount;
    }

    public void setTodayWatchCount(int todayWatchCount) {
        this.todayWatchCount = todayWatchCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getFansCount() {
        return fansCount;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public int getThreadsCount() {
        return threadsCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public int getRecentThreadsCount() {
        return recentThreadsCount;
    }

    public long getCommunityGroupId() {
        return communityGroupId;
    }

    public long getMarkId() {
        return markId;
    }

    public int getWeight() {
        return weight;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean followed) {
        isFollowed = followed;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isSameCity() {
        return isSameCity;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        HljTimeUtils.writeDateTimeToParcel(dest,this.updatedAt);
        dest.writeInt(this.fansCount);
        dest.writeInt(this.watchCount);
        dest.writeInt(this.threadsCount);
        dest.writeInt(this.postsCount);
        dest.writeInt(this.todayWatchCount);
        dest.writeInt(this.recentThreadsCount);
        dest.writeLong(this.communityGroupId);
        dest.writeLong(this.markId);
        dest.writeInt(this.weight);
        dest.writeString(this.coverPath);
        dest.writeString(this.desc);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFollowed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSameCity ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDefault ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
    }

    protected CommunityChannel(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.updatedAt = HljTimeUtils.readDateTimeToParcel(in);
        this.fansCount = in.readInt();
        this.watchCount = in.readInt();
        this.threadsCount = in.readInt();
        this.postsCount = in.readInt();
        this.todayWatchCount = in.readInt();
        this.recentThreadsCount = in.readInt();
        this.communityGroupId = in.readLong();
        this.markId = in.readLong();
        this.weight = in.readInt();
        this.coverPath = in.readString();
        this.desc = in.readString();
        this.hidden = in.readByte() != 0;
        this.deleted = in.readByte() != 0;
        this.isFollowed = in.readByte() != 0;
        this.selected = in.readByte() != 0;
        this.isSameCity = in.readByte() != 0;
        this.isDefault = in.readByte() != 0;
        this.type = in.readInt();
    }

    public static final Creator<CommunityChannel> CREATOR = new Creator<CommunityChannel>() {
        @Override
        public CommunityChannel createFromParcel(Parcel source) {
            return new CommunityChannel(source);
        }

        @Override
        public CommunityChannel[] newArray(int size) {return new CommunityChannel[size];}
    };
}
