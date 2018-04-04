package me.suncloud.marrymemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import me.suncloud.marrymemo.model.Story;

public class StoryDBAdapter {
    public static final String KEY_ID = "_id";
    public static final String KEY_EXTRAID = "_extraId";
    public static final String KEY_UPLOAD = "_upload";
    public static final String KEY_CHANGE = "_change";
    public static final String KEY_OPEN = "_open";
    public static final String KEY_USERID = "_userId";
    public static final String KEY_VERSION = "_version";
    public static final String KEY_PATH = "path";
    public static final String KEY_TITLE = "title";
    public static final String KEY_REVIEW_COUNT = "_reviewCount";
    public static final String KEY_PRAISE_COUNT = "_praiseCount";
    public static final String KEY_COLLECT_COUNT = "_collectCount";
    private static final String DATABASE_NAME = "storyData";
    private static final String DATABASE_TABLE = "story";
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_CREATE = "create table story (_extraId integer not null primary key,"
            + "title text not null,path text,_version text,"
            + "_userId integer not null,_id integer not null,_upload integer not null,_change integer not null,_reviewCount integer not null,_praiseCount integer not null,_collectCount integer not null,_open integer not null);";
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public StoryDBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public StoryDBAdapter open() {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // ---打开数据库---

    public void close() {
        DBHelper.close();
    }

    // ---关闭数据库---

    public long insertTitle(Story story, long extraId) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ID, story.getId());
        initialValues.put(KEY_EXTRAID, extraId);
        initialValues.put(KEY_PATH, story.getCover());
        initialValues.put(KEY_TITLE, story.getTitle());
        initialValues.put(KEY_USERID, story.getUser().getId());
        initialValues.put(KEY_VERSION, story.getVersion());
        initialValues.put(KEY_UPLOAD, 0);
        initialValues.put(KEY_CHANGE, 1);
        initialValues.put(KEY_REVIEW_COUNT, story.getCommentCount());
        initialValues.put(KEY_PRAISE_COUNT, story.getPraiseCount());
        initialValues.put(KEY_COLLECT_COUNT, story.getCollectCount());
        initialValues.put(KEY_OPEN, story.isOpen() ? 1 : 0);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // ---向数据库中插入一个标题---

    public boolean deleteLocalTitle(long extraId) {
        return db.delete(DATABASE_TABLE, KEY_EXTRAID + "=" + extraId, null) > 0;
    }

    // ---删除一个指定标题---

    public boolean deleteNullTitle(long id) {
        return db.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
    }

    // ---检索所有标题---
    public Cursor getStoryList(long userId) {
        return db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_EXTRAID,
                        KEY_TITLE, KEY_USERID, KEY_UPLOAD, KEY_VERSION, KEY_PATH,
                        KEY_CHANGE, KEY_REVIEW_COUNT, KEY_PRAISE_COUNT, KEY_COLLECT_COUNT, KEY_OPEN}, "(" + KEY_USERID + "=" + userId + ")", null,
                null, null, null);
    }

    public boolean hasStory(long storyId) {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ID,
                        KEY_EXTRAID, KEY_TITLE, KEY_USERID, KEY_UPLOAD, KEY_VERSION,
                        KEY_PATH, KEY_CHANGE, KEY_REVIEW_COUNT, KEY_PRAISE_COUNT, KEY_COLLECT_COUNT, KEY_OPEN}, "(" + KEY_EXTRAID + "=" + storyId
                        + ")or(" + KEY_ID + "=" + storyId + ")", null, null, null,
                null, null);
        boolean b = mCursor.moveToFirst();
        mCursor.close();
        return b;
    }

    public Cursor getStoryTitle(long storyId) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ID,
                        KEY_EXTRAID, KEY_TITLE, KEY_USERID, KEY_UPLOAD, KEY_VERSION,
                        KEY_PATH, KEY_CHANGE, KEY_REVIEW_COUNT, KEY_PRAISE_COUNT, KEY_COLLECT_COUNT, KEY_OPEN}, KEY_EXTRAID + "=" + storyId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getTitle(long extraId) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ID,
                        KEY_EXTRAID, KEY_TITLE, KEY_USERID, KEY_UPLOAD, KEY_VERSION,
                        KEY_PATH, KEY_CHANGE, KEY_REVIEW_COUNT, KEY_PRAISE_COUNT, KEY_COLLECT_COUNT, KEY_OPEN}, KEY_EXTRAID + "=" + extraId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // ---检索一个指定标题---

    public boolean updateTitle(Story story, long extraId) {
        ContentValues args = new ContentValues();
        args.put(KEY_ID, story.getId());
        args.put(KEY_PATH, story.getCover());
        args.put(KEY_TITLE, story.getTitle());
        args.put(KEY_USERID, story.getUser().getId());
        args.put(KEY_REVIEW_COUNT, story.getCommentCount());
        args.put(KEY_COLLECT_COUNT, story.getCollectCount());
        args.put(KEY_OPEN, story.isOpen() ? 1 : 0);
        return db.update(DATABASE_TABLE, args, KEY_EXTRAID + "=" + extraId,
                null) > 0;
    }

    // ---更新一个标题---

    public boolean updateStoryCount(Story story) {
        ContentValues args = new ContentValues();
        args.put(KEY_ID, story.getId());
        args.put(KEY_REVIEW_COUNT, story.getCommentCount());
        args.put(KEY_COLLECT_COUNT, story.getCollectCount());
        args.put(KEY_OPEN, story.isOpen() ? 1 : 0);
        return db.update(DATABASE_TABLE, args, KEY_ID + "=" + story.getId(),
                null) > 0;
    }

    public boolean updateStoryToLocale(long storyId) {
        ContentValues args = new ContentValues();
        args.put(KEY_ID, 0);
        return db.update(DATABASE_TABLE, args, KEY_ID + "=" + storyId,
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
                db.execSQL("ALTER TABLE " + DATABASE_TABLE + " ADD " + KEY_OPEN + " integer;");
                SQLiteStatement update = db.compileStatement("UPDATE " + DATABASE_TABLE + " SET " + KEY_OPEN + "=1;");
                update.execute();
            } catch (SQLiteException e) {
                db.execSQL("DROP TABLE IF EXISTS story");
                onCreate(db);
            }
        }
    }
}