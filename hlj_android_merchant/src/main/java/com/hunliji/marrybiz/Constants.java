package com.hunliji.marrybiz;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by LuoHanLin on 14/10/22.
 */
public class Constants {
    public static final boolean DEBUG = BuildConfig.APP_DEBUG; // 开发模式是 true 的时候可以动态修改服务器地址
    public static final String APP_VERSION = BuildConfig.VERSION_NAME;
    public static final String QINIU_HOST = "http://marry.qiniudn.com/"; //
    // 默认七牛云服务器地址
    public static final String QINIU_UPLOAD_URL = "http://up.qiniu.com";
    public static final String LABEl_FROM = "?from=%s";
    public static final String VIDEO_URL = "?vframe/jpg/offset/1/rotate/auto|imageView/2/w/%s";
    public static final String VIDEO_URL_TEN =
            "?vframe/jpg/offset/10/rotate/auto|imageView2/2/w/%s";
    public static final String VIDEO_URL_TEN_H =
            "?vframe/jpg/offset/10/rotate/auto|imageView2/2/h/%s";
    public static final String VIDEO_URL_TEN2 = "?vframe/jpg/offset/10/rotate/auto";
    public static final String VIDEO_URL2 = "?vframe/jpg/offset/1/rotate/auto";
    public static final String PHOTO_URL = "?imageView2/2/w/%s/format/webp";
    public static final String PHOTO_URL2 = "?imageView2/2/w/%s/h/%s/format/webp";
    public static final String PHOTO_URL3 = "?imageView2/1/w/%s/h/%s/format/webp";
    public static final String PHOTO_URL4 = "?imageView2/2/h/%s/format/webp";
    public static final String PHOTO_URL5 = "?imageView2/2/w/%s/h/%s";
    /* User setting preferences files */
    public static final String PREF_FILE = "pref";
    public static final String USER_FILE = "user.json";
    public static final String CARD_FILE = "card.json";
    public static final String CURRENT_CARD_FILE = "currentcard.json";
    public static final String TAGS_FILE = "tags.json";
    public static final String CITIES_FILE = "cities.json";
    public static final String MY_CITIE_FILE = "city-{1}.json";
    public static final String LOCATION_CITIE_FILE = "city-{0}.json";
    public static final String THEME_FILE = "theme.json";
    public static final String CATEFORIES_FILE = "catefories.json";
    public static final String BANNERS_FILE = "banners.json";
    public static final String SPLASH_FILE = "splash2.json";
    public static final String PROPERTIES_FILE = "propertie.json";
    public static final String CARDS_FILE = "cards-{0}.json";
    public static final String QQ_FILE = "qq.json";
    public static final String WEIBO_FILE = "weibo.json";
    public static final String QR_CODE_FILE = "qrCode.jpg";
    public static final String AVATAR_FILE = "avatar.jpg";
    public static final String STORY_COVER_FILE = "sCover.jpg";
    public static final String VERSION_FILE = "version.json";
    public static final String ADDRESS_AREA_FILE = "address_area.json";
    public static final String DATA_CONFIG_FILE = "data_config.json";
    public static final String LINKS_FILE = "links.json";
    /* Thread Executor Define */
    public static final Executor THEADPOOL = Executors.newFixedThreadPool(10);
    public static final Executor CARDTHEADPOOL = Executors.newFixedThreadPool(5);
    public static final Executor LISTTHEADPOOL = Executors.newFixedThreadPool(5);
    public static final Executor INFOTHEADPOOL = Executors.newFixedThreadPool(3);
    public static final Executor MERCHATINFOPOOL = Executors.newSingleThreadExecutor();

