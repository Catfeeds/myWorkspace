package me.suncloud.marrymemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.SparseArray;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.BuildConfig;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.tools.WeddingCalendarItem;

public class CalendarDBAdapter {

    private Context context;
    private SQLiteDatabase db;

    private final static int BUFFER_SIZE = 400000;
    private final static String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    private final static String DATABASE_PATH = "/data" + Environment.getDataDirectory()
            .getAbsolutePath() + "/" + PACKAGE_NAME;
    private final static String DATABASE_NAME = "hdjr.sqlite";
    private final static String DATABASE_TABLE = "黄历数据库";
    private final static String YEAR = "year";
    private final static String MONTH = "month";
    private final static String DAY = "day";
    private final static String YI = "yi";
    private final static String JI = "ji";

    private String fileName = DATABASE_PATH + "/" + DATABASE_NAME;

    public CalendarDBAdapter(Context context) {
        this.context = context;
    }

    public void open() throws SQLException {
        writeFromRaw(fileName);
        db = openDatabase();
    }

    public SQLiteDatabase openDatabase() throws SQLException {
        db = SQLiteDatabase.openOrCreateDatabase(fileName, null);
        return db;
    }

    private void writeFromRaw(String dbFile) {
        try {
            if (!(new File(dbFile).exists())) {
                InputStream is = context.getResources()
                        .openRawResource(R.raw.hdjr);
                FileOutputStream fos = new FileOutputStream(dbFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    /**
     * 获取吉日列表
     *
     * @param startYear
     * @param endYear
     * @return
     * @throws SQLException
     */
    public SparseArray<List<String>> getLuckyDays(int startYear, int endYear) throws SQLException {
        Cursor cursor = db.query(true,
                DATABASE_TABLE,
                new String[]{YI, YEAR, MONTH, DAY},
                "(" + YI + " like '%" + WeddingCalendarItem.KEY_MARRIAGE + "%' or " + YI + " " +
                        "like" + " '%" + WeddingCalendarItem.KEY_ENGAGEMENT + "%' or " + YI + " " +
                        "like '%" + WeddingCalendarItem.KEY_BETROTHAL_GIFT + "%' or " +
                        YI + " like '%" + WeddingCalendarItem.KEY_UXORILOCAL_MARRIAGE + "%') " +
                        "and " + YEAR + "" + " between " + startYear + " and " + endYear,
                null,
                null,
                null,
                null,
                null);
        SparseArray<List<String>> array = null;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                array = new SparseArray<>();
                List<String> allDays = new ArrayList<>();
                List<String> marriageDays = new ArrayList<>();
                List<String> engagementDays = new ArrayList<>();
                List<String> betrothalGiftDays = new ArrayList<>();
                List<String> uxorilocalMarriageDays = new ArrayList<>();
                while (cursor.moveToNext()) {
                    String yi = cursor.getString(0);
                    String year = cursor.getString(1);
                    String month = cursor.getString(2);
                    String day = cursor.getString(3);
                    String str = year + "-" + month + "-" + day;
                    allDays.add(str);
                    if (yi.contains(WeddingCalendarItem.KEY_MARRIAGE)) {
                        marriageDays.add(str);
                    }
                    if (yi.contains(WeddingCalendarItem.KEY_ENGAGEMENT)) {
                        engagementDays.add(str);
                    }
                    if (yi.contains(WeddingCalendarItem.KEY_BETROTHAL_GIFT)) {
                        betrothalGiftDays.add(str);
                    }
                    if (yi.contains(WeddingCalendarItem.KEY_UXORILOCAL_MARRIAGE)) {
                        uxorilocalMarriageDays.add(str);
                    }
                }
                array.put(WeddingCalendarItem.TYPE_ALL, allDays);
                array.put(WeddingCalendarItem.TYPE_MARRIAGE, marriageDays);
                array.put(WeddingCalendarItem.TYPE_ENGAGEMENT, engagementDays);
                array.put(WeddingCalendarItem.TYPE_BETROTHAL_GIFT, betrothalGiftDays);
                array.put(WeddingCalendarItem.TYPE_UXORILOCAL_MARRIAGE, uxorilocalMarriageDays);
            }
            cursor.close();
        }
        return array;
    }

    /**
     * 获取指定日期的宜、忌
     *
     * @param dateTime
     * @return
     * @throws SQLException
     */
    public String[] getYJInfo(DateTime dateTime) throws SQLException {
        Cursor cursor = db.query(true,
                DATABASE_TABLE,
                new String[]{YI, JI},
                YEAR + " = " + dateTime.getYear() + " and " + MONTH + " = " + dateTime
                        .getMonthOfYear() + " and " + DAY + " = " + dateTime.getDayOfMonth(),
                null,
                null,
                null,
                null,
                null);
        String[] array = new String[2];
        if (cursor.moveToFirst()) {
            array[0] = cursor.getString(0);
            array[1] = cursor.getString(1);
        }
        cursor.close();
        return array;
    }

    public void close() {
        if (db != null) {
            db.close();
        }
    }
}
