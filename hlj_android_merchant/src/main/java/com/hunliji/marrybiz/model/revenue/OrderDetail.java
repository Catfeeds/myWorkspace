package com.hunliji.marrybiz.model.revenue;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by hua_rong on 2017/10/19
 * 订单明细
 */

public class OrderDetail extends RevenueDetail {


    private boolean isInvalid;//是否无效单 true是
    private boolean isOfflinePay;//是否线下付尾款 1是
    private int status;
    private static final int REFUND_TAG = 24; //24 退款标识

    public OrderDetail(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            title = JSONUtil.getString(jsonObject, "order_no");
            amount = jsonObject.optDouble("in_wallet", 0);//已收入
            dateTime = JSONUtil.getDateTime(jsonObject, "created_at");
            isInvalid = jsonObject.optBoolean("is_invalid", false);
            isOfflinePay = jsonObject.optInt("is_offline_pay", 0) > 0;
            status = jsonObject.optInt("status", 0);
        }
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public boolean isOfflinePay() {
        return isOfflinePay;
    }

    public boolean isRefundTag() {
        return status == REFUND_TAG;
    }


}
