package com.hunliji.hljsharelibrary;

import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by werther on 16/8/10.
 */
public class HljShare {

    public static String QQ_FILE = "qq.json";
    public static String WEIBO_FILE = "weibo.json";
    public static String WEIXINKEY = "wx9acfc1464c57b9b4";
    public static String WEIXINSECRET = "7ef0bffb6e04b687dead503c8cd83638";
    public static String QQKEY = "100370679";
    public static String WEIBOKEY = "2726144177";
    public static String WEIBO_CALLBACK = "http://marrymemo.com";
    public static String SCOPE = "follow_app_official_microblog";

    private static boolean isShowSharePoster;
    private static List<String> sharePosterDisablePages;

    public static String SHARE_IMP_URL;
    public static String SHARE_CLICK_URL;

    public static class RequestCode {
        public static final int SHARE_TO_QQ = 1002;
        public static final int SHARE_TO_WEIBO = 1003;
        public static final int SHARE_TO_QQZONE = 1004;
        public static final int SHARE_TO_PENGYOU = 1005;
        public static final int SHARE_TO_WEIXIN = 1006;
        public static final int SHARE_TO_TXWEIBO = 1007;
    }

    public static String getShareTypeName(int shareRequestCode) {
        switch (shareRequestCode) {
            case RequestCode.SHARE_TO_QQ:
                return "QQ";
            case RequestCode.SHARE_TO_WEIBO:
                return "Weibo";
            case RequestCode.SHARE_TO_QQZONE:
                return "QQZone";
            case RequestCode.SHARE_TO_PENGYOU:
                return "Timeline";
            case RequestCode.SHARE_TO_WEIXIN:
                return "Session";
            case RequestCode.SHARE_TO_TXWEIBO:
                return "TXWeibo";
        }
        return null;
    }

    public static void initShareKey(Map<String, String> keys) {
        WEIXINKEY = keys.get("WEIXINKEY");
        WEIXINSECRET = keys.get("WEIXINSECRET");
        QQKEY = keys.get("QQKEY");
        WEIBOKEY = keys.get("WEIBOKEY");
        WEIBO_CALLBACK = keys.get("WEIBO_CALLBACK");
    }

    public static void setSharePosterDisablePages(List<String> sharePosterDisablePages) {
        HljShare.sharePosterDisablePages = sharePosterDisablePages;
    }

    public static void openSharePoster() {
        HljShare.isShowSharePoster = true;
    }

    public static boolean showSharePoster(String pageClassName) {
        if (CommonUtil.isCollectionEmpty(sharePosterDisablePages)) {
            return isShowSharePoster;
        }
        return !TextUtils.isEmpty(pageClassName) && !sharePosterDisablePages.contains
                (pageClassName);
    }

    public static void setMiaoZhenUrl(String shareImpUrl,String shareClickUrl) {
        SHARE_CLICK_URL = shareClickUrl;
        SHARE_IMP_URL = shareImpUrl;
    }
}
