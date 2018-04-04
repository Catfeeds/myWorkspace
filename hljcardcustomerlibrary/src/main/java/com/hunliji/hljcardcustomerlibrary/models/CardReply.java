package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by hua_rong on 2017/6/20.
 * 请帖回复列表
 */

public class CardReply implements Parcelable {

    //0：赴宴、1：待定、2：有事 3：祝福
    public static final int TYPE_FEAST = 0;
    public static final int TYPE_TO_BE_DETERMINED = 1;
    public static final int TYPE_BUSY = 2;
    public static final int TYPE_WISH = 3;

    private long id;
    @SerializedName(value = "card_id")
    private long cardId;
    private int count;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    private boolean deleted;
    private String name;
    private int state;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    @SerializedName(value = "user_id")
    private long userId;
    @SerializedName(value = "wish_language")
    private String wishLanguage;
    @SerializedName(value = "card_user_reply")
    private CardUserReply cardUserReply;
    private boolean expand;

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getWishLanguage() {
        return wishLanguage;
    }

    public void setWishLanguage(String wishLanguage) {
        this.wishLanguage = wishLanguage;
    }


    public CardUserReply getCardUserReply() {
        return cardUserReply;
    }

    public void setCardUserReply(CardUserReply cardUserReply) {
        this.cardUserReply = cardUserReply;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.cardId);
        dest.writeInt(this.count);
        dest.writeSerializable(this.createdAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeString(this.name);
        dest.writeInt(this.state);
        dest.writeSerializable(this.updatedAt);
        dest.writeLong(this.userId);
        dest.writeString(this.wishLanguage);
        dest.writeParcelable(this.cardUserReply, flags);
    }

    public CardReply() {}

    protected CardReply(Parcel in) {
        this.id = in.readLong();
        this.cardId = in.readLong();
        this.count = in.readInt();
        this.createdAt = (DateTime) in.readSerializable();
        this.deleted = in.readByte() != 0;
        this.name = in.readString();
        this.state = in.readInt();
        this.updatedAt = (DateTime) in.readSerializable();
        this.userId = in.readLong();
        this.wishLanguage = in.readString();
        this.cardUserReply = in.readParcelable(CardUserReply.class.getClassLoader());
    }

    public static final Creator<CardReply> CREATOR = new Creator<CardReply>() {
        @Override
        public CardReply createFromParcel(Parcel source) {return new CardReply(source);}

        @Override
        public CardReply[] newArray(int size) {return new CardReply[size];}
    };
}
