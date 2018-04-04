package me.suncloud.marrymemo.model;

import org.json.JSONObject;

/**
 * Created by werther on 15/9/23.
 */
public class CarSubOrder implements Identifiable {
    private static final long serialVersionUID = -143594797706732313L;

    private long id;
    private double actualMoney;
    private int quantity;
    private CarProduct carProduct;
    private CarSku carSku;
    private int refundStatus;
    private double refundMoney;
    private int activityStatus; //0默认 没活动，1表示有活动 2表示活动过期

    public CarSubOrder(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            actualMoney = jsonObject.optDouble("actual_money", 0);
            carProduct = new CarProduct(jsonObject.optJSONObject("product"));
            carSku = new CarSku(jsonObject.optJSONObject("sku"));
            quantity = jsonObject.optInt("quantity", 0);
            refundStatus = jsonObject.optInt("refund_status", 0);
            refundMoney = jsonObject.optDouble("refund_money", 0);
            activityStatus = jsonObject.optInt("activity_status", 0);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public int getQuantity() {
        return quantity;
    }

    public CarProduct getCarProduct() {
        return carProduct;
    }

    public CarSku getCarSku() {
        return carSku;
    }

    public int getRefundStatus() {
        return refundStatus;
    }

    public double getRefundMoney() {
        return refundMoney;
    }

    public int getActivityStatus() {
        return activityStatus;
    }
}
