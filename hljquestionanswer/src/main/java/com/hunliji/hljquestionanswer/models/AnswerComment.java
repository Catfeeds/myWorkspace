package com.hunliji.hljquestionanswer.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2016/8/22.回答详情评论列表
 */
public class AnswerComment implements Parcelable {

    @SerializedName(value = "id")
    long id;//评论id
    @SerializedName(value = "content")
    String content;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "deleted")
    boolean deleted;//是否删除
    @SerializedName(value = "is_like")
    boolean isLike;//是否喜欢
    @SerializedName(value = "likes_count")
    int likesCount;
    @SerializedName(value = "user")
    QaAuthor user;
    @SerializedName(value = "reply")
    AnswerComment reply;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public QaAuthor getUser() {
        return user;
    }

    public void setUser(QaAuthor user) {
        this.user = user;
    }

    public AnswerComment getReply() {
        return reply;
    }

    public void setReply(AnswerComment reply) {
        this.reply = reply;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public AnswerComment() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.content);
        dest.writeSerializable(this.createdAt);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isLike ? (byte) 1 : (byte) 0);
        dest.writeInt(this.likesCount);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.reply, flags);
    }

    protected AnswerComment(Parcel in) {
        this.id = in.readLong();
        this.content = in.readString();
        this.createdAt = (DateTime) in.readSerializable();
        this.deleted = in.readByte() != 0;
        this.isLike = in.readByte() != 0;
        this.likesCount = in.readInt();
        this.user = in.readParcelable(QaAuthor.class.getClassLoader());
        this.reply = in.readParcelable(AnswerComment.class.getClassLoader());
    }

    public static final Creator<AnswerComment> CREATOR = new Creator<AnswerComment>() {
        @Override
        public AnswerComment createFromParcel(Parcel source) {return new AnswerComment(source);}

        @Override
        public AnswerComment[] newArray(int size) {return new AnswerComment[size];}
    };
}
