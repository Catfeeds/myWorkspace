package me.suncloud.marrymemo.model.community;

import com.google.gson.annotations.SerializedName;

/**
 * 结婚宝典
 * Created by chen_bin on 2018/3/15 0015.
 */
public class WeddingBible {
    @SerializedName(value = "id")
    private long id;
    @SerializedName(value = "community_thread_id")
    private long threadId;
    @SerializedName(value = "title")
    private String title;

    public long getId() {
        return id;
    }

    public long getThreadId() {
        return threadId;
    }

    public String getTitle() {
        return title;
    }
}
