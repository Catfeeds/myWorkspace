package com.hunliji.hljcommonlibrary.models;

import android.support.annotation.Nullable;

/**
 * Created by mo_yu on 2017/12/25.
 * 专门用于BusEvent传递事件的event类
 */

public class MaintainEvent {

    private int type;
    private long millis;

    public class EventType {
        public static final int SERVICE_ERROR = 1;
        public static final int USER_TOKEN_ERROR = 2;
    }

    /**
     * 构造一个用于BusEvent消息传递的参数
     *
     * @param type   消息类型,MaintainEvent.EventType里面取
     * @param millis 传递的消息本身
     */
    public MaintainEvent(int type, @Nullable long millis) {
        this.type = type;
        this.millis = millis;
    }


    public int getType() {
        return type;
    }

    public long getMillis() {
        return millis;
    }
}
