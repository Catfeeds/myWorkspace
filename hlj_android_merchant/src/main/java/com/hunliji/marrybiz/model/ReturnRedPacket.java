package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by werther on 15/8/27.
 */
public class ReturnRedPacket implements Identifiable {

    private Date updatedAt;
    private String orderNo;
    private int status;
    private String statusName;
    private long reasonId;
    private String userNick;
    private int type;
    private int isPaid; // 0: 未退还, 1: 已退还
    private double money;

    public ReturnRedPacket(JSONObject jsonObject) {
        this.updatedAt = JSONUtil.getDate(jsonObject, "updated_at");
        this.orderNo = jsonObject.optString("order_no");
        this.status = jsonObject.optInt("status");
        this.statusName = jsonObject.optString("status_name");
        this.reasonId = jsonObject.optLong("reason_id");
        this.userNick = jsonObject.optString("user_nick");
        this.type = jsonObject.optInt("type");
        this.isPaid = jsonObject.optInt("is_paid", 0);
        this.money = jsonObject.optDouble("pay_money", 0);
    }

    @Override
    public Long getId() {
        return null;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusName() {
        return statusName;
    }

    public long getReasonId() {
        return reasonId;
    }

    public String getUserNick() {
        return userNick;
    }

    public int getType() {
        return type;
    }

    public int getIsPaid() {
        return isPaid;
    }

    public double getMoney() {
        return money;
    }
}
