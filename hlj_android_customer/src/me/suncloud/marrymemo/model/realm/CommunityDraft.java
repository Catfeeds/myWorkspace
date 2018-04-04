package me.suncloud.marrymemo.model.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by werther on 16/5/24.
 * 用于存储社区模块中,话题的草稿
 * 同时也是数据库的schema
 */

public class CommunityDraft extends RealmObject {
    private String id;
    private long userId;
    private long channelId;
    private long groupId;
    private int type; // 草稿类型: 1: 主页所属话题草稿, 2:小组所属话题草稿(已删除), 3:频道所属话题草稿
    private Date createdAt;
    private Date updatedAt;
    private String channelTitle;
    private String title;
    private String content;
    private RealmList<RealmJsonPic> pics;

    public transient final static int TYPE_OTHERS = 1; //不带频道的话题草稿
    public transient final static int TYPE_CHANNEL = 3; //频道所属话题草稿

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public RealmList<RealmJsonPic> getPics() {
        return pics;
    }

    public void setPics(RealmList<RealmJsonPic> pics) {
        this.pics = pics;
    }
}
