package com.hunliji.marrybiz.model.potential_customer;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Author;
import com.hunliji.hljcommonlibrary.models.realm.WSChat;

/**
 * Created by wangtao on 2017/8/9.
 */

public class PotentialCustomer  {

    private long id;
    private int status;  //0未处理 1已处理 2超时未处理
    @SerializedName("last_msg")
    private WSChat lastMessage;

    private Author user;

    public long getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public WSChat getLastMessage() {
        return lastMessage;
    }

    public Author getUser() {
        return user;
    }

    public void setLastMessage(WSChat lastMessage) {
        this.lastMessage = lastMessage;
    }
}
