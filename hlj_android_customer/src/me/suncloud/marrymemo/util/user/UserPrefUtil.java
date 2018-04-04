package me.suncloud.marrymemo.util.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * Created by chen_bin on 2017/11/10 0010.
 */
public class UserPrefUtil {

    private SharedPreferences preferences;
    private static UserPrefUtil INSTANCE;

    public final static String PREF_USER_NEWS_ICON_TIMESTAMP = "user_news_icon_timestamp";
    public final static String PREF_WEDDING_TABLE_HINT_CLICKED = "wedding_table_hint_clicked";
    public final static String PREF_WEDDING_TABLE_PARTNER_HINT_CLICKED =
            "wedding_table_partner_hint_clicked_%s";
    public final static String PREF_WEDDING_TABLE_DRAG_HINT_CLICKED =
            "wedding_table_drag_hint_clicked";

    private UserPrefUtil(Context context) {
        preferences = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
    }

    public static UserPrefUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UserPrefUtil(context);
        }
        return INSTANCE;
    }

    /**
     * 我们tab是否可以显示红点
     *
     * @return
     */
    public boolean isCanShowUserNewsIcon() {
        long timestamp = getUserNewsIconTimestamp();
        if (timestamp == 0) {
            return true;
        }
        DateTime signInTime = new DateTime(timestamp);
        DateTime currentTime = new DateTime(HljTimeUtils.getServerCurrentTimeMillis());
        return currentTime.getYear() > signInTime.getYear() || (currentTime.getYear() ==
                signInTime.getYear() && currentTime.getDayOfYear() - signInTime.getDayOfYear() >=
                1);
    }

    /**
     * 主页我们tab红点
     *
     * @return
     */
    public long getUserNewsIconTimestamp() {
        return preferences.getLong(PREF_USER_NEWS_ICON_TIMESTAMP, 0);
    }

    /**
     * 主页我们tab红点时间设置
     *
     * @param timestamp
     */
    public void setUserNewsIconTimestamp(long timestamp) {
        preferences.edit()
                .putLong(PREF_USER_NEWS_ICON_TIMESTAMP, timestamp)
                .apply();
    }

    /**
     * 宾客管理弹框
     *
     * @return
     */
    public boolean isWeddingTableHintClicked() {
        return preferences.getBoolean(PREF_WEDDING_TABLE_HINT_CLICKED, false);
    }

    /**
     * 宾客管理弹框
     */
    public void setWeddingTableHintClicked(boolean flag) {
        preferences.edit()
                .putBoolean(PREF_WEDDING_TABLE_HINT_CLICKED, flag)
                .apply();
    }

    /**
     * 宾客已绑定view点击
     *
     * @return
     */
    public boolean isWeddingTablePartnerHintClicked(long userId) {
        return preferences.getBoolean(String.format(PREF_WEDDING_TABLE_PARTNER_HINT_CLICKED,
                userId), false);
    }

    /**
     * 宾客已绑定view点击
     *
     * @param userId
     * @param flag
     */
    public void setWeddingTablePartnerHintClicked(long userId, boolean flag) {
        preferences.edit()
                .putBoolean(String.format(PREF_WEDDING_TABLE_PARTNER_HINT_CLICKED, userId), flag)
                .apply();
    }

    /**
     * 宾客桌子拖动提示是否点击过
     *
     * @return
     */
    public boolean isWeddingTableDragHintClicked() {
        return preferences.getBoolean(PREF_WEDDING_TABLE_DRAG_HINT_CLICKED, false);
    }

    /**
     * 设置宾客桌子拖动提示是否点击
     *
     * @param flag
     */
    public void setWeddingTableDragHintClicked(boolean flag) {
        preferences.edit()
                .putBoolean(PREF_WEDDING_TABLE_DRAG_HINT_CLICKED, flag)
                .apply();
    }
}
