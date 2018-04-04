/**
 *
 */
package me.suncloud.marrymemo;

import com.hunliji.hljcardlibrary.HljCard;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author iDay
 */
public class Constants {

    public static final boolean DEBUG = BuildConfig.APP_DEBUG; // 开发模式是 true 的时候可以动态修改服务器地址
    public static final String APP_VERSION = BuildConfig.VERSION_NAME;
    public static String HOST = "http://www.hunliji.com/"; // 默认数据服务器地址
    public static String HTTPS_HOST = "https://www.hunliji.com/"; // 默认数据服务器地址

    // 爱家分期支付的RSA public key
    public static final String AIJIA_PUB_KEY_DEBUG =
            "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAITnBZEqWRJfhSwlto3WY16MqRsZvlMgANYjDISFzggrjxGkCcXvW1MICBkNUwbnME4JlBJ8AOL0wP5Le4UlIqcCAwEAAQ==";
    public static final String AIJIA_PUB_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0Ez2xKpl85GbgxN9kIVhxNuBhes2rpmeEDSZ7cuZLY" +
                    "/3qTOzc+N4duRnN2cw2d7xuGE+t+cRzeoJgfVgKdp1tryoGlYJ2CS0gbqhnE4yOxmCQxlO4wwszNiTKBRcBVoP0ObIRTwafwZReIGrmapz6ITTB0hz3/Qf9IFqCXFt/MQIDAQAB";
    public static final String HLJ_PUB_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzSwfiDaPldx9bCnj+LWfSkk" + "++yo" +
                    "/sEpVntHaYE06axpXcp3b3qWeW4Upo9jPjGuDIE7NAg6QtvRbXIMgdqCHzaLjmFFshrqIOuTVJmpdMnynZ7oTL7C8UJALnfwn3xC0Y1WBjBwWRLMv4a3du7I+dmOvevZf2eGp1/JY8UKUIPQIDAQAB";

    public static final String QINIU_HOST = "http://marry.qiniudn.com/"; //
    // 默认七牛云服务器地址
    public static final String PAGE_COUNT = "&page=%s";
    public static final int PER_PAGE = 20;

    public static final String MERCHANTS_QUERY = "&property=%s&category_id=%s&area_id=%s&sort=%s";

    public static final String LABEl_FROM = "?from=%s";
    public static final String VIDEO_URL = "?vframe/jpg/offset/1/rotate/auto|imageView/2/w/%s";
    public static final String VIDEO_URL_TEN =
            "?vframe/jpg/offset/10/rotate/auto|imageView2/2/w/%s";
    public static final String PHOTO_URL = "?imageView2/2/w/%s/format/webp";
    public static final String PHOTO_URL2 = "?imageView2/2/w/%s/h/%s/format/webp";
    public static final String PHOTO_URL3 = "?imageView2/1/w/%s/h/%s/format/webp";
    public static final String PHOTO_URL4 = "?imageView2/2/h/%s/format/webp";
    public static final String PHOTO_URL5 = "?imageView2/2/w/%s";
    public static final String PHOTO_URL6 = "?imageView2/2/w/%s/h/%s";
    public static final String PHOTO_URL7 = "?imageView2/1/w/%s/h/%s";
    public static final String QINIU_UPLOAD_URL = "http://up.qiniu.com";
    public static final String USER_PROTOCOL_URL = "http://www.hunliji" + ".com/p/wedding/index"
            + "" + ".php/Home/AppH5/UserProtocol";
    public static final String SECRET_PROTOCOL_URL = "http://hunliji" + "" + "" + "" + "" + "" +
            ".com/privacy_policy" + ".html";
    public static final String INTRO_URL = "http://www.hunliji.com/p/wedding/Public/wap/activity"
            + "/memberenjoy_0414/index.html#/main";


    public static final String USER_FILE = "user.json";
    public static final String MY_CITIE_FILE = "city-{1}.json"; // 婚博汇选择的城市
    public static final String LOCATION_CITY_FILE = "location_city.json";
    public static final String ACCESS_CITIES_FILE = "access_cities.json";
    public static final String DATA_CONFIG_FILE = "data_config2.json";
    public static final String PROPERTIES_FILE = "propertie.json"; // 分类
    public static final String PROPERTIES2_FILE = "propertie2.json"; //
    public static final String PROPERTIES3_FILE = "propertie3.json";
    public static final String FEEDS_PAGE_FILE = "feedspage.json";
    public static final String COLOR_LABELS_FILE = "color_labels.json";
    public static final String MUSICS_FILE = "musics.json";
    public static final String SUPPORTS_FILE = "supports.json";
    public static final String QQ_FILE = "qq.json";
    public static final String WEIBO_FILE = "weibo.json";
    public static final String CALENDAR_FILE_DIR = "/hlj/cal_files";
    public static final String CALENDAR_FILE = "hlj_calendar_mapping.json";
    public static final String ADDRESS_AREA_FILE = "address_area.json";
    public static final String CAR_CART_FILE = "car_cart.json";
    public static final String NEW_CITIES_FILE = "new_cities.json";
    public static final String POINT_RECORD_FILE = "point_record_{%s}.json";
    public static final String PAGE_KEY_MAP = "pageKeyMap.json";
    public static final String FONTS_FILE = "fonts.json";
    public static final String SOCIAL_HOT = "socialhotV2.json";
    public static final String SOCIAL_FOLLOW = "socialfollowV2.json";
    public static final String MERCHANT_FILTER_FILE = "merchant_filters.json";
    public static final String RECENT_CHANNEL_FILE = "recent_channel.json";

    public static final Executor THEADPOOL = Executors.newFixedThreadPool(10);

    public static final Executor LISTTHEADPOOL = Executors.newFixedThreadPool(5);

    public static final Executor INFOTHEADPOOL = Executors.newFixedThreadPool(5);

    public static final Executor UPLOADTHEADPOOL = Executors.newFixedThreadPool(3);

    public static final Executor PVTHEADPOOL = Executors.newFixedThreadPool(6);

    public static final String WEIBOKEY = "2726144177";
    public static final String WEIBO_CALLBACK = "http://marrymemo.com";
    public static final String SCOPE = "follow_app_official_microblog";
    public static final String QQKEY = "100370679";

    public static final String WEIXINKEY = "wx9acfc1464c57b9b4";
    public static final String WEIXINSECRET = "7ef0bffb6e04b687dead503c8cd83638";

    public static final boolean KEY_AUTO_FOCUS = true;
    public static final boolean KEY_DISABLE_CONTINUOUS_FOCUS = false;
    public static final boolean KEY_FRONT_LIGHT = false;
    public static final boolean KEY_VIBRATE = false;
    public static final boolean KEY_PLAY_BEEP = true;
    public static final String LOGIN_SEED = "*#0621ix51y6679&";

    public static final String DATE_TIME_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'ZZ";
    public static final String DATE_TIME_FORMAT_LONG3 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'ZZ";
    public static final String DATE_TIME_FORMAT_LONG4 = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
    public static final String DATE_TIME_FORMAT_LONG5 = "yyyy-MM-dd'T'HH:mm:ssZZ";
    public static final String DATE_TIME_FORMAT_LONG6 = "yyyy-MM-dd'T'HH:mm'Z'ZZ";
    public static final String DATE_TIME_FORMAT_LONG7 = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    public static final String DATE_TIME_FORMAT_LONG2 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_LONG2 = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";

