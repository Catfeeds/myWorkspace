package com.hunliji.hljcommonlibrary.models.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.AreaLabel;

/**
 * Created by mo_yu on 2017/8/1.上一级市或省
 */

public class ParentArea extends AreaLabel implements Parcelable {
    @SerializedName("parent")
    private ParentArea parentArea;

    public ParentArea getParentArea() {
        return parentArea;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.parentArea, flags);
    }

    public ParentArea() {}

    protected ParentArea(Parcel in) {
        this.parentArea = in.readParcelable(ParentArea.class.getClassLoader());
    }

    public static final Parcelable.Creator<ParentArea> CREATOR = new Parcelable
            .Creator<ParentArea>() {
        @Override
        public ParentArea createFromParcel(Parcel source) {return new ParentArea(source);}

        @Override
        public ParentArea[] newArray(int size) {return new ParentArea[size];}
    };
}
