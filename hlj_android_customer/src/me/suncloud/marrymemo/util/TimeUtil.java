package me.suncloud.marrymemo.util;

import android.content.Context;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;

/**
 * Created by Suncloud on 2015/1/12.
 */
public class TimeUtil {
    private static long mTimeOffset = 0l;
    private static SimpleDateFormat dateTitleFormat;
    private static DecimalFormat decimalFormat;

    public static void setTimeOffset(long systemTime) {
        mTimeOffset = systemTime - System.currentTimeMillis();
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

    public static int calendarDayCompare(Calendar calendar1, Calendar calendar2) {
        if (calendar1 == null) {
            return -1;
        }
        if (calendar2 == null) {
            return 1;
        }
        int year = calendar1.get(Calendar.YEAR) - calendar2.get(Calendar.YEAR);
        if (year == 0) {
            return calendar1.get(Calendar.DAY_OF_YEAR) - calendar2.get(Calendar.DAY_OF_YEAR);
        } else {
            return year;
        }
    }

    public static String getTaskTimeStr(Context context, Calendar calendar) {
        Calendar nowCalendar = Calendar.getInstance();
        int year1 = calendar.get(Calendar.YEAR);
        int year2 = nowCalendar.get(Calendar.YEAR);
        if (year1 > year2) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string
                    .format_date_type7),
                    Locale.getDefault());
            return simpleDateFormat.format(calendar.getTime());
        } else if (year1 == year2) {
            int month1 = calendar.get(Calendar.MONTH);
            int month2 = nowCalendar.get(Calendar.MONTH);
            if (month1 - month2 > 1) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R
                        .string.format_date_type11),
                        Locale.getDefault());
                return simpleDateFormat.format(calendar.getTime());
            } else if (month1 - month2 > 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R
                        .string.format_date_type12),
                        Locale.getDefault());
                return simpleDateFormat.format(calendar.getTime());
            } else if (month1 == month2) {
                int day1 = calendar.get(Calendar.DAY_OF_MONTH);
                int day2 = nowCalendar.get(Calendar.DAY_OF_MONTH);
                if (day1 - day2 > 1) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R
                            .string.format_date_type13),
                            Locale.getDefault());
                    return simpleDateFormat.format(calendar.getTime());
                } else if (day1 - day2 > 0) {
                    return context.getString(R.string.format_date_type_tomorrow);
                } else if (day1 == day2) {
                    return context.getString(R.string.format_date_type_today);
                }
            }
        }
        return context.getString(R.string.label_ticket_tag3);

    }

    public static int getGapCount(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime()
                .getTime() - fromCalendar.getTime()
                .getTime()) / (1000 * 60 * 60 * 24));
    }

    public static String getTimeString(Date date, Context context) {
        if (dateTitleFormat == null) {
            dateTitleFormat = new SimpleDateFormat(context.getResources()
                    .getString(R.string.format_date_type10));
        }
        return dateTitleFormat.format(date);
    }

    public static String getTimeString2(Date date, Context context) {
        if (dateTitleFormat == null) {
            dateTitleFormat = new SimpleDateFormat(context.getResources()
                    .getString(R.string.format_date));
        }
        return dateTitleFormat.format(date);
    }

    public static Date getCardDate(JSONObject obj, String name) {
        try {
            String dateStr = JSONUtil.getString(obj, name);
            if (!JSONUtil.isEmpty(dateStr)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG,
                        Locale.getDefault());
                return simpleDateFormat.parse(obj.optString(name));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCardDateStr(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG,
                Locale.getDefault());
        return simpleDateFormat.format(date);
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

    public static String countDownMillisFormatHtml(Context context, long millisTime) {
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
                redHtmlFormat(decimalFormat.format(hours)),
                redHtmlFormat(decimalFormat.format(minutes)),
                redHtmlFormat(decimalFormat.format(seconds)));
    }

    private static String redHtmlFormat(String string) {
        return "<font color=\"#f83244\">" + string + "</font>";
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
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("00");
        }
        return context.getString(R.string.format_count_down4,
                decimalFormat.format(days),
                decimalFormat.format(hours),
                decimalFormat.format(minutes));
    }

    public static String millisFormat(Context context, long millisTime) {
        int days = (int) (millisTime / (1000 * 60 * 60 * 24));
        return days > 0 ? context.getString(R.string.label_day,
                days) : TimeUtil.countDownMillisFormat2(context, millisTime);
    }

    /**
     * 返回传入时间值的字面文字描述,小于1分钟的时候为1分钟
     *
     * @param context
     * @param millisTime
     * @return xx天xx小时xx分
     */
    public static String getSpecialTimeLiteral(Context context, long millisTime) {
        if (millisTime < 1000 * 60) {
            // 小于一分钟则统一为一分钟
            return "0天00小时01分";
        }

        int days = (int) (millisTime / (1000 * 60 * 60 * 24));

        return context.getString(R.string.label_day, days) + countDownMillisFormat3(context,
                millisTime);
    }

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

    public static int calendarCompareTo(Calendar calendar1, Calendar calendar2) {
        if (calendar1.get(Calendar.YEAR) > calendar2.get(Calendar.YEAR)) {
            return 1;
        } else if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)) {
            if (calendar1.get(Calendar.DAY_OF_YEAR) > calendar2.get(Calendar.DAY_OF_YEAR)) {
                return 1;
            } else if (calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}