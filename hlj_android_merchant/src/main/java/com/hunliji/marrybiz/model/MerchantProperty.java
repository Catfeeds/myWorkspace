package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by werther on 16/3/15.
 * 商家经营范围实体类
 */
public class MerchantProperty implements Identifiable {

    private long id;
    private String name;
    private String desc; // 额外的描述
    private List<MerchantProperty> children;

    public MerchantProperty(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.name = JSONUtil.getString(jsonObject, "name");
            this.desc = JSONUtil.getString(jsonObject, "desc");
            children = new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("children");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    MerchantProperty property = new MerchantProperty(jsonArray.optJSONObject(i));
                    children.add(property);
                }
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

    public List<MerchantProperty> getChildren() {
        return children;
    }

    public String getDesc() {
        return desc;
    }
}