    public static final String PREF_FILE = "pref";
    public static final String PREF_LAST_SERVE_TIME = "last_serve_time_";
    public static final String PREF_LAST_CAR_USE_TIME = "last_car_use_time_";
    public static final String PREF_LAST_SERVE_CUSTORMER = "last_serve_customer_";
    public static final String PREF_LAST_SERVE_PHONE = "last_serve_phone_";
    public static final String PREF_LAST_SERVE_ADDR = "last_serve_addr_";
    public static final String PREF_BIND_PARTNER_HINT_CLICKED = "pref_bind_partner_hint_clicked";
    public static final String PREF_QUESTION_ANSWER_HINT_CLICKED =
            "pref_question_answer_hint_clicked";
    public static final String PREF_BUY_WORK_HINT_CLICKED = "pref_buy_work_hint_clicked";
    public static final String PREF_THREAD_POSTED = "pref_thread_posted"; // 是不是已经发过话题
    public static final String PREF_THREAD_REPLIED = "pref_thread_replied"; // 是不是已经回过帖子
    public static final String PREF_CREATE_THREAD_HINT_CLICKED =
            "pref_create_thread_hint_clicked"; // 是否显示发布引导
    public static final String PREF_SUBJECT_PRAISE_HINT_CLICKED =
            "pref_subject_praise_hint_clicked";
    public static final String PREF_FIRST_RECEIVE_COUPON = "pref_first_receive_coupon";
    public static final String PREF_FIRST_MEMBER_CLICK = "pref_first_member_click";//会员入口红点
    public static final String PREF_NOTIFICATION_OPEN_DLG = "has_showed_notification_open_dlg";//
    // 显示过通知开关dlg
    public static final String PREF_NOTICE_OPEN_DLG_EVENT = "has_showed_notice_open_dlg_event";
    public static final String PREF_NOTICE_OPEN_DLG_THREAD = "has_showed_notice_open_dlg_thread";
    public static final String PREF_QA_HOME_MARK_HINT_SHOWED = "pref_qa_home_mark_hint_showed";
    public static final String PREF_COMMUNITY_DAY = "pref_community_day";

    public static class Login {
        public static final int CARD_LOGIN = 9;
        public static final int REVIEW_LOGIN = 10;
        public static final int LIKE_LOGIN = 11;
        public static final int ITEM_REVIEW_LOGIN = 12;
        public static final int ITEM_LIKE_LOGIN = 13;
        public static final int PRAISE_LOGIN = 14;
        public static final int SEND_THREAD_LOGIN = 19;
        public static final int MAIN_LOGIN = 24;
        public static final int SUBMIT_LOGIN = 26;
        public static final int COUPON_GET_LOGIN = 27;
        public static final int WEDDING_REGISTER_LOGIN = 28;
        public static final int CONTACT_LOGIN = 29;
        public static final int INFORM_LOGIN = 30;
        public static final int MY_STORY_LOGIN = 32;
        public static final int WEDDING_DAY_PROGRAMS_LOGIN = 33;
        public static final int APPLY_IOU_LOGIN = 36;
        public static final int WEDDING_TASKS_LOGIN = 38;
        public static final int WEB_LOGIN = 39;
        public static final int CONFIRM_LOGIN = 40;
        public static final int SUPPORT_LOGIN = 43;
        public static final int WALL_LOGIN = 44;
        public static final int SHOP_CART_LOGIN = 45;
        public static final int CAR_CART_LOGIN = 46;
        public static final int ADV_HELPER_LOGIN = 48;
        public static final int COMMENT_LOGIN = 49;
        public static final int BUDGET_LOGIN = 52;
        public static final int MSG_LOGIN = 53;
        public static final int REGISTER = 55;//登录注册
        public static final int LOGINCHECK = 57;//登录检测
        public static final int RECEIVE_COUPON_RED_PACKET = 58;
        public static final int WEDDING_DATE_LOGIN = 59;//设置婚期 登录检测
    }

    public static class SHARE {
        public static final int SHARE_TO_PENGYOU = 21;
        public static final int SHARE_TO_WEIXING = 22;
        public static final int SHARE_TO_WEIBO = 23;
        public static final int SHARE_TO_TXWEIBO = 24;
        public static final int SHARE_TO_QQZONE = 25;
        public static final int SHARE_TO_QQ = 27;
    }

    public static class Property {
        public static final long HOTEL = 13L;
    }

    public static class ItemType {
        public static final int OpuItem = 2;
        public static final int StoryItem = 3;
        public static final int WorkItem = 4;
        public static final int SnapshotItem = 5;
    }

    public static class ReviewType {
        public static final int StoryItemReview = 46;
    }

    /**
     * 客户端所有的订单类型,将来所有的需要标记订单类型标识的地方尽量都是用这个
     */
    public static class OrderType {
        public static final int NOMAL_WORK_ORDER = 1; // 普通套餐(本地服务)订单
        public static final int CUSTOM_SETMEAL_WORK_ORDER = 2; // 定制套餐订单
        public static final int WEDDING_PRODUCT_ORDER = 3; // 婚品订单
        public static final int WEDDING_CAR_ORDER = 4; // 婚车订单
    }

