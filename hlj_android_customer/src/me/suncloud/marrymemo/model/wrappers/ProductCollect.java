package me.suncloud.marrymemo.model.wrappers;

import com.google.gson.annotations.SerializedName;

/**
 * 婚品收藏临时对象
 * Created by wangtao on 2016/11/16.
 */

public class ProductCollect {

    //请求参数
    @SerializedName("id")
    private long postId;
    //请求结果
    @SerializedName("action")
    private String resultAction;

    public ProductCollect(long id) {
        this.postId = id;
    }

    public String getAction() {
        return resultAction;
    }
}
