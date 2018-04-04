package com.hunliji.hljlivelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljlivelibrary.models.wrappers.LiveAuthor;

import org.joda.time.DateTime;

/**
 * Created by Suncloud on 2016/10/28.
 */

public class LiveMessage implements Parcelable {

    public transient static final int MSG_KIND_TEXT = 1; // 文本信息,包含图片
    public transient static final int MSG_KIND_AUDIO = 2; // 语音信息
    public transient static final int MSG_KIND_VIDEO = 3; // 视频
    public transient static final int MSG_KIND_RED_PACKET = 4; // 红包
    public transient static final int MSG_KIND_COUPON = 5; // 优惠券

    private long id;
    private long clientMessageId;
    @SerializedName("content")
    private String contentStr;
    private LiveContent liveContent;
    @SerializedName("user_info")
    private LiveAuthor user;
    private DateTime time;
    private boolean stick;
    private boolean deleted;
    @SerializedName("room_type")
    private int roomType;
    @SerializedName("msg_kind")
    private int msgKind;
    @SerializedName("reply")
    private LiveMessage reply;

    private boolean isError;
    private boolean isSending;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LiveContent getLiveContent() {
        if (liveContent != null) {
            return liveContent;
        }
        if (!TextUtils.isEmpty(contentStr)) {
            if (msgKind > 5) {
                liveContent = new LiveContent("该内容暂不支持，请更新至最新版查看");
                return liveContent;
            }
            try {
                if (msgKind == MSG_KIND_COUPON) {
                    liveContent = LiveContentCoupon.parseLiveContentCoupon(contentStr);
                } else if (msgKind == MSG_KIND_RED_PACKET) {
                    liveContent = LiveContentRedpacket.parseLiveContentRedpacket(contentStr);
                } else {
                    liveContent = GsonUtil.getGsonInstance()
                            .fromJson(contentStr, LiveContent.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
                contentStr = null;
            }
        }
        return liveContent;
    }

    public String getContentStr() {
        return contentStr;
    }

    public int getMsgKind() {
        return msgKind;
    }

    public void setLiveContent(LiveContent liveContent) {
        this.liveContent = liveContent;
    }

    public LiveAuthor getUser() {
        return user;
    }

    public void setUser(LiveAuthor user) {
        this.user = user;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    /**
     * 要求有stick标识，并且有图片或者视频
     *
     * @return
     */
    public boolean isStick() {
        return stick && (!CommonUtil.isCollectionEmpty(getLiveContent().getAllMedias()));
    }

    public void setStick(boolean stick) {
        this.stick = stick;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getRoomType() {
        return roomType;
    }

    public void setRoomType(int roomType) {
        this.roomType = roomType;
    }

    public LiveMessage getReply() {
        return reply;
    }

    public void setReply(LiveMessage reply) {
        this.reply = reply;
    }

    public boolean isSending() {
        return isSending;
    }

    public boolean isError() {
        return isError;
    }

    public void setSending(boolean sending) {
        isSending = sending;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public void setClientMessageId(long clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public long getClientMessageId() {
        return clientMessageId;
    }

    public String websocketSerialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("client_message_id", clientMessageId);
        jsonObject.addProperty("room_type", roomType);
        if (liveContent != null) {
            jsonObject.addProperty("content",
                    GsonUtil.getGsonInstance()
                            .toJson(liveContent));
            jsonObject.addProperty("msg_kind", liveContent.getKind());
        }
        if (reply != null) {
            jsonObject.addProperty("replyid", reply.getId());
        }
        return jsonObject.toString();
    }

    public LiveMessage() {}

    public LiveMessage(LiveContent liveContent, LiveAuthor user, int roomType, LiveMessage reply) {
        this.liveContent = liveContent;
        this.user = user;
        this.roomType = roomType;
        this.reply = reply;
        this.time = new DateTime();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.clientMessageId);
        dest.writeString(this.contentStr);
        dest.writeParcelable(this.liveContent, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeSerializable(this.time);
        dest.writeByte(this.stick ? (byte) 1 : (byte) 0);
        dest.writeByte(this.deleted ? (byte) 1 : (byte) 0);
        dest.writeInt(this.roomType);
        dest.writeParcelable(this.reply, flags);
    }

    protected LiveMessage(Parcel in) {
        this.id = in.readLong();
        this.clientMessageId = in.readLong();
        this.contentStr = in.readString();
        this.liveContent = in.readParcelable(LiveContent.class.getClassLoader());
        this.user = in.readParcelable(Author.class.getClassLoader());
        this.time = (DateTime) in.readSerializable();
        this.stick = in.readByte() != 0;
        this.deleted = in.readByte() != 0;
        this.roomType = in.readInt();
        this.reply = in.readParcelable(LiveMessage.class.getClassLoader());
    }

    public static final Creator<LiveMessage> CREATOR = new Creator<LiveMessage>() {
        @Override
        public LiveMessage createFromParcel(Parcel source) {return new LiveMessage(source);}

        @Override
        public LiveMessage[] newArray(int size) {return new LiveMessage[size];}
    };
}
