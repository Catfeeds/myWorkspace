package com.hunliji.hljcommonlibrary;

/**
 * Created by werther on 16/7/21.
 * HljCommon这个类库中的一些Constants参数
 * 和一些常用的,暴露给使用者的静态方法
 */
public class HljCommon {
    public static boolean debug = false;
    public static final String TAG = "HljCommon";
    public static final String LOGIN_SEED = "*#0621ix51y6679&";
    public static final int PER_PAGE = 20;//分页数目
    public static final String WX_EDU_URL = "https://pay.weixin.qq.com/news/20150415.shtml";


    public static final String MARKER_ICON_RED = "https://qnm.hunliji.com/icon_map_mark.png";

    /**
     * 设置HljCommon库的debug开关
     *
     * @param debug
     */
    public static void setDebug(boolean debug) {
        HljCommon.debug = debug;
    }

    /**
     * 一些常用的配置文件中存储的文件的文件名key value定义
     */
    public static class FileNames {
        public static final String PREF_FILE = TAG + "pref";
        public static final String USER_FILE = TAG + "user.json";
        public static final String HX_USER_FILE = TAG + "hx_user.json";
        public static final String LOCATION_FILE = TAG + "location.json";
        public static final String CITY_FILE = TAG + "city.json";
        public static final String UPLOAD_FILE = TAG + "upload.json";
        public static final String ADDRESS_AREA_FILE = TAG + "address_area.json";
        public static final String SUPPORTS_FILE = "supports.json";
        public static final String NOTIFY_FILE = "notify";
        public static final String CAR_CART_FILE = "car_cart.json";
        public static final String CONFIG_FILE = TAG + "config{%s}{%s}";
    }

    /**
     * POST相关
     */
    public static class POST_SITES {
        public static final String MAIN_TOP_BANNER = "MAIN_TOP_BANNER";
        public static final String MAIN_ENTRY_BUTTON = "MAIN_ENTRY_BUTTON";
        public static final String CAR_BANNER2 = "CAR_BANNER_V2";
        public static final String SITE_CAR_BANNER_V3 = "SITE_CAR_BANNER_V3";
        public static final String TOOLS_BANNER = "TOOLS_BANNER";
        public static final String SITE_MAIN_SINGLE_PIC_BANNER = "SITE_MAIN_SINGLE_PIC_BANNER";
        public static final String SITE_MAIN_TRIPLE_PIC_BANNER = "SITE_MAIN_TRIPLE_PIC_BANNER";
        public static final String SITE_FINANCIAL_MARKET_BANNER = "SITE_FINANCIAL_MARKET_BANNER";
        public static final String SITE_FIND_TOP_BANNER = "SITE_FIND_TOP_BANNER";//发现页面 推荐
        public static final String SITE_BRIDE_GROUP = "SITE_BRIDE_GROUP";
        public static final String SITE_BRIDE_MIDDLE = "SITE_BRIDE_MIDDLE";
        public static final String SITE_MAIN_HOME_CHANNEL = "SITE_HOME_CHANNEL";
        public static final String SITE_MAIN_HOME_CHANNEL_NEW = "SITE_HOME_CHANNEL_NEW";
        public static final String SITE_FINANCIAL_HOTEL = "SITE_FINANCIAL_HOTEL"; // 首页金融和酒店
        public static final String BANNER_PROPERTY_CATEGORY_PAGE =
                "BANNER_PROPERTY_CATEGORY_PAGE";//分类页
        public static final String SITE_DACU = "SITE_DACU";//首页大促
        public static final String SITE_ASQUESTION_BANNER = "SITE_ASQUESTION_BANNER";//问答
        public static final String SITE_SHOP_CHANNEL_BANNER = "SITE_SHOP_CHANNEL_BANNER"; //婚品频道
        public static final String SITE_SHOP_HLG_CENTRE_SITE = "HLG_CENTRE_SITE"; //婚品频道中间三个Poster
        public static final String SITE_USER_ASQUESTION_BANNER = "SITE_USER_ASQUESTION_BANNER";
        public static final String USER_QA_HEADLINE = "USER_QA_HEADLINE";//问答头条
        public static final String CITY_WEDDING_TOP_BANNER = "CITY_WEDDING_TOP_BANNER";
        public static final String SHOP_ACTIVITY_BANNER = "SHOP_ACTIVITY_BANNER";
        public static final String SITE_SHOP_CATEGORY = "SITE_SHOP_CATEGORY";
        public static final String SHOP_BRAND_MERCHANT = "SHOP_BRAND_MERCHANT";
        public static final String SITE_TRAVEL_MERCHANT_EXPOSURE_TOP =
                "SITE_TRAVEL_MERCHANT_EXPOSURE_TOP";
        public static final String TRAVEL_LIMIT_TOP_BANNER = "TRAVEL_LIMIT_TOP_BANNER";
        public static final String SITE_CUSTOMER_CARD_LIST = "SITE_CARD_FEATURE_NOTICE";//用户端 请帖列表
        public static final String SITE_CARD_MASTER_CARD_LIST =
                "SITE_CARD_MASTER_CARD_FEATURE_NOTICE";//请帖大师 请帖列表
        public static final String SITE_CARD_CASH__BOTTOM_BANNER =
                "SITE_CARD_CASH__BOTTOM_BANNER";//用户端 礼物礼金
        public static final String SITE_CARD_MASTER_CASH__BOTTOM_BANNER =
                "SITE_CARD_MASTER_CASH__BOTTOM_BANNER";//请帖大师 礼物礼金
        public static final String GET_WEDDING_MATERIAL_BANNER = "GET_WEDDING_MATERIAL_BANNER";
        //新娘圈领取资料
        public static final String WEDDING_BIBLE_BANNER = "WEDDING_BIBLE_BANNER"; //结婚宝典
        public static final String SITE_ALL_INCLUSIVE_BANNER="SITE_ALL_INCLUSIVE_BANNER";//一价全包banner
        public static final String SITE_ALL_INCLUSIVE_MARKETING="SITE_ALL_INCLUSIVE_MARKETING";//一价全包-营销模块
        public static final String SITE_LOOK_FOR_MERCHANT = "SITE_LOOK_FOR_MERCHANT";//找商家
        public static final String SITE_SHARE_TOP_BANNER = "SITE_SHARE_TOP_BANNER";//分享弹窗广告
        public static final String SITE_MAIN_BEFORE_MARRIAGE = "SITE_MAIN_BEFORE_MARRIAGE";//备婚前
        public static final String SITE_MAIN_IN_MARRIAGE = "SITE_MAIN_IN_MARRIAGE";//备婚中
        public static final String SITE_MAIN_AFTER_MARRIAGE = "SITE_MAIN_AFTER_MARRIAGE";//备婚后
    }


