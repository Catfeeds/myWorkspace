package com.hunliji.hljcommonlibrary.models;

import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

/**
 * Created by wangtao on 2018/1/26.
 */

public class SystemNotificationData {

    private int notifyId;
    private String title;
    private String content;
    private int mPriority;
    private int smallIconRes;
    private int largeIconRes;
    private Intent intent;
    private int groupCount;

    public SystemNotificationData(
            int notifyId,
            String title,
            String content,
            @DrawableRes int mPriority,
            @DrawableRes int smallIconRes,
            int largeIconRes,
            Intent intent) {
        this.notifyId = notifyId;
        this.title = title;
        this.content = content;
        this.mPriority = mPriority;
        this.smallIconRes = smallIconRes;
        this.largeIconRes = largeIconRes;
        this.intent = intent;
        this.groupCount = 1;
    }

    public int getNotifyId() {
        return notifyId;

    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getmPriority() {
        return mPriority;
    }

    public int getSmallIconRes() {
        return smallIconRes;
    }

    public int getLargeIconRes() {
        return largeIconRes;
    }

    public Intent getIntent() {
        return intent;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }
}
