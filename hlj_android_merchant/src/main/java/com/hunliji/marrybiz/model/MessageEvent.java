package com.hunliji.marrybiz.model;

/**
 * Created by werther on 15/8/20.
 * 专门用于BusEvent传递事件的event类
 */
public class MessageEvent {
    // 用于标识不同的事件类型
    // 3 => 拉取通知完成,object 新消息数 Integer
    // 新增的自定义即可
    private int type;
    private Object object;

    public class EventType {
        /**
         * 3 => 拉取通知完成,object 新消息数 Integer
         */
        public static final int NEW_NOTIFICATION_COUNT = 3;
        /**
         * 4 =>  刷新私信数
         */
        public static final int NEW_MESSAGE_COUNT = 4;
        /**
         * 5 =>  更新单条客资 ,object 客资对象 ADVHMerchant,
         */
        public static final int ADVHMERCHANT_REFRESH_WITH_OBJECT = 5;
        /**
         * 6 =>  特权编辑更新 ,object 客资对象 Privilege,
         */
        public static final int PRIVILEGE_REFRESH_WITH_OBJECT = 6;
        /**
         * 7 => service order refresh, object 是 NewOrder类型
         */
        public static final int SERVICE_ORDER_REFRESH_WITH_OBJECT = 7;
        /**
         * 8 => custom order refresh, object 是 CustomSetmealOrder
         */
        public static final int CUSTOM_ORDER_REFRESH_WITH_OBJECT = 8;

        /**
         * 9 => 环形客服发送新消息，object没有值EMMessageChat
         */
        public static final int EMCHAT_NEW_MESSAGE_FLAG = 9;
        /**
         * 10 => 套餐标签修改刷新
         */
        public static final int WORK_MARKET_REFRESH_FLAG = 10;
    }

    public MessageEvent(int type, Object object) {
        this.type = type;
        this.object = object;
    }

    public MessageEvent(Object object) {
        this.object = object;
    }

    public int getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }
}
