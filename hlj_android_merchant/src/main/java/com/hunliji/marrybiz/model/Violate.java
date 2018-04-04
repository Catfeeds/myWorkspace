package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Suncloud on 2016/1/8.
 */
public class Violate implements Identifiable {

    private String info;
    private Date time;

    public Violate(JSONObject jsonObject){
        if(jsonObject!=null){
            info= JSONUtil.getString(jsonObject,"type_name");
            time=JSONUtil.getDateFromFormatLong(jsonObject,"created_at",true);
        }
    }

    @Override
    public Long getId() {
        return null;
    }

    public Date getTime() {
        return time;
    }

    public String getInfo() {
        return info;
    }
}
