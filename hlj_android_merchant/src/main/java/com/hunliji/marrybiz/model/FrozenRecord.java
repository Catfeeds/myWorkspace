package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.DateTime;
import org.json.JSONObject;

/**
 * Created by werther on 16/3/11.
 * 待结算订单Entity
 */
public class FrozenRecord implements Identifiable {

    private static final long serialVersionUID = -7628533852237077538L;
    private long id; // 这个id不是这个记录的id,而是对应的order的id
    private int orderType; // 0:定制套餐, 1:普通套餐,
    private String orderNo; // 定制套餐是order_no, 普通套餐是parent_order_no
    private DateTime createdAt;
    private double frozenAmount;

    public FrozenRecord(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.orderType = jsonObject.optInt("order_type");
            orderNo = JSONUtil.getString(jsonObject, "order_no");
            createdAt = JSONUtil.getDateTime(jsonObject, "created_at");
            frozenAmount = jsonObject.optDouble("forzen_amount", 0);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getOrderType() {
        return orderType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public double getFrozenAmount() {
        return frozenAmount;
    }

    public long getDateVal() {
        String str = createdAt.getYear() + "" + createdAt.getMonthOfYear();
        long val = 0;
        try {
            val = Long.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;
    }
}
