package com.hunliji.cardmaster;

import com.hunliji.hljcardlibrary.HljCard;

/**
 * Created by wangtao on 2017/11/22.
 */

public class Constants {

    public static final boolean DEBUG = BuildConfig.APP_DEBUG;
    public static String HOST = "http://www.hunliji.com/"; // 默认数据服务器地址
    public static String APP_VERSION = BuildConfig.VERSION_NAME;

    public static void setHOST(String HOST) {
        Constants.HOST = HOST;
        HljCard.setCardHost(Constants.HOST);
    }

    //用户使用协议
    public static final String USER_PROTOCOL_URL = "https://www.hunliji.com/p/wedding/index" +
            ".php/Home/AppH5/UserProtocol";
    //用户隐私政策
    public static final String SECRET_PROTOCOL_URL = "https://www.hunliji.com/p/wedding/index" +
            ".php/Home/AppH5/UserPrivacyPolicy";
    public static final String INTRO_URL = "http://www.hunliji" + "" + "" +
            ".com/p/wedding/Public/wap/activity/invitation_member/index.html";//开通会员默认地址
    //购买保险
    public static final String PAY_INSURANCE = "p/wedding/index.php/home/APIUserInsurance/submit";


    public static final String WEIXINKEY = "wx54f77b3691b7814a";
    public static final String WEIXINSECRET = "e7e74969aebd5aca048b0e3420612435";
    public static final String QQKEY = "1106477367";
    public static final String WEIBOKEY = "3642622038";
    public static final String WEIBO_CALLBACK = "http://www.hunliji.com";

    public static final String DATA_CONFIG_FILE = "data_config.json";

}
