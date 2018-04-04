package com.hunliji.marrybiz.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.hunliji.marrybiz.BuildConfig;
import com.hunliji.marrybiz.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarDBAdapter {
    public static final int BUFFER_SIZE = 400000;
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String DATABASE_PATH = "/data" +
            Environment.getDataDirectory()
                    .getAbsolutePath() + "/" + PACKAGE_NAME;
    private static final String DATABASE_NAME = "hdjr.sqlite";
    private static final String DATABASE_TABLE = "黄历数据库";
    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";
    public static final String YI = "yi";
    public static final String JI = "ji";
    private Context context;
    private SQLiteDatabase db;
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

    //宜
    public ArrayList<String> getYIList(int startYear, int endYear) throws SQLException {
        Cursor cursor = db.query(true,
                DATABASE_TABLE,
                new String[]{YEAR, MONTH, DAY},
                YI + " LIKE " + "'%" +
                        context.getString(R.string.label_weddmerry) + "%'" + " AND " + YEAR + " " +
                        "BETWEEN " +
                        startYear + " AND " + endYear,
                null,
                null,
                null,
                null,
                null);
        ArrayList<String> yiList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String year = cursor.getString(0);
                String month = cursor.getString(1);
                String day = cursor.getString(2);
                yiList.add(year + "-" + month + "-" + day);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return yiList;
    }

    //忌
    public ArrayList<String> getJIList(int startYear, int endYear) throws SQLException {
        Cursor cursor = db.query(true,
                DATABASE_TABLE,
                new String[]{YEAR, MONTH, DAY},
                JI + " LIKE " + "'%" +
                        context.getString(R.string.label_weddmerry) + "%'" + " AND " + YEAR + " " +
                        "BETWEEN " +
                        startYear + " AND " + endYear,
                null,
                null,
                null,
                null,
                null);
        ArrayList<String> jiList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String year = cursor.getString(0);
                String month = cursor.getString(1);
                String day = cursor.getString(2);
                jiList.add(year + "-" + month + "-" + day);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return jiList;
    }

    //宜忌信息
    public String[] getYJInfo(Calendar currentCalendar) throws SQLException {
        Cursor cursor = db.query(true, DATABASE_TABLE, new String[]{YI, JI}, YEAR + " = " +
                currentCalendar.get(Calendar.YEAR) + " AND " + MONTH + " = " +
                (currentCalendar.get(Calendar.MONTH) + 1) + " AND " + DAY + " = " +
                currentCalendar.get(Calendar.DAY_OF_MONTH), null, null, null, null, null);
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
