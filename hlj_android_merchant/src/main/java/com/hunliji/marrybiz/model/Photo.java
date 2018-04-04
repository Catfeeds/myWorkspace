/**
 *
 */
package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;


/**
 * @author iDay
 */
public class Photo implements Identifiable {

    /**
     *
     */
    private static final long serialVersionUID = -1629152624526799335L;

    private long id;
    private String path;
    private String netPath;
    private int width;
    private int height;
    private String description;
    private long bucketId;

    public Photo(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.path = JSONUtil.getString(jsonObject, "path");
            if (JSONUtil.isEmpty(path)) {
                this.path = JSONUtil.getString(jsonObject, "photo_path");
            }
            if (JSONUtil.isEmpty(path)) {
                this.path = JSONUtil.getString(jsonObject, "img");
            }
            if (JSONUtil.isEmpty(path)) {
                this.path = JSONUtil.getString(jsonObject, "url");
            }
            if (JSONUtil.isEmpty(path)) {
                this.path = JSONUtil.getString(jsonObject, "image_path");
            }
            if (!JSONUtil.isEmpty(path) && !path.startsWith("http")) {
                path = "http://" + path;
            }
            this.width = jsonObject.optInt("width", 0);
            this.height = jsonObject.optInt("height", 0);
            this.id = jsonObject.optLong("id", 0);
            //            this.kind = jsonObject.optInt("kind", 2);
            this.description = JSONUtil.getString(jsonObject, "description");
            if (JSONUtil.isEmpty(description)) {
                this.description = JSONUtil.getString(jsonObject, "describe");
            }
        }
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the path
     */
    public String getImagePath() {
        return JSONUtil.isEmpty(path) ? "" : path;
    }

    /**
     * @param path the path to set
     */
    public void setImagePath(String path) {
        this.path = path;
    }

    public String getNetImagePath() {
        return JSONUtil.isEmpty(netPath) ? "" : netPath;
    }

    public void setNetImagePath(String netPath) {
        this.netPath = netPath;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    public String getDescription() {
        return JSONUtil.isEmpty(description) ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public long getBucketId() {
        return bucketId;
    }
}
