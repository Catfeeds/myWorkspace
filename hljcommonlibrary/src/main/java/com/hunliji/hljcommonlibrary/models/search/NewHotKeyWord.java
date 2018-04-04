package com.hunliji.hljcommonlibrary.models.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2018/1/17 0017
 */

public class NewHotKeyWord implements Parcelable {

    private long id;
    private String category;
    private String title;//标题
    private int cid;//城市
    @SerializedName(value = "is_hot")
    private boolean isHot;//是否推荐热词


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.category);
        dest.writeString(this.title);
        dest.writeInt(this.cid);
        dest.writeByte(this.isHot ? (byte) 1 : (byte) 0);
    }

    public NewHotKeyWord() {}

    protected NewHotKeyWord(Parcel in) {
        this.id = in.readLong();
        this.category = in.readString();
        this.title = in.readString();
        this.cid = in.readInt();
        this.isHot = in.readByte() != 0;
    }

    public static final Parcelable.Creator<NewHotKeyWord> CREATOR = new Parcelable
            .Creator<NewHotKeyWord>() {
        @Override
        public NewHotKeyWord createFromParcel(Parcel source) {return new NewHotKeyWord(source);}

        @Override
        public NewHotKeyWord[] newArray(int size) {return new NewHotKeyWord[size];}
    };
}
