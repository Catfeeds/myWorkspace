package com.hunliji.marrybiz.util;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public final class DateHelper {
    public static final String UTC_TIME_ZONE = "UTC";

    public static final int YEAR1900 = 1900;

    private DateHelper() {
    }

    public static Date createDate(final int year, final int month, final int day, final int hour,
                                  final int minute) {

        Calendar calendar = new GregorianCalendar(year, month, day, hour, minute);
        return calendar.getTime();
    }

    public static Date createDate(final int year, final int month, final int day) {
        return createDate(year, month, day, 0, 0);
    }

    public static Date createDateGMT(final int year, final int month, final int day) {
        return convertToUTC(DateHelper.createDate(year, month, day, 0, 0));
    }

    public static boolean dateMoreOrEqual(final Date date1, final Date date2) {
        return date1.compareTo(date2) >= 0;
    }

    public static boolean dateLess(final Date date1, final Date date2) {
        return date1.compareTo(date2) < 0;
    }

    public static Date add(final Date date, final int field, final int value) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(field, value);
        return calendar.getTime();
    }

    public static Date clearTime(final Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static boolean equalsIgnoreTime(final Date date1, final Date date2) {
        Date clearedDate1 = DateHelper.clearTime(date1);
        Date clearedDate2 = DateHelper.clearTime(date2);
        return clearedDate1.equals(clearedDate2);
    }

    public static Date replaceDate(final Date sourceDate, final int year, final int monthOfYear,
                                   final int dayOfMonth) {
        java.util.Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(sourceDate);

        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.MONTH, monthOfYear);
        calendar.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);

        return calendar.getTime();
    }

    public static void changeDate(final java.util.Calendar calendar, final int year,
                                  final int monthOfYear, final int dayOfMonth) {
        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.MONTH, monthOfYear);
        calendar.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);
    }

    public static Date replaceTime(final Date sourceDate, final int hourOfDay, final int minute,
                                   final int second) {
        java.util.Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(sourceDate);

        calendar.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(GregorianCalendar.MINUTE, minute);
        calendar.set(GregorianCalendar.SECOND, second);

        return calendar.getTime();
    }

    public static void changeTime(final java.util.Calendar calendar, final int hourOfDay,
                                  final int minute, final int second) {
        calendar.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(GregorianCalendar.MINUTE, minute);
        calendar.set(GregorianCalendar.SECOND, second);
    }

    public static GregorianCalendar fromDateToCalendar(final Date date) {
        GregorianCalendar calendar = new GregorianCalendar(0, 0, 0, 0, 0);
        calendar.setTimeInMillis(date.getTime());
        return calendar;
    }

    public static String dateFormat(final String format, final Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static Date convertToUTC(final Date date) {
        GregorianCalendar calendar = new GregorianCalendar(date.getYear() + YEAR1900,
                date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(),
                date.getSeconds());
        calendar.setTimeZone(TimeZone.getTimeZone(UTC_TIME_ZONE));

        return calendar.getTime();
    }

    public static void changeToBeginDayUTC(final java.util.Calendar calendar) {
        int year = calendar.get(GregorianCalendar.YEAR);
        int month = calendar.get(GregorianCalendar.MONTH);
        int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.MONTH, month);
        calendar.set(GregorianCalendar.DAY_OF_MONTH, day);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
    }

    public static void changeTimeAndTimeZone(final java.util.Calendar calendar, final int hours,
                                             final int minutes, final String timeZone) {
        int year = calendar.get(GregorianCalendar.YEAR);
        int month = calendar.get(GregorianCalendar.MONTH);
        int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

        calendar.setTimeZone(TimeZone.getTimeZone(timeZone));
        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.MONTH, month);
        calendar.set(GregorianCalendar.DAY_OF_MONTH, day);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, hours);
        calendar.set(GregorianCalendar.MINUTE, minutes);
    }

    public static Date convertToCurrentTimeZone(final Date dateGMT) {
        long date = dateGMT.getTime() + dateGMT.getTimezoneOffset() * DateUtils.MINUTE_IN_MILLIS;
        return new Date(date);
    }

    /**
     * @param date date.
     * @return sql date format string.
     */
    public static String dateToSqlFormat(Date date) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        StringBuilder now = new StringBuilder(dateformat.format(date));
        return now.toString();
    }

    public static Calendar createCalendar(final int year, final int month, final int day,
                                          final String timezone) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));

        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.MONTH, month);
        calendar.set(GregorianCalendar.DAY_OF_MONTH, day);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);

        return calendar;
    }

    public static Calendar createCurrentBeginDayCalendar() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    public static Calendar createCalendar(final int year, final int month, final int day,
                                          final int hour, final int minute, final String timezone) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timezone));

        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.MONTH, month);
        calendar.set(GregorianCalendar.DAY_OF_MONTH, day);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, hour);
        calendar.set(GregorianCalendar.MINUTE, minute);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * increment by 1 day.
     *
     * @param currentDate date.
     */
    public static void increment(Calendar currentDate) {
        currentDate.add(Calendar.DATE, 1);
    }

    /**
     * @param day day.
     * @return is day weekend.
     */
    public static boolean isWeekendDay(Calendar day) {
        // 不管是周一还是周日作为第一天,周六和周日都是周末
        //        if (isMondayFirst()) {
        return (day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) || (day.get(Calendar
                .DAY_OF_WEEK) == Calendar.SUNDAY);
        //        } else {
        //            return (day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
        //        }
    }

    /**
     * @param day day.
     * @return is last day of week.
     */
    public static boolean isLastDayOfWeek(Calendar day) {
        if (isMondayFirst()) {
            return (day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
        } else {
            return (day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY);
        }
    }

    /**
     * @return is week starts from monday.
     */
    public static boolean isMondayFirst() {
        return Calendar.getInstance().getFirstDayOfWeek() == Calendar.MONDAY;
    }

    public static Calendar createCurrentBeginDayInUTC() {
        Calendar now = new GregorianCalendar();

        Calendar result = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIME_ZONE));
        result.set(Calendar.YEAR, now.get(Calendar.YEAR));
        result.set(Calendar.MONTH, now.get(Calendar.MONTH));
        result.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
        result.set(Calendar.HOUR_OF_DAY, 0);
        result.set(Calendar.MINUTE, 0);
        result.set(Calendar.SECOND, 0);
        result.set(Calendar.MILLISECOND, 0);

        return result;
    }
}
