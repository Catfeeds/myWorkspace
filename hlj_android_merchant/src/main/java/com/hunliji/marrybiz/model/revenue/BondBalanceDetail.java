package com.hunliji.marrybiz.model.revenue;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by werther on 6/21/16.
 */
public class BondBalanceDetail extends RevenueDetail {
    private double balance;

    public BondBalanceDetail(JSONObject jsonObject) {
        this.id = jsonObject.optLong("id", 0);
        this.amount = jsonObject.optDouble("value", 0);
        this.dateTime = JSONUtil.getDateTime(jsonObject, "created_at");
        this.entityId = jsonObject.optLong("entity_id", 0);
        this.memo = JSONUtil.getString(jsonObject, "type_message");
        this.title = JSONUtil.getString(jsonObject, "message");
        this.type = jsonObject.optInt("type", 0);
        this.balance = jsonObject.optDouble("balance", 0);
    }

    @Override
    /**
     * 1：用户付款重置 2: 钱包余额充值 3：退款扣款
     */
    public int getType() {
        return super.getType();
    }

    public double getBalance() {
        return balance;
    }
}
