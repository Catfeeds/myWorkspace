package com.example.suncloud.hljweblibrary.jsinterface;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.hunliji.hljsharelibrary.utils.ShareUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler.ON_WEB_SHARE;
import static com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler.WEB_SHARE_CHECK;

/**
 * Mad 广告网页js接口
 * Created by wangtao on 2016/12/30.
 */

public class MadWebHandler {

    private Context context;
    private Handler handler;
    private ShareUtil shareUtil;


    public MadWebHandler(
            Context context, Handler handler) {
        this.handler = handler;
        this.context = context;
    }

    public ShareUtil getShareUtil() {
        return shareUtil;
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
                shareUtil = new ShareUtil(context, link, title, desc, desc, imgUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (shareUtil != null) {
            Message msg = new Message();
            msg.what = ON_WEB_SHARE;
            handler.sendMessage(msg);
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
    public void loadUrlBrowser(String url) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
