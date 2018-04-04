package com.hunliji.hljcarlibrary.util;

import java.util.Random;

/**
 * Created by jinxin on 2018/1/16 0016.
 */

public class WeddingCarSupportUtil {

    /**
     * 从固定的几个提示语中随机取出一个
     *
     * @return
     */
    public static String getCarMerchantChatLinkMsg() {
        String[] messageArray = {"亲爱哒，对婚车租用有疑问就戳我哦！", "亲爱哒，婚车租用优惠多多！戳我小窗告诉你哦~",
                "小主，担心婚车没有档期？点我！纪小犀来帮你！", "不知道如何挑选婚车？点我！纪小犀来帮你！", "该怎么预订婚车？点我！纪小犀来帮你！",
                "好烦恼，提前多久订婚车最合适呢？戳我小窗告诉你哦~", "怎么办，哪款套餐最实惠？戳我小窗告诉你哦~", "怎么组合车队又大气又实惠？戳我小窗告诉你哦~",
                "哎呀，用车超时怎么办？纪小犀来帮你！", "哎呀，用车超过约定距离怎么办？纪小犀来帮你！", "预订婚车有优惠吗？戳我小窗告诉你哦~",
                "看晕了，如何挑选物美价廉的婚车？戳我小窗告诉你哦~", "婚车的费用如何计算？纪小犀在线为您解答！", "亲，婚车档期火爆抢购中，速速戳我预订哦！",
                "HELLO，我是您的专属婚车顾问，有什么能够帮助您的呢？"};
        Random r = new Random();
        int randomIndex = r.nextInt(messageArray.length);

        return messageArray[randomIndex];
    }
}
