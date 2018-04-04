package me.suncloud.marrymemo.model.marry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hua_rong on 2017/11/9
 * 结婚任务 种类
 */

public class TaskCategory implements Parcelable {

    private int position;
    private String title;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeString(this.title);
    }

    public TaskCategory() {}

    protected TaskCategory(Parcel in) {
        this.position = in.readInt();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<TaskCategory> CREATOR = new Parcelable
            .Creator<TaskCategory>() {
        @Override
        public TaskCategory createFromParcel(Parcel source) {return new TaskCategory(source);}

        @Override
        public TaskCategory[] newArray(int size) {return new TaskCategory[size];}
    };
}
