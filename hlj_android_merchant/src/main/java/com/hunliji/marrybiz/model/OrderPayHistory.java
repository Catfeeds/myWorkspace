package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by werther on 15/8/28.
 */
public class OrderPayHistory implements Identifiable {
    private static final long serialVersionUID = 3871233074131154500L;

    private long id;
    private long orderId;
    private String event;
    private long userId;
    private DateTime createdAt;
    private int status;
    private String details;
    private double money;

    public static final String PAY_HISTORY_EVENT_DEPOSITE = "pay_front";
    public static final String PAY_HISTORY_EVENT_INTENT = "pay_intent";
    public static final String PAY_HISTORY_EVENT_REST = "pay_rest";
    public static final String PAY_HISTORY_EVENT_CONFIRM_REST = "confirm_rest";
    public static final String PAY_HISTORY_EVENT_ALL = "pay_all";

    public OrderPayHistory(JSONObject jsonObject) {
        this.id = jsonObject.optLong("id");
        this.orderId = jsonObject.optLong("order_id");
        this.event = jsonObject.optString("event");
        Date date = JSONUtil.getDateFromFormatLong(jsonObject, "created_at",true);
        if (date != null) {
            createdAt = new DateTime(date);
        } else {
            createdAt = new DateTime();
        }
        this.status = jsonObject.optInt("status");
        this.details = jsonObject.optString("details");
        JSONObject object = jsonObject.optJSONObject("object_changes");
        if (object != null) {
            this.money = object.optDouble("money");
        } else {
            this.money = jsonObject.optDouble("money");
        }
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    @Override
    public Long getId() {
        return serialVersionUID;
    }

    public String getEventStr() {
        if (event.equals("pay_front")) {
            return "买家支付定金";
        }else if (event.equals("pay_rest")) {
            return "买家分笔支付";
        }else if (event.equals("pay_all")) {
            return "买家支付全款";
        }else {
            return "";
        }
    }
}
