package com.hunliji.hljvideolibrary.models;

import android.support.annotation.Nullable;

/**
 * Created by wangtao on 2017/7/22.
 */

public class VideoRxEvent {

    private RxEventType type;
    private Object object;

    public enum  RxEventType {
        VIDEO_CALLBACK, //视频操作完成返回
    }

    public VideoRxEvent(RxEventType type, @Nullable Object object) {
        this.type = type;
        this.object = object;
    }

    public RxEventType getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
