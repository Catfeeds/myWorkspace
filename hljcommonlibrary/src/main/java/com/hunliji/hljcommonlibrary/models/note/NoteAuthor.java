package com.hunliji.hljcommonlibrary.models.note;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2017/7/13.笔记用户
 */

public class NoteAuthor extends Author {

    @SerializedName(value = "weddingday")//婚期
    DateTime weddingDay;
    @SerializedName(value = "gender")//性别
    boolean gender;
    @SerializedName(value = "is_pending")//婚期 0表示已设置
    int isPending;

    public DateTime getWeddingDay() {
        return weddingDay;
    }

    public boolean isGender() {
        return gender;
    }

    public int getIsPending() {
        return isPending;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeSerializable(this.weddingDay);
        dest.writeByte(this.gender ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isPending);
    }

    public NoteAuthor() {}

    protected NoteAuthor(Parcel in) {
        super(in);
        this.weddingDay = (DateTime) in.readSerializable();
        this.gender = in.readByte() != 0;
        this.isPending = in.readInt();
    }

    public static final Creator<NoteAuthor> CREATOR = new Creator<NoteAuthor>() {
        @Override
        public NoteAuthor createFromParcel(Parcel source) {return new NoteAuthor(source);}

        @Override
        public NoteAuthor[] newArray(int size) {return new NoteAuthor[size];}
    };
}
