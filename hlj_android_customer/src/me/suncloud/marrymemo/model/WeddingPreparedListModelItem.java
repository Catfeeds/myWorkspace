package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kingsun on 15/12/16.
 */
public class WeddingPreparedListModelItem implements Identifiable {
    private long id;
    private String title;
    private int type;
    private String goodTitle;
    private String imgHead;
    private String imgList;
    private int categoryId;
    private int watchCount;
    private List<JsonPic> content;
    private int mediaItemCount;
    private int isNew;
    private String link;
    private long entityId;

    public WeddingPreparedListModelItem(JSONObject object) {
        if (object != null) {
            this.id = object.optLong("id");
            this.title = object.optString("title");
            this.type = object.optInt("type");
            this.categoryId = object.optInt("category_id");
            this.watchCount = object.optInt("watch_count");
            this.goodTitle = object.optString("good_title");
            this.imgHead = object.optString("img_head");
            this.imgList = object.optString("img_list");
            this.isNew = object.optInt("is_new");
            link = object.optString("link");
            this.content = new ArrayList<>();
            if (type != 1) {
                JSONArray contents = object.optJSONArray("content");
                if (contents != null) {
                    JSONObject contentObj = contents.optJSONObject(0);
                    if (contentObj != null) {
                        this.entityId = contentObj.optLong("entity_id");
                        JSONObject data = contentObj.optJSONObject("data");
                        if (data != null) {
                            JSONObject post = data.optJSONObject("post");
                            if (post != null) {
                                this.mediaItemCount = post.optInt("media_items_count");
                                JSONArray items = post.optJSONArray("media_items");
                                if (items != null) {
                                    for (int i = 0; i < items.length(); i++) {
                                        JSONObject item = items.optJSONObject(i);
                                        this.content.add(new JsonPic(item));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String getImgHead() {
        return imgHead;
    }

    public void setImgHead(String imgHead) {
        this.imgHead = imgHead;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGoodTitle() {
        return goodTitle;
    }

    public void setGoodTitle(String goodTitle) {
        this.goodTitle = goodTitle;
    }

    public String getImgList() {
        return imgList;
    }

    public void setImgList(String imgList) {
        this.imgList = imgList;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }

    public void setContent(List<JsonPic> content) {
        this.content = content;
    }

    public List<JsonPic> getContent() {
        return content;
    }


    public int getMediaItemCount() {
        return mediaItemCount;
    }

    public void setMediaItemCount(int mediaItemCount) {
        this.mediaItemCount = mediaItemCount;
    }

    public boolean getIsNew() {
        return isNew == 1;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }
}
