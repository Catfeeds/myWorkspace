package com.hunliji.hljpushlibrary.models;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hunliji.hljpushlibrary.models.activity.ActivityData;
import com.hunliji.hljpushlibrary.models.live.LiveData;
import com.hunliji.hljpushlibrary.models.notify.NotifyLog;
import com.hunliji.hljpushlibrary.websocket.PushSocket;

import org.joda.time.DateTime;

/**
 * Created by wangtao on 2017/11/29.
 */

public class PushBody {

    private JsonElement data;
    private DateTime time;
    private String type;

    public PushBody(String type) {
        this.type = type;
        this.data = new JsonPrimitive(System.currentTimeMillis() / 1000);
    }

    /**
     * 切换城市
     *
     * @param cid 城市cid
     */
    public PushBody(long cid) {
        this.type = PushSocket.LOCALE_CHANGE;
        this.data = new JsonPrimitive(cid);
    }


    /**
     * 已收到消息
     *
     * @param log 收到通知log
     */
    public PushBody(NotifyLog log) {
        this.type = PushSocket.TASK_NOTIFY_ACK;
        JsonObject feedBack = new JsonObject();
        feedBack.addProperty("log_id", log.getId());
        feedBack.addProperty("task_id", log.getTaskId());
        this.data = feedBack;
    }


    /**
     * 已收到消息
     *
     * @param liveData 收到直播通知
     */
    public PushBody(LiveData liveData) {
        this.type = PushSocket.LIVE_ON_ACK;
        this.data = new JsonPrimitive(liveData.getLogId());
    }

    /**
     * 已收到消息
     *
     * @param activityData 收到活动通知
     */
    public PushBody(ActivityData activityData) {
        this.type = PushSocket.ACTIVITY_ON_ACK;
        this.data = new JsonPrimitive(activityData.getLogId());
    }

    public String getType() {
        return type;
    }

    public JsonElement getData() {
        return data;
    }
}
