package com.hunliji.hljcommonlibrary.models.realm;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.chat_ext_object.WSExtObject;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Suncloud on 2016/10/11.
 */

public class WSChat extends RealmObject {

    @PrimaryKey
    @SerializedName(value = "_id", alternate = "id")
    private String idStr;

    @SerializedName("to")
    private long toId;
    @SerializedName("from")
    private long fromId;
    @Index
    private long sessionId;
    private String channel;
    @Index
    private String kind;
    private String content;
    @SerializedName("created_at")
    private Date createdAt;

    @Ignore
    @SerializedName("media_content")
    private JsonElement mediaContent;
    @Ignore
    @SerializedName("product_content")
    private JsonElement productContent;
    @Ignore
    @SerializedName("ext")
    private JsonElement extContent;
    @Ignore
    private WSMedia media;
    @Ignore
    private WSProduct product;
    @Ignore
    private WSExtObject extObject;

    private String mediaStr;
    private String productStr;
    private String extStr;

    private WSChatAuthor speaker;
    @SerializedName("speaker_to")
    private WSChatAuthor speakerTo;

    @SerializedName("read_time")
    private Date readTime; //消息阅读时间 未读为null

    private int source; //消息渠道 1普通2轻松聊3反向私信4自动回复5聚客宝6智能接待7案例预览

    private boolean unRead;
    @Index
    private boolean isNew;
    @Index
    private long userId;

    private boolean isError;
    @Ignore
    private boolean isSending;

    private String wsChannelKey; //本地字段标志数据迁移到 WSChannel
    @Ignore
    public static final String TEXT = "text";
    @Ignore
    public static final String IMAGE = "image";
    @Ignore
    public static final String VOICE = "voice";
    @Ignore
    public static final String WORK_OR_CASE = "opu";
    @Ignore
    public static final String CUSTOM_MEAL = "custom_meal";
    @Ignore
    public static final String PRODUCT = "product";
    @Ignore
    public static final String TRACK = "track"; // 专门用于表示用户在聊天上下文的行为轨迹，比如说从套餐页面进入聊天
    @Ignore
    public static final String TIPS = "tips"; // 服务器自行发送的商家提示tips
    @Ignore
    public static final String LOCATION = "location"; // 定位
    @Ignore
    public static final String MERCHANT_STATS = "merchant_stats"; // 商家私信统计信息
    /**
     * 商家发过来的提示消息作为新增类型hints，之后的提示类消息hints与tips类型共用
     * 需要默认tips视图类型的消息归为tips，除此之外为hints
     */
    @Ignore
    public static final String HINTS = "hints";

    //消息渠道 1普通2轻松聊3反向私信4自动回复5聚客宝6智能接待7案例预览
    public static class Source {
        public static final int CHAT_SOURCE_NORMAL = 1;
        public static final int CHAT_SOURCE_FREE_CHAT = 2;
        public static final int CHAT_SOURCE_REVERSE_MESSAGE = 3;
        public static final int CHAT_SOURCE_AUTO_REPLY = 4;
        public static final int CHAT_SOURCE_ACTIVITY = 5;
        public static final int CHAT_SOURCE_SMART_RECEPTION = 6;
        public static final int CHAT_SOURCE_CASE_PREVIEW = 7;
    }

    public WSChat() {
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
    }

