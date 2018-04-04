package com.hunliji.marrybiz.model.revenue;

import android.text.TextUtils;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by hua_rong on 2017/10/19
 * 流水明细
 */

public class WaterDetail extends RevenueDetail {

    public WaterDetail(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            amount = jsonObject.optDouble("value", 0);
            dateTime = JSONUtil.getDateTime(jsonObject, "created_at");
            type = jsonObject.optInt("type", 0);
            // 订单的信息
            if (jsonObject.optJSONObject("extra_info") != null) {
                entityId = jsonObject.optJSONObject("extra_info")
                        .optLong("id", 0);
                orderNo = jsonObject.optJSONObject("extra_info")
                        .optString("order_no");
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
