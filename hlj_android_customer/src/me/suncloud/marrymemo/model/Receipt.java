package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by Suncloud on 2015/4/1.
 */
public class Receipt implements Identifiable {

    private long id;
    private long orderId;
    private String orderNo;
    private String orderName;
    private double iouPrice;
    private double iouPayPrice;
    private double totalPrice;
    private Date payTime;
    private Date expirationTime;
    private String orderType;

    public Receipt(JSONObject jsonObject){
        if(jsonObject!=null){
            id=jsonObject.optLong("id",0);
            orderId=jsonObject.optLong("order_id",0);
            iouPrice=jsonObject.optDouble("loan", 0);
            iouPayPrice=jsonObject.optDouble("payment",0);
            payTime= JSONUtil.getDate(jsonObject, "used_date");
            expirationTime= JSONUtil.getDate(jsonObject,"payment_date");
            orderType=JSONUtil.getString(jsonObject,"order_type");
            if(!jsonObject.isNull("order")){
                JSONObject orderJson=jsonObject.optJSONObject("order");
                if(orderJson!=null){
                    orderNo=JSONUtil.getString(orderJson,"order_no");
                    orderName=JSONUtil.getString(orderJson,"order_name");
                    totalPrice=orderJson.optDouble("total_price",0);
                    if(orderId==0){
                        orderId=orderJson.optLong("order_id",0);
                    }
                    if(iouPrice==0){
                        iouPrice=orderJson.optDouble("loan", 0);
                    }
                }
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public Date getPayTime() {
        return payTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public double getIouPayPrice() {
        return iouPayPrice;
    }

    public double getIouPrice() {
        return iouPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getOrderType() {
        return orderType;
    }

    public long getOrderId() {
        return orderId;
    }

}