    /* all kinds of sizes used */
    public final static int DEFAULT_IMAGE_SIZE = 960;
    /* Date time format */
    public static final String DATE_TIME_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'ZZ";
    public static final String DATE_TIME_FORMAT_LONG3 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'ZZ";
    public static final String DATE_TIME_FORMAT_LONG4 = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
    public static final String DATE_TIME_FORMAT_LONG5 = "yyyy-MM-dd'T'HH:mm:ssZZ";
    public static final String DATE_TIME_FORMAT_LONG6 = "yyyy-MM-dd'T'HH:mm'Z'ZZ";
    public static final String DATE_TIME_FORMAT_LONG2 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";
    public static final String WEIBOKEY = "2161716899";
    public static final String WEIBO_CALLBACK = "http://hunliji.com";
    public static final String SCOPE = "follow_app_official_microblog";
    public static final String QQKEY = "1103603096";
    public static final String WEIXINKEY = "wx1dce835182cdc736";
    /* Handler message what constants*/
    public static final int HANDLE_EMAIL_CORRECT = 1;
    public static final int HANDLE_EMAIL_WRONG = 0;
    public static final int HANDLE_LOGIN_NOW = 2;
    public static final int HANDLE_LOGIN_DONE = 3;
    /* Getui from value */
    public static final String GE_TUI_FROM = "android_biz"; //
    // "android_biz"->正式,
    public static final String LOCATION_FILE = "location.json";
    public static final String LOGIN_SEED = "*#0621ix51y6679&";
    public static final String PRE_QR_URL = "http://www.hunliji.com";
    public static final String PAGE_COUNT = "&page=%s";
    public static final String SUPPORTS_FILE = "supports.json";
    /*
    ************************************************************************************************
    * HTTP请求或者WebSocket连接的 host,socket_host, urls
     */
    public static String HOST = "http://www.hunliji.com/"; // 默认数据服务器地址
    public static String WEB_HOST = "https://m.hunliji.com/";
    // 默认私信服务器地址
    public static int PER_PAGE = 20;
    public static final int MIN_PSW_LENGTH = 6;
    public static final int MAX_PSW_LENGTH = 16;

    //商家类型，0表示普通版，1表示专业版，2表示旗舰版
    public static final int MERCHANT_NORMAL = 0;
    public static final int MERCHANT_PRO = 1;
    public static final int MERCHANT_ULTIMATE = 2;

    public static void setHOST(String HOST2) {
        Constants.HOST = HOST2;
    }


    public static void setWebHost(String HOST) {
        Constants.WEB_HOST = HOST;
    }

    public static class LinkNames {
        public static final String MERCHANT_CLASS = "merchant_class";
        public static final String GRADE_RULES = "grade_rules";
        public static final String MERCHANT_HELP = "merchant_help";
        public static final String MERCHANT_HELP_V2 = "merchant_help_v2";
        public static final String CUSTOM_MEAL_EDU = "custom_meal_edu_url";
        // public static final String MERCHANT_PRO_PAGE = "shop_professional_buy_page";
        public static final String MERCHANT_PRO_PROTOCOL = "shop_professional_buy_protocol";
        // public static final String MERCHANT_PRO_RECORD = "shop_professional_buy_record";
        public static final String SHOP_SERVICE_PAGE = "shop_service_page";//商家服务业
        public static final String HOT_TAG_HELP = "hot_tag_help";
        public static final String FINDER_ACTIVITY_HELP = "finder_activity_help";
        public static final String FINDER_ACTIVITY_POINT_INSTRUCTIONS =
                "finder_activity_point_instructions";
        public static final String BIND_WECHAT_EDU = "bind_wechat_edu_url";
        public static final String LIVE_CHAT_APPLY = "live_chat_apply";//直播宣传
        public static final String WECAHT_WALL = "weChat_wall";//微信墙
        public static final String MERCHANT_FEED_EDU_URL = "merchant_feed_edu_url";
    }

    public static class LocalValueNames {
        public static final String MERCHANT_CLASSROOM_VERSION = "merchant_classroom_version";
        public static final String DATA_FLOW_FLAG = "data_flow_flag";
    }

    public static class POST_SITES {
        public static final String SERVICE_MERCHANT_BANNER = "SERVICE_MERCHANT_BANNER";
        public static final String MERCHANT_GRADE_BANNER = "MERCHANT_GRADE_BANNER";
        public static final String MERCHANT_MARKET_TOP_BANNER = "MERCHANT_MARKET_TOP_BANNER";
        public static final String SERVICE_MERCHANT_INTERACTION_BANNER =
                "SERVICE_MERCHANT_INTERACTION_BANNER";

    }


    public static class BLOCK_ID {
        public static final int HomeFragment = 2001;
        public static final int MyLevelActivity = 2002;
        public static final int MarketingFragment = 2021;
        public static final int InteractionFragment = 2022;
    }

    public static class PrefKeys {
        public static final String HINT_MONTH_NUMBER = "month";
    }

    /*
    ************************************************************************************************
    * HTTP请求或者WebSocket连接的 host,socket_host, urls
     */
    public static String getAbsUrl(String path) {
        return HOST + path;
    }

