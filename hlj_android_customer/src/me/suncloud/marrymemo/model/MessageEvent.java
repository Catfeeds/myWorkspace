package me.suncloud.marrymemo.model;

import android.support.annotation.Nullable;

/**
 * Created by werther on 15/8/20.
 * 专门用于BusEvent传递事件的event类
 */
public class MessageEvent {

    private int type;
    private Object object;

    public class EventType {
        /**
         * 1 => shop order refresh, object 是 ProductOrder 类型
         */
        public static final int PRODUCT_ORDER_REFRESH_WITH_OBJECT = 1;
        /**
         * 2 => shop order refresh 更新标记,只是更新消息标记,object没有值
         */
        public static final int PRODUCT_ORDER_REFRESH_FLAG = 2;
        /**
         * 3 => 拉取通知完成,object 新消息数 Integer
         */
        public static final int NEW_NOTIFICATION_COUNT = 3;
        /**
         * 4 => 拉取气泡完成
         */
        public static final int NEW_BUBBLE_MESSAGE = 4;
        /**
         * 5 => car order refresh, object 是 CarOrder 类型
         */
        public static final int CAR_ORDER_REFRESH_WITH_OBJECT = 5;
        /**
         * 6 => car order refresh 更新标记,object没有值
         */
        public static final int CAR_ORDER_REFRESH_FLAG = 6;
        /**
         * 7 => card 的音乐或模板更新标记,object没有值
         */
        public static final int CARD_MUSIC_TEMPLATE_UPDATE_FLAG = 7;
        /**
         * 8 => 环形客服发送新消息，object没有值EMMessageChat
         */
        public static final int EMCHAT_NEW_MESSAGE_FLAG = 8;
        /**
         * 9 => service order refresh, object 是 NewOrderPacket类型
         */
        public static final int SERVICE_ORDER_REFRESH_WITH_OBJECT = 9;
        /**
         * 10 => service order refresh 更新标记,object没有值
         */
        public static final int SERVICE_ORDER_REFRESH_FLAG = 10;
        /**
         * 11 => service order list 页面退款订单数增加1
         */
        public static final int SERVICE_ORDER_NEW_REFUND_COUNT = 11;
        /**
         * 12 => 定制套餐订单列表刷新,object是CustomSetmealOrder类型
         */
        public static final int CUSTOM_SETMEAL_ORDER_REFRESH_WITH_OBJECT = 12;
        /**
         * 13 => 定制套餐订单列表刷新标记
         */
        public static final int CUSTOM_SETMEAL_ORDER_REFRESH_FLAG = 13;
        /**
         * 14 => 定制套餐退款单数增加1
         */
        public static final int CUSTOM_SETMEAL_ORDER_NEW_REFUND_COUNT = 14;
        /**
         * 15 => 销毁NewLoginActivity
         */
        public static final int DISTROY_NEW_LOGIN_ACTIVITY = 15;

        /**
         * 定制套餐退款订单状态修改刷新标记
         */
        public static final int CUSTOM_REFUND_ORDER_FLAG = 16;

        /**
         * 个人中心页面 通知金融条目更新
         * @deprecated  转到rn
         */
        public static final int FINANCIAL_MARKET = 17;

        /**
         * 商品退款详情页面刷新标志
         */
        public static final int PRODUCT_REFUND_DETAIL_REFRESH_FLAG = 18;
        /**
         * 销毁商家评论页 同时发送评论成功的标志给NewMerchantActivity
         */
        public static final int COMMENT_MERCHANT_ACTIVITY = 19;
        /*
         * 20 => cardV2 的音乐或模板更新标记,object没有值
         */
        public static final int CARD_THEMEV2_UPDATE_FLAG = 20;
        /**
         * 21 => cardV2 内容更新通知，object cardV2
         */
        public static final int CARD_UPDATE_FLAG = 21;
        /**
         * 22 => 关注频道后，关注列表刷新标志
         */
        public static final int COMMUNITY_CHANNEL_FOLLOW_FLAG = 22;
        /**
         * 23 结婚预算  未预算过的用户 第一次预算后进入结果页 关闭 activity
         */
        public static final int CLOSE_BUDGET = 23;
        /**
         * 24 收到新消息
         */
        public static final int NEW_MSG = 24;

        /**
         * 登录检测
         */
        public static final  int LOGINCHECK = 25;

        /**
         * 请帖更新
         */
        public static final int OLD_CARD_UPDATE = 26;
        /**
         * 请帖页更新
         */
        public static final int OLD_CARD_PAGE_UPDATE = 27;
        /**
         * 跟新体验店列表
         */
        public static final int COMMENT_TEST_STORE_ACTIVITY = 28;
        /**
         * 跟新统筹师评论列表
         */
        public static final int COMMENT_PLANNER_ACTIVITY = 29;
    }

    /**
     * 构造一个用于BusEvent消息传递的参数
     *
     * @param type   消息类型,MessageEvent.EventType里面取
     * @param object 传递的消息本身
     */
    public MessageEvent(int type, @Nullable Object object) {
        this.type = type;
        this.object = object;
    }


    public int getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }
}
