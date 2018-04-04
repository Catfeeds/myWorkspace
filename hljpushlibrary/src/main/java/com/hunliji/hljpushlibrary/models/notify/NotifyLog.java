package com.hunliji.hljpushlibrary.models.notify;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/11/30.
 */

public class NotifyLog {

    @SerializedName("id")
    private long id;
    @SerializedName("task_id")
    private long taskId;
    @SerializedName("user_id")
    private long userId;

    public long getId() {
        return id;
    }

    public long getTaskId() {
        return taskId;
    }

    public long getUserId() {
        return userId;
    }
}
