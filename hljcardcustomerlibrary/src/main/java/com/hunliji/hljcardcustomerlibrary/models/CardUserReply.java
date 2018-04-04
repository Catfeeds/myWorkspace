package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mo_yu on 2017/8/21. 请帖礼金礼物回复
 */

public class CardUserReply implements Parcelable {

    private long id;//用户回复id 提交回复时使用
    private int status;//状态 0未回复 1已回复 2回复失败

    public long getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public CardUserReply() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.status);
    }

    protected CardUserReply(Parcel in) {
        this.id = in.readLong();
        this.status = in.readInt();
    }

    public static final Creator<CardUserReply> CREATOR = new Creator<CardUserReply>() {
        @Override
        public CardUserReply createFromParcel(Parcel source) {return new CardUserReply(source);}

        @Override
        public CardUserReply[] newArray(int size) {return new CardUserReply[size];}
    };
}
