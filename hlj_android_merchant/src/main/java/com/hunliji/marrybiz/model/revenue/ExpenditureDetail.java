package com.hunliji.marrybiz.model.revenue;

import android.text.TextUtils;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by werther on 16/3/17.
 */
public class ExpenditureDetail extends RevenueDetail {


    public ExpenditureDetail(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            amount = jsonObject.optDouble("value", 0);
            amount = Math.abs(amount);
            dateTime = JSONUtil.getDateTime(jsonObject, "created_at");
            if (jsonObject.optJSONObject("extra_info") != null) {
                orderNo = JSONUtil.getString(jsonObject.optJSONObject("extra_info"), "order_no");
                entityId = jsonObject.optJSONObject("extra_info")
                        .optLong("id", 0);
                if (JSONUtil.isEmpty(orderNo)) {
                    orderNo = JSONUtil.getString(jsonObject.optJSONObject("extra_info"),
                            "trade_no");
                }
            }
            type = jsonObject.optInt("type", 0);
            // 订单的信息
            if (jsonObject.optJSONObject("extra_info") != null) {
                orderNo = JSONUtil.getString(jsonObject.optJSONObject("extra_info"), "order_no");
                entityId = jsonObject.optJSONObject("extra_info")
                        .optLong("id", 0);
            }
            String payStatus = JSONUtil.getString(jsonObject, "pay_status");
            if (!JSONUtil.isEmpty(payStatus)) {
                memo = payStatus;
            }
            title = JSONUtil.getString(jsonObject, "show_message");
            if (TextUtils.isEmpty(title)) {
                title = "订单退款";
            }
        }
    }
}
