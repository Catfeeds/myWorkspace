package com.hunliji.cardmaster.jsinterface;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;
import com.hunliji.cardmaster.utils.BannerUtil;
import com.hunliji.cardmaster.utils.CardMasterSupportUtil;
import com.hunliji.hljcommonlibrary.models.Poster;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.utils.AuthUtil;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Subscription;

/**
 * Created by Administrator on 2017/11/30.
 */

public class WebHandler extends BaseWebHandler {

    public WebHandler(
            Context context, String path, WebView webView, Handler handler) {
        super(context, path, webView, handler);
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

    @JavascriptInterface
    public void bannerAction(String bannerStr) {
        if (TextUtils.isEmpty(bannerStr)) {
            return;
        }
        ArrayList<String> strings = stringSplit(bannerStr);
        int property = 0;
        long forwardId = 0;
        String url = null;
        if (strings.size() > 0) {
            String string = strings.get(0);
            if (!TextUtils.isEmpty(string)) {
                try {
                    property = Integer.valueOf(string);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        if (strings.size() > 1) {
            String string = strings.get(1);
            if (!TextUtils.isEmpty(string)) {
                try {
                    forwardId = Long.valueOf(string);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        if (strings.size() > 2) {
            url = strings.get(2);
        }
        BannerUtil.bannerJump(context, new Poster(forwardId,url,property),null);
    }


    @JavascriptInterface
    public void talkToSupport(String string) {
        if (TextUtils.isEmpty(string)) {
            return;
        }
        ArrayList<String> strings = stringSplit(string);
        String kindStr, type = null, idStr2 = null;
        if (strings.size() > 0) {
            kindStr = strings.get(0);
        } else {
            talkToSupport(string, null, "0");
            return;
        }
        if (strings.size() > 1) {
            type = strings.get(1);
        }
        if (strings.size() > 2) {
            idStr2 = strings.get(2);
        }
        talkToSupport(kindStr, type, idStr2);

    }

    @JavascriptInterface
    public void talkToSupport(String kindStr, String type, String idStr2) {
        if (TextUtils.isEmpty(kindStr)) {
            return;
        }
        if (kindStr.contains(":")) {
            talkToSupport(kindStr);
            return;
        }
        int kind = 0;
        try {
            kind = Integer.valueOf(kindStr);
        } catch (Exception ignored) {
        }
        if(AuthUtil.loginBindCheck(context)){
            CardMasterSupportUtil.goToSupport(context,kind);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