    public long getFromId() {
        return fromId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        if (createdAt == null) {
            createdAt = new Date();
        }
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public WSMedia getMedia() {
        if (media != null) {
            return media;
        }
        if (TextUtils.isEmpty(mediaStr) && mediaContent != null) {
            mediaStr = mediaContent.toString();
        }
        if (!TextUtils.isEmpty(mediaStr)) {
            try {
                media = new Gson().fromJson(mediaStr, WSMedia.class);
            } catch (Exception e) {
                media = new WSMedia();
                e.printStackTrace();
            }
        }
        return media;
    }

    public void setMedia(WSMedia media) {
        this.media = media;
        this.mediaStr = new Gson().toJson(media);
    }

    public WSProduct getProduct() {
        if (product != null) {
            return product;
        }
        if (TextUtils.isEmpty(productStr) && productContent != null) {
            productStr = productContent.toString();
        }
        if (!TextUtils.isEmpty(productStr)) {
            try {
                product = new Gson().fromJson(productStr, WSProduct.class);
            } catch (Exception e) {
                product = new WSProduct();
                e.printStackTrace();
            }
        }
        return product;
    }

    public void setProduct(WSProduct product) {
        this.product = product;
        this.productStr = new Gson().toJson(product);
    }

    public void setExtContent(WSExtObject extContent) {
        this.extObject = extContent;
        this.extStr = new Gson().toJson(extContent);
    }

    public WSChatAuthor getSpeaker() {
        return speaker;
    }

    public void setSpeaker(WSChatAuthor speaker) {
        this.speaker = speaker;
    }

    public WSChatAuthor getSpeakerTo() {
        return speakerTo;
    }

    public void setSpeakerTo(WSChatAuthor speakerTo) {
        this.speakerTo = speakerTo;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isUnRead() {
        return unRead;
    }

    public void setUnRead(boolean unRead) {
        this.unRead = unRead;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public boolean isSending() {
        return isSending;
    }

    public void setSending(boolean sending) {
        isSending = sending;
    }

    public boolean isCurrentUser() {
        return userId == fromId;
    }

    public void setMediaStr(String mediaStr) {
        this.mediaStr = mediaStr;
    }

    public void setProductStr(String productStr) {
        this.productStr = productStr;
    }

    public String getSessionName() {
        String name = null;
        if (fromId == userId && speakerTo != null) {
            name = speakerTo.getName();
        } else if (toId == userId && speaker != null) {
            name = speaker.getName();
        }
        if (TextUtils.isEmpty(name)) {
            name = "temp";
        }
        return name;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public WSExtObject getExtObject(Gson gson) {
        if (extObject != null) {
            return extObject;
        }
        if (TextUtils.isEmpty(extStr) && extContent != null) {
            extStr = extContent.toString();
        }
        if (!TextUtils.isEmpty(extStr)) {
            try {
                extObject = gson.fromJson(extStr, WSExtObject.class);
            } catch (Exception e) {
                extObject = new WSExtObject();
                e.printStackTrace();
            }
        }
        return extObject;
    }

    public WSExtObject getExtObject() {
        return extObject;
    }

    public String getWsChannelKey() {
        return wsChannelKey;
    }

    public void setWsChannelKey(String wsChannelKey) {
        this.wsChannelKey = wsChannelKey;
    }


    /**
     * @param userId 用户id
     */
    public void onRealmChange(long userId) {
        this.userId = userId;
        if (fromId != userId) {
            sessionId = fromId;
        } else {
            sessionId = toId;
        }
        this.wsChannelKey = WSChannel.getWSChannelKey(userId, sessionId);
        if (mediaContent != null) {
            mediaStr = mediaContent.toString();
        }
        if (productContent != null) {
            productStr = productContent.toString();
        }
        if (extContent != null && !extContent.isJsonNull()) {
            if (extContent.isJsonPrimitive()) {
                try {
                    extStr = extContent.getAsString();
                } catch (Exception e) {
                    extStr = extContent.toString();
                }
            } else {
                extStr = extContent.toString();
            }
        }
        unRead = readTime == null || readTime.getTime() <= 0;
    }

    /**
     * 服务器 ChannelMessage 转换
     * <p/>
     * ChannelMessage 只有speaker 需要补全speakerTo
     * <p/>
     *
     * @param userId
     * @param user
     * @param chatUser
     * @param channel
     */
    public void onChannelMessage(
            long userId, WSChatAuthor user, WSChatAuthor chatUser, String channel) {
        this.channel = channel;
        if (speaker.getId() == userId) {
            speaker = user;
            speakerTo = chatUser;
        } else {
            speaker = chatUser;
            speakerTo = user;
        }
        fromId = speaker.getId();
        toId = speakerTo.getId();
        onRealmChange(userId);
    }
}
