package com.hunliji.marrybiz.model.chat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangtao on 2017/11/6.
 */

public class FastReply implements Parcelable {

    private long id;
    private String content;
    private boolean isSelecked;

    public String getContent() {
        return content;
    }

    public boolean isSelecked() {
        return isSelecked;
    }

    public void setSelecked(boolean selecked) {
        isSelecked = selecked;
    }

    public long getId() {
        return id;
    }

    protected FastReply(Parcel in) {
        id = in.readLong();
        content = in.readString();
    }

    public static final Creator<FastReply> CREATOR = new Creator<FastReply>() {
        @Override
        public FastReply createFromParcel(Parcel in) {
            return new FastReply(in);
        }

        @Override
        public FastReply[] newArray(int size) {
            return new FastReply[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(content);
    }
}
