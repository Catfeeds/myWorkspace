package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by mo_yu on 2016/5/10.社区频道
 */
public class CommunityChannel implements Identifiable {

    private Long id;
    private String title;
    private Date created_at;
    private Date updated_at;
    private int fans_count;
    private int watch_count;
    private int threads_count;
    private int posts_count;
    private int recent_threads_count;
    private Long community_group_id;
    private Long mark_id;
    private int weight;
    private String cover_path;
    private String desc;
    private int hidden;
    private int deleted;
    private boolean is_followed;
    private boolean selected;
    private boolean isSameCity;
    private int type;

    public CommunityChannel(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.community_group_id = jsonObject.optLong("community_group_id", 0);
            this.mark_id = jsonObject.optLong("mark_id", 0);
            this.title = JSONUtil.getString(jsonObject, "title");
            this.cover_path = JSONUtil.getString(jsonObject, "cover_path");
            this.desc = JSONUtil.getString(jsonObject, "desc");
            this.created_at = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            this.updated_at = JSONUtil.getDateFromFormatLong(jsonObject, "updated_at", true);
            this.fans_count = jsonObject.optInt("fans_count", 0);
            this.watch_count = jsonObject.optInt("watch_count", 0);
            this.threads_count = jsonObject.optInt("threads_count", 0);
            this.posts_count = jsonObject.optInt("posts_count", 0);
            this.recent_threads_count = jsonObject.optInt("recent_threads_count", 0);
            this.weight = jsonObject.optInt("weight", 0);
            this.hidden = jsonObject.optInt("hidden", 0);
            this.deleted = jsonObject.optInt("deleted", 0);
            this.is_followed = jsonObject.optBoolean("is_followed", false);
            if (!is_followed) {
                is_followed = jsonObject.optInt("is_followed") > 0;
            }
            isSameCity = jsonObject.optBoolean( "is_same_city", false );
            if (!isSameCity) {
                isSameCity = jsonObject.optInt( "is_same_city", 0 ) > 0;
            }
            this.type = jsonObject.optInt("type",0);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public int getFans_count() {
        return fans_count;
    }

    public int getWatch_count() {
        return watch_count;
    }

    public int getThreads_count() {
        return threads_count;
    }

    public int getPosts_count() {
        return posts_count;
    }

    public int getRecent_threads_count() {
        return recent_threads_count;
    }

    public Long getCommunity_group_id() {
        return community_group_id;
    }

    public Long getMark_id() {
        return mark_id;
    }

    public int getWeight() {
        return weight;
    }

    public String getCover_path() {
        return cover_path;
    }

    public String getDesc() {
        return desc;
    }

    public int getHidden() {
        return hidden;
    }

    public int getDeleted() {
        return deleted;
    }

    public boolean is_followed() {
        return is_followed;
    }

    public void setIs_followed(boolean is_followed) {
        this.is_followed = is_followed;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSameCity() {
        return isSameCity;
    }

    public void setSameCity(boolean sameCity) {
        isSameCity = sameCity;
    }

    public int getType() {
        return type;
    }
}
