package com.hunliji.hljcardlibrary;

import android.content.Context;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by mo_yu on 2017/6/13.
 * HljCommon这个类库中的一些Constants参数
 * 和一些常用的,暴露给使用者的静态方法
 */

public class HljCard {

    private static String CARD_HOST = "http://www.hunliji.com/";
    public static String invitationBankListUrl;
    public static String cardFaqUrl;
    public static String ecardTutorialUrl;
    public static boolean isFund;//礼金理财开关
    public static String fundQaUrl;//常见问题的地址
    public static String fundProtocolUrl;//礼金理财服务协议
    public static final String CARD_MASTER_MEMBER = "http://a.app.qq.com/o/simple.jsp?pkgname=me"
            + "" + ".suncloud.marrymemo&ckey=CK1381763704645";//请帖大师会员
    public static final String CARD_MASTER_RED_POCKET = "http://a.app.qq.com/o/simple" + "" + ""
            + ".jsp?pkgname=me" + "" + ".suncloud.marrymemo&ckey=CK1381758045671";//请帖大师 红包弹窗
    public static final String CARD_MASTER_MARRY_PREPARE = "http://a.app.qq.com/o/simple" + "" +
            ".jsp?pkgname=me.suncloud.marrymemo&ckey=CK1381758216264";//请帖大师结婚筹备
    public static double fundIncomeMax;//转入最大值
    public static double fundIncomeMin;//转入最小值

    public static String getEcardTutorialUrl() {
        return ecardTutorialUrl;
    }

    public static void setEcardTutorialUrl(String ecardTutorialUrl) {
        HljCard.ecardTutorialUrl = ecardTutorialUrl;
    }

    public static String getCardFaqUrl() {
        return cardFaqUrl;
    }

    public static void setCardFaqUrl(String cardFaqUrl) {
        HljCard.cardFaqUrl = cardFaqUrl;
    }

    public static String getInvitationBankListUrl() {
        return invitationBankListUrl;
    }

    public static void setInvitationBankListUrl(String invitationBankListUrl) {
        HljCard.invitationBankListUrl = invitationBankListUrl;
    }

    public static boolean isFund() {
        return isFund;
    }

    public static void setFund(boolean fund) {
        isFund = fund;
    }

    public static String getFundQaUrl() {
        return fundQaUrl;
    }

    public static void setFundQaUrl(String fundQaUrl) {
        HljCard.fundQaUrl = fundQaUrl;
    }

    public static String getFundProtocolUrl() {
        return fundProtocolUrl;
    }

    public static void setFundProtocolUrl(String fundProtocolUrl) {
        HljCard.fundProtocolUrl = fundProtocolUrl;
    }

    public static final String FONTS_FILE = "fonts.json";

    public static boolean isCustomer(Context context) {
        return CommonUtil.getAppType() == CommonUtil.PacketType.CUSTOMER;
    }

    public static boolean isCardMaster(Context context) {
        return CommonUtil.getAppType() == CommonUtil.PacketType.CARD_MASTER;
    }

    public static double getFundIncomeMax() {
        return fundIncomeMax;
    }

    public static void setFundIncomeMax(double fundIncomeMax) {
        HljCard.fundIncomeMax = fundIncomeMax;
    }

    public static double getFundIncomeMin() {
        return fundIncomeMin;
    }

    public static void setFundIncomeMin(double fundIncomeMin) {
        HljCard.fundIncomeMin = fundIncomeMin;
    }

    public static class RequestCode {
        public static final int REPLY_GIFT_CASH = 1;
        public static final int REPLY_CARD_REPLY = 2;//回复宾客回复
    }


    public static String getCardUrl(String path, Object... query) {
        if (query != null && query.length > 0) {
            return CARD_HOST + String.format(path, query);
        } else {
            return CARD_HOST + path;
        }
    }

    public static String getCardHost() {
        return CARD_HOST;
    }

    public static void setCardHost(String hljHost) {
        CARD_HOST = hljHost;
    }
}
