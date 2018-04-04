package me.suncloud.marrymemo.model.marry;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * Created by hua_rong on 2017/11/8
 * 结婚任务
 */

public class MarryTask implements Parcelable {

    private long id;
    private int status;//0未完成 1已完成
    private String title;
    @SerializedName(value = "category_id")
    private Long categoryId;
    @SerializedName(value = "template_id")
    private Long templateId;
    @SerializedName(value = "user_id")
    private long userId;
    @SerializedName(value = "to_ta")
    private int toTa;//0分配给我 1分配给她
    @SerializedName(value = "expire_at")
    private DateTime expireAt;//到期时间
    @SerializedName(value = "completed_at")
    private DateTime completedAt;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    private boolean deleted;
    private TaskCategory category;
    private Template template;//模板

    private boolean isGroup;//本地属性
    private boolean isLastLine;
    /**
     * 用来 区分 系统任务、近期任务、远期任务 、已完成任务、过期任务
     */
    private int type;

    public static final int TYPE_SYSTEM = 10;
    public static final int TYPE_RECENT = 20;
    public static final int TYPE_REMOTE = 30;
    public static final int TYPE_COMPLETED = 40;
    public static final int TYPE_EXPIRED = 50;

    public transient static final int STATUS_UNDONE = 0; //未完成
    public transient static final int STATUS_DONE = 1; //已完成

    public int getType() {

        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLastLine() {
        return isLastLine;
    }

    public void setLastLine(boolean lastLine) {
        isLastLine = lastLine;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getToTa() {
        return toTa;
    }

    public void setToTa(int toTa) {
        this.toTa = toTa;
    }


    public DateTime getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(DateTime expireAt) {
        this.expireAt = expireAt;
    }

    public DateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(DateTime completedAt) {
        this.completedAt = completedAt;
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

    public TaskCategory getCategory() {
        if (category == null) {
            category = new TaskCategory();
        }
        return category;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }


    public MarryTask() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.status);
        dest.writeString(this.title);
        dest.writeValue(this.categoryId);
        dest.writeValue(this.templateId);
        dest.writeLong(this.userId);
        dest.writeInt(this.toTa);
        HljTimeUtils.writeDateTimeToParcel(dest, this.expireAt);
        HljTimeUtils.writeDateTimeToParcel(dest, this.completedAt);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.category, flags);
        dest.writeParcelable(this.template, flags);
        dest.writeByte(this.isGroup ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLastLine ? (byte) 1 : (byte) 0);
        dest.writeInt(this.type);
    }

    protected MarryTask(Parcel in) {
        this.id = in.readLong();
        this.status = in.readInt();
        this.title = in.readString();
        this.categoryId = (Long) in.readValue(Long.class.getClassLoader());
        this.templateId = (Long) in.readValue(Long.class.getClassLoader());
        this.userId = in.readLong();
        this.toTa = in.readInt();
        this.expireAt = HljTimeUtils.readDateTimeToParcel(in);
        this.completedAt = HljTimeUtils.readDateTimeToParcel(in);
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.deleted = in.readByte() != 0;
        this.category = in.readParcelable(TaskCategory.class.getClassLoader());
        this.template = in.readParcelable(Template.class.getClassLoader());
        this.isGroup = in.readByte() != 0;
        this.isLastLine = in.readByte() != 0;
        this.type = in.readInt();
    }

    public static final Creator<MarryTask> CREATOR = new Creator<MarryTask>() {
        @Override
        public MarryTask createFromParcel(Parcel source) {return new MarryTask(source);}

        @Override
        public MarryTask[] newArray(int size) {return new MarryTask[size];}
    };
}
