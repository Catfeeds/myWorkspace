package com.hunliji.hljlivelibrary.models.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;

/**
 * Created by Suncloud on 2016/10/31.
 */

public class LiveSocketObject {

    @SerializedName("msg_type")
    private int type;
    @SerializedName("status")
    private HljHttpStatus status;
    @SerializedName("data")
    private LiveSocketData data;
    @SerializedName("client_message_id")
    private long clientMessageId;
    @SerializedName("message_id")
    private long messageId;

    public HljHttpStatus getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }

    public LiveSocketData getData() {
        return data;
    }

    public long getClientMessageId() {
        return clientMessageId;
    }

    public long getMessageId() {
        return messageId;
    }
}
