package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;


/**
 * Created by Suncloud on 2014/11/13.
 */
public class Place {

    private long id;
    private long parentId;
    private String name;
    private String type;

    public Place(JSONObject jsonObject){
        if(jsonObject!=null){
            id=jsonObject.optLong("id",0);
            parentId=jsonObject.optLong("parent_id",0);
            name= JSONUtil.getString(jsonObject, "name");
            type= JSONUtil.getString(jsonObject,"type");
        }
    }

    public long getId() {
        return id;
    }

    public long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
