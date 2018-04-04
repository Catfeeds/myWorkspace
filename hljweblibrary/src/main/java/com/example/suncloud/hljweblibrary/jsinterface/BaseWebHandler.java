package com.example.suncloud.hljweblibrary.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Suncloud on 2016/8/19.
 */
public class BaseWebHandler {

    protected String path;
    protected Context context;
    protected Handler handler;
    protected WebView webView;
    protected ShareUtil shareUtil;
    protected boolean canBack;


    public static final int ON_WEB_SHARE = 1;
    public static final int WEB_SHARE_CHECK = 2;
    public static final int WEB_UPDATE_DONE = 3;

    public BaseWebHandler(
            Context context, String path, WebView webView, Handler handler) {
        this.path = path;
        this.handler = handler;
        this.webView = webView;
        this.context = context;
    }

    private Handler shareHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (webView == null) {
                return false;
            }
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    webView.loadUrl("javascript:window.fw.app_v3.wxShare.onShareTimeLine();");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    webView.loadUrl("javascript:window.fw.app_v3.wxShare.onShareAppMessage();");
                    break;
            }
            return false;
        }
    });


    public boolean isCanBack() {
        return canBack;
    }

    public ShareUtil getShareUtil() {
        return shareUtil;
    }


    @JavascriptInterface
    public void pageInfoUpdate(String... strings) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = WEB_UPDATE_DONE;
            handler.sendMessage(msg);
        }
    }

    @JavascriptInterface
    public void getShareInfo(String data) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject object = new JSONObject(data);
                String title = object.optString("title");
                String imgUrl = object.optString("imgUrl");
                String link = object.optString("link");
                String desc = object.optString("desc");
                shareUtil = new ShareUtil(context, link, title, desc, desc, imgUrl, shareHandler);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (shareUtil != null && webView != null) {
            Message msg = new Message();
            msg.what = ON_WEB_SHARE;
            handler.sendMessage(msg);
        }
    }

    @JavascriptInterface
    public void webCanback(String indexStr) {
        try {
            int index = Integer.valueOf(indexStr);
            canBack = index > 0;
        } catch (NumberFormatException ignored) {

        }
    }

    @JavascriptInterface
    public void checkShareInfo(String data) {
        boolean hasShareInfo = false;
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONObject object = new JSONObject(data);
                String title = object.optString("title");
                String link = object.optString("link");
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(link)) {
                    hasShareInfo = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (handler != null) {
            Message msg = new Message();
            msg.what = WEB_SHARE_CHECK;
            msg.obj = hasShareInfo;
            handler.sendMessage(msg);
        }
    }

    @JavascriptInterface
    public void onShare() {
        webView.loadUrl("javascript:window.handler.getShareInfo(getShareData());");
    }

    @JavascriptInterface
    public void close() {
        Activity activity = (Activity) context;
        if (activity != null) {
            activity.finish();
        }
    }

    @JavascriptInterface
    public void share(
            String thirdparty, String title, String desc1, String desc2, String url, String icon) {
        if (TextUtils.isEmpty(thirdparty)) {
            return;
        }
        if (thirdparty.contains(":")) {
            share(thirdparty);
            return;
        }
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(url)) {
            shareUtil = new ShareUtil(context, url, title, desc1, desc2, icon, handler);
        }
        if (shareUtil == null) {
            return;
        }
        switch (thirdparty) {
            case "WechatFriend":
                shareUtil.shareToPengYou();
                break;
            case "Wechat":
                shareUtil.shareToWeiXin();
                break;
            case "SinaWeibo":
                shareUtil.shareToWeiBo();
                break;
            case "QQ":
                shareUtil.shareToQQ();
                break;
        }
    }

    @JavascriptInterface
    public void share(String strings) {
        if (TextUtils.isEmpty(strings)) {
            return;
        }
        String thirdparty = null, title = null, desc1 = null, desc2 = null, url = null, icon = null;
        ArrayList<String> s = stringSplit(strings);
        if (s.size() > 0) {
            thirdparty = s.get(0);
        }
        if (s.size() > 1) {
            title = s.get(1);
        }
        if (s.size() > 2) {
            desc1 = s.get(2);
        }
        if (s.size() > 3) {
            desc2 = s.get(3);
        }
        if (s.size() > 4) {
            url = s.get(4);
        }
        if (s.size() > 5) {
            icon = s.get(5);
        }
        share(thirdparty, title, desc1, desc2, url, icon);
    }


    private ArrayList<String> stringSplit(String string) {
        ArrayList<String> strings = new ArrayList<>();
        Pattern facePattern = Pattern.compile("[a-zA-z]+://[^:]+|[^:]+");
        Matcher matcher = facePattern.matcher(string);
        while (matcher.find()) {
            strings.add(matcher.group(0));
        }
        return strings;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    public void onDestroy() {

    }

}
