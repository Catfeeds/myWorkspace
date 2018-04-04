package com.example.suncloud.hljweblibrary;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;

/**
 * Created by Suncloud on 2016/8/19.
 */
public class HljWeb {

    public static void startWebView(Context context, String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Intent intent = new Intent(context, HljWebViewActivity.class);
        intent.putExtra(HljWebViewActivity.ARG_PATH, path);
        context.startActivity(intent);
    }

    public static void startHtmlWebView(Context context, String html) {
        if (TextUtils.isEmpty(html)) {
            return;
        }
        Intent intent = new Intent(context, HljWebViewActivity.class);
        intent.putExtra(HljWebViewActivity.ARG_HTML, html);
        context.startActivity(intent);
    }
}
