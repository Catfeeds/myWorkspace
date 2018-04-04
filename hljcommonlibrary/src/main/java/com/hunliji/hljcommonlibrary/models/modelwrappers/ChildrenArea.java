package com.hunliji.hljcommonlibrary.models.modelwrappers;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.AreaLabel;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mo_yu on 2017/8/1.通过省获取下属城市信息
 */

public class ChildrenArea extends AreaLabel implements Parcelable {
    @SerializedName("children")
    private List<ChildrenArea> childrenAreas;
    private transient List<String> childrenNames;

    public List<ChildrenArea> getChildrenAreas() {
        return childrenAreas;
    }

    public void setChildrenAreas(List<ChildrenArea> childrenAreas) {
        this.childrenAreas = childrenAreas;
    }

    public List<String> getChildrenNames() {
        if (!CommonUtil.isCollectionEmpty(childrenNames)) {
            return childrenNames;
        }
        if (!CommonUtil.isCollectionEmpty(childrenAreas)) {
            childrenNames = new ArrayList<>();
            for (ChildrenArea childrenArea : childrenAreas) {
                childrenNames.add(childrenArea.getName());
            }
        }
        return childrenNames;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {dest.writeTypedList(this.childrenAreas);}

    public ChildrenArea() {}

    protected ChildrenArea(Parcel in) {
        this.childrenAreas = in.createTypedArrayList(ChildrenArea.CREATOR);
    }

    public static final Parcelable.Creator<ChildrenArea> CREATOR = new Parcelable
            .Creator<ChildrenArea>() {
        @Override
        public ChildrenArea createFromParcel(Parcel source) {return new ChildrenArea(source);}

        @Override
        public ChildrenArea[] newArray(int size) {return new ChildrenArea[size];}
    };
}
