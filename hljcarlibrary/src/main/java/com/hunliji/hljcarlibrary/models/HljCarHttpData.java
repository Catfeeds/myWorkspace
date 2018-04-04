package com.hunliji.hljcarlibrary.models;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.entities.HljHttpData;

/**
 * Created by mo_yu on 2018/1/3.
 * 婚礼纪婚车Http接口返回的真正有用的数据结构
 */

public class HljCarHttpData<T> extends HljHttpData<T>{
    @SerializedName(value = "count_down_time")
    long countDownTime;

    public long getCountDownTime() {
        return countDownTime;
    }
}