    public static class RequestCode {
        public static final int STORY_REVIEW = 54;
        public static final int STORY_ITEM_REVIEW = 55;
        public static final int ITEM_REVIEW = 56;
        public static final int CARD_EDIT = 74;
        public static final int PHOTO_FROM_GALLERY = 81;
        public static final int PHOTO_FROM_CAMERA = 82;
        public static final int VIDEO_FROM_GALLERY = 83;
        public static final int VIDEO_FROM_CAMERA = 84;
        public static final int PHOTO_CROP = 85;
        public static final int AUDIO_FROM_PHONE = 86;
        public static final int COVER_FROM_GALLERY = 87;
        public static final int COVER_FROM_CAMERA = 88;
        public static final int EDIT_USER_INFO = 91;
        public static final int STORY_ITEM_TEXT_EDIT = 104;
        public static final int STORY_ITEM_DESCRIBE_EDIT = 107;
        public static final int STORY_CREATE = 111;
        public static final int STORY_EDIT = 112;
        public static final int LOCATION = 113;
        public static final int PHOTO_TEXT_EDIT = 141;
        public static final int ITEM_PAGE_VIEW = 153;
        public static final int SEND_THREAD_COMPLETE = 169;
        public static final int FILECHOOSER_RESULTCODE = 185;
        public static final int ADD_WEDDING_PLAN = 186;
        public static final int APPLY_IOU = 226;
        public static final int ADD_CUSTOM_TASK = 227;
        public static final int ADD_TASKS = 228;
        public static final int EDIT_TASK = 229;
        public static final int MODIFIED_TIME = 230;
        public static final int REFUND_APPLY = 232;
        public static final int ORDER_COMMENT = 233;
        public static final int UMPAY = 234;
        public static final int ORDER_CONFIRM = 235;
        public static final int PAGE_PHOTO_FROM_GALLERY = 240;
        public static final int CANCEL_REFUND = 254;
        public static final int COMPLETE_PROFILE = 255;
        public static final int EDIT_NICK_NAME = 256;
        public static final int EDIT_INTRO = 257;
        public static final int WALLET_PHONE = 258;
        public static final int SETTING_SAFE = 260;
        public static final int NOTIFICATION_PAGE = 262;
        public static final int EDIT_REAL_NAME = 263;
        public static final int EDIT_SHIPPING_ADDRESS = 264;
        public static final int SELECT_SHIPPING_ADDRESS = 265;
        public static final int EDIT_CAR_USE_INFO = 271;
        public static final int CITY_CHANGE = 272;
        public static final int GET_CONTACT_NAME = 275;
        public static final int SET_PAY_PASSWORD = 276;
        public static final int AIJIA_WEB_VIEW = 278;
        public static final int HOTEL_COLLECT = 280;
        public static final int ADD_REFUND_MESSAGE = 281;
        public static final int EDIT_EXPRESS_INFO = 282;
        public static final int APPLY_REFUND_AGAIN = 283;
        public static final int COMMUNITY_CHANNEL = 284;
        public static final int SELECT_COMMUNITY_CHANNEL = 286;
        public static final int EDIT_COMMUNITY_THREAD = 287;
        public static final int REPLY_THREAD_POST = 289;
        public static final int NOTIFICATION_SOCIAL_PAGE = 291;
        public static final int SIGN_COLD_COIN = 293;
        public static final int POST_PRAISE = 295;
        public static final int SUB_PAGE_DETAIL = 296;
        public static final int SUB_PAGE_COMMENT_LIST = 297;
        public static final int MERCHANT_INFO = 298;
        public static final int EDIT_WEDDING_TABLE = 299;
        public static final int POST_REPORT = 300;
        public static final int SIGN_UP = 301;
        public static final int POST_SERVICE_ORDER_COMMENT = 302;
        public static final int SELECT_MERCHANT_LIST = 303;
        public static final int PHOTO_FROM_GALLERY_WEDDING_PHOTO = 304;
        public static final int SEND_WEDDING_PHOTO_COMPLETE = 305;
        public static final int WEDDING_PHOTO_THREAD_VIEW_LARGE_IMAGE = 306;
        public static final int LOGIN_OPEN_MEMBER = 307;
        public static final int WEDDING_PHOTO_PREVIEW = 308;
        public static final int SERVICE_ORDER_EDIT_WEDDING_TIME = 309;
        public static final int USER_PREPARE_CATEGORY_LIST = 310;
        public static final int IMPORT_WEDDING_GUESTS = 311;
        public static final int WORK_MEDIA_ITEM_IMAGE = 312;
        public static final int EDIT_WEDDING_ACCOUNT = 313;
        public static final int PRODUCT_IMAGE_ITEM_IMAGE = 314;
        public static final int SIGN_IN = 315;
    }


    public static class ENTRY_ITEM {
        public static final int FOOTER = 2;
        public static final int WORK = 7;
        public static final int MERCHANT = 9;
    }

    /**
     * 旅拍item type
     */
    public static class JOURNEY_TYPE {
        public static final int IMAGE = 1;//照片
        public static final int DESTINATION = 2;//目的地
        public static final int GUIDE = 3;//攻略
        public static final int WORK = 4;//热门套餐
        public static final int MERCHANT = 5;//大牌商家
        public static final int AMOROUS = 6;//特色风情
        public static final int AMOROUSLEVEL2 = 7;//特色风情二级页banner
    }

    /**
     * theme type
     */
    public static class THEME_TYPE {
        public static final int AMOROUS_CITY = 2;//特色风情二级页(单元热城)
        public static final int UNIT = 3;// 旅拍单元详情即单元热城
        public static final int HOTCITY = 1;//旅拍热城
        public static final int AMOROUS = 4;//特色风情
        public static final int AMOROUSLEVEL2 = 5;//特色风情二级页
    }

    public static class MARK_TYPE {
        public static final int HEADER_LABEL = -1;
        public static final int HEADER_ORDER = 0;
        public static final int FOOTER = 6;
        public static final int WORK = 1;
        public static final int CASE = 2;
        public static final int THREAD = 4;
        public static final int PRODUCT = 5;
    }

    public static class POST_SITES {
        public static final String MAIN_TOP_BANNER = "MAIN_TOP_BANNER";
        public static final String MAIN_ENTRY_BUTTON_V2 = "MAIN_ENTRY_BUTTON_V2";
        public static final String CAR_BANNER2 = "CAR_BANNER_V2";
        public static final String TOOLS_BANNER = "TOOLS_BANNER";
        public static final String SITE_MAIN_SINGLE_PIC_BANNER = "SITE_MAIN_SINGLE_PIC_BANNER";
        public static final String SITE_MAIN_TRIPLE_PIC_BANNER = "SITE_MAIN_TRIPLE_PIC_BANNER";
        public static final String SITE_FINANCIAL_MARKET_BANNER = "SITE_FINANCIAL_MARKET_BANNER";
        public static final String SITE_FIND_TOP_BANNER = "SITE_FIND_TOP_BANNER";
        public static final String SITE_BRIDE_GROUP = "SITE_BRIDE_GROUP";
        public static final String SITE_BRIDE_MIDDLE = "SITE_BRIDE_MIDDLE";
        public static final String SITE_MAIN_HOME_CHANNEL = "SITE_HOME_CHANNEL";
        public static final String SITE_MAIN_HOME_CHANNEL_NEW = "SITE_HOME_CHANNEL_NEW";
        public static final String SITE_FINANCIAL_HOTEL = "SITE_FINANCIAL_HOTEL"; // 首页金融和酒店
        public static final String BANNER_PROPERTY_CATEGORY_PAGE =
                "BANNER_PROPERTY_CATEGORY_PAGE";//分类页
        public static final String SITE_DACU = "SITE_DACU";//首页大促
        public static final String SITE_DAILY_FOCUS = "SITE_DAILY_FOCUS";
        public static final String SITE_MAIN_TRAVEL_TAKE_PHOTO = "SITE_MAIN_TRAVEL_TAKE_PHOTO";//旅拍
        public static final String SITE_MAIN_TRAVEL_TAKE_PHOTO_V2 =
                "SITE_MAIN_TRAVEL_TAKE_PHOTO_V2";
        public static final String MAIN_TOP_BANNER_V2 = "MAIN_TOP_BANNER_V2";
        public static final String SITE_HOME_CHANNEL_V2 = "SITE_HOME_CHANNEL_V2";
        public static final String SITE_TRAVEL_LIGHT_LUXURY = "SITE_TRAVEL_LIGHT_LUXURY";
        public static final String EXPERIENCE_STORE_SINGELPIC_BANNER =
                "EXPERIENCE_STORE_SINGELPIC_BANNER";//体验店单图
        public static final String EEXPERIENCE_STORE_TOP_BANNER = "EXPERIENCE_STORE_TOP_BANNER";
        //体验店顶图
        public static final String COST_EFFECTIVE_TOP_BANNER = "COST_EFFECTIVE_TOP_BANNER";//性价比
        public static final String PINGPAI_TOP_BANNER = "PINGPAI_TOP_BANNER";//品牌馆
        public static final String SITE_HOTEL_BANNER = "SITE_HOTEL_BANNER";//酒店频道(RN)
        public static final String SITE_HOTEL_TOP_BANNER = "SITE_HOTEL_TOP_BANNER";//酒店频道(原生)
        public static final String SITE_HOTEL_ADV = "SITE_HOTEL_ADV";//婚宴顾问
        public static final String SITE_HOTEL_GIFT = "SITE_HOTEL_GIFT";//婚宴礼物
        public static final String SITE_HOTEL_ADV_BOTTOM = "SITE_HOTEL_ADV_BOTTOM";//婚宴顾问底部
        public static final String FIND_SETMEAL_CATEGORY = "FIND_SETMEAL_CATEGORY";//找套餐
        public static final String MAIN_FIRST_CHANNEL_BANNER = "MAIN_FIRST_CHANNEL_BANNER";//第一行3个坑位
        public static final String MAIN_PREPARE_MARRIAGE_STROLL = "MAIN_PREPARE_MARRIAGE_STROLL";
        //旅拍
        public static final String MAIN_LVPAI_V3 = "MAIN_LVPAI_V3";//旅拍坑位
        public static final String MAIN_LVPAI_DESTINATION = "MAIN_LVPAI_DESTINATION";//旅拍目的地
        public static final String TRAVEL_STRICT_SELECTED_BANNER =
                "TRAVEL_STRICT_SELECTED_BANNER";//旅拍严选
        public static final String TRAVEL_STRICT_SELECTED_ALBUM = "TRAVEL_STRICT_SELECTED_ALBUM";
        //旅拍严选专辑
        public static final String SITE_BRIDE_LOCAL_RANKING_LIST =
                "SITE_BRIDE_LOCAL_RANkING_LIST";//同城备婚和排行榜
        public static final String SITE_POP_RECOMMAND_FLOOR1 = "SITE_POP_RECOMMAND_FLOOR1";//婚品分类
        public static final String SITE_POP_RECOMMAND_FLOOR2 = "SITE_POP_RECOMMAND_FLOOR2";//婚品分类
        public static final String SITE_WEEKLY_HOT_SALE = "SITE_WEEKLY_HOT_SALE";//本周热门