    public static String getAbsUrl(String path, Object... args) {
        return HOST + String.format(path, args);
    }

    public static String getAbsWebUrl(String path) {
        return WEB_HOST + path;
    }

    public static class HttpPath {

        //Ruby
        /* Orders API*/
        public static final String GET_ORDERS = "v1/api/merchant/orders" + "" + "" + "" + "" + ""
                + ".json?kind=%s&per_page=%s";
        public static final String GET_ORDER_DETAIL = "v1/api/merchant/orders/%s.json";
        public static final String CHANGE_ORDER_PRICE = "v1/api/merchant/orders/%s/change_price"
                + ".json";
        public static final String ACCEPT_ORDER = "v1/api/merchant/orders/%s/confirm.json";
        public static final String GET_ORDER_HISTORY = "v1/api/merchant/orders/%s/history.json";
        /* Schedule Events API */
        public static final String GET_SCHEDULE_EVENTS_URL =
                "v1/api/merchant/tasks?year=%s&month=%s";
        public static final String CLOSE_DAY = "v1/api/merchant/tasks/close";
        public static final String OPEN_DAY = "v1/api/merchant/tasks/open";
        public static final String UPDATE_MEMO_TASK = "v1/api/merchant/tasks/%s";
        public static final String NEW_CALENDAR_TASK = "v1/api/merchant/tasks";
        public static final String DELETE_MEMO_TASK = "v1/api/merchant/tasks/%s";

        /* Data dashboard */
        public static final String GET_DATA_STATISTICS = "v1/api/merchant/data.json";
        public static final String GET_WORK_STATISTICS = "v1/api/merchant/data/works" + "" + "" +
                ".json?per_page=20&page=%s";
        public static final String GET_WORK_COLLECTOR_DATA = HOST +
                "v1/api/merchant/data/works/%s/collector.json";
        public static final String GET_WORK_HIT_DATA = "v1/api/merchant/data/works/%s/hit.json";


        //PHP
        public static final String TOKEN_POST_PATH = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APIPhone/create";
        /* Messages API */
        public static final String GEXIN_TOKEN_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APIGeTuiUser/SaveClientInfo";
        public static final String WORK_COMMENTS_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Home/APIOrderComment/MealsComments?page=%s&per_page=%s&meal_id=%s";
        public static final String GET_VERSION_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APIVersion/check?version=%s&phone=21";


