package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by mo_yu on 2016/9/6.
 * banner通用model
 */
public class Poster implements Parcelable {
    private long id;
    @SerializedName(value = "target_id", alternate = {"forward_id"})
    private Long targetId;
    @SerializedName(value = "image_path")
    private String imagePath;
    private String title;
    private String desc;
    private String icon;
    @SerializedName(value = "target_url", alternate = {"url"})
    private String url;
    @SerializedName(value = "target_type", alternate = {"property"})
    private int targetType;
    private User user;
    private JSONObject extra;//poster跳转的时候 用来传递数据
    private String extention;//扩展字段 以后可能是json 谁用谁解析 这里做String处理
    private List<Mark> marks;
    private String cpm;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return imagePath;
    }

    public void setPath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTargetType() {
        return targetType;
    }

    public long getTargetId() {
        if (targetId == null) {
            targetId = 0L;
        }
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public String getDesc() {
        return desc;
    }

    public String getIcon() {
        return icon;
    }

    public User getUser() {
        return user;
    }

    public String getExtention() {
        return extention;
    }

    public List<Mark> getMarks() {
        return marks;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }

    public String getCpm() {
        return cpm;
    }

    public Poster() {}

    public Poster(Long targetId, String url, int targetType) {
        this.targetId = targetId;
        this.url = url;
        this.targetType = targetType;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeValue(this.targetId);
        dest.writeString(this.imagePath);
        dest.writeString(this.title);
        dest.writeString(this.desc);
        dest.writeString(this.icon);
        dest.writeString(this.url);
        dest.writeInt(this.targetType);
        dest.writeParcelable(this.user, flags);
        dest.writeString(this.extention);
        dest.writeTypedList(this.marks);
        dest.writeString(this.cpm);
    }

    protected Poster(Parcel in) {
        this.id = in.readLong();
        this.targetId = (Long) in.readValue(Long.class.getClassLoader());
        this.imagePath = in.readString();
        this.title = in.readString();
        this.desc = in.readString();
        this.icon = in.readString();
        this.url = in.readString();
        this.targetType = in.readInt();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.extention = in.readString();
        this.marks = in.createTypedArrayList(Mark.CREATOR);
        this.cpm = in.readString();
    }

    public static final Creator<Poster> CREATOR = new Creator<Poster>() {
        @Override
        public Poster createFromParcel(Parcel source) {
            return new Poster(source);
        }

        @Override
        public Poster[] newArray(int size) {return new Poster[size];}
    };
}
