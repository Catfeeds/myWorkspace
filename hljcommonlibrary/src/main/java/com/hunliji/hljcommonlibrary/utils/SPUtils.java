package com.hunliji.hljcommonlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.Nullable;

import com.hunliji.hljcommonlibrary.HljCommon;

import java.util.Set;


public class SPUtils {

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(HljCommon.FileNames.PREF_FILE, Context.MODE_PRIVATE);
    }

    public static int getInt(Context ctx, String key, int defValue) {
        return getSharedPreference(ctx).getInt(key, defValue);
    }

    public static String getString(Context context, String key, String defValue) {
        return getSharedPreference(context).getString(key, defValue);
    }

    public static long getLong(Context context, String key, long defValue) {
        return getSharedPreference(context).getLong(key, defValue);
    }


    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSharedPreference(context).getBoolean(key, defValue);
    }


    /**
     * set集合只能存 Set<String>
     */
    public static void put(Context context, String key, @Nullable Object value) {
        SharedPreferences sp = getSharedPreference(context);
        Editor edit = sp.edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else if (value instanceof Set) {
            edit.putStringSet(key, (Set<String>) value);
        }
        edit.apply();
    }

    public static void remove(Context context, String key) {
        SharedPreferences sp = getSharedPreference(context);
        Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

}
