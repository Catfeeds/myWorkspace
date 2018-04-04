package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/6/14.请帖设置信息
 */

public class CardSetupStatus implements Parcelable {
    @SerializedName("danmu")
    private boolean danmu;//弹幕开关
    @SerializedName("gift")
    private boolean gift;//礼物开关
    @SerializedName("gold")
    private boolean gold;//礼金开关
    @SerializedName("wish")
    private boolean wish;//回复开关
    @SerializedName("can_delete")
    private boolean canDelete;//是否可删除
    @SerializedName("can_modify_name")
    private boolean canModifyName;//是否可修改请帖新人姓名

    public boolean isDanmu() {
        return danmu;
    }

    public boolean isGift() {
        return gift;
    }

    public boolean isGold() {
        return gold;
    }

    public boolean isWish() {
        return wish;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public boolean isCanModifyName() {
        return canModifyName;
    }

    public CardSetupStatus() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(
            Parcel dest, int flags) {
        dest.writeByte(this.danmu ? (byte) 1 : (byte) 0);
        dest.writeByte(this.gift ? (byte) 1 : (byte) 0);
        dest.writeByte(this.gold ? (byte) 1 : (byte) 0);
        dest.writeByte(this.wish ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canDelete ? (byte) 1 : (byte) 0);
        dest.writeByte(this.canModifyName ? (byte) 1 : (byte) 0);
    }

    protected CardSetupStatus(Parcel in) {
        this.danmu = in.readByte() != 0;
        this.gift = in.readByte() != 0;
        this.gold = in.readByte() != 0;
        this.wish = in.readByte() != 0;
        this.canDelete = in.readByte() != 0;
        this.canModifyName = in.readByte() != 0;
    }

    public static final Creator<CardSetupStatus> CREATOR = new Creator<CardSetupStatus>() {
        @Override
        public CardSetupStatus createFromParcel(Parcel source) {return new CardSetupStatus(source);}

        @Override
        public CardSetupStatus[] newArray(int size) {return new CardSetupStatus[size];}
    };
}
