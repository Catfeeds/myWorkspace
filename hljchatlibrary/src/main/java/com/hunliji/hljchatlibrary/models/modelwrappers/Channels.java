package com.hunliji.hljchatlibrary.models.modelwrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljchatlibrary.models.Channel;

import java.util.List;

/**
 * Created by Suncloud on 2016/10/18.
 */

public class Channels {
    @SerializedName("channels")
    private List<Channel> channels;

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }
}
