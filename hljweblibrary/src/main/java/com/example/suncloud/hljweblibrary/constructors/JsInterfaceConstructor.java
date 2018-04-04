package com.example.suncloud.hljweblibrary.constructors;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.webkit.WebView;

import com.example.suncloud.hljweblibrary.jsinterface.BaseWebHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Suncloud on 2016/8/19.
 */
public class JsInterfaceConstructor {

    private static String packageName;

    private static String getPackageName(Context context) {
        if (TextUtils.isEmpty(packageName)) {
            packageName = context.getPackageName();
        }

        return packageName;
    }

    @SuppressWarnings("unchecked")
    public static BaseWebHandler getJsInterface(
            Context context, String path, WebView webView, Handler handler) {
        try {
            Class clazz = Class.forName(getPackageName(context) + ".jsinterface.WebHandler");
            Constructor<BaseWebHandler> c = clazz.getConstructor(Context.class,
                    String.class,
                    WebView.class,
                    Handler.class);
            return c.newInstance(context, path, webView, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
