package com.hunliji.hljpushlibrary.models.notify;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wangtao on 2017/11/30.
 */

public class NotifyData {

    @SerializedName("log")
    private NotifyLog log;
    @SerializedName("task")
    private NotifyTask task;

    public NotifyLog getLog() {
        return log;
    }

    public NotifyTask getTask() {
        return task;
    }

    public long getLogId(){
        if(log==null){
            return 0;
        }
        return log.getId();
    }

    public long getUserId(){
        if(log==null){
            return 0;
        }
        return log.getUserId();
    }

    public void setTask(NotifyTask task) {
        this.task = task;
    }
}