        /* SPLASH*/
        public static final String SPLASH_LIST_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APISplashe/SplasheList?width=%s&height=%s";
        public static final String QINIU_IMAGE_URL = "p/wedding/home/APIUtils/image_upload_token";
        /* LOGIN, KEYS, TOKEN, SEED, ... */
        public static final String LOGIN_URL = "p/wedding/index" + "" + "" + "" + "" + "" + "" +
                ".php/Admin/APIAuth/phone_login";
        public static final String GET_NOTIFY_INFO = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Admin/APIMerchant/notify";
        /* Opu API , Works*/
        public static final String GET_WORK_INFO = "p/wedding/admin/APISetMeal/get_meal/id/%s";
        public static final String GET_WORKS = "p/wedding/index" + "" + "" + "" + "" + "" + "" +
                ".php/Admin/APISetMeal?per_page=%s&page=%s&is_sold_out=%s" + "&commodity_type=%s";
        public static final String GET_ARTICLES = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/home/APIMerchant/GetMealsItem/id/%s";
        public static final String GET_SNAPSHOT_INFO = "p/wedding/admin/APISetMeal/get_meal/id/%s";
        /* Register */
        public static final String MERCHANTS_REGISTER = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Admin/APIMerchant/register";
        public static final String POST_MERCHANTS_INFO = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APIMerchant/edit";
        /* New orders*/
        public static final String GET_NEW_ORDERS = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Admin/APIOrder/index_v2?per_page=20";
        public static final String CHANGE_NEW_ORDER_PRICE = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Admin/APIOrder/change_price_v2";
        public static final String ACCEPT_NEW_ORDER = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APIOrder/recieve";
        public static final String GET_REJECT_REASON = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APIOrder/reject_reason";
        public static final String REJECT_NEW_ORDER = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APIOrder/reject";
        public static final String GET_REVENUE_STATICS = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APICashFlow/statistic";
        public static final String GET_INCOME_LIST = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Admin/APICashFlow/index?per_page=20&page=%s";
        public static final String GET_WITHDRAW_LIST = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APICashFlow/withdraw_list?per_page=20&page=%s";
        public static final String POST_WITHDRAW = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/Admin/APIWithdraw/add";
        public static final String GET_WITHDRAWING_LIST = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APIWithdraw?per_page=20&page=%s";
        public static final String GET_WITHDRAW_DETAIL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APIWithdraw/edit?id=%s";
        public static final String GET_NEW_ORDER_DETAIL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APIOrder/info_v2?id=%s";
        public static final String GET_REFUND_COMPENSATION_LIST = "p/wedding/index" + "" + "" +
                "" + ".php/Admin/APIOrderRefund/index?per_page=20&page=%s";
        public static final String POST_CONFIRM_SERVICE = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APIOrder/finish";
        public static final String GET_LINKS_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/Admin/Index/links";
        public static final String GET_RETURN_REDPACKET_LIST = "p/wedding/index" + "" + "" + "" +
                ".php/Admin/APIOrderRefund/index?tab=red_packet&per_page=20" + "&page=%s";
        //发布动态
        public static final String GET_PUBLISH_DYNAMIC_URL = "p/wedding/index" + "" + "" + "" +
                "" + ".php/Shopadmin/APIMerchantFeed/edit";
        public static final String GET_SEARCH_ORDERS = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APIOrder/index?per_page=20&page=%s&keyword=%s";
        public static final String GET_FEED_LIST = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/ShopAdmin/APIMerchantFeed/list?page=%s&per_page=20";
        public static final String GET_FEED_COMMENT_LIST = "p/wedding/index" + "" + "" + "" + ""
                + ".php/ShopAdmin/APIMerchantFeedComment/comment_list?feed_id" +
                "=%s&per_page=20&page=%s";
        public static final String GET_FEED_INFO = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/ShopAdmin/APIMerchantFeed/info?id=%s";
        public static final String ADD_COMMENT_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Shopadmin/APIMerchantFeedComment/comment_add";
        public static final String MERCHANT_COMMENTS_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Home/APIOrderComment/MerchantComments?page=%s&per_page" +
                "=%s&merchant_id=%s";
        public static final String MERCHANT_INFO_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIMerchant/detailMerchant/?mer_id=%s";
        public static final String DElETE_COMMENT_URL = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Shopadmin/APIMerchantFeedComment/merchant_comment?id=%s";
        public static final String DElETE_TWITTER_URL = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/ShopAdmin/APIMerchantFeed/feed?id=%s";