    public static class BLOCK_ID {
        public static final int HomePageFragment_V2 = 1101;//客户端7.3.2首页使用的block_id
        public static final int HomePageFragment = 1001;
        public static final int CategoryFragment = 1002;
        public static final int ToolsFragment = 1004;
        public static final int SocialHotFragment = 1005;
        public static final int FinancialMarketListActivity = 1006;
        public static final int WeddingCarActivity = 1007;
        public static final int SplashActivity = 1011;
        public static final int BrigadeLimitBuyActivity = 1016;
        public static final int SubPageFragment = 1018;
        public static final int ProductChannelActivity = 1026;
        public static final int QuestionAnswerFragment = 1028;
        public static final int QuestionHeadlineFragment = 1029;
        public static final int SameCityThreadListActivity = 1031;
        public static final int TravelChannelActivity = 1033;
        public static final int KBLOCK_LIGHT_LUXURY_PAGE = 1030;//旅拍严选
        public static final int EXPERIENCE_STORE_BANNER_EXHIBITION = 1023;//体验店
        public static final int PINGPAI_TOP_BANNER = 1024;//品牌馆
        public static final int COST_EFFECTIVE_TOP_BANNER = 1025;//性价比
        public static final int FIND_SETMEAL_CATEGORY = 1027;//找套餐
        public static final int BLOCK_HOTEL_CHANNEL = 1014;    //酒店频道
        public static final int SHOPPING_CATEGORY = 1032;//婚品分类
        public static final int MainActivityPopup = 1034;//首页弹窗
        public static final int WeekHotsActivity = 1035;//本周热门
        public static final int CustomerCardListFragment = 4001; //用户端 请帖列表
        public static final int CustomerReceiveGiftCashActivity = 1036; //用户端 礼物礼金
        public static final int CardMasterCardListFragment = 4011; //请帖大师 请帖列表
        public static final int CardMasterReceiveGiftCashActivity = 4012; //请帖大师 礼物礼金
        public static final int RecommendNoteListFragment = 1018;//发现页-推荐
        public static final int NINETY_NINE_LV_PAI_BANNER = 6001; // 9.9抢旅拍列表顶部banner
        public static final int COMMUNITY_OBTAIN_MATERIAL = 1042;//领取备婚资料
        public static final int WeddingBibleActivity = 1043; //结婚宝典
        public static final int ONE_PAY_ALL_INCLUSIVE =1037;//一价全包poster
        public static final int FindMerchantHomeFragment = 1038; // 找商家
        public static final int ShareDialog = 1039; // 分享弹窗banner