        //banner
        public static final String SITE_MAIN_TOP_BANNER_V3 = "SITE_MAIN_TOP_BANNER_V3";
        //button
        public static final String SITE_MAIN_ENTRY_BUTTON_V3 = "SITE_MAIN_ENTRY_BUTTON_V3";
        //旅拍
        public static final String SITE_MAIN_GLOBAL_TRIP_SHOOT_V2 =
                "SITE_MAIN_GLOBAL_TRIP_SHOOT_V2";
        public static final String SITE_MAIN_GLOBAL_TRIP_SHOOT_V3 =
                "SITE_MAIN_GLOBAL_TRIP_SHOOT_V3";
        // 9.9抢旅拍顶部banner
        public static final String SITE_GRAB_TRAVEL = "SITE_GRAB_TRAVEL";
        //旅拍目的地
        public static final String SITE_MAIN_GLOBAL_TRIP_SHOOT_DESTINATION =
                "SITE_MAIN_GLOBAL_TRIP_SHOOT_DESTINATION";
        //备婚工具
        public static final String SITE_MAIN_WEDDING_TOOL_BUTTON = "SITE_MAIN_WEDDING_TOOL_BUTTON";
        //备婚工具横幅
        public static final String SITE_MAIN_WEDDING_TOOL_BANNER = "SITE_MAIN_WEDDING_TOOL_BANNER";
        //婚品
        public static final String SITE_MAIN_WEDDING_PRODUCT = "SITE_MAIN_WEDDING_PRODUCT";
        //备婚必逛
        public static final String SITE_MAIN_PREPARE_MARRIAGE_STROLL_V2 =
                "SITE_MAIN_PREPARE_MARRIAGE_STROLL_V2";
        //酒店
        public static final String SITE_MAIN_HOTEL = "SITE_MAIN_HOTEL";
        // 新娘说本地频道
        public static final String SITE_BRIDE_LOCAL_CHANNEL = "SITE_BRIDE_LOCAL_CHANNEL";
        // 新娘说顶部轮播
        public static final String SITE_BRIDE_TOP_CAROUSEL = "SITE_BRIDE_TOP_CAROUSEL";
        // 新娘说精选中间2个固定位置
        public static final String SITE_BRIDE_CHOICE_MIDDLE_FLOOR =
                "SITE_BRIDE_CHOICE_MIDDLE_FLOOR";
        // 新娘说精选顶部2个固定位置
        public static final String SITE_BRIDE_CHOICE_TOP_FLOOR = "SITE_BRIDE_CHOICE_TOP_FLOOR";
    }

    public static class PayResultStatus {
        public static final int WHAT_SUCCESS = 2;
        public static final int WHAT_FAIL = 3;
        public static final int WHAT_BIND_RED_PACKET = 4;
        public static final int WHAT_ZERO_PAY = 5;
    }

    /**
     * 爱家理财接口返回的结果参数类型
     */
    public static class AijiaResponseCode {
        public static final int SUCCESS = 1; // 成功
        public static final int BALANCE_SHORT = 10006; // 授信额度不够
        public static final int PAY_AMOUNT_LOW = 20002; // 付款金额过小,小于100
    }

    public static class PayAgent {
        public static final String KEPLER = "hunliji";//平安普惠
    }


    // 开发调试才会实用到的接口, 用于重置修改服务器地址
    // !!!!!!!注意!!!!!!! 新增与 HOST 有关的接口需要在申明的地方创建默认值
    // 也需要在这里新增修改, 与申明初始化一样
    public static void setHOST(String HOST) {
        Constants.HOST = HOST;
        HljCard.setCardHost(Constants.HOST);
    }

    public static void setHttpsHost(String httpsHost) {
        Constants.HTTPS_HOST = httpsHost;
    }

    public static class HttpPath {
        //网页
        public static final String WORK_SHARE_URL = "shop/works/%s";
        public static final String CASES_SHARE_URL = "shop/cases/%s";
        public static final String STORY_SHARE_URL = "shop/stories/%s";
        public static final String LLPAY_AGREEMENT_URL = "p/wedding/Public/wap/activity/paycol" +
                ".html";
        public static final String QINIU_IMAGE_URL = "p/wedding/home/APIUtils/image_upload_token";
        public static final String QINIU_VIDEO_URL = "p/wedding/home/APIUtils/video_upload_token";
        public static final String QINIU_AUDIO_URL =
                "p/wedding/home/APIUtils/audio_upload_token?from=CardAudio";
        public static final String QINIU_AUDIO_V2_URL =
                "p/wedding/home/APIUtils/audio_upload_token?from=CardAudiosV2";


        //php
        public static final String GET_WEDDING_REGISTER_URL = "p/wedding/index" + "" + "" + "" +
                ".php/Home/APIWeddingRegisters/index?city_id=%s&page=%s" + "&per_page=%s";
        public static final String WEDDING_ACCOUNTS_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APICashGifts/index";
        public static final String INFORM_THREAD_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APICommunityReport/community_report";
        public static final String GET_WEDDING_TASKS_SUMMARIZE_URL = "p/wedding/index" + "" + ""
                + ".php/Home/APITodo/todos_summarize";
        public static final String GET_TASKS_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/Home/APITodo/todos_v2";
        public static final String GET_WEDDING_TASKS_IF_URL = "p/wedding/index" + "" + "" + "" +
                ".php/Home/APITodo/todos_pwd";
        public static final String POST_TO_DOS_BATCH_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Home/APITodo/batch_todos";
        public static final String SETUP_WEDDING_TASKS_DATE_URL = "p/wedding/index" + "" + "" +
                "" + ".php/Home/APITodo/todos_setup";
        public static final String EDIT_WEDDING_TASK_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Home/APITodo/todo";
        public static final String DELETE_WEDDING_TASK_URL = "p/wedding/index" + "" + "" + "" +
                "" + ".php/Home/APITodo/todo?id=%s";
        public static final String CHECK_WEDDING_TASKS_URL = "p/wedding/index" + "" + "" + "" +
                "" + ".php/Home/APITodo/check_todo";
        public static final String UNCHECK_WEDDING_TASKS_URL = "p/wedding/index" + "" + "" + "" +
                ".php/Home/APITodo/uncheck_todo";
        public static final String STORIES_URL = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/Home/APIStory/story_list?sort=%s&order=desc";
        public static final String USER_STORIES_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Home/APIStory/story_list?user_id=%s&page=1&per_page=9999";
        public static final String STORY_INFO_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APIStory/story_detail?id=%s";
        public static final String DELETE_STORY_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Home/APIStory/story_move";

