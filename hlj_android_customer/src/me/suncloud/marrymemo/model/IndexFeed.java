package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by mo_yu on 2016/5/18.每一个小组下的话题数据
 */
public class IndexFeed implements Identifiable{


    /**
     * id : 1300
     * title : 测试照片//帖子名称
     * hidden : false
     * tag_id : 3
     * tag_desc : null
     * post_count : 4//帖子回复数
     * author_id : 410594
     * have_pics : true
     * good_title : null
     * praised_sum : 1//点赞数据
     * is_praised : 0// 当前用户是否点赞  1表示已点赞 0表示未点赞
     * author_nick : 你猜
     */

    private Long id;
    private String title;
    private boolean hidden;
    private Long tag_id;
    private Object tag_desc;
    private int post_count;
    private Long author_id;
    private boolean have_pics;
    private Object good_title;
    private int praised_sum;
    private boolean is_praised;
    private String author_nick;
    private Post post;
    private CommunityChannel community_channel;
    private CommunityGroup community_group;

    public IndexFeed(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            tag_id = jsonObject.optLong("tag_id", 0);
            author_id = jsonObject.optLong("author_id", 0);
            praised_sum = jsonObject.optInt("praised_sum", 0);
            post_count = jsonObject.optInt("post_count", 0);
            title= JSONUtil.getString(jsonObject,"title");
            author_nick= JSONUtil.getString(jsonObject,"author_nick");
            hidden = jsonObject.optBoolean("hidden",false);
            if (!hidden){
                hidden = jsonObject.optInt("hidden",0)>0;
            }
            have_pics = jsonObject.optBoolean("have_pics",false);
            if (!have_pics){
                have_pics = jsonObject.optInt("have_pics",0)>0;
            }
            is_praised = jsonObject.optBoolean("is_praised",false);
            if (!is_praised){
                is_praised = jsonObject.optInt("is_praised",0)>0;
            }
            if (!jsonObject.isNull("post")){
                post = new Post(jsonObject.optJSONObject("post"));
            }
            if (!jsonObject.isNull("community_channel")){
                community_channel = new CommunityChannel(jsonObject.optJSONObject("community_channel"));
            }
            if(!jsonObject.isNull("community_group")){
                community_group = new CommunityGroup(jsonObject.optJSONObject("community_group"));
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Long getTag_id() {
        return tag_id;
    }

    public void setTag_id(Long tag_id) {
        this.tag_id = tag_id;
    }

    public Object getTag_desc() {
        return tag_desc;
    }

    public void setTag_desc(Object tag_desc) {
        this.tag_desc = tag_desc;
    }

    public int getPost_count() {
        return post_count;
    }

    public void setPost_count(int post_count) {
        this.post_count = post_count;
    }

    public Long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(Long author_id) {
        this.author_id = author_id;
    }

    public boolean isHave_pics() {
        return have_pics;
    }

    public void setHave_pics(boolean have_pics) {
        this.have_pics = have_pics;
    }

    public Object getGood_title() {
        return good_title;
    }

    public void setGood_title(Object good_title) {
        this.good_title = good_title;
    }

    public int getPraised_sum() {
        return praised_sum;
    }

    public void setPraised_sum(int praised_sum) {
        this.praised_sum = praised_sum;
    }

    public boolean is_praised() {
        return is_praised;
    }

    public void setIs_praised(boolean is_praised) {
        this.is_praised = is_praised;
    }

    public String getAuthor_nick() {
        return author_nick;
    }

    public void setAuthor_nick(String author_nick) {
        this.author_nick = author_nick;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public CommunityChannel getCommunity_channel() {
        return community_channel;
    }

    public void setCommunity_channel(CommunityChannel community_channel) {
        this.community_channel = community_channel;
    }

    public CommunityGroup getCommunity_group() {
        return community_group;
    }

    public void setCommunity_group(CommunityGroup community_group) {
        this.community_group = community_group;
    }
}
