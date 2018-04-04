package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/8/5.
 */
public class Sku implements Identifiable {

    private long id;
    private String name;
    private int quantity;
    private double actualPrice;
    private double marketPrice;
    private double salePrice;
    private double showPrice;
    private int limitNum;
    private int showNum;
    private int limit_sold_out;

    public Sku(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id");
            name = JSONUtil.getString(jsonObject, "name");
            quantity = jsonObject.optInt("quantity");
            actualPrice = jsonObject.optDouble("actual_price", 0);
            marketPrice = jsonObject.optDouble("market_price", 0);
            salePrice = jsonObject.optDouble("sale_price", 0);
            limitNum = jsonObject.optInt("limit_num", 0);
            showPrice = jsonObject.optDouble("show_price", 0);
            showNum = jsonObject.optInt("show_num", 0);
            limit_sold_out = jsonObject.optInt("limit_sold_out", 0);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public double getShowPrice() {
        return showPrice;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public int getShowNum() {
        return showNum;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }

    public void setShowPrice(double showPrice) {
        this.showPrice = showPrice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLimit_sold_out() {
        return limit_sold_out;
    }



    /**
     * 婚品sku model 转换 用于ShoppingCartItem
     * id
     * title
     * photo
     *
     * @param sku 新婚品sku
     */
    public Sku(
            com.hunliji.hljcommonlibrary.models.product.Sku sku) {
        id = sku.getId();
        name=sku.getName();
        quantity=sku.getQuantity();
        actualPrice=sku.getActualPrice();
        marketPrice=sku.getMarketPrice();
        salePrice=sku.getSalePrice();
        limitNum=sku.getLimitNum();
        showPrice=sku.getShowPrice();
        showNum=sku.getShowNum();
        limit_sold_out=sku.getLimitSoldOut();
    }
}
