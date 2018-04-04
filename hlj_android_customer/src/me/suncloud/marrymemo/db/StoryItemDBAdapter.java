package me.suncloud.marrymemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import me.suncloud.marrymemo.model.Item;

public class StoryItemDBAdapter {
    public static final String KEY_ID = "_id";
    public static final String KEY_EXTRAID = "_extraId";
    public static final String KEY_STORYID = "_storyId";
    public static final String KEY_UPLOAD = "_upload";
    public static final String KEY_DETELE = "_detele";
    public static final String KEY_PERSISTENTID = "_persistentId";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_HIGHT = "_hight";
    public static final String KEY_WIDTH = "_width";
    public static final String KEY_ADDR = "addr";
    public static final String KEY_LONGITUDE = "_lon";
    public static final String KEY_LATITUDE = "_lat";
    public static final String KEY_REVIEW_COUNT = "_reviewCount";
    public static final String KEY_COLLECT_COUNT = "_collectCount";
    public static final String KEY_PATH = "mediaPath";
    public static final String KEY_POSITION = "_position";
    public static final String KEY_KIND = "_kind";
    private static final String TAG = "DBAdapter";
    private static final String DATABASE_NAME = "storyItem";
    private static final String DATABASE_TABLE = "item";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_CREATE = "create table item (_extraId integer not null primary key, "
            + "description text , mediaPath text,addr text,"
            + "_position integer not null,_hight integer not null,_width integer not null,"
            + "_kind integer not null,_id integer not null,_storyId integer not null,_upload integer not null,"
            + "_detele integer not null,_reviewCount integer not null,_collectCount integer not null,_lon integer not null,_lat integer not null,_persistentId text);";
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public StoryItemDBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public StoryItemDBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // ---打开数据库---

    public void close() {
        DBHelper.close();
    }

    // ---关闭数据库---

    public long insertTitle(Item item, long extraId, long storyId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ID, item.getId());
        initialValues.put(KEY_EXTRAID, extraId);
        initialValues.put(KEY_DESCRIPTION, item.getDescription());
        initialValues.put(KEY_DETELE, item.isDetele() ? 1 : 0);
        initialValues.put(KEY_HIGHT, item.getHight());
        initialValues.put(KEY_WIDTH, item.getWidth());
        initialValues.put(KEY_KIND, item.getKind());
        initialValues.put(KEY_PATH, item.getMediaPath());
        initialValues.put(KEY_STORYID, storyId);
        initialValues.put(KEY_UPLOAD, 0);
        initialValues.put(KEY_POSITION, item.getPosition());
        initialValues.put(KEY_REVIEW_COUNT, item.getCommentCount());
        initialValues.put(KEY_COLLECT_COUNT, item.getCollectCount());
        if (item.hasPlace()) {
            initialValues.put(KEY_LATITUDE, item.getLatitude());
            initialValues.put(KEY_LONGITUDE, item.getLongitude());
            initialValues.put(KEY_ADDR, item.getPlace());
        } else {
            initialValues.put(KEY_LATITUDE, 0);
            initialValues.put(KEY_LONGITUDE, 0);
        }
        initialValues.put(KEY_PERSISTENTID, item.getPersistentId());
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // ---向数据库中插入一个标题---

    public boolean deleteLocalTitle(long extraId) {
        return db.delete(DATABASE_TABLE, "(" + KEY_EXTRAID + "=" + extraId
                + ")", null) > 0;
    }

    // ---删除一个指定标题---

    public boolean deleteALLTitle(long storyId) {
        return db.delete(DATABASE_TABLE, "(" + KEY_STORYID + "=" + storyId
                + ")", null) > 0;
    }

    public boolean deleteNullTitle(long storyId) {
        return db.delete(DATABASE_TABLE, "(" + KEY_STORYID + "=" + storyId
                + ")&&(" + KEY_DETELE + "=1)", null) > 0;
    }

    public Cursor getAllTitles() {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_DESCRIPTION,
                        KEY_EXTRAID, KEY_HIGHT, KEY_KIND, KEY_PATH, KEY_DETELE,
                        KEY_POSITION, KEY_STORYID, KEY_UPLOAD, KEY_WIDTH,
                        KEY_REVIEW_COUNT, KEY_COLLECT_COUNT, KEY_LATITUDE,
                        KEY_LONGITUDE, KEY_ADDR, KEY_PERSISTENTID}, null, null, null,
                null, null);
    }

    // ---检索所有标题---

    public Cursor getChangeTitles(long storyId) {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_DESCRIPTION,
                        KEY_EXTRAID, KEY_HIGHT, KEY_KIND, KEY_PATH, KEY_DETELE,
                        KEY_POSITION, KEY_STORYID, KEY_UPLOAD, KEY_WIDTH,
                        KEY_REVIEW_COUNT, KEY_COLLECT_COUNT, KEY_LATITUDE,
                        KEY_LONGITUDE, KEY_ADDR, KEY_PERSISTENTID}, "(" + KEY_STORYID
                        + "=" + storyId + ")and(" + KEY_UPLOAD + "=0)", null, null,
                null, null);
    }

    public Cursor getItemList(long storyId) {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_DESCRIPTION,
                KEY_EXTRAID, KEY_HIGHT, KEY_KIND, KEY_PATH, KEY_DETELE,
                KEY_POSITION, KEY_STORYID, KEY_UPLOAD, KEY_WIDTH,
                KEY_REVIEW_COUNT, KEY_COLLECT_COUNT, KEY_LATITUDE,
                KEY_LONGITUDE, KEY_ADDR, KEY_PERSISTENTID}, "(" + KEY_STORYID
                + "=" + storyId + ")", null, null, null, KEY_POSITION + " ASC");
    }

    public Cursor getTitle(long storyId, long position) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ID,
                        KEY_DESCRIPTION, KEY_EXTRAID, KEY_HIGHT, KEY_KIND, KEY_PATH,
                        KEY_DETELE, KEY_POSITION, KEY_STORYID, KEY_UPLOAD, KEY_WIDTH,
                        KEY_REVIEW_COUNT, KEY_COLLECT_COUNT, KEY_LATITUDE,
                        KEY_LONGITUDE, KEY_ADDR, KEY_PERSISTENTID},
                "(" + KEY_STORYID + "=" + storyId + ")and(" + KEY_POSITION
                        + "=" + position + ")", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // ---检索一个指定标题---

    public boolean updateTitle(Item item, long extraId) {
        ContentValues args = new ContentValues();
        args.put(KEY_PATH, item.getMediaPath());
        return db.update(DATABASE_TABLE, args, KEY_EXTRAID + "=" + extraId,
                null) > 0;
    }

    // ---更新一个标题---

    public boolean updateItemToLocale(long storyId) {
        ContentValues args = new ContentValues();
        args.put(KEY_ID, 0);
        args.put(KEY_STORYID, storyId);
        return db.update(DATABASE_TABLE, args, KEY_STORYID + "=" + storyId,
                null) > 0;
    }

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
                db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD "
                        + KEY_PERSISTENTID + " text;");
            } catch (SQLiteException e) {
                db.execSQL("DROP TABLE IF EXISTS item");
                onCreate(db);
            }
        }
    }
}