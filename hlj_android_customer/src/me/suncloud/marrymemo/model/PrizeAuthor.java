package me.suncloud.marrymemo.model;


import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;


/**
 * 活动中奖名单model
 * Created by chen_bin on 2016/8/10.
 */
public class PrizeAuthor implements Identifiable {

    private long id;
    private User author;

    public PrizeAuthor(JSONObject json) {
        if (json != null) {
            id = json.optLong("id", 0);
            if (!json.isNull("user")) {
                author = new User(json.optJSONObject("user"));
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

}
