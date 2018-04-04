package com.hunliji.hljcommonlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.HljCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luohanlin on 2017/12/26.
 * 专门用于处理那些只进行一次操作的行为，记录、检测的帮助类
 */

public enum OncePrefUtil {
    INSTANCE;

    private static SharedPreferences preferences;
    private static Gson gsonInstance;

    public static void init(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                    Context.MODE_PRIVATE);
        }
    }

    public static void initGsonInstance() {
        if (gsonInstance == null) {
            gsonInstance = new GsonBuilder().create();
        }
    }

    /**
     * 检测这个事情是否已经做过了
     *
     * @param context
     * @param prefName
     * @return
     */
    public static boolean hasDoneThis(Context context, String prefName) {
        init(context);
        return preferences.getBoolean(prefName, false);
    }

    /**
     * 检测记录为这个ID的这件事情是否已经做过了
     *
     * @param context
     * @param prefName
     * @param id
     * @return
     */
    public static boolean hasDoneThisById(Context context, String prefName, long id) {
        List<Long> ids = getIdsOfPrefName(context, prefName);
        return ids.contains(id);
    }


    /**
     * 记录这个事情已经做过了
     *
     * @param context
     * @param prefName
     */
    public static void doneThis(Context context, String prefName) {
        init(context);
        preferences.edit()
                .putBoolean(prefName, true)
                .apply();
    }

    /**
     * 记录为这个ID的这个事情已经做过了
     *
     * @param context
     * @param prefName
     * @param id
     */
    public static void doneThisById(Context context, String prefName, long id) {
        List<Long> ids = getIdsOfPrefName(context, prefName);
        ids.add(id);
        preferences.edit()
                .putString(prefName, gsonInstance.toJson(ids))
                .apply();
    }

    private static List<Long> getIdsOfPrefName(Context context, String prefName) {
        init(context);
        initGsonInstance();
        String idString = preferences.getString(prefName, null);
        List<Long> ids = new ArrayList<>();
        try {
            if (!TextUtils.isEmpty(idString)) {
                ids = gsonInstance.fromJson(idString, new TypeToken<List<Long>>() {}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    /**
     * 重置标志位
     *
     * @param context
     * @param prefName
     */
    public static void resetThisRecord(Context context, String prefName) {
        init(context);
        preferences.edit()
                .putBoolean(prefName, false)
                .apply();
    }
}
