package me.suncloud.marrymemo.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Created by wangtao on 2017/12/25.
 */

public class GoogleAnalyticsUtil {
    //    google统计
    private static GoogleAnalyticsUtil INSTANCE;
    private FirebaseAnalytics mFirebaseAnalytics;


    public static GoogleAnalyticsUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new GoogleAnalyticsUtil(context);
        }
        return INSTANCE;
    }

    private GoogleAnalyticsUtil(Context context) {
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        }
    }

    public void sendScreen(Activity activity,String screenName) {
        if (TextUtils.isEmpty(screenName)) {
            return;
        }
        try {
            if (mFirebaseAnalytics != null) {
                mFirebaseAnalytics.setCurrentScreen(activity,screenName,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