        //商家店铺编辑
        public static final String GET_STORE_COMPILE_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Admin/APIMerchant/edit";
        public static final String POSTER_BLOCK_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIPosterBlock/block_info?id=%s&app_version=" + APP_VERSION +
                "&city=%s";
        // 新版通知
        public static final String GET_NOTIFICATIONS = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APINotification/list?last_id=%s";
        public static final String GET_HINT_DATA = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/home/APIMine/GetHintData";
        public static final String EDIT_PASSWORD_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APIMerchant/edit_pwd";
        public static final String PHONE_BIND_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Admin/APIMerchant/BindPhone";
        public static final String SEND_MSMCODE = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/Admin/APIMerchant/SendAgain";
        public static final String GET_REG_CODE = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/admin/APIMerchant/wed_reg_code?contact_phone=%s";
        public static final String POST_PHONE_REGISTER = "p/wedding/index" + "" + "" + "" + "" +
                ".php/admin/APIMerchant/wed_register";
        public static final String USER_PROTOCOL_URL = "p/wedding/Public/wap/merRegProt/mrp.html";
        public static final String ADDRESS_AREA_LIST = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/shop/APIShopAddress/AllAddress";
        public static final String POST_FOR_FORGET_PSW = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APIMerchant/ForgetPwd";
        public static final String POST_SETTING_PHONE = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/admin/APIMerchant/SetPhone";
        public static final String SEND_SMS_CODE_AGIN = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/admin/APIMerchant/SendAgain";
        public static final String OPEN_TRADE_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/admin/APIMerchant/openTrade";
        public static final String CHANGE_PASSWORD = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Admin/APIMerchant/edit_pwd";
        public static final String BOND_PAY_WEB = "p/wedding/Public/wap/baozhengjin/bzj_xieyi.html";
        public static final String OPEN_TRADE_WEB = "p/wedding/Public/wap/zaixianjiaoyi/zxjy.html";
        public static final String BOND_PAY_URL = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/admin/APIMerchantPro/bond_pay";
        public static final String BANK_CARD_BIN = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/Home/APIUserBankInfo/bankCardBin?card_no=%s";
        public static final String ADVH_PAY = "p/wedding/index" + "" + "" + "" + "" + "" + "" +
                "" + ".php/Admin/APIAdvHelperMerchant/pay";
        public static final String ADVH_PRICE_LIST = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Admin/APIAdvHelperMerchant/price_list?merchant_id=%s";
        public static final String ADVH_MERCHANT_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APIAdvHelperMerchant?per_page=%s";
        public static final String ADVH_MERCHANT_INFO_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Admin/APIAdvHelperMerchant/merchantInfo?id=%s";
        public static final String ADVH_PURCHASE_HISTORY = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Admin/APIAdvHelperMerchant/purchaseList?per_page=20" + "&page=%s";
        public static final String ADVH_MERCHANT_CHANGE_STATUS = "p/wedding/index" + "" + "" + ""
                + ".php/Admin/APIAdvHelperMerchant/changeStatus";
        public static final String ZHIKEBAO_WEB = "p/wedding/Public/wap/activity/zhikebao.html";
        public static final String DATA_CONFIG = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/home/APISetting/GetAppSetting";
        public static final String GRADE_LEVEL_LIST = "p/wedding/Admin/APIMerchantPrivilege/list";
        public static final String VIOLATE_LIST = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/Admin/APIMerchantGradeRecord/violate_list?per_page=20" + "&page=%s";
        public static final String RECORD_LIST = "p/wedding/Admin/APIMerchantPrivilege/record_list";
        public static final String PRIVILEGE_INFO =
                "p/wedding/Admin/APIMerchantPrivilege/current_list?id=%s";
        public static final String PRIVILEGE_ADD = "p/wedding/Admin/APIMerchantPrivilege/add";
        public static final String PRIVILEGE_EDIT = "p/wedding/Admin/APIMerchantPrivilege/edit";
        public static final String NEW_WORK_INFO = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/home/APISetMeal/info/id/%s?admin=1";
        public static final String GET_CUSTOM_ORDERS = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APICustomOrder/CustomOrderList?per_page=20&page=%s";
        public static final String EDIT_CUSTOM_ORDER = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APICustomOrder/EditOrder";
        public static final String CUSTOM_ORDER_INFO = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APICustomOrder/OrderDetail?id=%s";
        public static final String CUSTOM_ORDER_RECEIVING = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Admin/APICustomOrder/IsReceiving";
        public static final String CUSTOM_ORDER_CONFIRM = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APICustomOrder/confirm_order";
        public static final String FIRST_POST_ORDER = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIOrder/ConfirmOrder";
        public static final String SETMEAL_DETAIL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APICustomSetMeal/MealDetail?id=%s";
        public static final String SETMEAL_DETAIL_WORKS = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APICustomSetMeal/RecommendMeals?id=%s";
        public static final String CUSTOM_SETMEAL_LIST = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APICustomSetMeal/list?per_page=%s&page=%s";
        public static final String CUSTOM_SETMEAL_OPU = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Admin/APICustomSetMeal/edit";
        public static final String GET_WITHDRAW_STATICS = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Shopadmin/APIShopWallet/withdraw_statistics_v2";
        public static final String GET_FROZEN_LIST =
                "p/wedding/Shopadmin/APIShopWallet/frozen_list?per_page=20" + "&page=%s";
        public static final String HOT_RECOMMEND_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Home/APISetMeal/merchantMeals?set_meal_id=%s";
        public static final String NEW_SNAPSHOT_INFO = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIOrder/GetSnapshot";
        public static final String NEW_PROPERTIES_URL = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Home/APIManagers/properties";
        public static final String REVENUE_INCOME_LIST = "/p/wedding/index" +
                ".php/admin/APIOrderSub/" + "index?per_page=20&page=%s";
        public static final String REVENUE_EXPEND_LIST = "p/wedding/index" + "" + "" + "" + "" +
                ".php/ShopAdmin/APIShopWallet/index?per_page=20&page=%s" + "&value_type=%s";
        public static final String REVENUE_WITHDRAW_DETAIL_LIST = "p/wedding/index" + "" + "" +
                "" + ".php/ShopAdmin/APIShopMerchantWithdraw/index?status=%s" +
                "&per_page=20&page=%s";
        public static final String APPLY_WITHDRAW = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/ShopAdmin/APIShopMerchantWithdraw/add";
        public static final String GET_REVENUE_WITHDRAW_DETAIL =
                "p/wedding/Shopadmin/APIShopMerchantWithdraw/info?id=%s";
        public static final String GET_CUSTOM_COMMENT_LIST = "p/wedding/index" + "" + "" + "" +
                "" + ".php/home/APIOrderComment/custom_comment_list?set_meal_id=%s";
        public static final String MERCHANT_COMMENTS_WITH_CUSTOMER_URL = "p/wedding/index" + "" +
                ".php/home/APIOrderComment/MerchantCommentsV2?page=%s&per_page=%s&merchant_id=%s";
        public static final String SPEIAL_ADVH_MERCHANT_CHANGE_STATUS = "p/wedding/index" + "" +
                ".php/Admin/APIAdvHelperMerchant/specialChangestatus";
        public static final String GET_NEW_SUPPORTS_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIManagers/Supports/kind";
        public static final String GET_USER_INFO = "p/wedding/index.php/Home/APIUser/UserBaseInfo";
        public static final String PRO_PAY_URL = "p/wedding/index.php/admin/APIMerchantPro/pay";
        public static final String PRO_PAY_INFO = "p/wedding/index.php/admin/APIMerchantPro/fee";
        public static final String PRO_HISTORY_LIST = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/admin/APIMerchantPro/history?page=%s&per_page=20";
        public static final String WORK_MARKET_INFO = "p/wedding/index.php/admin/APISetMeal/market";
        public static final String MARKET_WORKS_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/admin/APISetMeal/setmeal_list?hot_tag=%s&page=1&per_page=9999";
        public static final String WORK_TAG_URL = "p/wedding/index.php/admin/APISetMeal/tag";
        public static final String GET_PENDDING_SETTLE_LIST = "p/wedding/index" + "" + "" + "" +
                ".php/Shopadmin/APIShopWallet/pendding_settle_list?tab=%s&page=%s";
        public static final String GET_ACCOUNT_OVERVIEW = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Shopadmin/APIShopWallet/account_overview";
        public static final String GET_BOND_MONEY_LIST = "p/wedding/index" + "" + "" + "" + "" +
                ".php/ShopAdmin/APIShopWallet/bond_list?tab=%s&page=%s";
        public static final String GET_BOND_BALANCE_LIST = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Admin/APIMerchantBondBalance/list?page=%s";
        public static final String GET_WATER_DETAIL_LIST = "/p/wedding/index" +
                ".php/ShopAdmin/APIShopWallet/index?page=%s";
        public static final String GET_ORDER_DETAIL_LIST = "/p/wedding/index" +
                ".php/admin/APIOrderSub/list?page=%s";
        public static final String GET_SAME_BUSINESS = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Admin/APIMerchant/peerCompareList?order=%s";
        public static final String GET_COMPETE_BUSINESS = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Admin/APIMerchant/noticeCompareList?order=%s";
        public static final String SEARCH_COMPETE = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Admin/APIMerchant/searchCompareMerchant?keyword=%s";
        public static final String ADD_COMPETE = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/Admin/APIMerchant/addCompareMerchant";
        public static final String CANCEL_COMPETE = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Admin/APIMerchant/cancelNoticeCompareMerchant";
        public static final String GET_SHOP_STATIC = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Admin/APIMerchant/shopStatic";
        public static final String GET_REFUNDING_RECORD_LIST = "p/wedding/index" + "" + "" + "" +
                ".php/Admin/APIOrderRefund/refunding_list?page=%s";
        //商家活动支付
        public static final String MERCHANT_EVENT_POINT_PAY = "p/wedding/index" + "" + "" + "" +
                ".php/Admin/APIFinderActivity/pay";
        //商家套餐和案例(等work model统一了删除)
        public static final String GET_MERCHANT_WORKS_AND_CASES = "p/wedding/index" + "" + "" +
                "" + ".php/home/APISetMeal/list?id=%s&kind=%s&sort=%s&page=%s&per_page=%s";
        public static final String GET_MERCHANT_GOODS = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Shop/APIShopProduct/MerchantGoodsList?merchant_id=%s";
        //主题预览
        public static final String GET_THEME_PREVIEW = "p/wedding/Public/wap/m/merchant" + "" + "" +
                ".html?id=%s&is_ultimate=%s&theme=%s&preview=%s";
        //店铺预览
        public static final String GET_SHOP_PREVIEW = "p/wedding/Public/wap/m/merchant" + "" + "" +
                ".html?id=%s&preview=%s";
        //轻松聊帮助
        public static final String EASY_CHAT_HELP =
                "p/wedding/Public/wap/m/20170626-easyChat-help.html";
        //轻松聊开通
        public static final String EASY_CHAT_ACTIVE =
                "p/wedding/Public/wap/m/20170626-easyChat-cutover.html";
        //商家订单支付
        public static final String MERCHANT_ORDER_PAY = "p/wedding/index.php/Admin/APIMerchantOrder/pay";
        //商家订单支付
        public static final String MERCHANT_WEAPP_PAY = "p/wedding/Admin/APIMerchantWeapp/pay";
    }

