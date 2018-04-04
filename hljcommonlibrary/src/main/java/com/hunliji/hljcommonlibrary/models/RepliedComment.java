package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/7/27.
 * 回复Model
 */
public class RepliedComment implements Parcelable {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "entity_id")
    private long entityId;
    @SerializedName(value = "user")
    private Author user;
    @SerializedName(value = "reply_user")
    private Author replyUser;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "content")
    private String content;
    @SerializedName(value = "appeal_status")
    private int appealStatus;//0 待审核 1 已处理 2未发起申诉

    public int getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(int appealStatus) {
        this.appealStatus = appealStatus;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.entityId);
        dest.writeParcelable(this.user, flags);
        dest.writeParcelable(this.replyUser, flags);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeString(this.content);
        dest.writeInt(this.appealStatus);
    }

    public RepliedComment() {}

    protected RepliedComment(Parcel in) {
        this.id = in.readLong();
        this.entityId = in.readLong();
        this.user = in.readParcelable(Author.class.getClassLoader());
        this.replyUser = in.readParcelable(Author.class.getClassLoader());
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.content = in.readString();
        this.appealStatus = in.readInt();
    }


    public static final Creator<RepliedComment> CREATOR = new Creator<RepliedComment>() {
        @Override
        public RepliedComment createFromParcel(Parcel source) {return new RepliedComment(source);}

        @Override
        public RepliedComment[] newArray(int size) {return new RepliedComment[size];}
    };


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Author getUser() {
        if (user == null) {
            user = new Author();
        }
        return user;
    }

    public void setUser(Author user) {
        this.user = user;
    }

    public Author getReplyUser() {
        if (replyUser == null) {
            replyUser = new Author();
        }
        return replyUser;
    }

    public void setReplyUser(Author replyUser) {
        this.replyUser = replyUser;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return TextUtils.isEmpty(content) ? "" : content.trim();
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getEntityId() {
        return entityId;
    }
}