package com.hunliji.marrybiz.util;

import android.content.Context;

import com.hunliji.marrybiz.R;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Suncloud on 2015/1/12.
 */
public class TimeUtil {
    private static long mTimeOffset = 0l;
    private static DecimalFormat decimalFormat;


    public static void setTimeOffset(long systemTime) {
        if (systemTime > 0 && mTimeOffset == 0) {
            mTimeOffset = systemTime - System.currentTimeMillis();
        }
    }

    public static long getmTimeOffset() {
        return mTimeOffset;
    }

    public static Date getLocalTime(Date date) {
        if (date == null) {
            return null;
        }
        if (mTimeOffset != 0) {
            date.setTime(date.getTime() - mTimeOffset);
        }
        return date;
    }

    public static String countDownMillisFormat(Context context, long millisTime) {
        long leftMillis = millisTime % (1000 * 60 * 60 * 24);
        int hours = (int) (leftMillis / (1000 * 60 * 60));
        leftMillis %= 1000 * 60 * 60;
        int minutes = (int) (leftMillis / (1000 * 60));
        leftMillis %= 1000 * 60;
        int seconds = (int) (leftMillis / 1000);
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("00");
        }
        return context.getString(R.string.format_count_down,
                decimalFormat.format(hours),
                decimalFormat.format(minutes),
                decimalFormat.format(seconds));
    }

    public static String countDownMillisFormat2(Context context, long millisTime) {
        long leftMillis = millisTime % (1000 * 60 * 60 * 24);
        int hours = (int) (leftMillis / (1000 * 60 * 60));
        leftMillis %= 1000 * 60 * 60;
        int minutes = (int) (leftMillis / (1000 * 60));
        leftMillis %= 1000 * 60;
        int seconds = (int) (leftMillis / 1000);
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("00");
        }
        return context.getString(R.string.format_count_down2,
                decimalFormat.format(hours),
                decimalFormat.format(minutes),
                decimalFormat.format(seconds));
    }


    public static String countDownMillisFormat3(Context context, long millisTime) {
        long leftMillis = millisTime % (1000 * 60 * 60 * 24);
        int hours = (int) (leftMillis / (1000 * 60 * 60));
        leftMillis %= 1000 * 60 * 60;
        int minutes = (int) (leftMillis / (1000 * 60));
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("00");
        }
        return context.getString(R.string.format_count_down3,
                decimalFormat.format(hours),
                decimalFormat.format(minutes));
    }


    public static String countDownMillisFormat4(Context context, long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        long leftMillis = millisTime % (1000 * 60 * 60 * 24);
        int hours = (int) (leftMillis / (1000 * 60 * 60));
        leftMillis %= 1000 * 60 * 60;
        int minutes = (int) (leftMillis / (1000 * 60));
        leftMillis %= 1000 * 60;
        int seconds = (int) (leftMillis / 1000);
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("00");
        }
        return context.getString(R.string.format_count_down4,
                decimalFormat.format(days),
                decimalFormat.format(hours),
                decimalFormat.format(minutes),
                decimalFormat.format(seconds));
    }

    public static String millisFormat(Context context, long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        return days > 0 ? context.getString(R.string.label_day,
                days) : TimeUtil.countDownMillisFormat2(context, millisTime);
    }

    //判断两个日期是否是同一天
    public static boolean isSameDay(DateTime date1, DateTime date2) {
        if (date1 == null || date2 == null) {
            return true;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1.toDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2.toDate());
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        return isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }
}
