package com.hunliji.hljlivelibrary.models;

import android.support.annotation.Nullable;

/**
 * Created by Suncloud on 2016/10/31.
 */

public class LiveRxEvent {

    private RxEventType type;
    private long channelId;
    private Object object;

    public enum RxEventType {
        CHANNEL_UPDATE, //频道消息更新 object LiveChannel 更新Channel 状态和在线用户数
        NEW_MESSAGE, //websocket消息 object 消息 LiveSocketData
        SEND_MESSAGE, //消息发送 object LiveMessage
        REPLY_MESSAGE, //回复消息 object LiveMessage
        RESEND_MESSAGE, //错误消息重发 object LiveMessage
        LIVE_NEWS, //直播消息提醒 object int 直播新消息数
        CHAT_NEWS, //聊天室消息提醒 object int 聊天新消息数
        CHANNEL_THREAD, //直播回顾 object CommunityThread 回顾帖子
        MERCHANT_UPDATE, // 直播介绍商家更新
        WORK_UPDATE, // 直播正在介绍的套餐更新
        PRODUCT_UPDATE, // 直播正在介绍的商品更新
        CLEAR_INTRODUCING, // 清空正在介绍中的商品
        MESSAGE_LIST_SCROLL_TOP, // 消息列表往上滑动
    }

    /**
     * 构造一个用于直播消息传递的参数
     *
     * @param type   消息类型,LiveRxEvent.RxEventType里面取
     * @param object 传递的消息本身
     */
    public LiveRxEvent(RxEventType type, long channelId, @Nullable Object object) {
        this.channelId = channelId;
        this.type = type;
        this.object = object;
    }

    public RxEventType getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public long getChannelId() {
        return channelId;
    }
}
