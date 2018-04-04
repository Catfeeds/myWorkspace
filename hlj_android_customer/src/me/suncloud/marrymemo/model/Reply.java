/**
 *
 */
package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * @author iDay
 */
public class Reply implements Identifiable {

    /**
     *
     */
    private static final long serialVersionUID = 7618347833021793951L;
    private long id;
    private String content;
    private User user;
    private Date time;
    private String quote;
    private User quoteUser;

    /**
     *
     */
    public Reply(JSONObject object) {
        this.id = object.optLong("id");
        if (!object.isNull("time")) {
            this.time = JSONUtil.getDate(object, "time");
        }
        if (!object.isNull("created_at")) {
            this.time = JSONUtil.getDate(object, "created_at");
        }
        this.content = JSONUtil.getString(object, "content");
        if (!object.isNull("body")) {
            this.content = JSONUtil.getString(object, "body");
        }
        this.quote = JSONUtil.getString(object, "quote");
        JSONObject quoteJson = object.optJSONObject("quote");
        if (quoteJson != null) {
            quoteUser = new User(quoteJson);
        }
        this.user = object.isNull("user") ? null : new User(
                object.optJSONObject("user"));
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    public long getUserId() {
        return user == null ? 0 : user.getId();
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /*
     * (non-Javadoc)
     *
     * @see me.suncloud.marrymemo.model.Identifiable#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getQuote() {
        return quote;
    }

    public String getQuoteName() {
        return quoteUser != null ? quoteUser.getNick() : null;
    }
}
