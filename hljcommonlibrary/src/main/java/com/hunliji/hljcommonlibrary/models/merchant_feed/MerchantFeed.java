package com.hunliji.hljcommonlibrary.models.merchant_feed;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.models.ShareInfo;
import com.hunliji.hljcommonlibrary.models.Video;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by werther on 16/7/19.
 * 商家动态的详情model
 */
public class MerchantFeed implements Parcelable {
    @SerializedName(value = "id")
    long id;
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName(value = "merchant_id")
    long merchantId;
    @SerializedName(value = "content")
    ArrayList<Photo> photos;
    @SerializedName(value = "describe")
    String describe;
    @SerializedName(value = "read_count")
    int readCount;
    @SerializedName(value = "comment_count")
    int commentCount;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "updated_at")
    DateTime updatedAt;
    @SerializedName(value = "merchant")
    Merchant merchant;
    @SerializedName(value = "praised_users")
    ArrayList<Author> praisedUsers;
    @SerializedName(value = "comments")
    ArrayList<RepliedComment> repliedComments;
    @SerializedName(value = "share")
    ShareInfo shareInfo;
    @SerializedName(value = "video_content")
    Video videoContent; //视频
    @SerializedName(value = "marks")
    ArrayList<Mark> marks;

    /**
     * 用户端独有 关注与喜欢字段
     */
    @SerializedName(value = "is_followed")
    boolean isFollowed;
    @SerializedName(value = "is_like")
    boolean isLike;
    @SerializedName(value = "likes_count")
    int likesCount;

    public transient final static int MERCHANT_FEED_FOR_PHOTO = 0; //照片
    public transient final static int MERCHANT_FEED_FOR_VIDEO = 1; //视频

    public long getId() {
        return id;
    }

    public String getDescribe() {
        if (TextUtils.isEmpty(describe) && !CommonUtil.isCollectionEmpty(photos)) {
            for (Photo photo : photos) {
                if (!TextUtils.isEmpty(photo.getDescribe())) {
                    return photo.getDescribe();
                }
            }
        }
        return describe;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public long getUserId() {
        return userId;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public int getReadCount() {
        return readCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void minusCommentCount() {
        if (commentCount <= 1) {
            commentCount = 0;
        } else {
            commentCount--;
        }
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public Merchant getMerchant() {
        if (this.merchant == null) {
            this.merchant = new Merchant();
        }
        return merchant;
    }

    public ArrayList<Author> getPraisedUsers() {
        if (this.praisedUsers == null) {
            this.praisedUsers = new ArrayList<>();
        }
        return praisedUsers;
    }

    public ArrayList<RepliedComment> getRepliedComments() {
        if (this.repliedComments == null) {
            this.repliedComments = new ArrayList<>();
        }
        return repliedComments;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setMarks(ArrayList<Mark> marks) {
        this.marks = marks;
    }

    public boolean isFollowed() {
        return isFollowed;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public Video getVideoContent() {return videoContent;}

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public MerchantFeed() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeLong(this.merchantId);
        dest.writeTypedList(this.photos);
        dest.writeString(this.describe);
        dest.writeInt(this.readCount);
        dest.writeInt(this.commentCount);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeParcelable(this.merchant, flags);
        dest.writeTypedList(this.praisedUsers);
        dest.writeTypedList(this.repliedComments);
        dest.writeParcelable(this.shareInfo, flags);
        dest.writeParcelable(this.videoContent, flags);
        dest.writeTypedList(this.marks);
        dest.writeByte(this.isFollowed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLike ? (byte) 1 : (byte) 0);
        dest.writeInt(this.likesCount);
        dest.writeParcelable(this.merchant, flags);
    }

    protected MerchantFeed(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.merchantId = in.readLong();
        this.photos = in.createTypedArrayList(Photo.CREATOR);
        this.describe = in.readString();
        this.readCount = in.readInt();
        this.commentCount = in.readInt();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.praisedUsers = in.createTypedArrayList(Author.CREATOR);
        this.repliedComments = in.createTypedArrayList(RepliedComment.CREATOR);
        this.shareInfo = in.readParcelable(ShareInfo.class.getClassLoader());
        this.videoContent = in.readParcelable(Video.class.getClassLoader());
        this.marks = in.createTypedArrayList(Mark.CREATOR);
        this.isFollowed = in.readByte() != 0;
        this.isLike = in.readByte() != 0;
        this.likesCount = in.readInt();
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
    }

    public static final Creator<MerchantFeed> CREATOR = new Creator<MerchantFeed>() {
        @Override
        public MerchantFeed createFromParcel(Parcel source) {return new MerchantFeed(source);}

        @Override
        public MerchantFeed[] newArray(int size) {return new MerchantFeed[size];}
    };
}