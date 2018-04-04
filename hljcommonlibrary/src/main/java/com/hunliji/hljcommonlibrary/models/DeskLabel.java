package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;

/**
 * Created by mo_yu on 2017/10/8.桌数使用的简单label
 */

public class DeskLabel extends Label {

    private long deskStart;
    private long deskEnd;

    public long getDeskStart() {
        return deskStart;
    }

    public void setDeskStart(long deskStart) {
        this.deskStart = deskStart;
    }

    public long getDeskEnd() {
        return deskEnd;
    }

    public void setDeskEnd(long deskEnd) {
        this.deskEnd = deskEnd;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.deskStart);
        dest.writeLong(this.deskEnd);
    }

    public DeskLabel() {}

    protected DeskLabel(Parcel in) {
        super(in);
        this.deskStart = in.readLong();
        this.deskEnd = in.readLong();
    }

    public static final Creator<DeskLabel> CREATOR = new Creator<DeskLabel>() {
        @Override
        public DeskLabel createFromParcel(Parcel source) {return new DeskLabel(source);}

        @Override
        public DeskLabel[] newArray(int size) {return new DeskLabel[size];}
    };
}
