package me.suncloud.marrymemo.model.finder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.RepliedComment;

/**
 * 专题评论的model
 * Created by chen_bin on 2016/9/13 0013.
 */
public class EntityComment extends RepliedComment implements Parcelable {
    @SerializedName(value = "is_like")
    private boolean isLike;
    @SerializedName(value = "deleted")
    private boolean isDeleted;
    @SerializedName(value = "likes_count")
    private int likesCount;
    @SerializedName(value = "reply")
    private EntityComment reply;

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.isLike ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDeleted ? (byte) 1 : (byte) 0);
        dest.writeInt(this.likesCount);
        dest.writeParcelable(this.reply, flags);
    }

    public EntityComment() {}

    protected EntityComment(Parcel in) {
        super(in);
        this.isLike = in.readByte() != 0;
        this.isDeleted = in.readByte() != 0;
        this.likesCount = in.readInt();
        this.reply = in.readParcelable(EntityComment.class.getClassLoader());
    }

    public static final Creator<EntityComment> CREATOR = new Creator<EntityComment>() {
        @Override
        public EntityComment createFromParcel(Parcel source) {return new EntityComment(source);}

        @Override
        public EntityComment[] newArray(int size) {return new EntityComment[size];}
    };

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public EntityComment getReply() {
        if (reply == null) {
            reply = new EntityComment();
        }
        return reply;
    }

    public void setReply(EntityComment reply) {
        this.reply = reply;
    }
}