        public static final String LOCK_STORY_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APIStory/story_lock";
        public static final String STORY_ITEM_COLLECT_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Home/APIStory/StoryItemPraises";
        public static final String STORY_REPLY_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/home/APIStory/StoryComments";
        public static final String GET_STORY_REPLY_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIStory/story_comment?story_id=%s&page=%s" + "&per_page=20";
        public static final String STORY_REPLY_DELETE_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Home/APIStory/delete_story_comment";
        public static final String GET_STORY_ITEM_REPLY_URL = "p/wedding/index" + "" + "" + "" +
                ".php/home/APIStory/story_item_comment?story_item_id=%s&page" + "=%s" +
                "&per_page=20";
        public static final String REPLY_STORY_ITEM_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APIStory/story_item_comment";
        public static final String STORY_ITEM_REPLY_DELETE_URL = "p/wedding/index" + "" + "" + ""
                + ".php/Home/APIStory/delete_story_item_comment";
        public static final String NEW_POST_STORY_URL = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Home/APIStory/story_modify";
        public static final String TOOLS_SHARE = "p/wedding/Home/APISetting/app_share";
        public static final String WEDDING_PROGRAMS_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APIWeddingPrograms/programs";
        public static final String WEDDING_PROGRAM_ITEM_URL = "p/wedding/index" + "" + "" + "" +
                ".php/Home/APIWeddingPrograms/program_items";
        public static final String WEDDING_PROGRAMS_SORT_PATCH_URL = "p/wedding/index" + "" + ""
                + ".php/Home/APIWeddingPrograms/sort_programs";
        public static final String RESTORE_WEDDING_PROGRAMS_URL = "p/wedding/index" + "" + "" +
                "" + ".php/Home/APIWeddingPrograms/default_programs";
        public static final String WEDDING_SEATS_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Home/APIInvationV2/seat";
        public static final String GET_MY_CITY_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/home/APICity/Positioning?city=%s&district=%s&province=%s&lat_lng" + "=%s,%s";
        public static final String STORY_COLLECT_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIStory/StoryPraises";
        public static final String GET_COLLECT_SHOP_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/shop/APIShopProduct/collect_list?per_page=20&page=%s";
        public static final String POST_PRAISE_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APICommunityPostPraise/PostPraises";
        public static final String GET_ARTICLES = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/home/APIMerchant/GetMealsItem/id/%s";
        public static final String WORK_COLLECT = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/home/APIMerchant/collectors/id/%s";
        public static final String FAVORITES_WORKS_URL = "p/wedding/index" + ".php/home/APIUser/"
                + "my_set_meal_collect_list?commodity_type=%s&page=%s&per_page" + "=20&property=%s";
        public static final String FAVORITES_MERCHANTS_URL = "p/wedding/index" + ".php/home/" +
                "APIMerchant/favourites?page=%s&per_page=20&user_id=%s";
        public static final String GET_PHONES_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APIUser/Phone?user_id=%s";
        public static final String EXCHANGE_CODES = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Shop/APIRedPacket/ExchangeRedPacket?exchangeCode=%s";
        public static final String DATA_CONFIG = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/home/APISetting/GetAppSetting";
        public static final String GET_WORKS_OF_RECOMMEND = "p/wedding/index" + "" + "" + "" + ""
                + ".php/home/APIMerchant/GetRecommendMeals?cid=%s&id=%s";
        public static final String LIGHT_UP_HOTEL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/home/APILight/Light_Hotel/city/%s";
        public static final String LIGHT_UP_CAR = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/home/APILight/Light_Car/city/%s";
        public static final String GET_POPUP_POSTER_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APISetting/NewFuncRemind?cid=%s";
        public static final String FIRST_POST_ORDER = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIOrder/ConfirmOrder";
        public static final String GET_RED_PACKETS_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Shop/APIRedPacket/MyServerRedPacketList";
        public static final String POST_SUBMIT_ORDER = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIOrder/SubmitOrder";
        public static final String POST_ORDER_PAY = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/home/APIOrder/PayOrderV2";
        public static final String POST_ORDER_PAY_REST = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIOrder/PayRestV2";
        public static final String POST_REFUND_APPLY = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIOrder/ApplyRefund";
        public static final String GET_REFUND_REASON = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIOrder/GetRefundReason";
        public static final String POST_FOR_ORDER_DETAIL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/home/APIOrder/GetOrderDetail";
        public static final String POST_CANCEL_REFUND_APPLICATION2 = "p/wedding/index" + "" + ""
                + ".php/home/APIOrder/CancelRefundInDetail";
        public static final String MUSICS_URL = "p/wedding/index" + "" + "" + "" + "" + "" + "" +
                ".php/home/APIInvationV2/cardMusicV2";
        public static final String NEW_CARD_REPLIES_LIST = "p/wedding/index" + "" + "" + "" + ""
                + ".php/home/APIInvation/repliesList?user_id=%s";
        public static final String GET_OFFLINE_ORDERS = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/home/APIOrder/GetFaceOrders?pagecount=20&offset=%s";
        public static final String POST_SUBMIT_OFFLINE_ORDER = "p/wedding/index" + "" + "" + "" +
                ".php/home/APIOrder/SubmitFaceOrder";
        public static final String UPDATE_CARD_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/home/APIInvation/updateCard";
        public static final String NEW_SIGN_REPLY_DETELE = "p/wedding/index" + "" + "" + "" + ""
                + ".php/home/APIInvation/delReply";
        public static final String POST_PAY_OFFLINE = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIOrder/PayFace";
        public static final String GET_OFFLINE_ORDER = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIOrder/GetFaceOrderDetail";
        public static final String POST_CANCEL_OFFLINE_ORDER = "p/wedding/index" + "" + "" + "" +
                ".php/home/APIOrder/CancelFaceOrder";
        public static final String USER_PROFILE_COMPLETE = "p/wedding/index" + "" + "" + "" + ""
                + ".php/home/APIUser/checkNeedCompleteBaseInfo";
        public static final String COMPLETE_USER_PROFILE = "p/wedding/index" + "" + "" + "" + ""
                + ".php/home/APIUser/EditMyBaseInfo";
        public static final String GET_NEW_SUPPORTS_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIManagers/Supports/kind";
        public static final String GET_REGISTER_INFO_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/home/APIMarryRegister/RegInfo/city/%s";
        public static final String GET_USER_INFO = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/Home/APIUser/UserBaseInfo";
        public static final String GET_MARRY_TASKS_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APIToDoCategory/MarryTaskList";
        public static final String SHOP_PRODUCT_INFO = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/shop/APIShopProduct/info?id=%s&cid=%s";
        public static final String SHIPPING_ADDRESS_LIST = "p/wedding/index" + "" + "" + "" + ""
                + ".php/shop/APIShopAddress/list";
        public static final String ADDRESS_AREA_LIST = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/shop/APIShopAddress/AllAddress";
        public static final String ADD_SHIPPING_ADDRESS = "p/wedding/index" + "" + "" + "" + "" +
                ".php/shop/APIShopAddress/address";
        public static final String DELETE_SHIPPING_ADDRESS = "p/wedding/index" + "" + "" + "" +
                "" + ".php/Shop/APIShopAddress/Address?id=%s";
        public static final String DEFAULT_SHIPPING_ADDRESS = "p/wedding/index" + "" + "" + "" +
                ".php/shop/APIShopAddress/default";
        public static final String DELETE_SHOP_CART_ITEM = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Shop/APIShopCart/cart?id=%s";
        public static final String SUBMIT_PRODUCT_ORDER = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Shop/APIShopOrder/submit";
        public static final String GET_NEW_RED_PACKET_COUNT = "p/wedding/index" + "" + "" + "" +
                ".php/Shop/APIRedPacket/my_red_pack_num";
        public static final String PRODUCT_RED_PACKET = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Shop/APIRedPacket/MyWeddingProductRedPacketListV2";
        public static final String PRODUCT_COMMENT_LIST = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Shop/APIShopComment/list?product_id=%s&page=%s&per_page" + "=20";
        public static final String COMMENT_PRODUCT_ORDER = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Shop/APIShopComment/Comment";
        public static final String POST_FREE_PRODUCT_ORDER =
                "p/wedding/Shop/APIShopOrder/FreeOrder";
        public static final String REFUND_PRODUCT_ORDER_LIST = "p/wedding/index" + "" + "" + "" +
                ".php/shop/APIShopOrderRefund/index?per_page=20&page" + "=%s";
        public static final String GET_CATEGORY_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Shop/APIShopProduct/shop_category";
        public static final String CHANGE_SHOPPING_CART_ITEM_QUANTITY = "p/wedding/index" + "" +
                ".php/Shop/APIShopCart/cart";
        public static final String GET_CAR_DETAIL_URL = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Car/APICarProduct/info?id=%s";
        public static final String CHECK_IS_PUBLISHED_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Car/APICarProduct/CheckIsPublished";
        public static final String SUBMIT_CAR_ORDER_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Car/APICarOrder/submit";
        public static final String CAR_COMMENT_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Car/APICarOrderComment/MerchantCommentList?cid=%s" + "&per_page=%s";
        public static final String GET_CAR_RED_PACKT_LIST = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Shop/APIRedPacket/CarProductRedPacketList";
        public static final String CAR_PRODUCT_ORDER_LIST = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Car/APICarOrder/OrderList?per_page=20&page=%s";
        public static final String FOCUS_MERCHANT_URL = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/shop/APIProduct/focus_merchant";
        public static final String CAR_PROUDCT_PAY = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Car/APICarOrder/Pay";
        public static final String CAR_PRODUCT_PAY_REST = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Car/APICarOrder/PayRest";
        public static final String CAR_ORDER_DETAIL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Car/APICarOrder/OrderDetail?id=%s";
        public static final String GET_CAR_ORDER_DISCOUNT = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Car/APICarOrder/DiscountAmount";
        public static final String CANCEL_CAR_ORDER = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Car/APICarOrder/CloseOrder";
        public static final String CONFIRM_CAR_ORDER_SERVICE = "p/wedding/index" + "" + "" + "" +
                ".php/Car/APICarOrder/ConfirmReceive";
        public static final String POST_CAR_COMMENT_URL = "p/wedding/Car/APICarOrderComment" +
                "/addComment";
        public static final String POST_FREE_CAR_ORDER = "p/wedding/Car/APICarOrder/FreeOrder";
        public static final String REFUND_CAR_ORDER_LIST = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Car/APICarOrder/OrderList?status=24&per_page=20&page=%s";
        public static final String REFUND_CAR_ORDERS_COUNT = "p/wedding/index" + "" + "" + "" +
                "" + ".php/Car/APICarOrderComment/RefundOrderCount";
        public static final String COLOR_LABELS_SYNC_URL = "p/wedding/index" + "" + "" + "" + ""
                + ".php/home/APIMark/ColorList";
        public static final String SECONDARY_PAGE_LIST_URL = "p/wedding/index" + "" + "" + "" +
                "" + ".php/home/APISubPageWeddingService/list?per_page=20" +
                "&entity_type=%s&property=%s" + "&cid=%s";
        public static final String RELATIVE_CITY_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Home/APICity/LocateCity?cid=%s";
        public static final String POSTER_WATCH_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIFrontPageFeed/poster_watch?id=%s";
        public static final String WEDDING_PREPAERLIST_NAME = "p/wedding/index" + "" + "" + "" +
                ".php/home/APIWeddingPreparation/CategoryList";
        public static final String WEDDING_PREPAERLIST = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIWeddingPreparation/WeddingPreparationIndex?cid" +
                "=%1$s&category_id=%2$s";
        public static final String GET_USER_BIND_BANK_CARD_LIST = "p/wedding/index" + "" + "" +
                "" + ".php/Home/APIUserBankInfo";
        public static final String BANK_CARD_BIN = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/Home/APIUserBankInfo/bankCardBin?card_no=%s";
        public static final String LLPAY_SET_PAY_PASSWORD = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Home/APIUserSecurity/setPwd";
        public static final String LLPAY_CHECK_PASSWORD = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APIUserSecurity/checkPwd";
        public static final String LLPAY_PAY_A_PENY = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Home/APIUserSecurity/pay";
        public static final String LLPAY_FIND_PAY_PASSWORD = "p/wedding/index" + "" + "" + "" +
                "" + ".php/Home/APIUserSecurity/forgetPwd";
        public static final String LLPAY_RESET_PAY_PASSWORD = "p/wedding/index" + "" + "" + "" +
                ".php/Home/APIUserSecurity/setNewPwd";
        public static final String LLPAY_SUPPORT_BANK_LIST = "p/wedding/index" + "" + "" + "" +
                "" + ".php/Home/APIUserBankInfo/bankList";
        public static final String CUSTOM_SETMEAL_ORDER_SUBMIT = "p/wedding/index" + "" + "" + ""
                + ".php/Home/APICustomOrder/SubmitOrder";
        public static final String CUSTOM_SETMEAL_ORDER_CANCEL = "p/wedding/index" + "" + "" + ""
                + ".php/Home/APICustomOrder/cancel_order";
        public static final String CUSTOM_SETMEAL_ORDER_DETAIL = "p/wedding/index" + "" + "" + ""
                + ".php/Home/APICustomOrder/OderDetail?id=%s";
        public static final String CUSTOM_SETMEAL_ORDER_PAY = "p/wedding/index" + "" + "" + "" +
                ".php/Home/APICustomOrder/pay";
        public static final String CUSTOM_SETMEAL_ORDER_APPLY_REFUND = "p/wedding/index" + "" +
                "" + ".php/Home/APICustomOrderRefund/refund_apply";
        public static final String CUSTOM_SETMEAL_ORDER_CANCEL_REFUND = "p/wedding/index" + "" +
                ".php/Home/APICustomOrderRefund/cancel_refund";
        public static final String CUSTOM_SETMEAL_ORDER_REFUND_DETAIL = "p/wedding/index" + "" +
                ".php/Home/APICustomOrderRefund/order_refund_detail" + "?refund_id" + "=%s";
        public static final String NEW_WORK_INFO = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/home/APISetMeal/info/id/%s";
        public static final String AIJIA_FINANCIAL_USER_CENTER = "/p/wedding/index" + "" + "" +
                "" + ".php/home/APIUtils/aijia_api?api=service&service" + "=userCenter";
        public static final String GET_POINT_RECORD = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/shop/APIUserPointRecord";
        public static final String GET_USER_FANS = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/home/APIUser/fans?per_page=20&page=%s&&user_id=%s";
        public static final String GET_RESERVATION_LIST = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIUser/my_merchant_appointment?page=1&per_page" +
                "=9999&property_id=%s";
        public static final String GET_PUBLISH_THREAD_LIST = "p/wedding/index" + "" + "" + "" +
                "" + ".php/home/APICommunityThread/my_thread_list?user_id" + "=%1$s" +
                "&page=%2$s&per_page=%3$s";
        public static final String NEW_FOLLOW_USER = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/home/APISubscription/focus";
        public static final String UPLOAD_CREDIT_INFO = "/p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIUserCreditInfo/user_credit";
        public static final String GET_WEDDING_CONSULT = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIAdvHelper/weddingConsult";
        public static final String ADD_BUDGET_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/home/APIAdvHelper/AddBudgetInfo";
        public static final String CUSTOM_SETMEAL_REDPACKET = "p/wedding/index" + "" + "" + "" +
                ".php/Shop/APIRedPacket/CustomRedPacket";
        public static final String SETMEAL_DETAIL = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/Home/APICustomSetMeal/MealDetail?id=%s";
        public static final String SETMEAL_DETAIL_WORKS = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APICustomSetMeal/RecommendMeals?id=%s";
        public static final String CUSTOM_SETMEAL_ORDER_CONFIRM_SERVICE = "p/wedding/index" + ""
                + ".php/Home/APICustomOrder/confirm_order";
        public static final String CUSTOM_SETMEAL_ORDER_COMMENT = "p/wedding/index" + "" + "" +
                "" + ".php/Home/APICustomOrder/AddComment";
        public static final String GET_REFUND_ORDERS_V2 = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIOrder/GetRefundOrdersV2";
        public static final String POST_FOR_PRODUCT_ORDER_SHIPPING_FEE = "p/wedding/index" + "" +
                ".php/Shop/APIShopOrder/confirmOrder";
        public static final String GET_MERCHANT_FILTER = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIMerchant/merchant_filter?city=%s";
        public static final String MERCHANTS_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/home/APIMerchant/merchantV2?per_page=20&page=%s";
        public static final String COMMENT_TEST_STORE = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Home/APICommunityComment/addFunc";
        public static final String GET_PRODUCT_ORDER_REFUND_REASON = "p/wedding/index" + "" + ""
                + ".php/shop/APIShopOrderRefund/refundReason?type=%s";
        public static final String GET_PRODUCT_REFUND_MAX_MONEY = "p/wedding/index" + "" + "" +
                "" + ".php/shop/APIShopOrderRefund/maxRefundMoney" + "?order_sub_id=%s";
        public static final String POST_PRODUCT_REFUND = "p/wedding/index" + "" + "" + "" + "" +
                ".php/shop/APIShopOrderRefund/submitApplyRefund";
        public static final String GET_PRODUCT_REFUND_DETAIL = "p/wedding/index" + "" + "" + "" +
                ".php/shop/APIShopOrderRefund/refundDetail" + "?order_sub_id=%s";
        public static final String POST_REFUND_MESSAGE = "p/wedding/index" + "" + "" + "" + "" +
                ".php/shop/APIShopOrderRefund/refundMessage";
        public static final String FAVORITES_HOTEL_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIUser/followed_hotel?page=%s&per_page=20&user_id" + "=%s";
        public static final String GET_USER_FOLLOWS = "p/wedding/index" + ".php/home/APIUser/" +
                "follower?per_page=20&page=%s&&user_id=%s";
        public static final String COMMUNITY_SETUP_FOLLOW_URL =
                "p/wedding/Home/APICommunitySetup/follow";
        public static final String COMMUNITY_SETUP_UNFOLLOW_URL =
                "p/wedding/Home/APICommunitySetup/unfollow";
        public static final String COMMUNITY_MERCHANTFEED_LIKE_URL = "p/wedding/index" + "" + ""
                + ".php/Home/APIMerchantFeed/like";
        public static final String COMMUNITY_MERCHANTFEED_UNLIKE_URL = "p/wedding/index" + "" +
                "" + ".php/Home/APIMerchantFeed/unlike";
        public static final String GET_ORDER_PAYMENT_PRE_INFO = "p/wedding/index" + "" + "" + ""
                + ".php/Home/APIOrder/before_pay?order_id=%s&cid=%s";
        public static final String CANCEL_PRODUCT_ORDER_REFUND = "p/wedding/index" + "" + "" + ""
                + ".php/shop/APIShopOrderRefund/cancel";
        public static final String GET_EXPRESS_METHODS = "p/wedding/index" + "" + "" + "" + "" +
                ".php/shop/APIShopOrderRefund/express";
        public static final String POST_EXPRESS_INFO = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/shop/APIShopOrderRefund/addExpress";
        public static final String POST_EXPRESS_INFO_EDIT = "p/wedding/index" + "" + "" + "" + ""
                + ".php/shop/APIShopOrderRefund/editExpress";
        public static final String GET_MARK_INFO = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/home/APIMark/info?markId=%s&city_code=%s";
        public static final String GET_MARK_LABEL_INFO = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APIMark/relativeMarks?id=%s";
        public static final String GET_MARK_LIST = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/home/APIMark/list?markId=%s&markType=%s&order=%s&sort" +
                "=%s&city_code=%s&page" + "=%s&per_page=20";
        public static final String MARK_CANCEL_FOLLOW = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Home/APIMark/Mark?id=%s";
        public static final String MARK_ADD_FOLLOW = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/home/APIMark/addMark?id=%s";
        public static final String LIANG_HUA_PAI_URL = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/home/APIUtils/financial_api?type=liang_hua_pai";
        public static final String GET_BUDGET_CATEGORY = "p/wedding/home/APIBudget/category";
        public static final String POST_EDIT_BUDGET = "p/wedding/home/APIBudget/edit";
        public static final String GET_BUDGET_INFO = "p/wedding/home/APIBudget/info";
        public static final String MY_ALL_FOLLOWED_CHANNELS =
                "p/wedding/Home/APICommunityChannel/index?per_page=200&page=1";
        public static final String COMMUNITY_HOT_CHANNEL_URL2 =
                "p/wedding/Home/APICommunitySetup/HotCommunityChannel" + "?&per_page=200&page=1";
        public static final String THEME_V2_LIST = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/Home/APIInvationV2/templateList" + (DEBUG ? "Preview" : "");
        public static final String TEMPLATE_V2_LIST = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Home/APIInvationV2/puzzleTplByThemeId" + (DEBUG ? "Preview" : "") +
                "?id=%s";
        public static final String CARD_V2_SAVE = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/Home/APIInvationV2/saveCard";
        public static final String CARD_V2_LIST = "p/wedding/index" + "" + "" + "" + "" + "" + ""
                + ".php/Home/APIInvationV2/cardList";
        public static final String PAGE_V2_CHANGE_POSITION = "p/wedding/index" + "" + "" + "" +
                "" + ".php/Home/APIInvationV2/changePosition";
        //获取用户参与的活动列表
        public static final String FONT_LIST_URL = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/Home/APIInvationV2/fontList";
        public static final String CARD_V2_REPLY_LIST = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Home/APIInvationV2/repliesList";
        public static final String CARD_V2_REPLY_DELETE = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APIInvationV2/delReply";
        public static final String UNLOCK_THEME_V2_URL = "p/wedding/index" + "" + "" + "" + "" +
                ".php/Home/APIInvationV2/unlockTheme";
        public static final String CARD_V2_SHARE = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/Home/APIInvationV2/share";
        public static final String LIGHT_UP = "p/wedding/index" + "" + ".php/home/APILight";
        public static final String BUDGET_RANK = "p/wedding/home/APIBudget/level?money=%s";
        public static final String GET_BUDGET_SHARE = "p/wedding/home/APIBudget/" +
                "share_info?id=%s";
        //动态点赞列表
        public static final String GET_PRAISED_LIST = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/Shop/APIMerchantFeed/praised_list?id=%s&page=%s" + "&per_page=20";
        //专题活动报名
        public static final String SIGN_UP = "p/wedding/index" + "" + "" + "" + "" + "" + "" + ""
                + ".php/Home/APIFinderActivity/signUp";
        public static final String GET_JOURNEY_THEME = "p/wedding/Home/APIContentBundle/theme";
        //旅拍全部商家
        public static final String GET_ALL_THEME_MERCHANT =
                "p/wedding/Home/APIContentBundle/AllMerchant";
        //旅拍全部套餐
        public static final String GET_ALL_THEME_WORK =
                "p/wedding/Home/APIContentBundle/AllPackage";

