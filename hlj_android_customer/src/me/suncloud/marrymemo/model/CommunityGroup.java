package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by mo_yu on 2016/5/18.来自哪里的小组
 */
public class CommunityGroup implements Identifiable{


    /**
     * id : 1
     * title : 婚筹居委会
     * closed : false
     * hidden : false
     * support_local : false
     */

    private Long id;
    private String title;
    private boolean closed;
    private boolean hidden;
    private boolean support_local;

    public CommunityGroup(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            title= JSONUtil.getString(jsonObject,"title");
            hidden = jsonObject.optBoolean("hidden",false);
            if (!hidden){
                hidden = jsonObject.optInt("hidden",0)>0;
            }
            closed = jsonObject.optBoolean("closed",false);
            if (!closed){
                closed = jsonObject.optInt("closed",0)>0;
            }
            support_local = jsonObject.optBoolean("support_local",false);
            if (!support_local){
                support_local = jsonObject.optInt("support_local",0)>0;
            }
        }
    }
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isSupport_local() {
        return support_local;
    }

    public void setSupport_local(boolean support_local) {
        this.support_local = support_local;
    }
}
