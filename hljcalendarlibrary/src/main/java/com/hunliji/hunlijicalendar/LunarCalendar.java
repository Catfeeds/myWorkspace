package com.hunliji.hunlijicalendar;

import android.content.Context;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LunarCalendar {

    private Context mContext;
    public int lunarYear = 0;
    public int lunarMonth = 0;
    public int lunarDay = 0;
    public int solarYear = 0;
    public int solarMonth = 0;
    public int solarDay = 0;
    public boolean isLeapMonth = false;
    private double D_solar_terms = 0.2422;

    private static String[] lunarCalendarNumber;
    private static String[] lunarCalendarTen;
    private static String[] year_of_birth;
    private static String[] solarTerms;
    private static Integer[] solarTermMonths;
    private static String lunarLeapTag, lunarMonthTag, zhengyueTag;

    public LunarCalendar(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        if (lunarCalendarNumber == null) {
            lunarCalendarNumber = new String[12];
            lunarCalendarNumber[0] = getString(R.string.chineseNumber1);
            lunarCalendarNumber[1] = getString(R.string.chineseNumber2);
            lunarCalendarNumber[2] = getString(R.string.chineseNumber3);
            lunarCalendarNumber[3] = getString(R.string.chineseNumber4);
            lunarCalendarNumber[4] = getString(R.string.chineseNumber5);
            lunarCalendarNumber[5] = getString(R.string.chineseNumber6);
            lunarCalendarNumber[6] = getString(R.string.chineseNumber7);
            lunarCalendarNumber[7] = getString(R.string.chineseNumber8);
            lunarCalendarNumber[8] = getString(R.string.chineseNumber9);
            lunarCalendarNumber[9] = getString(R.string.chineseNumber10);
            lunarCalendarNumber[10] = getString(R.string.chineseNumber11);
            lunarCalendarNumber[11] = getString(R.string.chineseNumber12);
        }

        if (lunarCalendarTen == null) {
            lunarCalendarTen = new String[5];
            lunarCalendarTen[0] = getString(R.string.chineseTen0);
            lunarCalendarTen[1] = getString(R.string.chineseTen1);
            lunarCalendarTen[2] = getString(R.string.chineseTen2);
            lunarCalendarTen[3] = getString(R.string.chineseTen3);
            lunarCalendarTen[4] = getString(R.string.chineseTen4);
        }

        if (year_of_birth == null) {
            year_of_birth = new String[12];
            year_of_birth[0] = getString(R.string.animals0);
            year_of_birth[1] = getString(R.string.animals1);
            year_of_birth[2] = getString(R.string.animals2);
            year_of_birth[3] = getString(R.string.animals3);
            year_of_birth[4] = getString(R.string.animals4);
            year_of_birth[5] = getString(R.string.animals5);
            year_of_birth[6] = getString(R.string.animals6);
            year_of_birth[7] = getString(R.string.animals7);
            year_of_birth[8] = getString(R.string.animals8);
            year_of_birth[9] = getString(R.string.animals9);
            year_of_birth[10] = getString(R.string.animals10);
            year_of_birth[11] = getString(R.string.animals11);
        }

        if (lunarLeapTag == null) {
            lunarLeapTag = getString(R.string.leap_month);
        }
        if (lunarMonthTag == null) {
            lunarMonthTag = getString(R.string.month);
        }
        if (zhengyueTag == null) {
            zhengyueTag = getString(R.string.zheng);
        }

        if (solarTerms == null) {
            solarTerms = new String[24];
            solarTerms[0] = getString(R.string.terms0);
            solarTerms[1] = getString(R.string.terms1);
            solarTerms[2] = getString(R.string.terms2);
            solarTerms[3] = getString(R.string.terms3);
            solarTerms[4] = getString(R.string.terms4);
            solarTerms[5] = getString(R.string.terms5);
            solarTerms[6] = getString(R.string.terms6);
            solarTerms[7] = getString(R.string.terms7);
            solarTerms[8] = getString(R.string.terms8);
            solarTerms[9] = getString(R.string.terms9);
            solarTerms[10] = getString(R.string.terms10);
            solarTerms[11] = getString(R.string.terms11);
            solarTerms[12] = getString(R.string.terms12);
            solarTerms[13] = getString(R.string.terms13);
            solarTerms[14] = getString(R.string.terms14);
            solarTerms[15] = getString(R.string.terms15);
            solarTerms[16] = getString(R.string.terms16);
            solarTerms[17] = getString(R.string.terms17);
            solarTerms[18] = getString(R.string.terms18);
            solarTerms[19] = getString(R.string.terms19);
            solarTerms[20] = getString(R.string.terms20);
            solarTerms[21] = getString(R.string.terms21);
            solarTerms[22] = getString(R.string.terms22);
            solarTerms[23] = getString(R.string.terms23);
        }
        if (solarTermMonths == null) {
            solarTermMonths = new Integer[24];
            solarTermMonths[0] = 1;
            solarTermMonths[1] = 1;
            solarTermMonths[2] = 2;
            solarTermMonths[3] = 2;
            solarTermMonths[4] = 3;
            solarTermMonths[5] = 3;
            solarTermMonths[6] = 4;
            solarTermMonths[7] = 4;
            solarTermMonths[8] = 5;
            solarTermMonths[9] = 5;
            solarTermMonths[10] = 6;
            solarTermMonths[11] = 6;
            solarTermMonths[12] = 7;
            solarTermMonths[13] = 7;
            solarTermMonths[14] = 8;
            solarTermMonths[15] = 8;
            solarTermMonths[16] = 9;
            solarTermMonths[17] = 9;
            solarTermMonths[18] = 10;
            solarTermMonths[19] = 10;
            solarTermMonths[20] = 11;
            solarTermMonths[21] = 11;
            solarTermMonths[22] = 12;
            solarTermMonths[23] = 12;
        }
    }

    private String getString(int id) {
        return mContext.getString(id);
    }

    public String getTraditionalFestival() {
        return getTraditionalFestival(lunarYear, lunarMonth, lunarDay);
    }

    public String getTraditionalFestival(int lunarYear, int lunarMonth, int lunarDay) {
        if (lunarMonth == 1 && lunarDay == 1) {
            return getString(R.string.chunjie);
        }
        if (lunarMonth == 1 && lunarDay == 15) {
            return getString(R.string.yuanxiao);
        }
        if (lunarMonth == 5 && lunarDay == 5) {
            return getString(R.string.duanwu);
        }
        if (lunarMonth == 7 && lunarDay == 7) {
            return getString(R.string.qixi);
        }
        if (lunarMonth == 8 && lunarDay == 15) {
            return getString(R.string.zhongqiu);
        }
        if (lunarMonth == 9 && lunarDay == 9) {
            return getString(R.string.chongyang);
        }
        if (lunarMonth == 12 && lunarDay == 8) {
            return getString(R.string.laba);
        }
        if (lunarMonth == 12 && lunarDay == 23) {
            return getString(R.string.xiaonian);
        }

        if (lunarMonth == 12) {
            if (lunarDay == LunarCalendarConvertUtil.getLunarMonthDays(lunarYear, lunarMonth)) {
                return getString(R.string.chuxi);
            }
        }
        return "";
    }

    public String getFestival() {
        return getFestival(solarMonth, solarDay);
    }

    public String getFestival(int solarMonth, int solarDay) {
        if (solarMonth == 1 && solarDay == 1) {
            return getString(R.string.new_Year_day);
        }
        if (solarMonth == 2 && solarDay == 14) {
            return getString(R.string.valentin_day);
        }
        if (solarMonth == 3 && solarDay == 8) {
            return getString(R.string.women_day);
        }
        if (solarMonth == 3 && solarDay == 12) {
            return getString(R.string.arbor_day);
        }
        if (solarMonth == 5 && solarDay == 1) {
            return getString(R.string.labol_day);
        }
        if (solarMonth == 5 && solarDay == 4) {
            return getString(R.string.youth_day);
        }
        if (solarMonth == 6 && solarDay == 1) {
            return getString(R.string.children_day);
        }
        if (solarMonth == 8 && solarDay == 1) {
            return getString(R.string.army_day);
        }
        if (solarMonth == 9 && solarDay == 10) {
            return getString(R.string.teacher_day);
        }
        if (solarMonth == 10 && solarDay == 1) {
            return getString(R.string.national_day);
        }
        if (solarMonth == 12 && solarDay == 25) {
            return getString(R.string.christmas_day);
        }
        return "";
    }


    public String getLunarSolarTerms(int year) {
        int yy;
        double[] lunar_century_C;
        if (year > 1999) {
            yy = year - 2000;
            lunar_century_C = LunarCalendarConvertUtil.lunar_21th_century_C;
        } else {
            yy = year - 1900;
            lunar_century_C = LunarCalendarConvertUtil.lunar_20th_century_C;
        }
        for (int i = 0; i < lunar_century_C.length; i++) {
            int day;
            if (i < 4) {
                day = (int) Math.floor((yy * D_solar_terms + lunar_century_C[i]) - ((yy - 1) / 4));
            } else {
                day = (int) Math.floor((yy * D_solar_terms + lunar_century_C[i]) - (yy / 4));
            }
            if (solarMonth == solarTermMonths[i] && solarDay == day) {
                return solarTerms[i];
            }
        }
        return "";
    }

    public Map<String, DateTime> getLunarSolarTermDates(int year) {
        Map<String, DateTime> map = new HashMap<>();
        int yy;
        double[] lunar_century_C;
        if (year > 1999) {
            yy = year - 2000;
            lunar_century_C = LunarCalendarConvertUtil.lunar_21th_century_C;
        } else {
            yy = year - 1900;
            lunar_century_C = LunarCalendarConvertUtil.lunar_20th_century_C;
        }
        for (int i = 0; i < lunar_century_C.length; i++) {
            int day;
            if (i < 4) {
                day = (int) Math.floor((yy * D_solar_terms + lunar_century_C[i]) - ((yy - 1) / 4));
            } else {
                day = (int) Math.floor((yy * D_solar_terms + lunar_century_C[i]) - (yy / 4));
            }
            map.put(solarTerms[i], new DateTime(year, solarTermMonths[i], day, 0, 0, 0));
        }
        return map;
    }

    final public String getAnimalsYear() {
        return getAnimalsYear(lunarYear);
    }

    final public String getAnimalsYear(int lunarYear) {
        return year_of_birth[(lunarYear - 4) % 12];
    }

    public String getChinaMonthString() {
        return getChinaMonthString(lunarMonth, isLeapMonth);
    }

    public String getChinaMonthString(int lunarMonth, boolean isLeapMonth) {
        String chinaMonth = (isLeapMonth ? lunarLeapTag : "") + ((lunarMonth == 1) ? zhengyueTag
                : lunarCalendarNumber[lunarMonth - 1]) + lunarMonthTag;
        return chinaMonth;
    }

    public String getChinaDayString(boolean isDisplayLunarMonthForFirstDay) {
        return getChinaDayString(lunarMonth, lunarDay, isLeapMonth, isDisplayLunarMonthForFirstDay);
    }

    public String getChinaDayString(
            int lunarMonth,
            int lunarDay,
            boolean isLeapMonth,
            boolean isDisplayLunarMonthForFirstDay) {
        if (lunarDay > 30) {
            return "";
        }
        if (lunarDay == 1 && isDisplayLunarMonthForFirstDay) {
            return getChinaMonthString(lunarMonth, isLeapMonth);
        }
        if (lunarDay == 10) {
            return lunarCalendarTen[0] + lunarCalendarTen[1];
        }
        if (lunarDay == 20) {
            return lunarCalendarTen[4] + lunarCalendarTen[1];
        }

        return lunarCalendarTen[lunarDay / 10] + lunarCalendarNumber[(lunarDay + 9) % 10];
    }

    public String getChinaYearString() {
        return getChinaYearString(lunarYear);
    }

    public String getChinaYearString(int lunarYear) {
        return String.valueOf(lunarYear);
    }

    public String[] getLunarCalendarInfo() {
        if (lunarYear == 0 || lunarMonth == 0 || lunarDay == 0) {
            return null;//new String[]{null,null,null,null,null};
        }
        String lunarYearStr = getChinaYearString();
        String lunarMonthStr = getChinaMonthString();
        String lunarDayStr = getChinaDayString(true);

        String traditionFestivalStr = getTraditionalFestival();
        String festivalStr = getFestival();
        String solarTermStr = getLunarSolarTerms(solarYear);

        return new String[]{lunarYearStr, lunarMonthStr, lunarDayStr, traditionFestivalStr,
                festivalStr, solarTermStr};
    }

    public boolean isLunarSetting() {
        String language = getLanguageEnv();

        if (language != null && (language.trim()
                .equals("zh-CN") || language.trim()
                .equals("zh-TW"))) {
            return true;
        } else {
            return false;
        }
    }

    private String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry()
                .toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }


}