        //=============商家端===================//

        public static final int HomeFragment = 2001;
        public static final int MyLevelActivity = 2002;
        public static final int MarketingFragment = 2021;
        public static final int InteractionFragment = 2022;
    }

    /**
     * 登录相关
     */
    public static class Login {
        public static final int REGISTER = 55;
        public static final int PARTNER = 56;
        public static final int LOGINCHECK = 57;
    }

    public static class QINIU {
        public static final String PHOTO_URL = "?imageView2/2/w/%s/format/webp/q/100";
        public static final String PHOTO_URL2 = "?imageView2/2/w/%s/h/%s/format/webp/q/100";
        public static final String PHOTO_URL3 = "?imageView2/1/w/%s/h/%s/format/webp/q/100";
        public static final String SCREEN_SHOT_URL_0_SECONDS = "?vframe/jpg/offset/0/rotate/auto";
        public static final String SCREEN_SHOT_URL_1_SECONDS = "?vframe/jpg/offset/1/rotate/auto";
        public static final String SCREEN_SHOT_URL_3_SECONDS = "?vframe/jpg/offset/3/rotate/auto";
    }

    public static class Report {
        //举报类型 thread:话题 post:回帖 question:问题 answer:回答
        public static final String REPORT_THREAD = "thread";
        public static final String REPORT_POST = "post";
        public static final String REPORT_QUESTION = "question";
        public static final String REPORT_ANSWER = "answer";
        public static final String REPORT_COMMENT = "comment";
        public static final String REPORT_NOTE = "note";
        public static final String REPORT_NOTE_BOOK = "note_book";
    }

    public static class RequestCode {}

    //SharedPreferences 相关的key
    public static class SharedPreferencesNames {
        //点赞dialog是否显示
        public static final String SHOW_PRAISE_DIALOG = "show_praise_dialog";
        //点赞dialog显示的版本号
        public static final String SHOW_PRAISE_DIALOG_VERSION = "show_praise_dialog_version";
        //app启动次数
        public static final String APP_START_COUNT = "app_start_count";
        // 直播页面分享hint
        public static final String SHOW_LIVE_SHARE_HINT = "show_live_share_hint";
        public static final String PREF_LIVE_HINT_IDS = "pref_live_hint_ids";
        public static final String CART_LIST_COUNT = "cart_list_count";//购物车数量
        public static final String PREF_NOTICE_OPEN_DLG_PAY = "has_showed_notice_open_dlg_pay";
        public static final String PREF_FUND_DIALOG_DATE = "fund_dialog_date";//礼金理财弹窗显示的时间
        public static final String PREF_FUND_DIALOG_COUNT = "fund_dialog_count";//礼金理财弹窗显示的次数
        //结婚预算
        public static final String WEDDING_BUDGET = "wedding_budget";
        // 金融超市页面的poster弹窗记录id
        public static final String FINANCIAL_HOME_POSTER_IDS = "financial_poster_ids";
        public static final String SHOWED_CARD_RENAME_DENIED = "showed_card_rename_denied";
        // 已关闭过的请帖列表页面公告id
        public static final String CLOSED_CARD_NOTICE_IDS = "closed_card_notice_ids";
        public static final String COMMUNITY_CHANNEL_NEW = "community_channel_new";//新娘圈红点引导
        public static final String PREF_LAST_BIBLE = "last_bible"; //上一次浏览的宝典
        public static final String PREF_SELECT_CHANNEL_HINT_CLICKED = "select_channel_hint_clicked"; //发话题频道选择引导
        // 首页icon滑动记录
        public static final String HOME_PAGE_POSTER_BUTTON_SCROLL_FIRST =
                "home_page_poster_button_scroll_first";
    }

    public static class DateFormat {
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
        public static final String DATE_FORMAT_SHORT1 = "yyyy.MM.dd";
    }

}
