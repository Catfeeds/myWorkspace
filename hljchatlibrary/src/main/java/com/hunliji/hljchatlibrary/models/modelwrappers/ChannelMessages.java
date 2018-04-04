package com.hunliji.hljchatlibrary.models.modelwrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;

import java.util.List;

/**
 * Created by Suncloud on 2016/10/20.
 */

public class ChannelMessages {

    @SerializedName("messages")
    private List<WSChat> messages;

    public List<WSChat> getMessages() {
        return messages;
    }

    public void setMessages(List<WSChat> messages) {
        this.messages = messages;
    }
}
