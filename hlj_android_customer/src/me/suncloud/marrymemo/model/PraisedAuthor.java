package me.suncloud.marrymemo.model;


import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;


/**
 * Created by mo_yu on 2016/7/28. 商家动态点赞用户model
 */
public class PraisedAuthor implements Identifiable {

    private long id;
    private long userId;
    private Date createdAt;
    private User author;

    public PraisedAuthor(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            userId = json.optLong("user_id", 0);
            createdAt = JSONUtil.getDate(json, "created_at");
            if (!json.isNull( "user" )){
                author = new User(json.optJSONObject( "user" ));
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public User getAuthor() {
        return author;
    }

}
