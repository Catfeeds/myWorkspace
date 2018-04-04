package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2017/2/7.我的礼物
 */

public class UserGift implements Parcelable {

    public static final int TYPE_CASH = 1; // 礼金
    public static final int TYPE_GIFT = 2; // 礼物

    long id;
    @SerializedName("card_gift2_id")
    long cardGift2Id; // 如果这个值大于0表示为礼物，此时有可能没有cardGift字段的
    @SerializedName("gift_name")
    String giftName; // 礼物的名称
    @SerializedName(value = "card_gift")
    CardGift cardGift;//为null表示礼金
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "giver_name")
    String giverName;
    @SerializedName(value = "price")
    double price;
    @SerializedName(value = "wishes")
    String wishes;
    @SerializedName(value = "title")
    String title;//标题
    @SerializedName(value = "card_user_reply")
    CardUserReply cardUserReply;
    @SerializedName(value = "hidden")
    boolean hidden;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getWishes() {
        return wishes;
    }

    public CardGift getCardGift() {
        return cardGift;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getGiverName() {
        return giverName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public CardUserReply getCardUserReply() {
        return cardUserReply;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public long getCardGift2Id() {
        return cardGift2Id;
    }

    public String getGiftName() {
        return giftName;
    }

    public UserGift() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.cardGift, flags);
        dest.writeSerializable(this.createdAt);
        dest.writeString(this.giverName);
        dest.writeDouble(this.price);
        dest.writeString(this.wishes);
        dest.writeString(this.title);
        dest.writeParcelable(this.cardUserReply, flags);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
        dest.writeLong(this.cardGift2Id);
        dest.writeString(this.giftName);
    }

    protected UserGift(Parcel in) {
        this.id = in.readLong();
        this.cardGift = in.readParcelable(CardGift.class.getClassLoader());
        this.createdAt = (DateTime) in.readSerializable();
        this.giverName = in.readString();
        this.price = in.readDouble();
        this.wishes = in.readString();
        this.title = in.readString();
        this.cardUserReply = in.readParcelable(CardUserReply.class.getClassLoader());
        this.hidden = in.readByte() != 0;
        this.cardGift2Id = in.readLong();
        this.giftName = in.readString();
    }

    public static final Creator<UserGift> CREATOR = new Creator<UserGift>() {
        @Override
        public UserGift createFromParcel(Parcel source) {return new UserGift(source);}

        @Override
        public UserGift[] newArray(int size) {return new UserGift[size];}
    };
}
