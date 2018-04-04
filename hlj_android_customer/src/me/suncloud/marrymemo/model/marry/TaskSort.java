package me.suncloud.marrymemo.model.marry;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hua_rong on 2017/11/9
 * 结婚任务数据排序的中间model
 */

public class TaskSort implements Parcelable {

    public TaskSort(String title) {
        this.title = title;
    }

    private String title;
    private List<MarryTask> marryTasks;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<MarryTask> getMarryTasks() {
        if (marryTasks == null) {
            marryTasks = new ArrayList<>();
        }
        return marryTasks;
    }

    public void setMarryTasks(List<MarryTask> marryTasks) {
        this.marryTasks = marryTasks;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeTypedList(this.marryTasks);
    }

    public TaskSort() {}

    protected TaskSort(Parcel in) {
        this.title = in.readString();
        this.marryTasks = in.createTypedArrayList(MarryTask.CREATOR);
    }

    public static final Parcelable.Creator<TaskSort> CREATOR = new Parcelable.Creator<TaskSort>() {
        @Override
        public TaskSort createFromParcel(Parcel source) {return new TaskSort(source);}

        @Override
        public TaskSort[] newArray(int size) {return new TaskSort[size];}
    };
}
