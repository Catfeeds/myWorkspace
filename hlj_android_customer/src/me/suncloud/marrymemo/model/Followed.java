package me.suncloud.marrymemo.model;

import org.json.JSONObject;

/**
 * Created by LuoHanLin on 15/1/4.
 */
public class Followed implements Identifiable {
    private static final long serialVersionUID = 2032735375443888980L;
    private long id;
    private String nick;
    private String avatarPath;
    private long threadId;
    private String threadTitle;
    private boolean isHidden;
    private boolean havePics;
    private boolean isRefined;
    private boolean isFollowing;

    public Followed(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.nick = jsonObject.optString("nick");
            this.avatarPath = jsonObject.optString("avatar");
            this.isFollowing = true;
            JSONObject jsonObject1 = jsonObject.optJSONObject("thread");
            if (jsonObject1 != null) {
                this.threadId = jsonObject1.optLong("id", 0);
                this.threadTitle = jsonObject1.optString("title");
                this.isHidden = jsonObject1.optBoolean("hidden", true);
                this.havePics = jsonObject1.optBoolean("have_pics", false);
                this.isRefined = jsonObject1.optBoolean("is_refined", false);
            }
        }
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getThreadTitle() {
        return threadTitle;
    }

    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isHavePics() {
        return havePics;
    }

    public void setHavePics(boolean havePics) {
        this.havePics = havePics;
    }

    public boolean isRefined() {
        return isRefined;
    }

    public void setRefined(boolean isRefined) {
        this.isRefined = isRefined;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
