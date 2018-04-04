package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by werther on 15/8/13.
 */
public class ProductSubOrder implements Identifiable {
    private static final long serialVersionUID = 1612648295531205252L;
    private long id;
    private long productId;
    private double actualMoney; // 子订单的总价 = 商品单价 * quantity
    private double redPacketMoney;
    private int quantity;
    private boolean isGift;
    private ShopProduct product;
    private Sku sku;
    private int activityStatus; //0默认 没活动，1表示有活动 2表示活动过期
    private String content;
    private ArrayList<JsonPic> pics;
    private int rating;
    private int refundStatus; // 子订单退款状态, 1: 申请, 2: 通过, 3: 失败
    private double refundMoney;
    private int refundType; // 退款类型

    public ProductSubOrder(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.productId = jsonObject.optLong("product_id", 0);
            this.redPacketMoney = jsonObject.optDouble("red_packet_money", 0);
            this.quantity = jsonObject.optInt("quantity", 0);
            this.isGift = jsonObject.optInt("is_gift", 0) > 0;
            this.product = new ShopProduct(jsonObject.optJSONObject("product"));
            this.sku = new Sku(jsonObject.optJSONObject("sku"));
            this.actualMoney = jsonObject.optDouble("actual_money", 0);
            this.activityStatus = jsonObject.optInt("activity_status", 0);
            this.refundStatus = jsonObject.optInt("refund_status", 0);
            if (!jsonObject.isNull("refund")) {
                JSONObject refundObj = jsonObject.optJSONObject("refund");
                refundMoney = refundObj.optDouble("pay_money", 0);
                refundType = refundObj.optInt("type", 0);
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getProductId() {
        return productId;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isGift() {
        return isGift;
    }

    public ShopProduct getProduct() {
        return product;
    }

    public Sku getSku() {
        return sku;
    }

    public int getActivityStatus() {
        return activityStatus;
    }

    public ArrayList<JsonPic> getPics() {
        return pics;
    }

    public void addPics(ArrayList<JsonPic> jsonPics) {
        if (pics == null) {
            pics = new ArrayList<>();
        }
        pics.addAll(jsonPics);
    }

    public int getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 0: 未申请退款
     * 1,2,3 状态是老版的退款流程,其他是新的退款流程状态,0是共有的默认未退款的状态
     * 1: 后台发起退款中
     * 2: 后台退款成功
     * 3: 后台退款失败
     * 4: 等待处理退款
     * 5: 等待处理退货
     * 6: 商家拒绝退款
     * 7: 商家拒绝退货
     * 8: 等待退货
     * 9: 等待商家确认
     * 10: 商家未收到货
     * 11: 退款/退货完成
     * 12: 退款/退货取消
     * 13: 自动关闭
     *
     * @return
     */
    public int getRefundStatus() {
        return refundStatus;
    }

    public String getRefundStatusStr() {
        switch (refundStatus) {
            case 1:
                return "退款中";
            case 2:
                return "退款成功";
            case 3:
                return "退款失败";
            default:
                return "";
        }
    }

    public double getRefundMoney() {
        return refundMoney;
    }

    public int getRefundType() {
        return refundType;
    }

}

