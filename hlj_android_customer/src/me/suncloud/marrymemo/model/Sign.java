package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

public class Sign implements Identifiable {

    /**
     *
     */
    private static final long serialVersionUID = 623565655238684249L;

    private long id;
    private long userId;
    private String name;
    private String content;
    private int state;
    private int count;
    private Date time;
    private boolean isV2;
    private String cardTitle;

    public Sign(JSONObject object) {
        if(object!=null) {
            this.id = object.optLong("id", 0);
            this.userId=object.optLong("user_id",0);
            if (!object.isNull("created_at")) {
                this.time = JSONUtil.getDate(object, "created_at");
            }
            this.content = JSONUtil.getString(object, "wish_language");
            this.name = JSONUtil.getString(object, "name");
            this.state = object.optInt("state", 0);
            this.count = object.optInt("count", 0);
            if(object.optJSONObject("card")!=null){
                cardTitle=JSONUtil.getString(object.optJSONObject("card"),"title");
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getContent() {
        return content;
    }


    public Date getTime() {
        return time;
    }

    public int getState() {
        return state;
    }

    public int getCount() {
        return count;
    }

    public boolean isV2() {
        return isV2;
    }

    public void setV2(boolean v2) {
        isV2 = v2;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public long getUserId() {
        return userId;
    }
}