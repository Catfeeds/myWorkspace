package com.hunliji.hljhttplibrary.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/7/17
 * 婚礼纪Http接口返回的真正有用的数据结构
 * 带day_limit的list
 */

public class HljHttpDayLimitData<T> extends HljHttpData<T> {
    @SerializedName(value = "day_limit")
    int dayLimit;

    public int getDayLimit() {
        return dayLimit;
    }
}
