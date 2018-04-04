package me.suncloud.marrymemo.model.message;

/**
 * Created by luohanlin on 2017/12/28.
 */

public class NotificationGroupItem {
    private int newCount;
    private NotificationGroup group;
    private long lastId;

    public NotificationGroupItem(NotificationGroup group) {
        this.group = group;
    }

    public NotificationGroupItem(
            int newCount, NotificationGroup group, long lastId) {
        this.newCount = newCount;
        this.group = group;
        this.lastId = lastId;
    }

    public void setNewCount(int newCount) {
        this.newCount = newCount;
    }

    public void setGroup(NotificationGroup group) {
        this.group = group;
    }

    public void setLastId(long lastId) {
        this.lastId = lastId;
    }

    public int getNewCount() {
        return newCount;
    }

    public NotificationGroup getGroup() {
        return group;
    }

    public long getLastId() {
        return lastId;
    }
}
