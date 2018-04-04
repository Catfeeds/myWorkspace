package com.hunliji.marrybiz.model;


import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by Suncloud on 2014/11/4.
 */
public class Article {

    private long id;
    private String name;
    private String describe;
    private boolean isMustWrite;
    private ArrayList<Photo> images;

    public Article(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            name = JSONUtil.getString(json, "name");
            describe = JSONUtil.getString(json, "describe");
            isMustWrite = json.optBoolean("is_must_write", false);
            if (!json.isNull("images")) {
                JSONArray array = json.optJSONArray("images");
                int size = array.length();
                if (size > 0) {
                    images = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        Photo image = new Photo(array.optJSONObject(i));
                        if (!JSONUtil.isEmpty(image.getImagePath())) {
                            images.add(image);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Photo> getImages() {
        return images==null?new ArrayList<Photo>():images;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }
}
