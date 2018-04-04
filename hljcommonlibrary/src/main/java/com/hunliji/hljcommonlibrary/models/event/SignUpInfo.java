package com.hunliji.hljcommonlibrary.models.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;

import org.joda.time.DateTime;

/**
 * 报名model
 * Created by chen_bin on 2016/9/13 0013.
 */
public class SignUpInfo implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "user_id")
    private long userId;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    @SerializedName(value = "realname")
    private String realName;
    @SerializedName(value = "activity_name")
    private String title;
    @SerializedName(value = "tel")
    private String tel;
    @SerializedName(value = "valid_code")
    private String validCode;
    @SerializedName(value = "valid_code_url")
    private String validCodeUrl;
    @SerializedName(value = "is_merchant_pay")
    private boolean isMerchantPay;
    @SerializedName(value = "winner_limit")
    private int winnerLimit;
    @SerializedName(value = "status")
    private int status;
    @SerializedName(value = "user")
    private Author author;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeString(this.realName);
        dest.writeString(this.title);
        dest.writeString(this.tel);
        dest.writeString(this.validCode);
        dest.writeString(this.validCodeUrl);
        dest.writeByte(this.isMerchantPay ? (byte) 1 : (byte) 0);
        dest.writeInt(this.winnerLimit);
        dest.writeInt(this.status);
        dest.writeParcelable(this.author, flags);
    }

    public SignUpInfo() {}

    protected SignUpInfo(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.updatedAt = (DateTime) in.readSerializable();
        this.realName = in.readString();
        this.title = in.readString();
        this.tel = in.readString();
        this.validCode = in.readString();
        this.validCodeUrl = in.readString();
        this.isMerchantPay = in.readByte() != 0;
        this.winnerLimit = in.readInt();
        this.status = in.readInt();
        this.author = in.readParcelable(Author.class.getClassLoader());
    }

    public static final Creator<SignUpInfo> CREATOR = new Creator<SignUpInfo>() {
        @Override
        public SignUpInfo createFromParcel(Parcel source) {return new SignUpInfo(source);}

        @Override
        public SignUpInfo[] newArray(int size) {return new SignUpInfo[size];}
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getRealName() {
        return realName;
    }

    public String getTitle() {
        return title;
    }

    public String getTel() {
        return tel;
    }

    public String getValidCode() {
        return validCode;
    }

    public String getValidCodeUrl() {
        return validCodeUrl;
    }

    public boolean isMerchantPay() {
        return isMerchantPay;
    }

    public int getWinnerLimit() {
        return winnerLimit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Author getAuthor() {
        if (this.author == null) {
            this.author = new Author();
        }
        return author;
    }
}