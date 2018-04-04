package com.hunliji.hljhttplibrary.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/7/20.
 * 婚礼纪http返回的result,对应http结构里的body
 */
public class HljHttpResult<T> {
    HljHttpStatus status;
    T data;
    @SerializedName("current_time")
    long currentTime;

    public HljHttpStatus getStatus() {
        return status;
    }

    public void setStatus(HljHttpStatus status) {
        this.status = status;
    }

    /**
     * Data数据就是接口返回的数据节点,每个接口对应不同的结构,但主要就是有几种
     * 一种是HljHttpData中定义的含有list节点和分页等信息的列表结构
     * 一种是model实体本身的JsonObject
     *
     * @return
     */
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getCurrentTime() {
        return currentTime;
    }
}