        //旅拍单元
        public static final String TRAVELBUNDLE = "p/wedding/Home/APIContentBundle/TravelBundle";
        //旅拍全部攻略
        public static final String GUIDELIST = "p/wedding/index" + "" + "" + "" + "" + "" + "" +
                ".php/home/APIContentBundle/guideList";
        //轻奢优品
        public static final String GET_LUXURY_LIST = "p/wedding/index" + "" + "" + "" + "" + "" +
                ".php/home/APIContentBundle/luxury";

        // 新版服务订单支付接口
        public static final String SERVICE_ORDER_PAYMENT = "p/wedding/Home/APIOrderV2/pay";
        //婚宴酒店订单支付接口
        public static final String HOTEL_PERIOD_ORDER_PAYMENT =
                "p/wedding/home/APIHotelPeriodOrder/pay";
        //新直播数量
        public static final String GET_LIVE_NEW_COUNT = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Home/APILiveChannel/new_count?last_channel_id=%s";
        // 服务订单套餐快照信息
        public static final String GET_SERVICE_ORDER_WORK_SNAPSHOT = "/p/wedding/index" + "" + ""
                + ".php/home/APIOrderV2/snapshot?order_id=%s&set_meal_id=%s";
        //商家套餐和案例(等work model统一了删除)
        public static final String GET_MERCHANT_WORKS_AND_CASES = "p/wedding/index" + "" + "" +
                "" + ".php/home/APISetMeal/list?id=%s&kind=%s&sort=%s&page=%s&per_page=%s";
        //婚品商家主页列表
        public static final String GET_MERCHANT_GOODS = "p/wedding/index" + "" + "" + "" + "" +
                "" + ".php/Shop/APIShopProduct/MerchantGoodsList?merchant_id=%s";
        //婚品二级分类列表
        public static final String SHOP_PRODUCT_LIST = "p/wedding/index" + "" + "" + "" + "" + ""
                + ".php/shop/APIShopProduct/product_list?category_id=%s";
        //婚品频道页列表
        public static final String HOME_PRODUCT_LIST_V2 = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APISubPageShop/product_listV2?pid=%s";
        // 新版婚品订单支付接口
        public static final String PRODUCT_ORDER_PAYMENT = "p/wedding/shop/APIShopOrder/pay";
        // 会员开通支付接口
        public static final String MEMBER_ORDER_PAYMENT = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APIUserMemberOrder/pay";
        //社区频道页话题列表
        public static final String GET_COMMUNITY_CHANNEL_THREADS_V2 =
                "p/wedding/Home/APICommunityChannel/threadsV2?community_channel_id=%s&sort=%s" +
                        "&city=%s";
        //同城备婚话题列表
        public static final String GET_SAME_CITY_THREADS = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Home/APICommunityThread/same_city_threads?list_type=%s";
        //同城最新,显示所在地为重点城市及该市关联城市用户所发的帖子。
        public static final String GET_SAME_CITY_NEWEST_THREADS = "p/wedding/index" + "" + "" +
                "" + ".php/Home/APICommunityThread/same_city_newest_thread";
        //婚品推荐列表(1:用户行为 2:购物车 3:支付成功)
        public static final String GET_RECOMMEND_PRODUCTS = "p/wedding/index" + "" + "" + "" + ""
                + ".php/Shop/APIShopProduct/userRecommendProduct?type=%s";

