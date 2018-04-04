package com.hunliji.marrybiz.model.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hua_rong on 2017/12/27 活动钱包相关信息
 */

public class EventWallet implements Parcelable {

    private int points;//点数
    private int tickets;//券数

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.points);
        dest.writeInt(this.tickets);
    }

    public EventWallet() {}

    protected EventWallet(Parcel in) {
        this.points = in.readInt();
        this.tickets = in.readInt();
    }

    public static final Parcelable.Creator<EventWallet> CREATOR = new Parcelable
            .Creator<EventWallet>() {
        @Override
        public EventWallet createFromParcel(Parcel source) {return new EventWallet(source);}

        @Override
        public EventWallet[] newArray(int size) {return new EventWallet[size];}
    };
}
