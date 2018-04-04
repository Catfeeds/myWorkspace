package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by werther on 15/8/10.
 */
public class AddressArea implements Identifiable {
    private static final long serialVersionUID = 4339681779185940602L;
    private long id;
    private long cid;
    private String areaName;
    private int level;
    private long parentId;
    private ArrayList<AddressArea> children;
    private ArrayList<String> childrenString;

    public AddressArea(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.cid = jsonObject.optLong("cid");
            this.parentId = jsonObject.optLong("parent_id", 0);
            this.level = jsonObject.optInt("level", 0);
            this.areaName = JSONUtil.getString(jsonObject, "area_name");
            this.children = new ArrayList<>();
            this.childrenString=new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("children");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    AddressArea area = new AddressArea(jsonArray.optJSONObject(i));
                    if(area.getLevel()!=2||!area.getChildren().isEmpty()){
                        children.add(area);
                        childrenString.add(area.getAreaName());
                    }
                }
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getCid() {
        return cid;
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
        if(children==null){
            children=new ArrayList<>();
        }
        return children;
    }

    public ArrayList<String> getChildrenNameList() {
        return childrenString;
    }
}
