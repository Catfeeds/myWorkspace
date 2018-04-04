package me.suncloud.marrymemo.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * 定制套餐退款单信息,CustomSetmealOrderRefundInfo
 * Created by werther on 16/3/29.
 * 兼容parcelable
 */
public class CSORefundInfo implements Identifiable {
    private static final long serialVersionUID = -4079189407393777934L;
    private long id;
    @SerializedName(value = "pay_money")
    private double payMoney;
    private int status;
    @SerializedName(value = "reason_id")
    private long reasonId;
    private String desc;
    @SerializedName(value = "refuse_reason")
    private String refuseReason;
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "order_no")
    private String orderNo; // 这个是退款单的编号,不是对应的订单编号
    @SerializedName(value = "reason_name")
    private String reasonName;
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;

    public CSORefundInfo(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            payMoney = jsonObject.optDouble("pay_money");
            reasonId = jsonObject.optInt("reason_id");
            desc = JSONUtil.getString(jsonObject, "desc");
            status = jsonObject.optInt("status", 0);
            refuseReason = JSONUtil.getString(jsonObject, "refuse_reason");
            Date dateCreatedAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            Date dateUpdatedAt = JSONUtil.getDateFromFormatLong(jsonObject, "updated_at", true);
            createdAt = new DateTime(dateCreatedAt);
            updatedAt = new DateTime(dateUpdatedAt);
            orderNo = JSONUtil.getString(jsonObject, "order_no");
            reasonName = JSONUtil.getString(jsonObject, "reason_name");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getStatusStr() {
        if (status == 20) {
            return "退款审核中";
        } else if (status == 21) {
            return "已取消退款申请";
        } else if (status == 23) {
            return "退款被拒绝";
        } else if (status == 24) {
            return "退款成功";
        } else {
            return "退款中";
        }
    }

    public double getPayMoney() {
        return payMoney;
    }

    public int getStatus() {
        return status;
    }

    public long getReasonId() {
        return reasonId;
    }

    public String getDesc() {
        return desc;
    }

    public String getRefuseReason() {
        return refuseReason;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getReasonName() {
        return reasonName;
    }
}
