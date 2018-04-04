package com.hunliji.hljcommonlibrary.models.realm;

import io.realm.RealmObject;

/**
 * Created by luohanlin on 2017/9/25.
 * HTTP请求日志
 */

public class HttpLogBlock extends RealmObject {
    private long id;
    private String message;

    public HttpLogBlock() {
    }

    public HttpLogBlock(String message) {
        id = System.currentTimeMillis();
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
