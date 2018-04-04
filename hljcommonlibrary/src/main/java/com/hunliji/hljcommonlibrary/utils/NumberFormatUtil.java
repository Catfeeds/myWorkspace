package com.hunliji.hljcommonlibrary.utils;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Suncloud on 2016/8/11.
 */
public class NumberFormatUtil {

    private static NumberFormat numberFormat;

    public static String formatDouble2String(double f) {
        if (f > (long) f) {
            return getNumberFormat().format(f);
        }
        return getNumberFormat().format((long) f);
    }

    /**
     * 将double类型的数字转换成有两位小数的字符串
     * 当等于0的时候直接显示0.00
     *
     * @param d
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String formatDouble2StringWithTwoFloat(double d) {
        return String.format("%.2f", d);
    }

    private static NumberFormat getNumberFormat() {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(false);
        }
        return numberFormat;
    }

    /**
     * 点赞数超过1000以1.0K形式表示
     */
    public static String formatThanThousand(int count) {
        if (count >= 1000) {
            DecimalFormat df = new DecimalFormat("###.0K");
            return df.format((float) (count / 1000.0));
        } else {
            return String.valueOf(count);
        }
    }

    /**
     * 使用问号隐藏活动价格
     * @param price
     * @return
     */
    public static String unknownPrice(double price) {
        String priceStr = formatDouble2String(price);
        if (priceStr.length() >= 4) {
            priceStr = "??" + priceStr.substring(2);
        } else {
            priceStr = "?" + priceStr.substring(1);
        }

        return priceStr;
    }
}
