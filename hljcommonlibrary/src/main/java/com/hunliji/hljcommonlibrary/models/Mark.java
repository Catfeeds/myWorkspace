package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/8/31.
 */
public class Mark implements Parcelable {

    public static final int MARK_TYPE_WORK = 2; // 套餐
    public static final int MARK_TYPE_PRODUCT = 5; // 婚品

    @SerializedName(value = "id", alternate = "mark_id")
    private long id;
    @SerializedName(value = "user_id")
    private long userId;
    @SerializedName(value = "name", alternate = {"mark_name", "title"})
    private String name;
    @SerializedName(value = "describe")
    private String describe;
    @SerializedName(value = "is_red")
    private boolean isRed;
    @SerializedName(value = "image_path", alternate = {"mark_image_path", "img"})
    private String imagePath;
    @SerializedName(value = "icon")
    private int iconType;
    @SerializedName(value = "marked_type") // 被标注的model的类型
    private int markedType;
    @SerializedName("markable_id") // 被标注的model的id
    private long markableId;
    @SerializedName(value = "high_light")
    private boolean isHighLight;//是否高亮显示 true 高亮 false 不高亮
    @SerializedName(value = "high_light_color", alternate = "color")
    private String highLightColor;
    @SerializedName(value = "mark_hidden")
    private boolean isMarkHidden;
    @SerializedName(value = "is_local")
    private boolean isLocal;//1是本地标签0不是

    private transient boolean isSelected;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isRed() {
        return isRed;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getIconType() {
        return iconType;
    }

    public int getMarkedType() {
        return markedType;
    }

    public String getHighLightColor() {
        return highLightColor;
    }

    public void setHighLightColor(String highLightColor) {
        this.highLightColor = highLightColor;
    }

    public boolean isHighLight() {
        return isHighLight;
    }

    public void setHighLight(boolean highLight) {
        isHighLight = highLight;
    }

    public boolean isMarkHidden() {
        return isMarkHidden;
    }

    public void setMarkHidden(boolean markHidden) {
        this.isMarkHidden = markHidden;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getMarkableId() {
        return markableId;
    }

    public Mark() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeString(this.name);
        dest.writeString(this.describe);
        dest.writeByte(this.isRed ? (byte) 1 : (byte) 0);
        dest.writeString(this.imagePath);
        dest.writeInt(this.iconType);
        dest.writeInt(this.markedType);
        dest.writeByte(this.isHighLight ? (byte) 1 : (byte) 0);
        dest.writeString(this.highLightColor);
        dest.writeByte(this.isMarkHidden ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLocal ? (byte) 1 : (byte) 0);
        dest.writeLong(this.markableId);
    }

    protected Mark(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.name = in.readString();
        this.describe = in.readString();
        this.isRed = in.readByte() != 0;
        this.imagePath = in.readString();
        this.iconType = in.readInt();
        this.markedType = in.readInt();
        this.isHighLight = in.readByte() != 0;
        this.highLightColor = in.readString();
        this.isMarkHidden = in.readByte() != 0;
        this.isLocal = in.readByte() != 0;
        this.markableId = in.readLong();
    }

    public static final Creator<Mark> CREATOR = new Creator<Mark>() {
        @Override
        public Mark createFromParcel(Parcel source) {return new Mark(source);}

        @Override
        public Mark[] newArray(int size) {return new Mark[size];}
    };
}
