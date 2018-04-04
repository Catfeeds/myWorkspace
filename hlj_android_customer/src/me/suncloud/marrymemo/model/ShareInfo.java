package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.io.Serializable;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/8/12.
 */
public class ShareInfo implements Serializable {

    private String title;
    private String desc;
    private String desc2;
    private String url;
    private String icon;

    public ShareInfo(JSONObject jsonObject){
        if(jsonObject!=null){
            title= JSONUtil.getString(jsonObject,"title");
            desc= JSONUtil.getString(jsonObject,"desc");
            desc2= JSONUtil.getString(jsonObject,"desc2");
            url= JSONUtil.getString(jsonObject,"url");
            icon= JSONUtil.getString(jsonObject,"icon");
        }
    }

    public ShareInfo() {
    }

    public String getDesc() {
        return desc;
    }

    public String getDesc2() {
        return desc2;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
