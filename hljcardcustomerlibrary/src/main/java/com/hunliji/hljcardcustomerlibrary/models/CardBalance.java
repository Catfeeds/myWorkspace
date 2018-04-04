package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcardlibrary.models.Card;
import com.hunliji.hljcommonlibrary.models.BindInfo;

/**
 * Created by mo_yu on 2017/6/13.可提现请帖列表
 */

public class CardBalance implements Parcelable {
    public final static int NEW_CARD = 1;
    public final static int OLD_CARD = 0;

    @SerializedName("card")
    private Card card;
    @SerializedName("bank")
    private BindInfo bindInfo;
    @SerializedName("balance")
    private double balance;
    @SerializedName("version")
    private int version;//1新版 0老版本
    transient boolean isSelected = true;

    public Card getCard() {
        return card;
    }

    public BindInfo getBindInfo() {
        return bindInfo;
    }

    public double getBalance() {
        return balance;
    }

    public int getVersion() {
        return version;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.card, flags);
        dest.writeParcelable(this.bindInfo, flags);
        dest.writeDouble(this.balance);
        dest.writeInt(this.version);
    }

    public CardBalance() {}

    protected CardBalance(Parcel in) {
        this.card = in.readParcelable(Card.class.getClassLoader());
        this.bindInfo = in.readParcelable(BindInfo.class.getClassLoader());
        this.balance = in.readDouble();
        this.version = in.readInt();
    }

    public static final Parcelable.Creator<CardBalance> CREATOR = new Parcelable
            .Creator<CardBalance>() {
        @Override
        public CardBalance createFromParcel(Parcel source) {return new CardBalance(source);}

        @Override
        public CardBalance[] newArray(int size) {return new CardBalance[size];}
    };
}
