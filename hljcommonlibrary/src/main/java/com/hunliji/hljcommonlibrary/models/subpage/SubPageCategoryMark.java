package com.hunliji.hljcommonlibrary.models.subpage;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Mark;

import java.util.ArrayList;

/**
 * 标签组(暂时用于发现页的标签组)
 * Created by chen_bin on 2017/5/10 0010.
 */
public class SubPageCategoryMark implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "marks_detail")
    private ArrayList<Mark> marks; //标签组下的标签列表
    @SerializedName(value = "name")
    private String name; //组名
    @SerializedName(value = "img")
    private String img; //封面

    public long getId() {
        return id;
    }

    public ArrayList<Mark> getMarks() {
        return marks;
    }

    public String getName() {
        return name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeTypedList(this.marks);
        dest.writeString(this.name);
        dest.writeString(this.img);
    }

    public SubPageCategoryMark() {}

    protected SubPageCategoryMark(Parcel in) {
        this.id = in.readLong();
        this.marks = in.createTypedArrayList(Mark.CREATOR);
        this.name = in.readString();
        this.img = in.readString();
    }

    public static final Parcelable.Creator<SubPageCategoryMark> CREATOR = new Parcelable
            .Creator<SubPageCategoryMark>() {
        @Override
        public SubPageCategoryMark createFromParcel(Parcel source) {return new SubPageCategoryMark(source);}

        @Override
        public SubPageCategoryMark[] newArray(int size) {return new SubPageCategoryMark[size];}
    };
}