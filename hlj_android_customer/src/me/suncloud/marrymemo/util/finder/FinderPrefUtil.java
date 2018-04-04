package me.suncloud.marrymemo.util.finder;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.model.finder.FinderFeed;
import me.suncloud.marrymemo.model.finder.FinderFeedHistory;

/**
 * Created by chen_bin on 2018/2/5 0005.
 */
public class FinderPrefUtil {

    private SharedPreferences preferences;

    private static FinderPrefUtil INSTANCE;

    private List<FinderFeed> feeds;
    private List<FinderFeedHistory> histories;
    private long cid;
    private String tab; //(精选choice 好评favor)

    private Boolean isNoteClickHintClicked;
    private Boolean isSimilarClickHintClicked;

    private final static String PREF_FINDER_FIRST30_FEEDS = "finder_first30_feeds_%s_%s"; //cid_tab
    private final static String PREF_FINDER_FEED_HISTORIES = "finder_feed_histories_%s_%s";
    private final static String PREF_LAST_ID = "last_id_%s_%s";
    private final static String PREF_LAST_TIMESTAMP = "last_timestamp_%s_%s";
    private final static String PREF_NOTE_CLICK_HINT_CLICKED = "note_click_hint_clicked";
    private final static String PREF_SIMILAR_CLICK_HINT_CLICKED = "similar_click_hint_clicked";

