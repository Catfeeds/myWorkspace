package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_bin on 2017/8/1 0001.
 */
public class CategoryMark implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "mark")
    private Mark mark;
    @SerializedName(value = "children")
    private List<CategoryMark> children;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Mark getMark() {
        if (this.mark == null) {
            this.mark = new Mark();
        }
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public List<CategoryMark> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<CategoryMark> children) {
        this.children = children;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.mark, flags);
        dest.writeTypedList(this.children);
    }

    public CategoryMark() {}

    protected CategoryMark(Parcel in) {
        this.id = in.readLong();
        this.mark = in.readParcelable(Mark.class.getClassLoader());
        this.children = in.createTypedArrayList(CategoryMark.CREATOR);
    }

    public static final Creator<CategoryMark> CREATOR = new Creator<CategoryMark>() {
        @Override
        public CategoryMark createFromParcel(Parcel source) {return new CategoryMark(source);}

        @Override
        public CategoryMark[] newArray(int size) {return new CategoryMark[size];}
    };
}