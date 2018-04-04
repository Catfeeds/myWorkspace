package me.suncloud.marrymemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class MsgDBAdapter {
    public static final String KEY_KEYID = "_key";
    public static final String KEY_ID = "_id";
    public static final String KEY_MSG_ID = "_msgId";
    public static final String KEY_MSG = "content";
    public static final String KEY_TIME = "time";
    public static final String KEY_FROM = "_fromId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_TO = "_toId";
    public static final String KEY_KIND = "kind";
    public static final String KEY_NEW = "_new";
    public static final String KEY_SESSION_ID = "_sessionId";
    public static final String KEY_SESSION_NICK = "_sessionNick";
    public static final String KEY_SESSION_ACATER = "_sessionAvater";
    public static final String KEY_USERID = "_userId";
    public static final String KEY_HEIGHT = "_height";
    public static final String KEY_WIDTH = "_width";
    public static final String KEY_OPUID = "_opuId";
    public static final String KEY_OPUTITLE = "opuTitle";
    public static final String KEY_OPUCOVER = "opuCover";
    public static final String KEY_ISLOCAL = "islocal";
    public static final String KEY_CHANNEL = "channel";
    public static final String KEY_OPUPRICE = "_opuPrice";
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "msg";
    private static final String DATABASE_TABLE = "content";
    private static final int DATABASE_VERSION = 8;
    private static final String DATABASE_CREATE = "create table content (_key integer not null " +
            "primary key,_id text not null, " + "content text, time integer not null,image text, " +
            "kind text,_sessionId integer,_sessionNick text,_sessionAvater text," + "_fromId " +
            "integer not null,_toId integer not null,_new integer,_userId integer not null," +
            "_height integer,_width integer,_opuId integer,opuTitle text,opuCover text,islocal " +
            "integer,channel text,_msgId text, _opuPrice float);";
    private final Context context;
    private int openCount;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public MsgDBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public MsgDBAdapter open() throws SQLException {
        openCount++;
        if (DBHelper != null && (db == null || !db.isOpen())) {
            db = DBHelper.getWritableDatabase();
        }
        return this;
    }

    // ---打开数据库---

    public void close() {
        openCount--;
        if (openCount < 1) {
            DBHelper.close();
        }
    }

    public Cursor getAllList() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_MSG, KEY_TIME, KEY_FROM,
                KEY_IMAGE, KEY_TO, KEY_KIND, KEY_HEIGHT, KEY_WIDTH, KEY_OPUID, KEY_OPUTITLE,
                KEY_OPUCOVER, KEY_SESSION_ID, KEY_SESSION_ACATER, KEY_SESSION_NICK, KEY_ISLOCAL,
                KEY_KEYID, KEY_CHANNEL, KEY_MSG_ID, KEY_OPUPRICE, KEY_USERID,KEY_NEW}, null,null,null,null,null);
    }

    public boolean deleteAll() {
        return db.delete(DATABASE_TABLE,null,null)>0;
    }


    // ---更新一个标题---

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                if (oldVersion < 6) {
                    db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD " + KEY_CHANNEL + " text;");
                }
                if (oldVersion < 7) {
                    db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD " + KEY_MSG_ID + " text;");
                }
                db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD " + KEY_OPUPRICE + " float");
            } catch (SQLiteException e) {
                db.execSQL("DROP TABLE IF EXISTS content");
                onCreate(db);
            }
        }
    }
}