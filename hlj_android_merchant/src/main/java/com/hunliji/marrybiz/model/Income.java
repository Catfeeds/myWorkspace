package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by luohanlin on 15/7/7.
 */
public class Income implements Identifiable {
    private static final long serialVersionUID = -7606497078374352552L;

    private long id;
    private String buyerName;
    private String buyerPhone;
    private DateTime createdAt;
    private String orderNo;
    private double incomeMoney;
    private int status;
    private String statusStr;
    private String payType;

    public Income(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            JSONObject buyerObject = jsonObject.optJSONObject("buyer_contact");
            if (buyerObject != null) {
                buyerName = JSONUtil.getString(buyerObject, "buyer_name");
                buyerPhone = JSONUtil.getString(buyerObject, "buyer_phone");
            }
            createdAt = JSONUtil.getDateTime(jsonObject, "created_at");
            orderNo = JSONUtil.getString(jsonObject, "order_no");
            payType = JSONUtil.getString(jsonObject, "pay_type");
            incomeMoney = jsonObject.optDouble("income_money", 0);
            if (incomeMoney == 0) {
                incomeMoney = jsonObject.optDouble("withdraw_money", 0);
            }
            status = jsonObject.optInt("status", 0);
            switch (status) {
                case 0:
                    statusStr = "未提现";
                    break;
                case 1:
                    statusStr = "已提现";
                    break;
                case 2:
                    statusStr = "部分提现";
                    break;
            }


        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public double getIncomeMoney() {
        return incomeMoney;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusStr() {
        return statusStr;
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

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public String getPayType() {
        return payType;
    }
}
