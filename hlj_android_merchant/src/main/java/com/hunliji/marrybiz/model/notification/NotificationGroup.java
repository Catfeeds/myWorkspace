package com.hunliji.marrybiz.model.notification;

import com.hunliji.hljcommonlibrary.models.realm.Notification;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by wangtao on 2017/8/18.
 */

public enum NotificationGroup implements Serializable {

    ORDER(Notification.NotificationType.ORDER),

    RESERVATION(Notification.NotificationType.RESERVATION),

    COUPON(Notification.NotificationType.COUPON_RECEIVED),

    EVENT(Notification.NotificationType.EVENT),

    COMMUNITY(Notification.NotificationType.COMMUNITY,
            Notification.NotificationType.MERCHANT_FEED,
            Notification.NotificationType.NOTE_TYPE),

    COMMENT(Notification.NotificationType.ORDER_COMMENT),


    INCOME(Notification.NotificationType.INCOME),

    OTHER(true,
            Notification.NotificationType.ORDER,
            Notification.NotificationType.RESERVATION,
            Notification.NotificationType.EVENT,
            Notification.NotificationType.COMMUNITY,
            Notification.NotificationType.MERCHANT_FEED,
            Notification.NotificationType.NOTE_TYPE,
            Notification.NotificationType.ORDER_COMMENT,
            Notification.NotificationType.INCOME,
            Notification.NotificationType.COUPON_RECEIVED),;


    private Integer[] includeTypes;
    private boolean isNot;

    NotificationGroup(Integer... types) {
        this(false, types);
    }

    NotificationGroup(boolean isNot, Integer... types) {
        this.isNot = isNot;
        this.includeTypes = types;
    }

    public boolean isNot() {
        return isNot;
    }

    public Integer[] getIncludeTypes() {
        return includeTypes;
    }


    public boolean isIncluded(int type) {
        try {
            if (isNot) {
                return !Arrays.asList(includeTypes)
                        .contains(type);
            } else
                return Arrays.asList(includeTypes)
                        .contains(type);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
