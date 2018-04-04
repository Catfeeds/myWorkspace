package com.hunliji.hljcommonlibrary.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/8/9.
 */
public class PostIdBody {
    @SerializedName("id")
    private long id;

    public PostIdBody(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}