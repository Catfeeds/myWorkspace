package com.hunliji.hljlivelibrary.models.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljlivelibrary.models.LiveMessage;

import java.util.List;

/**
 * Created by Suncloud on 2016/10/31.
 */

public class LiveSocketMessage {
    @SerializedName("is_history")
    private boolean isHistory;
    @SerializedName("live_list")
    private List<LiveMessage> liveMessages;
    @SerializedName("chat_room_list")
    private List<LiveMessage> chatMessages;

    public List<LiveMessage> getChatMessages() {
        return chatMessages;
    }

    public List<LiveMessage> getLiveMessages() {
        return liveMessages;
    }

    public boolean isHistory() {
        return isHistory;
    }
}
