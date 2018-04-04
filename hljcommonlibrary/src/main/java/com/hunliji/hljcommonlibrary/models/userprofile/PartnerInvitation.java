package com.hunliji.hljcommonlibrary.models.userprofile;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/11/12.
 * 用户结伴邀请model, user是邀请者，partner是被邀请者
 */

public class PartnerInvitation implements Parcelable {
    @SerializedName("user_id")
    long userId;
    @SerializedName("user_avatar")
    String userAvatar;
    @SerializedName("user_nick")
    String userNick;
    @SerializedName("user_phone")
    String userPhone;
    @SerializedName("partner_user_id")
    long partnerUserId;
    @SerializedName("partner_user_nick")
    String partnerUserNick;
    @SerializedName("partner_user_avatar")
    String partnerUserAvatar;
    @SerializedName("partner_user_phone")
    String partnerUserPhone;
    @SerializedName("created_at")
    DateTime createdAt;
    @SerializedName("updated_at")
    DateTime updatedAt;

    boolean deleted;
    String status; // established 结伴成功， inviting结伴中

    public static final String PARTNER_STATUS_ESTA = "established";
    public static final String PARTNER_STATUS_INVITE = "inviting";

    public long getUserId() {
        return userId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public String getUserNick() {
        return userNick;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public long getPartnerUserId() {
        return partnerUserId;
    }

    public String getPartnerUserNick() {
        return partnerUserNick;
    }

    public String getPartnerUserAvatar() {
        return partnerUserAvatar;
    }

    public String getPartnerUserPhone() {
        return partnerUserPhone;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.userAvatar);
        dest.writeString(this.userNick);
        dest.writeString(this.userPhone);
        dest.writeLong(this.partnerUserId);
        dest.writeString(this.partnerUserNick);
        dest.writeString(this.partnerUserAvatar);
        dest.writeString(this.partnerUserPhone);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeString(this.status);
    }

    public PartnerInvitation() {}

    protected PartnerInvitation(Parcel in) {
        this.userId = in.readLong();
        this.userAvatar = in.readString();
        this.userNick = in.readString();
        this.userPhone = in.readString();
        this.partnerUserId = in.readLong();
        this.partnerUserNick = in.readString();
        this.partnerUserAvatar = in.readString();
        this.partnerUserPhone = in.readString();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
        this.deleted = in.readByte() != 0;
        this.status = in.readString();
    }

    public static final Creator<PartnerInvitation> CREATOR = new Creator<PartnerInvitation>() {
        @Override
        public PartnerInvitation createFromParcel(Parcel source) {
            return new PartnerInvitation(source);
        }

        @Override
        public PartnerInvitation[] newArray(int size) {return new PartnerInvitation[size];}
    };
}
