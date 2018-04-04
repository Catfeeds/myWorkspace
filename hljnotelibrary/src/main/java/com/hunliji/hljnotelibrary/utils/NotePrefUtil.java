package com.hunliji.hljnotelibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.note.Note;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.utils.GsonUtil;
import com.hunliji.hljnotelibrary.HljNote;

/**
 * notePrefs工具类
 * Created by chen_bin on 2017/7/11 0011.
 */
public class NotePrefUtil {
    private SharedPreferences preferences;
    private static NotePrefUtil INSTANCE;
    
    private NotePrefUtil(Context context) {
        preferences = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE, Context.MODE_PRIVATE);
    }

    public static NotePrefUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new NotePrefUtil(context);
        }
        return INSTANCE;
    }

    /**
     * 从草稿拿取note
     *
     * @return
     */
    public Note getNoteDraft() {
        Note note = null;
        try {
            String noteDraftStr = preferences.getString(HljNote.PrefKeys.PREF_NOTE_DRAFT_STR, null);
            note = GsonUtil.getGsonInstance()
                    .fromJson(noteDraftStr, Note.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setNoteDraft(null);
        return note;
    }

    /**
     * 将note设到草稿
     *
     * @param note
     */
    public void setNoteDraft(Note note) {
        if (note != null) {
            note.getNoteMarks()
                    .clear(); //标签页带过来的mark不存草稿
        }
        String noteDraftStr = null;
        try {
            noteDraftStr = GsonUtil.getGsonInstance()
                    .toJson(note, Note.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        preferences.edit()
                .putString(HljNote.PrefKeys.PREF_NOTE_DRAFT_STR, noteDraftStr)
                .apply();
    }

    /**
     * 商家端发布动态引导
     *
     * @return
     */
    public boolean isShowNoteEdu() {
        boolean flag = preferences.getBoolean(HljNote.PrefKeys.PREF_NOTE_EDU, true);
        if (flag) {
            preferences.edit()
                    .putBoolean(HljNote.PrefKeys.PREF_NOTE_EDU, false)
                    .apply();
        }
        return flag;
    }

    /**
     * 是否创建了视频笔记
     *
     * @return
     */
    public boolean isVideoNoteCreated() {
        boolean flag = preferences.getBoolean(HljNote.PrefKeys.PREF_VIDEO_NOTE_CREATED, false);
        if (!flag) {
            preferences.edit()
                    .putBoolean(HljNote.PrefKeys.PREF_VIDEO_NOTE_CREATED, true)
                    .apply();
        }
        return flag;
    }

    /**
     * 收藏引导是否点击过
     *
     * @return
     */
    public boolean isNoteCollectHintClicked() {
        return preferences.getBoolean(HljNote.PrefKeys.PREF_NOTE_COLLECT_HINT_CLICKED, false);
    }

    /**
     * 设置引导点击
     */
    public void setNoteCollectHintClicked(boolean flag) {
        preferences.edit()
                .putBoolean(HljNote.PrefKeys.PREF_NOTE_COLLECT_HINT_CLICKED, flag)
                .apply();
    }

    /**
     * 滑动引导是否点击过
     *
     * @return
     */
    public boolean isNoteScrollHintClicked() {
        return preferences.getBoolean(HljNote.PrefKeys.PREF_NOTE_SCROLL_HINT_CLICKED, false);
    }

    /**
     * 设置滑动引导点击
     */
    public void setNoteScrollHintClicked(boolean flag) {
        preferences.edit()
                .putBoolean(HljNote.PrefKeys.PREF_NOTE_SCROLL_HINT_CLICKED, flag)
                .apply();
    }

    /**
     * 是否显示备婚的界面
     *
     * @return
     */
    public boolean isShowUserPrepareView() {
        return !isUserPrepareSaved() && isUserPrepareMoreThan3Days();
    }

    /**
     * 备婚是否已保存
     *
     * @return
     */
    public boolean isUserPrepareSaved() {
        return preferences.getBoolean(HljNote.PrefKeys.PREF_USER_PREPARE_SAVED, false);
    }

    /**
     * 设置备婚是否保存
     *
     * @param flag
     */
    public void setUserPrepareSaved(boolean flag) {
        preferences.edit()
                .putBoolean(HljNote.PrefKeys.PREF_USER_PREPARE_SAVED, flag)
                .apply();
    }

    /**
     * 备婚的时间，用于3天判断
     *
     * @return
     */
    public long getUserPrepareTimestamp() {
        return preferences.getLong(HljNote.PrefKeys.PREF_USER_PREPARE_TIMESTAMP, 0);
    }

    /**
     * 设置备婚时间，用于3天判断
     *
     * @param timestamp
     */
    public void setUserPrepareTimestamp(long timestamp) {
        preferences.edit()
                .putLong(HljNote.PrefKeys.PREF_USER_PREPARE_TIMESTAMP, timestamp)
                .apply();
    }

    /**
     * 用户备婚阶段判断点击是否超过了3天
     *
     * @return
     */
    public boolean isUserPrepareMoreThan3Days() {
        int days = (int) ((HljTimeUtils.getServerCurrentTimeMillis() - getUserPrepareTimestamp())
                / (1000 * 60 * 60 * 24));
        return days > 3;
    }
}