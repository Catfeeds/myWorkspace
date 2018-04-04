package me.suncloud.marrymemo.model.finder;

import org.joda.time.DateTime;

/**
 * 发现页feeds流ids缓存列表model
 * Created by chen_bin on 2018/2/6 0006.
 */
public class FinderFeedHistory {
    private String type;
    private long id;
    private DateTime date;
    private boolean isShowSimilarIcon = true; //是否显示找相似icon
    private boolean isShowSimilarRelevantHint; //笔记中提及，提供XX套餐/案例

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public boolean isShowSimilarIcon() {
        return isShowSimilarIcon;
    }

    public void setShowSimilarIcon(boolean showSimilarIcon) {
        isShowSimilarIcon = showSimilarIcon;
    }

    public boolean isShowSimilarRelevantHint() {
        return isShowSimilarRelevantHint;
    }

    public void setShowSimilarRelevantHint(boolean showSimilarRelevantHint) {
        isShowSimilarRelevantHint = showSimilarRelevantHint;
    }
}
