package com.hunliji.hljcommonlibrary.models.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * 聊天Channel
 * Created by wangtao on 2018/1/5.
 */

public class WSChannel extends RealmObject {

    @PrimaryKey
    @Index
    private String key; //Channel主键 userId+"#"+chatUser.getId();
    @Index
    private long userId;
    private WSChatAuthor chatUser;
    private WSChat lastMessage;
    private int unreadMessageCount;
    private int unreadTrackCount;
    private ChannelStick stick;
    private Date time;
    private ChatDraft draft;

    public WSChannel(
            long userId, WSChatAuthor chatUser, WSChat lastMessage) {
        this.userId = userId;
        this.key = getWSChannelKey(userId, chatUser.getId());
        this.lastMessage = lastMessage;
        this.chatUser = chatUser;
        this.time = lastMessage.getCreatedAt();
    }

    public WSChannel() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public WSChatAuthor getChatUser() {
        return chatUser;
    }

    public void setChatUser(WSChatAuthor chatUser) {
        this.chatUser = chatUser;
    }

    public WSChat getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(WSChat lastMessage) {
        this.lastMessage = lastMessage;
        this.time = lastMessage.getCreatedAt();
    }

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public int getUnreadTrackCount() {
        return unreadTrackCount;
    }

    public void setUnreadTrackCount(int unreadTrackCount) {
        this.unreadTrackCount = unreadTrackCount;
    }

    public void setNewsCount(WSChat chat) {
        switch (chat.getKind()) {
            case WSChat.TRACK:
                unreadTrackCount++;
                break;
            default:
                unreadMessageCount++;
                break;
        }
    }

    public ChatDraft getDraft() {
        return draft;
    }

    public void setDraft(ChatDraft draft) {
        this.draft = draft;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public ChannelStick getStick() {
        return stick;
    }

    public void setStick(ChannelStick stick) {
        this.stick = stick;
    }

    public static String getWSChannelKey(long userId, long chatUserId) {
        return userId + "#" + chatUserId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
