package me.suncloud.marrymemo.api.bindbank;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinxin on 2017/4/5 0005.
 */

public class SwitchCashGiftBody implements Parcelable {

    long card_id;//请帖id
    String on;//0关闭 1开启

    public long getCard_id() {
        return card_id;
    }

    public void setCard_id(long card_id) {
        this.card_id = card_id;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public SwitchCashGiftBody() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.card_id);
        dest.writeString(this.on);
    }

    protected SwitchCashGiftBody(Parcel in) {
        this.card_id = in.readLong();
        this.on = in.readString();
    }

    public static final Creator<SwitchCashGiftBody> CREATOR = new Creator<SwitchCashGiftBody>() {
        @Override
        public SwitchCashGiftBody createFromParcel(Parcel source) {
            return new SwitchCashGiftBody(source);
        }

        @Override
        public SwitchCashGiftBody[] newArray(int size) {return new SwitchCashGiftBody[size];}
    };
}