    public static class SocketPath {
        /* Message */
        public static final String WEBSOCKET = "websocket";
        public static final String GET_CHANNELS_URL = "api/v1/channels?page=1&per_page=9999";
        public static final String DELETE_CHANNEL_URL = "api/v1/channels/%s";
        public static final String DELETE_ALL_CHANNEL_URL = "api/v1/channels/all";
        public static final String GET_MESSAGES_URL =
                "api/v1/channels/%s/messages?page=1&per_page=9999";
    }

    /* Login request code */
    public static class Login {
        public static final int SPLASH_LOGIN = 1;
    }

    /* Start activity result request code */
    public static class RequestCode {
        public static final int SCAN = 1;
        public static final int USER_LOGOUT = 2;
        public static final int ADD_MEMO = 3;
        public static final int ORDER_DETAIL = 4;
        public static final int LOCATION = 5;
        public static final int CHANGE_PRICE = 6;
        public static final int OFFED_WORK = 7;
        public static final int WITHDRAW = 8;
        public static final int SELECT_WORK = 9;
        public static final int MERCHANT_FEED_INFO = 10;
        public static final int EDIT_MERCHANT_INTRO = 11;
        public static final int CERTIFY_FRONT_FOR_CAMERA = 12;
        public static final int CERTIFY_FRONT_FOR_GALLERY = 13;
        public static final int CERTIFY_BACK_FOR_CAMERA = 14;
        public static final int CERTIFY_BACK_FOR_GALLERY = 15;
        public static final int COMPANY_LICENSE_FOR_CAMERA = 16;
        public static final int COMPANY_LICENSE_FOR_GALLERY = 17;
        public static final int PROTOCOL_IMAGE_FOR_CAMERA = 18;
        public static final int PROTOCOL_IMAGE_FOR_GALLERY = 19;
        public static final int CHOOSE_PHOTO_PAGE = 20;
        public static final int PHOTO_FROM_CAMERA = 21;
        public static final int PHOTO_FROM_GALLERY = 22;
        public static final int PAY = 23;
        public static final int APPLY_EVENT = 24;
        public static final int GET_CONTACT = 25;
        public static final int CREATE_COUPON = 26;
        public static final int UPDATE_COUPON = 27;
        public static final int EDIT_TEXT = 28;
        public static final int WORK_SORT = 29;
        public static final int WORK_OPTIMIZE = 30;
        public static final int UPLOAD_PROTOCOL_PHOTOS = 31;
        public static final int CREATE_SCHEDULE = 32;
        public static final int SCHEDULE_DETAIL = 33;
        public static final int UPDATE_SCHEDULE = 34;
        public static final int CHOOSE_PHOTO = 35;
    }

    public static class WebCode {
        public static final int UPDATE = 1;
        public static final int CHECK = 2;
        public static final int SHARE = 3;
    }

    /* Orders Tabs Category */
    public static class OrdersCategory {
        public static final String PROGRESSING = "progressing";
        public static final String CLOSED = "closed";
        public static final String FINISHED = "finished";
    }

    public static class ItemType {
        public static final int Topic = 1;
        public static final int OpuItem = 2;
        public static final int StoryItem = 3;
        public static final int WorkItem = 4;
        public static final int SnapshotItem = 5;
    }

}
