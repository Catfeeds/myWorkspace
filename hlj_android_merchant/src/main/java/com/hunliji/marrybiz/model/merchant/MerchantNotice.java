package com.hunliji.marrybiz.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * 商家店铺公告Model
 * Created by jinxin on 2017/2/6 0006.
 */

public class MerchantNotice implements Parcelable {
    String content;
    long id;
    String reason;
    int status;//审核状态 0:审核中 1:审核通过2：审核不通过
    @SerializedName(value = "updated_at")
    DateTime updatedAt;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeLong(this.id);
        dest.writeString(this.reason);
        dest.writeInt(this.status);
        dest.writeSerializable(this.updatedAt);
    }

    public MerchantNotice() {}

    protected MerchantNotice(Parcel in) {
        this.content = in.readString();
        this.id = in.readLong();
        this.reason = in.readString();
        this.status = in.readInt();
        this.updatedAt = (DateTime) in.readSerializable();
    }

    public static final Parcelable.Creator<MerchantNotice> CREATOR = new Parcelable
            .Creator<MerchantNotice>() {
        @Override
        public MerchantNotice createFromParcel(Parcel source) {return new MerchantNotice(source);}

        @Override
        public MerchantNotice[] newArray(int size) {return new MerchantNotice[size];}
    };
}
