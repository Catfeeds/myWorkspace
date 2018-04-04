package com.hunliji.hljcommonlibrary.models.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 消息置顶记录
 * Created by wangtao on 2017/8/23.
 */

public class ChannelStick extends RealmObject {

    @PrimaryKey
    private String channel; // 私信：channel， 环信 客服名称
    private long userId;
    private Date stickAt;

    public ChannelStick(long userId,String channel, Date stickAt) {
        this.userId=userId;
        this.channel = channel;
        this.stickAt = stickAt;
    }

    public ChannelStick() {
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setStickAt(Date stickAt) {
        this.stickAt = stickAt;
    }

    public String getChannel() {

        return channel;
    }

    public Date getStickAt() {
        return stickAt;
    }
}
