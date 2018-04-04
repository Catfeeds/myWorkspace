package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

public class Item implements Identifiable {

    /**
     *
     */
    private static final long serialVersionUID = -7601967341566018286L;

    private long id;
    private long parentId;
    private long extraId;
    private String persistentId;
    private String mediaPath;
    private int kind;
    private int width;
    private int hight;
    private String description;
    private int collectCount;
    private int commentCount;
    private long position;
    private boolean isDetele;
    private boolean collected;
    private boolean hasPlace;
    private double longitude;
    private double latitude;
    private boolean remove;
    private String place;
    private int type;
    private Persistent persistent;

    public Item(JSONObject json) {
        if (json.isNull("item_id")) {
            this.id = json.optLong("id");
        } else {
            this.id = json.optLong("item_id");
        }
        this.parentId = json.optLong("parent_id");
        this.mediaPath = JSONUtil.isEmpty(JSONUtil.getString(json,
                "media_path")) ? JSONUtil.getString(json, "path") : JSONUtil.getString(json,
                "media_path");
        this.kind = json.optInt("kind", 0);
        this.width = json.optInt("width", 0);
        this.hight = json.optInt("height", 0);
        this.collectCount = json.optInt("collect_count", 0);
        this.commentCount = json.optInt("comment_count", 0);
        this.position = json.optLong("position", 0);
        this.description = JSONUtil.getString(json, "describe");
        this.longitude = json.optDouble("longitude", 0);
        this.latitude = json.optDouble("latitude", 0);
        this.place = JSONUtil.getString(json, "place");
        this.collected = json.optInt("collected", 0) == 1 ? true : false;
        if (!collected) {
            this.collected = json.optBoolean("collected", false);
        }
        this.type = json.optInt("team", 0) + 1;
        this.remove = json.optInt("removed", 0) == 1 ? true : false;
        this.hasPlace = json.optInt("has_place", 0) == 0 ? false : true;
        if (!json.isNull("persistent_path")) {
            persistent = new Persistent(json.optJSONObject("persistent_path"));
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String path) {
        this.mediaPath = path;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHight() {
        return hight;
    }

    public void setHight(int hight) {
        this.hight = hight;
    }

    public String getDescription() {
        return description != null ? description.trim() : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean hasPlace() {
        return hasPlace;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setExtraId(long extraId) {
        this.extraId = extraId;
    }

    public long getExtraId() {
        return extraId;
    }

    public boolean isDetele() {
        return isDetele;
    }

    public void detele(boolean detele) {
        this.isDetele = detele;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isRemove() {
        return remove;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public Persistent getPersistent() {
        return persistent;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public void setHasPlace(boolean hasPlace) {
        this.hasPlace = hasPlace;
    }

}
