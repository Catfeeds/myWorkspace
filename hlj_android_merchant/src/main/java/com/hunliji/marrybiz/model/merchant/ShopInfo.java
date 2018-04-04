package com.hunliji.marrybiz.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/9/18.
 * 商家工作台用来显示的一些统计信息
 */
public class ShopInfo implements Parcelable {
    @SerializedName(value = "appoint_count_todo")
    int appointCountTodo;//待处理预约数
    @SerializedName(value = "todo_order_count")
    int todoOrderCount;
    @SerializedName(value = "watch_count")
    int watchCount;
    @SerializedName(value = "today_calender_count")
    int todayCalenderCount;//今日待办事项
    @SerializedName(value = "cpm_status")
    private int cpmStatus;

    public static final int TYPE_OPEN = 1;//已经开通cpm 1开通 0未开通

    public int getAppointCountTodo() {
        return appointCountTodo;
    }

    public int getTodoOrderCount() {
        return todoOrderCount;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public int getTodayCalenderCount() {
        return todayCalenderCount;
    }

    public int getCpmStatus() {
        return cpmStatus;
    }

    public void setCpmStatus(int cpmStatus) {
        this.cpmStatus = cpmStatus;
    }

    public ShopInfo() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.appointCountTodo);
        dest.writeInt(this.todoOrderCount);
        dest.writeInt(this.watchCount);
        dest.writeInt(this.todayCalenderCount);
        dest.writeInt(this.cpmStatus);
    }

    protected ShopInfo(Parcel in) {
        this.appointCountTodo = in.readInt();
        this.todoOrderCount = in.readInt();
        this.watchCount = in.readInt();
        this.todayCalenderCount = in.readInt();
        this.cpmStatus = in.readInt();
    }

    public static final Creator<ShopInfo> CREATOR = new Creator<ShopInfo>() {
        @Override
        public ShopInfo createFromParcel(Parcel source) {return new ShopInfo(source);}

        @Override
        public ShopInfo[] newArray(int size) {return new ShopInfo[size];}
    };
}
