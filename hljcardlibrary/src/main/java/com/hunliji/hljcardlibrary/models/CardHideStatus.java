package com.hunliji.hljcardlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2017/6/23.
 * 隐藏显示宾客页
 */

public class CardHideStatus implements Parcelable {

    private String action;
    private boolean hidden;
    @SerializedName(value = "card_page_id")
    private long id;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.action);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
        dest.writeLong(this.id);
    }

    public CardHideStatus() {}

    protected CardHideStatus(Parcel in) {
        this.action = in.readString();
        this.hidden = in.readByte() != 0;
        this.id = in.readLong();
    }

    public static final Parcelable.Creator<CardHideStatus> CREATOR = new Parcelable
            .Creator<CardHideStatus>() {
        @Override
        public CardHideStatus createFromParcel(Parcel source) {return new CardHideStatus(source);}

        @Override
        public CardHideStatus[] newArray(int size) {return new CardHideStatus[size];}
    };
}
