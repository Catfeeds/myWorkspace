package com.hunliji.hljcommonlibrary.modules.helper;

/**
 * Created by luohanlin on 2017/7/5.
 * ARouter的路由Path统一管理定义
 */

public class RouterPath {
    static final String MAP_LIB = "/map_lib";

    public static class IntentPath {

        //-------------------customer app------------------------

        public static class Customer {
            static final String CUSTOMER_APP = "/customer";
            public static final String MAIN = CUSTOMER_APP + "/main_activity";
            public static final String CARD_V2_LIST = CUSTOMER_APP + "/card_v2_list_activity";
            public static final String OPEN_MEMBER = CUSTOMER_APP + "/open_member_activity";
            public static final String COMPLETE_PROFILE = CUSTOMER_APP +
                    "/complete_profile_activity";
            public static final String MERCHANT_HOME = CUSTOMER_APP + "/merchant_activity";
            public static final String PRODUCT_MERCHANT_HOME = CUSTOMER_APP +
                    "/product_merchant_activity";
            public static final String USER_PROFILE = CUSTOMER_APP + "/user_profile_activity";
            public static final String COMMUNITY_CHANNEL = CUSTOMER_APP +
                    "/community_channel_activity";
            public static final String COMMUNITY_THREAD_DETAIL = CUSTOMER_APP +
                    "/community_thread_detail_activity";
            public static final String WEDDING_DATE = CUSTOMER_APP + "/wedding_date_setActivity";
            public static final String COLLECT = CUSTOMER_APP + "/collect_activity";
            public static final String SHOP_PRODUCT = CUSTOMER_APP + "/product_detail_activity";
            public static final String WORK_ACTIVITY = CUSTOMER_APP + "/work_activity";
            public static final String CITY_LIST_ACTIVITY = CUSTOMER_APP + "/city_list_activity";
            public static final String MERCHANT_WORK_LIST = CUSTOMER_APP + "/merchant_work_list";
            public static final String FEED_BACK_ACTIVITY = CUSTOMER_APP + "/feed_back_activity";
            public static final String EVENT_DETAIL_ACTIVITY = CUSTOMER_APP +
                    "/event_detail_activity";
            public static final String INSTALMENT_MERCHANT_LIST_ACTIVITY = CUSTOMER_APP +
                    "/instalment_merchant_list_activity";
            public static final String NEW_SEARCH_RESULT_ACTIVITY = CUSTOMER_APP +
                    "/new_search_result_activity";
            public static final String WEDDING_CAR_TEAM_ACTIVITY = CUSTOMER_APP +
                    "/car_team_activity";
            public static final String MESSAGE_HOME_ACTIVITY = CUSTOMER_APP + "/message_ho";
            public static final String SUB_PAGE_DETAIL_ACTIVITY = CUSTOMER_APP +
                    "/sub_page_detail_activity";
            public static final String CAR_ORDER_CONFIRM_ACTIVITY = CUSTOMER_APP +
                    "/car_order_confirm_activity";
            public static final String WEDDING_TABLE_LIST_ACTIVITY = CUSTOMER_APP +
                    "/wedding_table_list_activity";
            public static final String MY_WALLET_ACTIVITY = CUSTOMER_APP + "/my_wallet_activity";

            public static class Login {
                public static final String ARG_IS_RESET = "reset";
            }

            public static class LoginActivityPath extends Login {
                public static final String LOGIN = CUSTOMER_APP + "/login_activity";
            }

            public static class BaseWsChat {
                public static final String ARG_USER = "user";
                public static final String ARG_USER_ID = "id";
                public static final String ARG_IS_COLLECTED = "is_collected";
                public static final String ARG_CITY = "city";
                public static final String ARG_AUTO_MSG = "auto_msg";
                public static final String ARG_WS_TRACK = "ws_track";
                public static final String ARG_CONTACT_PHONES = "contact_phones";
            }

            public static class WsCustomChatActivityPath extends BaseWsChat {
                public static final String WS_CUSTOMER_CHAT_ACTIVITY = CUSTOMER_APP +
                        "/ws_customer_chat_activity";
            }

            public static class WsCustomDialogChatActivityPath extends BaseWsChat {
                public static final String WS_CUSTOMER_CHAT_DIALOG_ACTIVITY = CUSTOMER_APP +
                        "/ws_customer_chat_dialog_activity";
            }

            public static class MyOrder {
                public static final String ARG_BACK_MAIN = "back_main";
                public static final String ARG_SELECT_TAB = "select_tab";

