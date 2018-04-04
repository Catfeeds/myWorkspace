package com.hunliji.cardmaster.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Poster;

/**
 * Created by wangtao on 2017/3/31.
 */

public class PushData implements Parcelable {

    public static final int INDEX = 0;
    public static final int POSTER = 4;

    private int type;
    private int property;
    private long forwardId;
    private String path;

    private String title;
    @SerializedName(value = "content",alternate = "message")
    private String content;

    private int index;//另一种格式推送 系统通知消息

    private String taskId;
    private String messageId;

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTaskId() {
        return taskId;
    }


    public String getMessageId() {
        return messageId;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Poster getPoster() {
        Poster poster = new Poster();
        poster.setTargetId(forwardId);
        poster.setPath(path);
        poster.setTargetType(property);
        return poster;
    }

    public int getIndex() {
        return index;
    }

    public PushData() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.property);
        dest.writeLong(this.forwardId);
        dest.writeString(this.path);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeInt(this.index);
        dest.writeString(this.taskId);
        dest.writeString(this.messageId);
    }

    protected PushData(Parcel in) {
        this.type = in.readInt();
        this.property = in.readInt();
        this.forwardId = in.readLong();
        this.path = in.readString();
        this.title = in.readString();
        this.content = in.readString();
        this.index = in.readInt();
        this.taskId = in.readString();
        this.messageId = in.readString();
    }

    public static final Creator<PushData> CREATOR = new Creator<PushData>() {
        @Override
        public PushData createFromParcel(Parcel source) {return new PushData(source);}

        @Override
        public PushData[] newArray(int size) {return new PushData[size];}
    };
}
