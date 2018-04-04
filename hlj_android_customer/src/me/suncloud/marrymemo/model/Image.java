package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2014/11/4.
 */
public class Image implements Identifiable {

    private long id;
    private int width;
    private int height;
    private String describe;
    private String imagePath;

    public Image(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            width = json.optInt("width", 0);
            height = json.optInt("height", 0);
            imagePath = JSONUtil.getString(json, "image_path");
            if (JSONUtil.isEmpty(imagePath)) {
                imagePath = JSONUtil.getString(json, "img");
            }
            if (JSONUtil.isEmpty(imagePath)) {
                imagePath = JSONUtil.getString(json, "path");
            }
            describe = JSONUtil.getString(json, "describe");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescribe() {
        return describe;
    }
}
