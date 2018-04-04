package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by luohanlin on 15/4/16.
 */
public class ThreadOrStory implements Identifiable {
    private static final long serialVersionUID = -2858876582033846706L;

    private long id;
    private int type;
    private long tagId;
    private String tagName;
    private String tagKind;
    private String goodTitle;
    private String desc;
    private ArrayList<String> pics;
    private String userNick;
    private int collectCount;
    private int commentCount;
    private boolean hidden;

    public ThreadOrStory(JSONObject json) {
        if (json != null) {
            String typeStr = JSONUtil.getString(json,"entity_type");
            if(JSONUtil.isEmpty(typeStr)){
                return;
            }
            switch (typeStr){
                case "CommunityThread":
                    type=0;
                    break;
                case "Story":
                    type=1;
                    break;
                default:
                    return;
            }
            JSONObject jsonObject=json.optJSONObject("entity");
            this.goodTitle = JSONUtil.getString(json, "good_title");
            if(jsonObject!=null) {
                this.id = jsonObject.optLong("id", 0);
                JSONObject tagObj = jsonObject.optJSONObject("tag");

                if (tagObj != null) {
                    this.tagId = tagObj.optLong("id", 0);
                    this.tagKind = JSONUtil.getString(tagObj, "kind");
                    this.tagName = JSONUtil.getString(tagObj, "name");
                }
                if (type == 0) {
                    // 这是个话题
                    this.desc = JSONUtil.getString(jsonObject, "description");
                    if (JSONUtil.isEmpty(goodTitle)) {
                        // 如果没有goodTitle则使用title替代
                        this.goodTitle = JSONUtil.getString(jsonObject, "title");
                    }
                    this.collectCount = jsonObject.optInt("praised_sum", 0);
                    this.commentCount = jsonObject.optInt("post_count", 0);
                    this.userNick = JSONUtil.getString(jsonObject, "author_nick");

                    JSONObject postObj = jsonObject.optJSONObject("post");
                    if (postObj != null) {
                        this.desc = JSONUtil.getString(postObj, "message");
                        JSONArray postItems = postObj.optJSONArray("media_items");
                        if (postItems != null && postItems.length() > 0) {
                            pics = new ArrayList<>();

                            for (int i = 0; i < postItems.length() && i < 3; i++) {
                                String pic = JSONUtil.getString(postItems.optJSONObject(i), "path");

                                if (!JSONUtil.isEmpty(pic)) {
                                    pics.add(pic);
                                }
                            }
                        } else pics = null;
                    }
                    this.hidden = jsonObject.optBoolean("hidden", false);
                } else {
                    // 这是个故事
                    // 故事没有good title，使用title替代
                    this.goodTitle = JSONUtil.getString(jsonObject, "title");
                    this.desc = JSONUtil.getString(jsonObject, "description");
                    JSONArray storyItems = jsonObject.optJSONArray("images");
                    if (storyItems != null && storyItems.length() > 0) {
                        pics = new ArrayList<>();

                        for (int i = 0; i < storyItems.length() && i < 3; i++) {
                            String pic = JSONUtil.getString(storyItems.optJSONObject(i), "media_path");

                            if (!JSONUtil.isEmpty(pic)) {
                                pics.add(pic);
                            }
                        }
                    } else pics = null;
                    JSONObject userObj = jsonObject.optJSONObject("user");
                    if (userObj != null) {
                        this.userNick = JSONUtil.getString(userObj, "nick");
                    }
                    this.collectCount = jsonObject.optInt("collects_count", 0);
                    this.commentCount = jsonObject.optInt("praises_count", 0);
                }
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getType() {
        return type;
    }

    public long getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagKind() {
        return tagKind;
    }

    public String getDesc() {
        return desc;
    }

    public ArrayList<String> getPics() {
        return pics;
    }

    public String getUserNick() {
        return userNick;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getGoodTitle() {
        return goodTitle;
    }

    public void setGoodTitle(String goodTitle) {
        this.goodTitle = goodTitle;
    }
}
