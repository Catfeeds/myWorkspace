package me.suncloud.marrymemo.model.wrappers;

import com.google.gson.annotations.SerializedName;

/**
 * 添加婚品购物车对象
 * Created by wangtao on 2016/11/16.
 */

public class ProductCartBody {

    //请求结果
    @SerializedName("id")
    private long id;

    //请求参数
    @SerializedName("sku_id")
    private long skuId;
    @SerializedName("product_id")
    private long productId;
    @SerializedName("quantity")
    private int quantity;

    public ProductCartBody(long skuId, long productId, int quantity) {
        this.skuId = skuId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }
}