                public static class Tab {
                    public static final int SERVICE_ORDER = 0; //服务订单tab
                    public static final int RESERVATION = 1; //预约tab
                    public static final int PRODUCT_ORDER = 2; //婚品订单tab
                    public static final int HOTEL_PERIOD_ORDER = 3; //婚宴订单tab
                    public static final int EVENT = 4; //活动tab
                    public static final int CAR_ORDER = 5; //婚车订单tab
                }

            }

            public static class MyOrderListActivityPath extends MyOrder {
                public static final String ORDER = CUSTOMER_APP + "/my_order_list_activity";
            }

            public static class MerchantList {
                public static final String ARG_PROPERTY_ID = "property_id";

                public static class Property {
                    public static final long HOTEL = 13L; //酒店分类
                }

            }

            public static class MerchantListActivityPath extends MerchantList {
                public static final String MERCHANT_LIST_ACTIVITY = CUSTOMER_APP +
                        "/merchant_list_activity";
            }

            public static class Debug {
                public static final String CHANGE_HOST = CUSTOMER_APP + "/change_host_activity";
            }

        }
        //-------------------customer app------------------------


        //-------------------merchant app------------------------
        public static class Merchant {
            static final String MERCHANT = "/merchant";

            public static final String HOME = MERCHANT + "/home_activity";
            public static final String QUESTION_COMPLAIN = MERCHANT + "/complain_activity";
        }
        //-------------------merchant app------------------------

        //-------------------card library------------------------
        public static class Card {
            static final String CARD_LIB = "/card_lib";

            public static final String CARD_INFO_EDIT = CARD_LIB + "/card_info_edit_activity";
            public static final String CARD_SEND = CARD_LIB + "/card_send_activity";
            public static final String CARD_SETTING = CARD_LIB + "/card_setting_activity";
        }
        //-------------------card library------------------------

        //-------------------note library------------------------
        public static class Note {
            static final String NOTE_LIB = "/note_lib";
            public static final String CREATE_NOTE = NOTE_LIB + "/create_note_for_video_activity";
        }
        //-------------------note library------------------------

        //-------------------debug library------------------------
        public static class Debug {
            static final String DEBUG_LIB = "/debug_lib";
            public static final String HTTP_LOG_LIST = DEBUG_LIB + "/http_log_activity";
            public static final String TRACKER_LOG_LIST = DEBUG_LIB + "/tracker_logs_activity";
            public static final String PAGE_LABEL_CHECK = DEBUG_LIB + "/page_label_check_activity";
        }
        //-------------------debug library------------------------

        //-------------------qa library------------------------
        public static class QuestionAnswer {
            static final String QUESTION_ANSWER_LIB = "/question_answer_lib";
            public static final String ASK_QUESTION_LIST = QUESTION_ANSWER_LIB +
                    "/ask_question_list_activity";
        }
        //-------------------qa library------------------------

        //-------------------map library------------------------
        public static class Map {
            public static final String NAVIGATE_MAP = MAP_LIB + "/navigate_map_activity";
        }
        //-------------------map library------------------------

        //-------------------map library------------------------
        public static class Live {
            static final String LIVE_LIB = "/live_lib";
            public static final String LIVE_CHANNEL_ACTIVITY = LIVE_LIB + "/live_channel_activity";
        }
        //-------------------map library------------------------

    }

    public static class ServicePath {
        static final String SERVICE_DOMAIN = "/hlj/app/service";

        public static final String APPLICATION_CONFIG = SERVICE_DOMAIN + "/application_config";
        public static final String BANNER_JUMP = SERVICE_DOMAIN + "/banner_jump";
        public static final String GO_MERCHANT_SER = SERVICE_DOMAIN +
                "/go_merchant_service_web_view";
        public static final String SET_LIVE_WEB_VIEW = SERVICE_DOMAIN + "/set_live_web_view";
        public static final String GO_NOTE_ADS_WEB_VIEW = SERVICE_DOMAIN + "/go_note_ads_web_view";

        public static final String GO_TO_SUPPORT = SERVICE_DOMAIN + "/go_to_support";

        public static final String TRACKER = SERVICE_DOMAIN + "/tracker";
        public static final String WEDDING_CAR_SERVICE = SERVICE_DOMAIN + "/wedding_car";

        public static class Map {
            public static final String MAP_LIBRARY_SERVICE = MAP_LIB + "/map_library_service";
        }
    }
}
