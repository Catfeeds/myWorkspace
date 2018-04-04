package com.hunliji.hljcommonlibrary.models.product;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Label;

import java.util.List;

/**
 * 婚品分类
 * Created by chen_bin on 2017/8/1 0001.
 */
public class ShopCategory extends Label {
    @SerializedName(value = "parent_id")
    private long parentId;
    @SerializedName(value = "cover_image")
    private String coverImage;
    @SerializedName(value = "children")
    private List<ShopCategory> children;

    public List<ShopCategory> getChildren() {
        return children;
    }

    public void setChildren(List<ShopCategory> children) {
        this.children = children;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.parentId);
        dest.writeString(this.coverImage);
        dest.writeTypedList(this.children);
    }

    public ShopCategory() {}

    protected ShopCategory(Parcel in) {
        super(in);
        this.parentId = in.readLong();
        this.coverImage = in.readString();
        this.children = in.createTypedArrayList(ShopCategory.CREATOR);
    }

    public static final Creator<ShopCategory> CREATOR = new Creator<ShopCategory>() {
        @Override
        public ShopCategory createFromParcel(Parcel source) {return new ShopCategory(source);}

        @Override
        public ShopCategory[] newArray(int size) {return new ShopCategory[size];}
    };
}
