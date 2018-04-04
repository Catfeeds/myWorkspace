package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 15/8/21.
 */
public class RefundProductOrder implements Identifiable {
    private static final long serialVersionUID = 2198941748379763444L;

    private long id;
    private String orderNo; // 退款单单号
    private double money; // 退款金额
    private double refundPayMoney; //  实际退款金额
    private ProductSubOrder subOrder;
    private NewMerchant merchant;

    public RefundProductOrder(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.orderNo = JSONUtil.getString(jsonObject, "order_no");
            JSONObject subObj = jsonObject.optJSONObject("ordersub");
            if (subObj != null) {
                this.subOrder = new ProductSubOrder(jsonObject.optJSONObject("ordersub"));
            }
            this.money = jsonObject.optDouble("actual_money", 0);
            this.refundPayMoney = jsonObject.optDouble("pay_money", 0);
            this.merchant = new NewMerchant(jsonObject.optJSONObject("merchant"));
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public double getMoney() {
        return money;
    }

    public double getRefundPayMoney() {
        return refundPayMoney;
    }

    public ProductSubOrder getSubOrder() {
        return subOrder;
    }

    public NewMerchant getMerchant() {
        return merchant;
    }
}
