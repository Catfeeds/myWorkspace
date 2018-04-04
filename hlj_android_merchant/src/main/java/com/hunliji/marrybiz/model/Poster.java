package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

public class Poster implements Identifiable {

    /**
     *
     */
    private static final long serialVersionUID = -2121846402321175031L;

    private long id;
    private long targetId;
    private String imagePath;
    private String title;
    private String url;
    private int targetType;

    public Poster(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.imagePath = JSONUtil.getString(jsonObject, "image_path");
            this.title = JSONUtil.getString(jsonObject, "title");
            this.url = JSONUtil.getString(jsonObject, "target_url");
            this.id = jsonObject.optLong("id");
            this.targetType = jsonObject.optInt("target_type");
            this.targetId = jsonObject.optLong("target_id", 0);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return imagePath;
    }

    public void setPath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTargetType() {
        return targetType;
    }

    public long getTargetId() {
        return targetId;
    }
}
