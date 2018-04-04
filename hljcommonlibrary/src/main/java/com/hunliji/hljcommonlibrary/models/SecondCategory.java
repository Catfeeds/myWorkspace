package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/11/21 0021.
 */
public class SecondCategory implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "list_hidden")
    private boolean isListHidden;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isListHidden() {
        return isListHidden;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeByte(this.isListHidden ? (byte) 1 : (byte) 0);
    }

    public SecondCategory() {}

    protected SecondCategory(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.isListHidden = in.readByte() != 0;
    }

    public static final Creator<SecondCategory> CREATOR = new Creator<SecondCategory>() {
        @Override
        public SecondCategory createFromParcel(Parcel source) {return new SecondCategory(source);}

        @Override
        public SecondCategory[] newArray(int size) {return new SecondCategory[size];}
    };
}
