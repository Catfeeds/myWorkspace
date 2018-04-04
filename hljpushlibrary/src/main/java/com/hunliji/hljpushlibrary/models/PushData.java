package com.hunliji.hljpushlibrary.models;

import com.hunliji.hljpushlibrary.models.activity.ActivityData;
import com.hunliji.hljpushlibrary.models.live.LiveData;
import com.hunliji.hljpushlibrary.models.notify.NotifyData;

/**
 * Created by luohanlin on 2018/3/16.
 * Push消息区分类型后合并为一个组合类
 */

public class PushData {

    public static final int DATA_TYPE_LIVE = 0;
    public static final int DATA_TYPE_NOTIFY = 1;
    public static final int DATA_TYPE_ACTIVITY = 2;
    private int pushDataType;
    private Object pushData;

    public PushData(NotifyData pushData) {
        this.pushDataType = DATA_TYPE_NOTIFY;
        this.pushData = pushData;
    }

    public PushData(LiveData pushData) {
        this.pushDataType = DATA_TYPE_LIVE;
        this.pushData = pushData;
    }

    public PushData(ActivityData pushData) {
        this.pushDataType = DATA_TYPE_ACTIVITY;
        this.pushData = pushData;
    }

    public int getPushDataType() {
        return pushDataType;
    }

    public Object getPushData() {
        return pushData;
    }

    public boolean isExceed() {
        switch (pushDataType) {
            case PushData.DATA_TYPE_LIVE:
                return ((LiveData) pushData).getLiveChannel()
                        .isExceed();
            case PushData.DATA_TYPE_NOTIFY:
                return ((NotifyData) pushData).getTask()
                        .isExceed();
            case PushData.DATA_TYPE_ACTIVITY:
                return ((ActivityData) pushData).getFinderActivity()
                        .isSignUpEnd();
        }
        return true;
    }

    public ActivityData getActivityData() {
        if (pushDataType == DATA_TYPE_ACTIVITY) {
            return (ActivityData) pushData;
        }
        return null;
    }

    public NotifyData getNotifyData() {
        if (pushDataType == DATA_TYPE_NOTIFY) {
            return (NotifyData) pushData;
        }
        return null;
    }

    public LiveData getLive() {
        if (pushDataType == DATA_TYPE_LIVE) {
            LiveData liveData = (LiveData) pushData;
            if (liveData.getLiveChannel()
                    .isExceed()) {
                return null;
            } else {
                return liveData;
            }
        }
        return null;
    }

}
