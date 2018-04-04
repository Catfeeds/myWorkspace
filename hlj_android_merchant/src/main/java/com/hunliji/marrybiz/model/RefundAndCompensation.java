package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by werther on 15-7-17.
 */
public class RefundAndCompensation implements Identifiable {
    private static final long serialVersionUID = 2301160826048410275L;

    private long id;
    private DateTime updatedAt;
    private int status;
    private String orderNo;
    private String statusStr;
    private int reasonId;
    private String userNick;
    private int type; // 1:退款,2:赔款
    private boolean isPaid; // 是否缴纳
    private double payMoney;

    public RefundAndCompensation(JSONObject jsonObject) {
        if (jsonObject != null) {
            Date date = JSONUtil.getDate(jsonObject, "updated_at");
            if (date != null) {
                updatedAt = new DateTime(date);
            } else {
                updatedAt = new DateTime();
            }
            payMoney = jsonObject.optDouble("pay_money", 0);
            orderNo = JSONUtil.getString(jsonObject, "order_no");
            status = jsonObject.optInt("status", 0);
            statusStr = JSONUtil.getString(jsonObject, "status_name");
            reasonId = jsonObject.optInt("reason_id", 0);
            userNick = JSONUtil.getString(jsonObject, "user_nick");
            type = jsonObject.optInt("type", 0);
            isPaid = jsonObject.optInt("is_paid", 0) > 0;
        }
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public int getReasonId() {
        return reasonId;
    }

    public String getUserNick() {
        return userNick;
    }

    public int getType() {
        return type;
    }

    public boolean isPaid() {
        return isPaid;
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getDateVal() {
        String str = updatedAt.getYear() + "" + updatedAt.getMonthOfYear();
        long val = 0;
        try {
            val = Long.valueOf(str);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return val;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public double getPayMoney() {
        return payMoney;
    }
}
