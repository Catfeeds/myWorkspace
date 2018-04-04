package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by LuoHanLin on 14/11/27.
 */
public class CashRecord implements Identifiable {

    private Long id;
    private Date createdAt;
    private String kind;
    private String status;
    private String orderNumber;
    private double withdrawsCount;
    private double withdrawSum;
    private String statusDesc;
    private double fee;

    public CashRecord(JSONObject jsonObject) {
        this.id = jsonObject.optLong("id", 0);
        this.createdAt = JSONUtil.getDate(jsonObject, "created_at");
        this.kind = JSONUtil.getString(jsonObject, "kind");
        this.status = JSONUtil.getString(jsonObject, "status");
        this.orderNumber = JSONUtil.getString(jsonObject, "order_no");
        this.withdrawsCount = jsonObject.optDouble("withdraws_count");
        this.withdrawSum = jsonObject.optDouble("withdraws_sum");
        this.fee = jsonObject.optDouble("fee");

        if (status.equals("retain")) {
            statusDesc = "未提现";
        } else if (status.equals("withdrawing")) {
            statusDesc = "正在提现";
        } else {
            statusDesc = "已提现";
        }
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public double getWithdrawsCount() {
        return withdrawsCount;
    }

    public void setWithdrawsCount(double withdrawsCount) {
        this.withdrawsCount = withdrawsCount;
    }

    public double getWithdrawSum() {
        return withdrawSum;
    }

    public void setWithdrawSum(double withdrawSum) {
        this.withdrawSum = withdrawSum;
    }

    @Override
    public Long getId() {
        return null;
    }
}
