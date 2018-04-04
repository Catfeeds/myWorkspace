package me.suncloud.marrymemo.model;

import org.json.JSONObject;

/**
 * Created by Suncloud on 2016/9/23.
 * 通知信息保存类
 */

public class Notify {
    private int id;
    private JSONObject lastMsg;
    private String taskid;
    private String messageid;


    public Notify(int id, JSONObject lastMsg, String taskid, String messageid) {
        this.id = id;
        this.lastMsg = lastMsg;
        this.taskid = taskid;
        this.messageid = messageid;
    }

    public int getId() {
        return id;
    }
    public JSONObject getLastMsg() {
        return lastMsg;
    }
    public String getTaskid() {
        return taskid;
    }
    public String getMessageid() {
        return messageid;
    }
}
