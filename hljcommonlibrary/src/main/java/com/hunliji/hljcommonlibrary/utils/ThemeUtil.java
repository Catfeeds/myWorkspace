package com.hunliji.hljcommonlibrary.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;

/**
 * Created by wangtao on 2017/5/31.
 */

public class ThemeUtil {

    public static boolean getAttrBoolean(Context context, int attr,Boolean defValue) {
        TypedArray a = null;
        try {
            int[] attrs={attr};
            a = context.getTheme()
                    .obtainStyledAttributes(attrs);
            return a.getBoolean(0,defValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
        return defValue;
    }

    public static @ColorInt int getAttrColor(Context context, int attr,@ColorInt int defValue) {
        TypedArray a = null;
        try {
            int[] attrs={attr};
            a = context.getTheme()
                    .obtainStyledAttributes(attrs);
            return a.getColor(0,defValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
        return defValue;
    }

    public static ColorStateList getAttrColorList(Context context, int attr) {
        TypedArray a = null;
        try {
            int[] attrs={attr};
            a = context.getTheme()
                    .obtainStyledAttributes(attrs);
            return a.getColorStateList(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
        return null;
    }


    public static @DrawableRes int getAttrResourceId (Context context, int attr, @DrawableRes int defValue) {
        TypedArray a = null;
        try {
            int[] attrs={attr};
            a = context.getTheme()
                    .obtainStyledAttributes(attrs);
            return a.getResourceId(0,defValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
        return defValue;
    }


    public static @ColorInt int parseColor(String colorStr) {
        if (TextUtils.isEmpty(colorStr)) {
            return Color.BLACK;
        }
        try {
            if (colorStr.startsWith("#") || colorStr.length() == 7) {
                long color = Long.parseLong(colorStr.substring(1), 16);
                if (colorStr.length() == 7) {
                    color |= 0x00000000ff000000;
                }
                return (int) color;
            }
        } catch (Exception ignored) {
        }
        return Color.BLACK;
    }
}
