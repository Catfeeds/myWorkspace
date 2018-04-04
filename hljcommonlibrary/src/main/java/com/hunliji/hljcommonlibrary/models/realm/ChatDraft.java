package com.hunliji.hljcommonlibrary.models.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wangtao on 2017/11/8.
 */

public class ChatDraft extends RealmObject {

    @PrimaryKey
    private String key;
    private String content;
    private Date date;

    @Ignore
    private long userId;
    @Ignore
    private long sessionId;

    public ChatDraft() {
    }


    public ChatDraft(long userId, long sessionId, String content) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.key = getWSChatKey(userId, sessionId);
        this.content = content;
        this.date = new Date();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getKey() {
        return key;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public long getSessionId() {
        return sessionId;
    }

    public long getUserId() {
        return userId;
    }

    public static String getWSChatKey(long userId, long sessionId) {
        return "ws" + userId + "-" + sessionId;
    }
}
