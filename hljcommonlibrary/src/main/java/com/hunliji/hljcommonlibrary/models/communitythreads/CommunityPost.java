package com.hunliji.hljcommonlibrary.models.communitythreads;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by werther on 16/8/25.
 * 话题下的回帖
 */
public class CommunityPost implements Parcelable {
    long id;
    @SerializedName(value = "community_thread_id")
    long communityThreadId;
    @SerializedName(value = "user_id")
    long userId;
    String message; // 回复贴的内容
    boolean hidden;
    @SerializedName(value = "quote")
    CommunityPost quotedPost;
    @SerializedName(value = "serial_no")
    int serialNo;
    @SerializedName(value = "praised_count")
    int praisedCount;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    CommunityAuthor author;
    @SerializedName(value = "quote_count")
    int quoteCount;
    @SerializedName(value = "community_thread")
    CommunityThread communityThread;
    @SerializedName(value = "media_items", alternate = "pics")
    ArrayList<Photo> photos;
    @SerializedName(value = "is_Landlord")
    boolean isLandlord;
    @SerializedName(value = "is_prasied")
    boolean isPraised;
    @SerializedName(value = "by_faker")
    String byFaker;
    int status;
    @SerializedName("city_name")
    String cityName;


    public CommunityPost() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCommunityThreadId() {
        return communityThreadId;
    }

    public void setCommunityThreadId(long communityThreadId) {
        this.communityThreadId = communityThreadId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public CommunityPost getQuotedPost() {
        return quotedPost;
    }

    public void setQuotedPost(CommunityPost quotedPost) {
        this.quotedPost = quotedPost;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public int getPraisedCount() {
        return praisedCount;
    }

    public void setPraisedCount(int praisedCount) {
        this.praisedCount = praisedCount;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CommunityAuthor getAuthor() {
        if (author != null) {
            return author;
        } else {
            return new CommunityAuthor();
        }
    }

    public void setAuthor(CommunityAuthor author) {
        this.author = author;
    }

    public int getQuoteCount() {
        return quoteCount;
    }

    public void setQuoteCount(int quoteCount) {
        this.quoteCount = quoteCount;
    }

    public CommunityThread getCommunityThread() {
        return communityThread;
    }

    public void setCommunityThread(CommunityThread communityThread) {
        this.communityThread = communityThread;
    }

    public ArrayList<Photo> getPhotos() {
        if (photos == null) {
            return new ArrayList<>();
        }
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public boolean isLandlord() {
        return isLandlord;
    }

    public void setLandlord(boolean landlord) {
        isLandlord = landlord;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setPraised(boolean praised) {
        isPraised = praised;
    }

    public String getByFaker() {
        return byFaker;
    }

    public void setByFaker(String byFaker) {
        this.byFaker = byFaker;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCityName() {
        return cityName;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.communityThreadId);
        dest.writeLong(this.userId);
        dest.writeString(this.message);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.quotedPost, flags);
        dest.writeInt(this.serialNo);
        dest.writeInt(this.praisedCount);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        dest.writeParcelable(this.author, flags);
        dest.writeInt(this.quoteCount);
        dest.writeParcelable(this.communityThread, flags);
        dest.writeTypedList(this.photos);
        dest.writeByte(this.isLandlord ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isPraised ? (byte) 1 : (byte) 0);
        dest.writeString(this.byFaker);
        dest.writeInt(this.status);
        dest.writeString(this.cityName);
    }

    protected CommunityPost(Parcel in) {
        this.id = in.readLong();
        this.communityThreadId = in.readLong();
        this.userId = in.readLong();
        this.message = in.readString();
        this.hidden = in.readByte() != 0;
        this.quotedPost = in.readParcelable(CommunityPost.class.getClassLoader());
        this.serialNo = in.readInt();
        this.praisedCount = in.readInt();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.author = in.readParcelable(CommunityAuthor.class.getClassLoader());
        this.quoteCount = in.readInt();
        this.communityThread = in.readParcelable(CommunityThread.class.getClassLoader());
        this.photos = in.createTypedArrayList(Photo.CREATOR);
        this.isLandlord = in.readByte() != 0;
        this.isPraised = in.readByte() != 0;
        this.byFaker = in.readString();
        this.status = in.readInt();
        this.cityName = in.readString();
    }

    public static final Creator<CommunityPost> CREATOR = new Creator<CommunityPost>() {
        @Override
        public CommunityPost createFromParcel(Parcel source) {
            return new CommunityPost(source);
        }

        @Override
        public CommunityPost[] newArray(int size) {
            return new CommunityPost[size];
        }
    };
}
