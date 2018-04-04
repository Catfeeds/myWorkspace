package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by luohanlin on 15/3/23.
 */
public class Tag implements Identifiable {

    private long id;
    private long userId;
    private boolean isRed;
    private String tagName;
    private String coverPath;
    private int worksCount;
    private int casesCount;
    private int threadsCount;
    private int iconType;
    private int markType;
    private boolean isHighLight;//是否高亮显示 true 高亮 false 不高亮
    private String highLightColor;

    public Tag(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.userId = jsonObject.optLong("user_id", 0);
            this.tagName = JSONUtil.getString(jsonObject, "name");
            this.isRed = jsonObject.optInt("is_red", 0) > 0;
            this.coverPath = JSONUtil.getString(jsonObject, "image_path");
            this.worksCount = jsonObject.optInt("packages_count", 0);
            this.casesCount = jsonObject.optInt("examples_count", 0);
            this.threadsCount = jsonObject.optInt("threads_count", 0);
            this.iconType = jsonObject.optInt("icon", 0);
            this.markType = jsonObject.optInt("marked_type",0);
            this.isHighLight = jsonObject.optInt("high_light",0)>0;
            this.highLightColor =jsonObject.optString("high_light_color");
        }
    }

    public boolean isRed() {
        return isRed;
    }

    public String getTagName() {
        return tagName;
    }

    public String getCoverPath() {
        return coverPath;
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getWorksCount() {
        return worksCount;
    }

    public int getCasesCount() {
        return casesCount;
    }

    public int getThreadsCount() {
        return threadsCount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * icon 标记,1表示new,2是hot
     *
     * @return
     */
    public int getIconType() {
        return iconType;
    }

    public int getMarkType() {
        return markType;
    }

    public boolean isHighLight() {
        return isHighLight;
    }

    public void setHighLight(boolean highLight) {
        isHighLight = highLight;
    }

    public String getHighLightColor() {
        return highLightColor;
    }

    public void setHighLightColor(String highLightColor) {
        this.highLightColor = highLightColor;
    }
}
