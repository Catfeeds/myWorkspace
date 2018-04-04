package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by luohanlin on 15/3/27.
 */
public class TagGroup implements Identifiable{
    private static final long serialVersionUID = 3414005773903863534L;

    private long id;
    private String name;
    private ArrayList<Tag> tags;

    public TagGroup(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.name = JSONUtil.getString(jsonObject, "name");
            JSONArray jsonArray = jsonObject.optJSONArray("marks");
            this.tags = new ArrayList<>();
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Tag tag = new Tag(jsonArray.optJSONObject(i));
                    tags.add(tag);
                }
            }
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    @Override
    public Long getId() {
        return id;
    }
}
