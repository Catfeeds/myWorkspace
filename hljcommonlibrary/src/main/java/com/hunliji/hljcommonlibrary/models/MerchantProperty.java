package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_bin on 2017/3/30.
 */
public class MerchantProperty extends Label {
    @SerializedName(value = "children")
    private List<MerchantProperty> children;
    @SerializedName(value = "mark_id")
    private long markId;
    private String icon;

    public List<MerchantProperty> getChildren() {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<MerchantProperty> children) {
        this.children = children;
    }

    public long getMarkId() {
        return markId;
    }

    public void setMarkId(long markId) {
        this.markId = markId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.children);
        dest.writeLong(this.markId);
        dest.writeString(this.icon);
    }

    public MerchantProperty() {}

    protected MerchantProperty(Parcel in) {
        super(in);
        this.children = in.createTypedArrayList(MerchantProperty.CREATOR);
        this.markId = in.readLong();
        this.icon = in.readString();
    }

    public static final Creator<MerchantProperty> CREATOR = new Creator<MerchantProperty>() {
        @Override
        public MerchantProperty createFromParcel(Parcel source) {
            return new MerchantProperty(source);
        }

        @Override
        public MerchantProperty[] newArray(int size) {return new MerchantProperty[size];}
    };
}