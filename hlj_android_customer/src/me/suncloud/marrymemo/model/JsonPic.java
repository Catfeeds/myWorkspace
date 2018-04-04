package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

public class JsonPic implements Identifiable {

    private long id;
    private String path;
    private int width;
    private int height;
    private int kind;

    public JsonPic(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.path = JSONUtil.getString(jsonObject, "path");
            this.width = jsonObject.optInt("width", 0);
            this.height = jsonObject.optInt("height", 0);
            this.kind = jsonObject.optInt("kind", 2);
        }
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public Long getId() {
        return id;
    }
}
