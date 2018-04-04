package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.TimeUtil;

/**
 * Created by werther on 15/8/13.
 */
public class ProductOrder implements Identifiable {
    private static final long serialVersionUID = -5476153097995064865L;

    private long id;
    private String orderNo;
    private long expressId;
    private String redPacketNo;
    private double redPacketMoney;
    private double shippingFee;
    private double aidMoney;
    private double actualMoney; // 此订单合集的总价
    private int priceChanged;
    private int status;
    private NewMerchant merchant;
    private ArrayList<ProductSubOrder> subOrders;
    private Date expireTime;
    private Date createdAt;
    private ExpressInfo expressInfo;
    private int reason;
    private String remark;

    public ProductOrder(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.orderNo = JSONUtil.getString(jsonObject, "order_no");
            this.expressId = jsonObject.optLong("express_id", 0);
            this.redPacketNo = JSONUtil.getString(jsonObject, "red_packet_no");
            this.redPacketMoney = jsonObject.optDouble("red_packet_money", 0);
            this.shippingFee = jsonObject.optDouble("shipping_fee", 0);
            this.aidMoney = jsonObject.optDouble("aid_money", 0);
            this.priceChanged = jsonObject.optInt("price_changed", 0);
            this.status = jsonObject.optInt("status", 10);
            this.actualMoney = jsonObject.optDouble("actual_money", 0);
            this.expireTime = JSONUtil.getDateFromFormatLong(jsonObject, "expire_time", true);
            this.expireTime = TimeUtil.getLocalTime(expireTime);
            this.createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            JSONArray subsArray = jsonObject.optJSONArray("ordersubs");
            subOrders = new ArrayList<>();
            if (subsArray != null && subsArray.length() > 0) {
                for (int i = 0; i < subsArray.length(); i++) {
                    ProductSubOrder subOrder = new ProductSubOrder(subsArray.optJSONObject(i));
                    subOrders.add(subOrder);
                }
            }
            merchant = new NewMerchant(jsonObject.optJSONObject("merchant"));
            JSONObject expressObj = jsonObject.optJSONObject("express");
            if (expressObj != null) {
                expressInfo = new ExpressInfo(expressObj);
            }
            reason = jsonObject.optInt("reason", 0);
            remark = JSONUtil.getString(jsonObject, "remark");
        }
    }

    public String getRemark() {
        return remark;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public long getExpressId() {
        return expressId;
    }

    public String getRedPacketNo() {
        return redPacketNo;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public double getAidMoney() {
        return aidMoney;
    }

    public int getPriceChanged() {
        return priceChanged;
    }

    public int getStatus() {
        return status;
    }

    /**
     * 倒计时截止,自动关闭订单,对应服务器自动关闭
     */
    public void autoCancelOrder() {
        status = 93;
    }

    public NewMerchant getMerchant() {
        return merchant;
    }

    public ArrayList<ProductSubOrder> getSubOrders() {
        return subOrders;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getStatusStr() {
        switch (status) {
            case 10:
                return "待付款";
            case 88:
                return "待发货";
            case 89:
                return "待收货";
            case 90:
            case 92:
                return "交易完成";
            case 91: // 用户关闭
            case 93: // 系统自动关闭
                return "交易关闭";
            default:
                return "";
        }
    }

    public String getClosedReason() {
        switch (reason) {
            case 1:
                return "用户取消订单";
            case 2:
                return "自动关闭订单";
            case 3:
                return "退款关闭订单";
            default:
                return getStatusStr();
        }
    }

    /**
     * 将订单状态改为"交易关闭"
     */
    public void setToCommented() {
        this.status = 92;
    }

    public ExpressInfo getExpressInfo() {
        return expressInfo;
    }

    /**
     * 子订单中是否有正在进行中的退款
     *
     * @return
     */
    public boolean isRefunding() {
        boolean isRefunding = false;
        for (ProductSubOrder subOrder : subOrders) {
            if (subOrder.getRefundStatus() == 1 || subOrder.getRefundStatus() == 4 || subOrder
                    .getRefundStatus() == 5 || subOrder.getRefundStatus() == 8 || subOrder
                    .getRefundStatus() == 9 || subOrder.getRefundStatus() == 10) {
                isRefunding = true;
            }
        }
        return isRefunding;
    }

    public int getReason() {
        return reason;
    }
}
