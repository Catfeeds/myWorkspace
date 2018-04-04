package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 16/3/15.
 * 商家经营范围实体类
 */
public class MerchantProperty extends Label {

    private List<MerchantProperty> children;
    private String icon;
    private long markId;

    public MerchantProperty(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.name = JSONUtil.getString(jsonObject, "name");
            this.desc = JSONUtil.getString(jsonObject, "desc");
            this.icon = JSONUtil.getString(jsonObject, "icon");
            markId = jsonObject.optLong("mark_id",0);
            children = new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("children");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    MerchantProperty property = new MerchantProperty
                            (jsonArray.optJSONObject(
                            i));
                    children.add(property);
                }
            }
        }
    }

    public MerchantProperty() {

    }

    public long getMarkId() {
        return markId;
    }

    public void setMarkId(long markId) {
        this.markId = markId;
    }

    public List<MerchantProperty> getChildren() {
        return children;
    }

    public void setChildren(List<MerchantProperty> children) {
        this.children = children;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof MerchantProperty) {
            MerchantProperty p = (MerchantProperty) o;
            if (p == null) {
                return super.equals(o);
            } else {
                return this.id == p.getId();
            }
        }
        return super.equals(o);
    }
}