    private FinderPrefUtil(Context context) {
        preferences = context.getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                Context.MODE_PRIVATE);
    }

    public static FinderPrefUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FinderPrefUtil(context);
        }
        return INSTANCE;
    }

    /**
     * 发现页feeds前30条数据，不包含cpm，poster
     *
     * @param cid
     * @param tab
     * @return
     */
    public List<FinderFeed> getFinderFist30Feeds(long cid, String tab) {
        if (this.cid != cid || !tab.equals(this.tab)) {
            this.cid = cid;
            this.tab = tab;
            this.feeds = new ArrayList<>();
            this.histories = new ArrayList<>();
        }
        if (CommonUtil.isCollectionEmpty(feeds)) {
            String str = preferences.getString(String.format(PREF_FINDER_FIRST30_FEEDS, cid, tab),
                    null);
            feeds = GsonUtil.getGsonInstance()
                    .fromJson(str, new TypeToken<List<FinderFeed>>() {}.getType());
        }
        return feeds;
    }

    /**
     * 设置发现页feeds的钱30条数据到文件中，不包含cpm，poster
     *
     * @param feeds
     * @param cid
     * @param tab
     */
    public void setFinderFirst30Feeds(List<FinderFeed> feeds, long cid, String tab) {
        this.feeds = feeds;
        this.cid = cid;
        this.tab = tab;
        String str = GsonUtil.getGsonInstance()
                .toJson(feeds, new TypeToken<List<FinderFeed>>() {}.getType());
        preferences.edit()
                .putString(String.format(PREF_FINDER_FIRST30_FEEDS, cid, tab), str)
                .apply();
    }

    /**
     * 发现页feeds流ids集合
     *
     * @param cid
     * @param tab
     * @return
     */
    public List<FinderFeedHistory> getFinderFeedHistories(long cid, String tab) {
        if (this.cid != cid || !tab.equals(this.tab)) {
            this.cid = cid;
            this.tab = tab;
            this.feeds = new ArrayList<>();
            this.histories = new ArrayList<>();
        }
        if (CommonUtil.isCollectionEmpty(histories)) {
            String str = preferences.getString(String.format(PREF_FINDER_FEED_HISTORIES, cid, tab),
                    null);
            histories = GsonUtil.getGsonInstance()
                    .fromJson(str, new TypeToken<List<FinderFeedHistory>>() {}.getType());
        }
        return histories;
    }

    /**
     * 设置发现页feeds的id
     *
     * @param histories
     * @param cid
     * @param tab
     */
    public void setFinderFeedHistories(List<FinderFeedHistory> histories, long cid, String tab) {
        this.histories = histories;
        this.cid = cid;
        this.tab = tab;
        String str = GsonUtil.getGsonInstance()
                .toJson(histories, new TypeToken<List<FinderFeedHistory>>() {}.getType());
        preferences.edit()
                .putString(String.format(PREF_FINDER_FEED_HISTORIES, cid, tab), str)
                .apply();
    }

    /**
     * 判断时间是否过期，过期时间已3天为限。
     *
     * @param date
     * @return
     */
    public boolean isDateExpired(DateTime date) {
        return date == null || (System.currentTimeMillis() - date.getMillis()) / (1000 * 60 * 60
                * 24) > 3;
    }

    /**
     * feeds列表转化为histories列表
     *
     * @param feeds
     */
    public List<FinderFeedHistory> covertFinderFeedsToHistories(List<FinderFeed> feeds) {
        List<FinderFeedHistory> histories = new ArrayList<>();
        for (FinderFeed feed : feeds) {
            FinderFeedHistory history = new FinderFeedHistory();
            history.setType(feed.getType());
            history.setId(feed.getEntityObjId());
            history.setDate(new DateTime(HljTimeUtils.getServerCurrentTimeMillis()));
            history.setShowSimilarIcon(feed.isShowSimilarIcon());
            history.setShowSimilarRelevantHint(feed.isShowRelevantHint());
            histories.add(history);
        }
        return histories;
    }


    /**
     * 获取最近一次拉取的列表的第一个记录的id
     *
     * @param cid
     * @param tab
     * @return
     */
    public long getLastId(long cid, String tab) {
        return preferences.getLong(String.format(PREF_LAST_ID, cid, tab), 0);
    }

    /**
     * 设置最近一次拉取的列表的第一个记录的id
     *
     * @param lastId
     * @param cid
     * @param tab
     */
    public void setLastId(long lastId, long cid, String tab) {
        preferences.edit()
                .putLong(String.format(PREF_LAST_ID, cid, tab), lastId)
                .apply();
    }

    /**
     * 最近一次拉取列表时间
     *
     * @param cid
     * @param tab
     * @return
     */
    public long getLastTimestamp(long cid, String tab) {
        return preferences.getLong(String.format(PREF_LAST_TIMESTAMP, cid, tab), 0);
    }


    /**
     * 最近一次拉取列表时间
     *
     * @param tab
     * @param lastTimestamp
     */
    public void setLastTimestamp(long lastTimestamp, long cid, String tab) {
        preferences.edit()
                .putLong(String.format(PREF_LAST_TIMESTAMP, cid, tab), lastTimestamp)
                .apply();
    }

    /**
     * 跳转到笔记详情的提示是否点击
     *
     * @return
     */
    public boolean isNoteClickHintClicked() {
        if (isNoteClickHintClicked == null) {
            isNoteClickHintClicked = preferences.getBoolean(PREF_NOTE_CLICK_HINT_CLICKED, false);
        }
        return isNoteClickHintClicked;
    }

    /**
     * 设置跳转到笔记详情的点击
     *
     * @param noteClickHintClicked
     */

    public void setNoteClickHintClicked(boolean noteClickHintClicked) {
        this.isNoteClickHintClicked = noteClickHintClicked;
        preferences.edit()
                .putBoolean(PREF_NOTE_CLICK_HINT_CLICKED, noteClickHintClicked)
                .apply();
    }

    /**
     * 是否能够显示"找相似"的提示
     *
     * @return
     */
    public boolean isCanShowSimilarClickHint() {
        return isNoteClickHintClicked() && !isSimilarClickHintClicked();
    }

    /**
     * 找相似的hint是否点击
     *
     * @return
     */
    public boolean isSimilarClickHintClicked() {
        if (isSimilarClickHintClicked == null) {
            isSimilarClickHintClicked = preferences.getBoolean(PREF_SIMILAR_CLICK_HINT_CLICKED,
                    false);
        }
        return isSimilarClickHintClicked;
    }

    /**
     * 设置找相似的hint点击
     */
    public void setSimilarClickHintClicked(boolean similarClickHintClicked) {
        this.isSimilarClickHintClicked = similarClickHintClicked;
        preferences.edit()
                .putBoolean(PREF_SIMILAR_CLICK_HINT_CLICKED, similarClickHintClicked)
                .apply();
    }

}