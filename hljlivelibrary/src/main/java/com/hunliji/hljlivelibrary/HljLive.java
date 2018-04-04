package com.hunliji.hljlivelibrary;

import com.hunliji.hljlivelibrary.api.LiveInterface;

/**
 * Created by Suncloud on 2016/10/24.
 */

public class HljLive {
    private static String LIVE_HOST = "https://message.hunliji.com:5051/";
    public static final String LIVE_HISTORIES =
            "history?channel_id=%s&room_type=%s&last_id=%s&no_stick=%s&per_page=%s";
    public static final String LIVE_CHAT =
            "chat?live_role=%s&channel_id=%s";


    public class ROLE{
        public static final int HOST=1;
        public static final int GUEST=2;
        public static final int CUSTORMER=3;
    }

    public class ROOM{
        public static final int LIVE=1;
        public static final int CHAT=2;
        public static final int RELEVANT=3;
    }

    public final static int CHOOSE_PHOTO = 1;
    public final static int CHOOSE_VIDEO = 2;

    public static String getLiveHost() {
        return LIVE_HOST;
    }

    public static String getLivePath(String path, Object... query) {
        if (query != null && query.length > 0) {
            return LIVE_HOST + String.format(path, query);
        } else {
            return LIVE_HOST + path;
        }
    }

    public static void setLiveHost(String liveHost) {
        LIVE_HOST = liveHost;
    }

    private static boolean debug = false;
    public static LiveInterface apiInterface;
    public static final String PREF_FILE = "pref";//本地存储，与主工程一致

    /**
     * HljLive
     *
     * @param debug
     */
    public static void setDebug(boolean debug) {
        HljLive.debug = debug;
    }

    /**
     * 初始化Live模块,在使用该模块相关功能之前必须先使用这个方法进行初始化
     *
     * @param debug debug开关
     * @param mfi   该模块使用的服务器接口定义
     */
    public static void init(boolean debug, LiveInterface mfi) {
        HljLive.debug = debug;
        HljLive.apiInterface = mfi;
    }
}
