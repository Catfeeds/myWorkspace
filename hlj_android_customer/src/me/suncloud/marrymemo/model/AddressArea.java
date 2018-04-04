package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 15/8/10.
 */
public class AddressArea implements Identifiable {
    private static final long serialVersionUID = 4339681779185940602L;
    private long id;
    private String areaName;
    private int level;
    private long parentId;
    private ArrayList<AddressArea> children;

    public AddressArea(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.parentId = jsonObject.optLong("parent_id", 0);
            this.level = jsonObject.optInt("level", 0);
            this.areaName = JSONUtil.getString(jsonObject, "area_name");
            this.children = new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("children");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    AddressArea area = new AddressArea(jsonArray.optJSONObject(i));
                    children.add(area);
                }
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getAreaName() {
        return areaName;
    }

    public int getLevel() {
        return level;
    }

    public long getParentId() {
        return parentId;
    }

    public ArrayList<AddressArea> getChildren() {
        return children;
    }
}
