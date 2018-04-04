package com.hunliji.hljchatlibrary.models;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;
import com.hunliji.hljcommonlibrary.models.realm.WSChatAuthor;

import java.util.Date;

/**
 * Created by Suncloud on 2016/10/18.
 */

public class Channel {

    @SerializedName("_id")
    private String channelId;
    @SerializedName("user")
    private WSChatAuthor user;
    @SerializedName("merchant")
    private WSChatAuthor merchant;
    @SerializedName("support")
    private WSChatAuthor support;
    @SerializedName("last_message")
    private WSChat lastMessage;
    @SerializedName("stick_at")
    private long stickAt;


    public WSChat getChat(long userId) {
        if (lastMessage == null) {
            return null;
        }
        WSChatAuthor speaker = lastMessage.getSpeaker();
        if (speaker == null) {
            return null;
        }
        WSChatAuthor speakerTo = null;
        if (user != null && speaker.getId() == user.getId()) {
            speaker=user;
            speakerTo = merchant != null ? merchant : support;
        } else if (merchant != null && speaker.getId() == merchant.getId()) {
            speaker=merchant;
            speakerTo = user != null ? user : support;
        } else if (support != null && speaker.getId() == support.getId()) {
            speaker=support;
            speakerTo = user != null ? user : merchant;
        }
        if(speakerTo==null){
            return null;
        }
        lastMessage.setChannel(channelId);
        lastMessage.setSpeaker(speaker);
        lastMessage.setSpeakerTo(speakerTo);
        lastMessage.setFromId(speaker.getId());
        lastMessage.setToId(speakerTo.getId());
        lastMessage.onRealmChange(userId);
        return lastMessage;
    }

    public Date getStickAt() {
        if(stickAt==0){
            return null;
        }
        return new Date(stickAt*1000);
    }

    public String getChannelId() {
        return channelId;
    }
}
