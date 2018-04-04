package com.hunliji.hljnotelibrary;

import android.content.Context;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;

/**
 * Created by chen_bin on 2017/6/22 0022.
 */
public class HljNote {

    public final static String NOTE_TYPE = "Note";
    public final static String NOTE_BOOK_TYPE = "NoteBook";
    public final static int MERCHANT_NOTE_TYPE = 1;
    public final static int NOTE_MAX_VIDEO_LENGTH = 600;

    public static class PrefKeys {
        public final static String PREF_NOTE_EDU = "note_edu";
        public final static String PREF_VIDEO_NOTE_CREATED = "video_note_created";
        public final static String PREF_NOTE_DRAFT_STR = "note_draft_str";
        public final static String PREF_NOTE_COLLECT_HINT_CLICKED = "note_collect_hint_clicked";
        public final static String PREF_NOTE_SCROLL_HINT_CLICKED = "note_scroll_hint_clicked";
        public final static String PREF_USER_PREPARE_SAVED = "user_prepare_saved";
        public final static String PREF_USER_PREPARE_TIMESTAMP = "user_prepare_timestamp";
        public static final String PREF_HAS_CREATED_WEDDING_PHOTO =
                "is_first_create_wedding_photo";//历史原因，存储字段有歧义
    }

    public static class RequestCode {
        public final static int CHOOSE_VIDEO = 1;
        public final static int CHOOSE_PHOTO = 2;
        public final static int SELECT_NOTE_CATEGORY_MARK = 3;
        public final static int SELECT_NOTE_SPOT_ENTITY = 4;
        public final static int NOTE_HELP = 5;
        public final static int NOTE_DETAIL = 6;
        public final static int NOTE_CREATE_POSTER = 7;
    }

    public static boolean isMerchant(Context context) {
        return CommonUtil.getAppType() == CommonUtil.PacketType.MERCHANT;
    }

}
