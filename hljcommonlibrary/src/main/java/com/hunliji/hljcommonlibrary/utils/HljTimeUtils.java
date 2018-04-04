package com.hunliji.hljcommonlibrary.utils;

import android.content.Context;
import android.os.Parcel;

import com.hunliji.hljcommonlibrary.R;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by werther on 16/7/22.
 */
public class HljTimeUtils {

    private static long mServerTimeOffset = 0l; //本地时间与服务器时间偏移

    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";

    public static final String DATE_FORMAT_MIDDLE = "yyyy-MM-dd HH:mm";

    public static final String DATE_FORMAT_LONG = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final String DATE_FORMAT_LONG_SIMPLE = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_YEAR_MONTH = "yyyy年MM月";

    private static final int SEVER_TIME_ZONE_OFFSET = TimeZone.getTimeZone("GMT+8")
            .getRawOffset();


    /**
     * 设置系统时间与本地时间偏移量
     *
     * @param serverTime 服务器时间戳
     */
    public static void setTimeOffset(long serverTime) {
        mServerTimeOffset = serverTime - System.currentTimeMillis();
    }

    /**
     * 计算制定date和现在之间的时间差
     * 数据类型为datetime
     *
     * @param date
     * @return
     */
    public static String getShowTime(Context context, DateTime date) {
        if (date == null) {
            return null;
        }
        long cur_time = Calendar.getInstance()
                .getTimeInMillis();
        DateTime currentDate = new DateTime();
        long time = date.getMillis();

        long del_time = cur_time - time;
        long sec = del_time / 1000;
        long min = sec / 60;
        if (min < 60) {
            if (min < 1) {
                return "刚刚";
            }
            return context.getResources()
                    .getString(R.string.label_last_min___cm, min);
        } else {
            long h = min / 60;
            if (h < 24) {
                return context.getResources()
                        .getString(R.string.label_last_hour___cm, h);
            } else if (h < 48) {
                return date.toString("昨天 HH:mm");
            } else if (currentDate.getYear() == date.getYear()) {
                return date.toString("MM-dd HH:mm");
            } else {
                return date.toString("yyyy-MM-dd HH:mm");
            }
        }
    }

