package me.suncloud.marrymemo.model.orders;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 16/10/18.
 * 服务订单中的快照model
 * 其中details字段中包含的数据不是确定的，老版本的订单返回的details就是原来的snapshort中的数据
 * 而新版本的话是新的套餐及子订单的快照数据，两者使用不通的解析方式，然后将老版本的details中的数据转换成新版的快照details
 * 通过version区分新老details
 */

public class ServiceOrderSnapshort {
    long id;
    @SerializedName(value = "order_id")
    long orderId;
    @SerializedName(value = "order_no")
    String orderNo;
    @SerializedName(value = "is_valid")
    boolean isValid;
    JsonElement details;

}
