package com.hunliji.marrybiz.model.easychat;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2017/7/7.
 */

public class EasyChat implements Parcelable {

    private boolean active;
    @SerializedName(value = "pend_word")
    private PendWord pendWord;


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public PendWord getPendWord() {
        if (pendWord == null){
            return new PendWord();
        }
        return pendWord;
    }

    public void setPendWord(PendWord pendWord) {
        this.pendWord = pendWord;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(
            Parcel dest, int flags) {
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.pendWord, flags);
    }

    public EasyChat() {}

    protected EasyChat(Parcel in) {
        this.active = in.readByte() != 0;
        this.pendWord = in.readParcelable(PendWord.class.getClassLoader());
    }

    public static final Parcelable.Creator<EasyChat> CREATOR = new Parcelable.Creator<EasyChat>() {
        @Override
        public EasyChat createFromParcel(Parcel source) {return new EasyChat(source);}

        @Override
        public EasyChat[] newArray(int size) {return new EasyChat[size];}
    };
}
