package com.hunliji.hljchatlibrary.models;

/**
 * 私信中使用的事件通知
 * Created by wangtao on 2017/2/4.
 */

public class ChatRxEvent {

    private RxEventType type;
    private Object object;

    public enum RxEventType {
        WRITING_MESSAGE, //对方正在输入 object 对方 userId
        READ_MESSAGE, // 已读消息 object 已读消息所在 channelId
    }

    public ChatRxEvent(RxEventType type, Object object) {
        this.type = type;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public RxEventType getType() {
        return type;
    }
}
