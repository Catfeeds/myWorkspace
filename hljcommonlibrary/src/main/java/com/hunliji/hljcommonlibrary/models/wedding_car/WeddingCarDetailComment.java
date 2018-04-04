package com.hunliji.hljcommonlibrary.models.wedding_car;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.CommentMark;
import com.hunliji.hljcommonlibrary.models.CommentStatistics;

import java.util.List;

/**
 * 婚车详情商家
 * Created by jinxin on 2017/12/28 0028.
 */

public class WeddingCarDetailComment implements Parcelable {

    long id;
    String address;
    @SerializedName(value = "merchant_comments_count")
    int merchantCommentsCount;
    @SerializedName("merchant_comment")
    CommentStatistics commentStatistics; // 商家评论相关的所有参数 包含近期评价概况
    @SerializedName(value = "last_merchant_comment")
    WeddingCarComment  lastMerchantComment;
    @SerializedName(value = "contact_phones")
    String contactPhones;
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName("logo_path")
    String logoPath;
    @SerializedName("is_appointment")
    boolean isAppointment;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMerchantCommentsCount() {
        return merchantCommentsCount;
    }

    public void setMerchantCommentsCount(int merchantCommentsCount) {
        this.merchantCommentsCount = merchantCommentsCount;
    }

    public CommentStatistics getCommentStatistics() {
        return commentStatistics;
    }

    public WeddingCarComment getLastMerchantComment() {
        return lastMerchantComment;
    }

    public void setLastMerchantComment(WeddingCarComment lastMerchantComment) {
        this.lastMerchantComment = lastMerchantComment;
    }

    public void setCommentStatistics(CommentStatistics commentStatistics) {
        this.commentStatistics = commentStatistics;
    }

    public String getContactPhones() {
        return contactPhones;
    }

    public void setContactPhones(String contactPhones) {
        this.contactPhones = contactPhones;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public boolean isAppointment() {
        return isAppointment;
    }

    public WeddingCarDetailComment() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.address);
        dest.writeInt(this.merchantCommentsCount);
        dest.writeParcelable(this.commentStatistics, flags);
        dest.writeParcelable(this.lastMerchantComment, flags);
        dest.writeString(this.contactPhones);
        dest.writeLong(this.userId);
        dest.writeString(this.logoPath);
        dest.writeByte(this.isAppointment ? (byte) 1 : (byte) 0);
    }

    protected WeddingCarDetailComment(Parcel in) {
        this.id = in.readLong();
        this.address = in.readString();
        this.merchantCommentsCount = in.readInt();
        this.commentStatistics = in.readParcelable(CommentStatistics.class.getClassLoader());
        this.lastMerchantComment = in.readParcelable(WeddingCarComment.class.getClassLoader());
        this.contactPhones = in.readString();
        this.userId = in.readLong();
        this.logoPath = in.readString();
        this.isAppointment = in.readByte() != 0;
    }

    public static final Creator<WeddingCarDetailComment> CREATOR = new
            Creator<WeddingCarDetailComment>() {
        @Override
        public WeddingCarDetailComment createFromParcel(Parcel source) {
            return new WeddingCarDetailComment(source);
        }

        @Override
        public WeddingCarDetailComment[] newArray(int size) {return new WeddingCarDetailComment[size];}
    };
}
