package com.hunliji.hljchatlibrary.utils;

import com.hunliji.hljcommonlibrary.models.realm.WSChat;

import org.joda.time.DateTime;

import io.realm.Realm;

/**
 * Created by luohanlin on 2017/8/10.
 */

public class ChatUtil {

    /**
     * 判断用户是否在今天给商家发送过信息
     *
     * @param userId
     * @param merchantUserId
     * @return
     */
    public static boolean hasChatWithMerchant(long userId, long merchantUserId) {
        DateTime today = new DateTime();
        DateTime dateTime = today.withTimeAtStartOfDay();
        Realm realm = Realm.getDefaultInstance();

        boolean hasChat = realm.where(WSChat.class)
                .equalTo("fromId", userId)
                .equalTo("toId", merchantUserId)
                .greaterThanOrEqualTo("createdAt", dateTime.toDate())
                .count() > 0;

        realm.close();
        return hasChat;
    }
}
