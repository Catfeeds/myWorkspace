package com.hunliji.marrybiz.model;

import org.json.JSONObject;

/**
 * Created by werther on 15/12/17.
 */
public class ADVHPrice implements Identifiable {
    private static final long serialVersionUID = -6932730577730483928L;

    private long id;
    private long priceId;
    private int quantity; // 客资数量
    private long propertyId; // 对应的商家经营类型
    private double price; // 这个套餐的价格
    private int giftNum;// 优惠数/满送的客资数量
    private double discountPercent; // 折扣百分比
    private int type;
    private boolean selected;

    public ADVHPrice(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.priceId = jsonObject.optLong("price_id", 0);
            this.quantity = jsonObject.optInt("quantity", 0);
            this.propertyId = jsonObject.optLong("property_id", 0);
            this.price = jsonObject.optDouble("price", 0);
            this.giftNum = jsonObject.optInt("gift_nums", 0);
            this.discountPercent = jsonObject.optDouble("discount", 0);
            this.type = jsonObject.optInt("type", 0);
            this.selected = jsonObject.optBoolean("selected", false);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getPropertyId() {
        return propertyId;
    }

    public double getPrice() {
        return price;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public long getPriceId() {
        return priceId;
    }

    public int getType() {
        return type;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
