package me.suncloud.marrymemo.model.marry;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hua_rong on 2017/11/7
 * 结婚记账 分类
 */

public class RecordBook implements Parcelable {

    @SerializedName(value = "createdAt")
    private DateTime createdAt;
    private boolean deleted;
    private boolean hidden;
    private long id;
    @SerializedName(value = "parent_id")
    private long parentId;
    private String title;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    private List<RecordBook> children;
    @SerializedName(value = "image_path")
    private String imagePath;
    @SerializedName(value = "selected_image_path")
    private String selectedImagePath;
    private boolean isSelect;//本地属性
    private RecordBook parent;


    public RecordBook(String title) {
        this.title = title;
    }

    public RecordBook getParent() {
        if (parent == null) {
            parent = new RecordBook();
        }
        return parent;
    }

    public void setParent(RecordBook parent) {
        this.parent = parent;
    }

    public String getSelectedImagePath() {
        return selectedImagePath;
    }

    public void setSelectedImagePath(String selectedImagePath) {
        this.selectedImagePath = selectedImagePath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<RecordBook> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public void setChildren(List<RecordBook> children) {
        this.children = children;
    }


    public RecordBook() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hidden ? (byte) 1 : (byte) 0);
        dest.writeLong(this.id);
        dest.writeLong(this.parentId);
        dest.writeString(this.title);
        HljTimeUtils.writeDateTimeToParcel(dest, this.updatedAt);
        dest.writeTypedList(this.children);
        dest.writeString(this.imagePath);
        dest.writeString(this.selectedImagePath);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.parent, flags);
    }

    protected RecordBook(Parcel in) {
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.deleted = in.readByte() != 0;
        this.hidden = in.readByte() != 0;
        this.id = in.readLong();
        this.parentId = in.readLong();
        this.title = in.readString();
        this.updatedAt = HljTimeUtils.readDateTimeToParcel(in);
        this.children = in.createTypedArrayList(RecordBook.CREATOR);
        this.imagePath = in.readString();
        this.selectedImagePath = in.readString();
        this.isSelect = in.readByte() != 0;
        this.parent = in.readParcelable(RecordBook.class.getClassLoader());
    }

    public static final Creator<RecordBook> CREATOR = new Creator<RecordBook>() {
        @Override
        public RecordBook createFromParcel(Parcel source) {return new RecordBook(source);}

        @Override
        public RecordBook[] newArray(int size) {return new RecordBook[size];}
    };
}
