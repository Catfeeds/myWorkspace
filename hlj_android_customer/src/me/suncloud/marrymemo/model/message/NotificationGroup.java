package me.suncloud.marrymemo.model.message;

import com.hunliji.hljcommonlibrary.models.realm.Notification;

/**
 * Created by luohanlin on 2017/12/28.
 */

/**
 * 手动将通知分组
 * 有新类型的通知的话，默认归类为系统通知，如需归入其他类型，需要手动更新对应数组和系统通知排除
 */
public enum NotificationGroup {
    // 订单动态
    ORDER(Notification.NotificationType.ORDER),

    // 互动消息
    COMMUNITY(Notification.NotificationType.COMMUNITY,
            Notification.NotificationType.NOTE_TYPE,
            Notification.NotificationType.SUB_PAGE),

    // 礼物礼金
    GIFT(Notification.NotificationType.GIFT),

    // 宾客回复
    SIGN(Notification.NotificationType.SIGN),

    // 结婚助手金融通知
    FINANCIAL(Notification.NotificationType.FINANCIAL),

    // 活动通知
    EVENT(Notification.NotificationType.EVENT),

    // 系统通知，除了以上所有归类的，其他都作为系统通知
    DEFAULT_SYSTEM_NOTICE(true,
            Notification.NotificationType.ORDER,
            Notification.NotificationType.COMMUNITY,
            Notification.NotificationType.NOTE_TYPE,
            Notification.NotificationType.SUB_PAGE,
            Notification.NotificationType.GIFT,
            Notification.NotificationType.SIGN,
            Notification.NotificationType.FINANCIAL,
            Notification.NotificationType.EVENT,
            Notification.NotificationType.UNDEFINED),;

    private Integer[] includeTypes;
    private boolean isNot; // 排除取反

    NotificationGroup(Integer... types) {
        this.includeTypes = types;
    }

    NotificationGroup(boolean isNot, Integer... types) {
        this.isNot = isNot;
        this.includeTypes = types;
    }

    public Integer[] getIncludeTypes() {
        return includeTypes;
    }

    public boolean isNot() {
        return isNot;
    }
}

