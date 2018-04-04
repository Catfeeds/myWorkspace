package com.hunliji.marrybiz.model.revenue;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by werther on 16/3/17.
 */
public class WithdrawDetail extends RevenueDetail {


    public WithdrawDetail(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            orderNo = JSONUtil.getString(jsonObject, "trade_no");//提现编号
            amount = jsonObject.optDouble("pending_money", 0);//提现金额
            // 提现状态  当前状态 0 申请中，1 提现成功 2 提现失败
            int status = jsonObject.optInt("status", 0);
            switch (status) {
                case 0:
                    memo = "提现中";
                    title = "申请时间";
                    dateTime = JSONUtil.getDateTime(jsonObject, "created_at");
                    break;
                case 1:
                    memo = "提现成功";
                    title = "打款时间";
                    dateTime = JSONUtil.getDateTime(jsonObject, "updated_at");
                    break;
                case 2:
                    memo = "提现失败";
                    title = "申请时间";
                    dateTime = JSONUtil.getDateTime(jsonObject, "created_at");
                    break;
            }
        }

    }
}
