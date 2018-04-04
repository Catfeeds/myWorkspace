package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2014/11/13.
 */
public class Place {

    private long id;
    private long parentId;
    private String name;
    private String type;

    public Place(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            parentId = jsonObject.optLong("parent_id", 0);
            name = JSONUtil.getString(jsonObject, "name");
            type = JSONUtil.getString(jsonObject, "type");
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
