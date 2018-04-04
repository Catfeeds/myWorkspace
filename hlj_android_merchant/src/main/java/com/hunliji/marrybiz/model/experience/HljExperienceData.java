package com.hunliji.marrybiz.model.experience;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

/**
 * Created by mo_yu on 2017/12/19.体验店推荐订单列表数据
 */

public class HljExperienceData<T> extends HljHttpData<T> {
    @SerializedName("come_count")
    private int comeCount;//已到店数量
    @SerializedName("formed_count")
    private int formedCount;//已成单数量
    @SerializedName("read_count")
    private int readCount;//已查看数量
    @SerializedName("unread_count")
    private int unreadCount;//未查看数量

    public int getComeCount() {
        return comeCount;
    }

    public int getFormedCount() {
        return formedCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }
}
