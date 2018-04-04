package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by Suncloud on 2016/1/19.
 */
public class PrivilegeOption implements Identifiable {

    private long id;
    private String title;
    private String detail;
    private String imagePath;
    private int status;

    public PrivilegeOption(JSONObject jsonObject){
        if(jsonObject!=null){
            id=jsonObject.optLong("id");
            title= JSONUtil.getString(jsonObject,"main");
            detail= JSONUtil.getString(jsonObject,"detail");
            imagePath = JSONUtil.getString(jsonObject,"image_path");
            status=jsonObject.optInt("status");
        }
    }


    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
