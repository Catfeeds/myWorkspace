package com.hunliji.hljhttplibrary;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.hunliji.hljcommonlibrary.HljCommon;

/**
 * Created by werther on 16/7/21.
 * 设置和获取整个应用的host地址,会在配置文件Preference中进行持久化
 * 默认为http://www.hunliji.com
 */
public class HostConfig {

    public static final String HOST_PREF_KEY = "HOST";
    private String host;

    /**
     * 获取设置好的host地址,此方法不用于暴露给外部使用,只给在同一个包里的HljCommon使用
     * 在需要获取Host地址的时候,请使用HljCommon中暴露的接口
     * @param context
     * @return
     */
    protected String getHost(Context context) {
        if (TextUtils.isEmpty(host)) {
            // 如果当前没有值,从配置文件中取得并赋值
            host = getHostFromPref(context);
        }
        return host;
    }

    /**
     * 设置host地址,并存储在Preference中供之后使用
     * 同时更新HljHttp中的全局变量HOST
     * @param context
     * @param host
     */
    public void setHost(Context context, String host) {
        this.host = host;
        HljHttp.setHOST(host);

        SharedPreferences preferences = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
        preferences.edit()
                .putString(HOST_PREF_KEY, host)
                .apply();
    }

    /**
     * 从配置文件中取得host
     * @param context
     * @return
     */
    private String getHostFromPref(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);

        return preferences.getString(HOST_PREF_KEY, HljHttp.getHOST());
    }

    private static class SingletonHolder {
        private static final HostConfig INSTANCE = new HostConfig();
    }

    public static HostConfig getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