    /**
     * 计算制定date和现在之间的时间差
     * 数据类型为date（兼容老数据）
     *
     * @param date
     * @return
     */
    public static String getShowTime(Context context, Date date) {
        if (date == null) {
            return null;
        }
        long cur_time = Calendar.getInstance()
                .getTimeInMillis();
        Date currentDate = Calendar.getInstance()
                .getTime();
        long time = date.getTime();

        long del_time = cur_time - time;
        long sec = del_time / 1000;
        long min = sec / 60;
        if (min < 60) {
            if (min < 1) {
                return "刚刚";
            }
            return context.getResources()
                    .getString(R.string.label_last_min___cm, min);
        } else {
            long h = min / 60;
            if (h < 24) {
                return context.getResources()
                        .getString(R.string.label_last_hour___cm, h);
            } else if (h < 48) {
                SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
                return sdf.format(date);
            } else if (currentDate.getYear() == date.getYear()) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                return sdf.format(date);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                return sdf.format(date);
            }
        }
    }

    /**
     * 计算制定date和现在之间的时间差
     * 用于显示结束时间（date>现在时间）
     *
     * @param date
     * @return
     */
    public static String getShowEndTime(Context context, DateTime date) {
        if (date == null) {
            return null;
        }
        long cur_time = Calendar.getInstance()
                .getTimeInMillis();
        long time = date.getMillis();

        long del_time = time - cur_time;
        long sec = del_time / 1000;
        long min = sec / 60;
        if (min < 60) {
            if (min < 1) {
                return "刚刚";
            }
            return context.getResources()
                    .getString(R.string.label_end_min___cm, min);
        } else {
            long h = min / 60;
            if (h < 24) {
                return context.getResources()
                        .getString(R.string.label_end_hour___cm, h);
            }
            long day = h / 24;
            if (day <= 3) {
                return context.getResources()
                        .getString(R.string.label_end_day___cm, day);
            } else {
                return date.toString("MM-dd");
            }
        }
    }

    /**
     * 计算制定date和现在之间的时间差
     * 用于显示开始时间（明天 XX:XX;今天 XX:XX;MM-dd HH:mm）
     *
     * @param date
     * @return
     */
    public static String getShowStartTime(Context context, DateTime date) {
        if (date == null) {
            return null;
        }
        int startDay = date.getDayOfYear();
        int curDay = new DateTime().getDayOfYear();
        if (startDay - curDay == 1) {
            //明天
            return "明天" + date.toString("HH:mm");
        } else if (startDay == curDay) {
            //今天
            return "今天" + date.toString("HH:mm");
        } else {
            return date.toString("MM-dd HH:mm");
        }
    }

    public static String time2UtcString(Date date) {
        DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT_LONG, Locale.getDefault());
        TimeZone pst = TimeZone.getTimeZone("UTC+0");
        dateFormatter.setTimeZone(pst);
        return dateFormatter.format(date);
    }


    /**
     * 时间解析校准
     * <p>
     * DateTime 解析时间为本地时区 服务器时间为东八区时间
     * <p>
     * 如 2016-11-11 12：00：00
     * <p>
     * DateTime 2016-11-11 12：00：00 +zone
     * <p>
     * ServerTime 2016-11-11 12：00：00 +8:00
     * <p>
     * 两边时间会出现偏差需要对时间进行转换
     *
     * @param dateTime 自动解析的时间
     * @return 校准后的时间
     */
    public static DateTime timeServerTimeZone(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return new DateTime(dateTime.getMillis() + dateTime.getZone()
                .toTimeZone()
                .getRawOffset() - SEVER_TIME_ZONE_OFFSET);
    }


    /**
     * 获取当前服务器时间 多用于倒计时
     *
     * @return 本地时间加上与服务器的时间差
     */
    public static long getServerCurrentTimeMillis() {
        return System.currentTimeMillis() + mServerTimeOffset;
    }

    /**
     * 计算制定date和现在之间的时间差
     *
     * @param dateTime
     * @return
     */
    public static boolean isWedding(DateTime dateTime) {
        long cur_time = Calendar.getInstance()
                .getTimeInMillis();
        long time = dateTime.getMillis();

        long del_time = time - cur_time;
        if (del_time > 0) {
            return true;
        }
        return false;
    }

    public static boolean isThreadNew(DateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        long cur_time = Calendar.getInstance()
                .getTimeInMillis();
        long time = dateTime.getMillis();

        long del_time = cur_time - time;
        if (del_time < 12 * 60 * 60 * 1000) {
            return true;
        }
        return false;
    }

    public static String getWeddingDate(DateTime date, int isPending, boolean isMale) {
        if (date != null && isPending != 0) {
            if (isWedding(date)) {
                return "婚期:" + date.toString("yyyy-MM-dd");
            } else {
                return isMale ? "已婚男" : "已为人妻";
            }
        } else {
            return isMale ? "" : "待字闺中";
        }
    }

    public static String getWeddingDate(
            DateTime date, int isPending, boolean isMale, String format) {
        if (date != null && isPending != 0) {
            if (isWedding(date)) {
                return "婚期  " + date.toString(format);
            } else {
                return isMale ? "已婚男" : "已为人妻";
            }
        } else {
            return isMale ? "" : "待字闺中";
        }
    }

    /**
     * 持续的时间 mm:ss 格式 显示
     *
     * @param duration
     * @return
     */
    public static String formatForDurationTime(long duration) {
        int timeInSeconds = (int) (duration / 1000);
        int seconds = timeInSeconds % 60;
        int minutes = (timeInSeconds / 60) % 60;
        int hours = timeInSeconds / 3600;
        Formatter mFormatter = new Formatter();
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds)
                    .toString();
        }
    }


    public static void writeDateTimeToParcel(Parcel dest, DateTime dateTime) {
        dest.writeLong(dateTime == null ? 0 : dateTime.getMillis());
    }

    public static DateTime readDateTimeToParcel(Parcel in) {
        long millis = in.readLong();
        if (millis > 0) {
            return new DateTime(millis);
        }
        return null;
    }

    public static boolean isSameDay(DateTime date, DateTime date2) {
        if (date != null && date2 != null) {
            return date.getYear() == date2.getYear() && date.getMonthOfYear() ==
                    date2.getMonthOfYear() && date.getDayOfMonth() == date2.getDayOfMonth();
        } else {
            throw new IllegalArgumentException("The date must not be null");
        }
    }

    public static ArrayList<Integer> convertDateToDays(List<String> dates) {
        ArrayList<Integer> list = null;
        if (!CommonUtil.isCollectionEmpty(dates)) {
            list = new ArrayList<>();
            for (String date : dates) {
                String[] dateStrs = date.split("-");
                if (dateStrs.length >= 2) {
                    list.add(Integer.valueOf(dateStrs[2]));
                }
            }
        }
        return list;
    }

    /**
     * 获得剩余天数
     *
     * @return -1 endTime =null 或者给定时间在服务器时间之前
     */
    public static int getSurplusDay(DateTime endTime) {
        if (endTime == null) {
            return -1;
        }
        DateTime endDateTime = new DateTime(endTime).withHourOfDay(23)
                .withMinuteOfHour(59)
                .withSecondOfMinute(59);
        long dis = endDateTime.getMillis() - HljTimeUtils.getServerCurrentTimeMillis();
        if (dis > 0) {
            long hours = dis / (60 * 60 * 1000);
            int days = (int) Math.ceil((float) hours / 24L);
            return days;
        }

        return -1;
    }


    public static int getSurplusDay(Date date) {
        if (date == null) {
            return -1;
        }
        return getSurplusDay(new DateTime(date));
    }

}

