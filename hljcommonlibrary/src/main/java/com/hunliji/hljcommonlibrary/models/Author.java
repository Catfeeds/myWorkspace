package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/7/27.
 * 商家动态专用的Author,动态或者回复所属作者
 */
public class Author implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "name", alternate = "nick")
    private String name;
    @SerializedName(value = "avatar")
    private String avatar;
    @SerializedName(value = "kind")
    private int kind;//用户类型判断,0是普通用户，1是商家，2是客服,3是小编
    @SerializedName(value = "is_merchant")
    private boolean isMerchant; //问答 动态使用
    @SerializedName(value = "merchant_id")
    private long merchantId;
    @SerializedName("specialty")
    private String specialty;//达人标志 不为空或不等于“普通用户”
    @SerializedName(value = "phone")
    private String phone;
    @SerializedName(value = "member")
    private Member member; //会员信息
    @SerializedName(value = "note_count")
    private int noteCount;
    @SerializedName(value = "hometown")
    private String hometown;
    @SerializedName(value = "is_following")
    private boolean isFollowing;
    @SerializedName("shop_type")
    private int shopType; // 这个用户可能是商家，商家的类型

    public transient final static int KIND_MERCHANT = 1; //商家

    public long getId() {
        return id;
    }

    public String getName() {
        return TextUtils.isEmpty(name) ? "" : name.trim();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public boolean isMerchant() {
        return isMerchant;
    }

    public void setMerchant(boolean merchant) {
        isMerchant = merchant;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Member getMember() {
        if (this.member == null) {
            this.member = new Member();
        }
        return member;
    }

    public int getNoteCount() {
        return noteCount;
    }

    public String getHometown() {
        return hometown;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    public int getShopType() {
        return shopType;
    }

    public Author() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeInt(this.kind);
        dest.writeByte(this.isMerchant ? (byte) 1 : (byte) 0);
        dest.writeLong(this.merchantId);
        dest.writeString(this.specialty);
        dest.writeString(this.phone);
        dest.writeParcelable(this.member, flags);
        dest.writeInt(this.noteCount);
        dest.writeString(this.hometown);
        dest.writeByte(this.isFollowing ? (byte) 1 : (byte) 0);
        dest.writeInt(this.shopType);
    }

    protected Author(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.avatar = in.readString();
        this.kind = in.readInt();
        this.isMerchant = in.readByte() != 0;
        this.merchantId = in.readLong();
        this.specialty = in.readString();
        this.phone = in.readString();
        this.member = in.readParcelable(Member.class.getClassLoader());
        this.noteCount = in.readInt();
        this.hometown = in.readString();
        this.isFollowing = in.readByte() != 0;
        this.shopType = in.readInt();
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel source) {return new Author(source);}

        @Override
        public Author[] newArray(int size) {return new Author[size];}
    };
}