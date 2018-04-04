package me.suncloud.marrymemo.model.realm;

import io.realm.RealmObject;

/**
 * Created by mo_yu on 2016/10/14.存储历史点击事件
 */

public class FeedClickHistory extends RealmObject {
    public long id;
    public int month;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