        //婚品分类 id 可选传参 不传返回全部一级分类
        public static final String GET_SHOP_PRODUCT_CATEGORY = "p/wedding/index" + "" + "" + "" +
                ".php/Shop/APIShopCategory/category_list";

        //获得婚品分类页 专题 @param id 单独查询，传0表示全部  level 层级 默认1级
        public static final String GET_SHOP_CATEGORY_SUB_PAGE =
                "p/wedding/shop/APIShopProduct/category_sub_page_map_uri";
        //购买保险
        public static final String PAY_INSURANCE = "p/wedding/index" + "" + "" + "" + "" + "" +
                "" + ".php/home/APIUserInsurance/submit";
        //笔记推荐列表
        public static final String GET_RECOMMEND_NOTES = "p/wedding/index" + "" + "" + "" + "" +
                ".php/home/APINote/recommends_v2?last_id=%s&timestamp=%s";
        //购物车凑单婚品列表
        public static final String GET_SHOPPING_CART_ADD_ON =
                "p/wedding/Shop/APIShopProduct/MerchantGoodsList?merchant_id=%s";
    }

    /*
    ************************************************************************************************
    * HTTP请求或者WebSocket连接的 host,socket_host, urls
     */
    public static String getAbsUrl(String path) {
        return HOST + path;
    }

    public static String getHttpsUrl(String path) {
        return HTTPS_HOST + path;
    }

    public static String getAbsUrl(String path, Object... args) {
        return HOST + String.format(path, args);
    }

    /**
     * 根据当前环境选择爱家分期public key
     */
    public static String getAijiaPubKey() {
        if (DEBUG) {
            // 如果是debug版本,则需要根据当前选择的环境切换公钥
            if (HOST.equals("http://www.hunliji.com/")) {
                // 正式环境
                return AIJIA_PUB_KEY;
            } else {
                return AIJIA_PUB_KEY_DEBUG;
            }
        } else {
            return AIJIA_PUB_KEY;
        }
    }

}
