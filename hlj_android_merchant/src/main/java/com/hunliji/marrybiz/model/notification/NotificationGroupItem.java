package com.hunliji.marrybiz.model.notification;
import java.util.Date;

/**
 * Created by wangtao on 2017/8/17.
 */

public class NotificationGroupItem {

    private int newsCount;
    private NotificationGroup group;
    private Date lastDate;

    public NotificationGroupItem(NotificationGroup group) {
        this.group = group;
    }

    public int getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(int newsCount) {
        this.newsCount = newsCount;
    }

    public NotificationGroup getGroup() {
        return group;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public Date getLastDate() {
        return lastDate;
    }
}
