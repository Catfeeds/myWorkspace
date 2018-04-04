package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by werther on 1/15/16.
 */
public class CustomSetmealOrder implements Identifiable {

    private static final long serialVersionUID = 6276654757247744671L;
    private long id;
    private long userId;
    private String buyerName;
    private String buyerPhone;
    private Date weddingTime;
    private long merchantId;
    private String orderNo;
    private ArrayList<Photo> protocolPhotos;
    private String message;
    private double actualPrice;
    private double redPacketMoney;
    private String redPacketNo;
    private double earnestMoney;
    private double paidMoney;
    private int status;
    private int oldStatus;
    private boolean isPayAll;
    private boolean isFinished;
    private int reason;
    private String reasonName;
    private Date createdAt;
    private Date expiredAt;
    private boolean hasException;
    private CustomSetmeal customSetmeal;
    private ArrayList<OrderPayHistory> payHistories;

    private long refundId;
    private double refundPayMoney;
    private int refundStatus;
    private long refundReasonId;
    private String refundDesc;
    private String refundRefuseReason;
    private Date refundCreateAt;
    private String refundOrderNo;
    private String refundReasonName;
    private User user;
    private boolean isInvalid;
    private Date refundUpdateAt;

    public CustomSetmealOrder(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            userId = jsonObject.optLong("user_id", 0);
            buyerName = JSONUtil.getString(jsonObject, "buyer_name");
            buyerPhone = JSONUtil.getString(jsonObject, "buyer_phone");
            weddingTime = JSONUtil.getDateFromFormatShort(jsonObject, "wedding_time", true);
            merchantId = jsonObject.optLong("merchant_id", 0);
            orderNo = JSONUtil.getString(jsonObject, "order_no");
            message = JSONUtil.getString(jsonObject, "message");
            actualPrice = jsonObject.optDouble("actual_price");
            redPacketMoney = jsonObject.optDouble("red_packet_money");
            redPacketNo = JSONUtil.getString(jsonObject, "red_packet_no");
            earnestMoney = jsonObject.optDouble("earnest_money");
            paidMoney = jsonObject.optDouble("paid_money");
            status = jsonObject.optInt("status");
            oldStatus = jsonObject.optInt("old_status");
            isPayAll = jsonObject.optInt("is_pay_all", 0) > 0;
            isFinished = jsonObject.optInt("is_finished", 0) > 0;
            reason = jsonObject.optInt("reason");
            reasonName = JSONUtil.getString(jsonObject, "reason_name");
            createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            expiredAt = JSONUtil.getDateFromFormatLong(jsonObject, "expired_at", true);
            expiredAt= TimeUtil.getLocalTime(expiredAt);
            hasException = jsonObject.optInt("has_exception", 0) > 0;
            customSetmeal = new CustomSetmeal(jsonObject.optJSONObject("snapshot"));
            if(customSetmeal.getId()==0){
                customSetmeal = new CustomSetmeal(jsonObject.optJSONObject("set_meal"));
            }
            protocolPhotos = new ArrayList<>();
            JSONArray imgArr = jsonObject.optJSONArray("protocol_images");
            if (imgArr != null && imgArr.length() > 0) {
                for (int i = 0; i < imgArr.length(); i++) {
                    Photo photo = new Photo(imgArr.optJSONObject(i));
                    protocolPhotos.add(photo);
                }
            }
            JSONObject userJson=jsonObject.optJSONObject("user");
            if(userJson!=null) {
                user = new User(userJson);
            }
            payHistories = new ArrayList<>();
            JSONArray hisArr = jsonObject.optJSONArray("order_history");
            if (hisArr != null && hisArr.length() > 0) {
                for (int i = 0; i < hisArr.length(); i++) {
                    OrderPayHistory orderPayHistory = new OrderPayHistory(hisArr.optJSONObject(i));
                    payHistories.add(orderPayHistory);
                }
            }

            isInvalid = jsonObject.optBoolean("is_invalid");
            JSONObject refund = jsonObject.optJSONObject("refund");
            if (refund != null) {
                refundId = refund.optLong("id");
                refundPayMoney = refund.optDouble("pay_money");
                refundStatus = refund.optInt("status");
                refundReasonId = refund.optLong("reason_id");
                refundDesc = refund.optString("desc");
                refundRefuseReason = refund.optString("refuse_reason");
                refundCreateAt = JSONUtil.getDateFromFormatLong(refund, "created_at", true);
                refundUpdateAt = JSONUtil.getDateFromFormatLong(refund, "updated_at", true);
                refundOrderNo = refund.optString("order_no");
                refundReasonName = refund.optString("reason_name");
            }
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public Date getWeddingTime() {
        return weddingTime;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public ArrayList<Photo> getProtocolPhotos() {
        if(protocolPhotos==null){
            protocolPhotos=new ArrayList<>();
        }
        return protocolPhotos;
    }

    public String getMessage() {
        return message;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public String getRedPacketNo() {
        return redPacketNo;
    }

    public double getEarnestMoney() {
        return earnestMoney;
    }

    public double getPaidMoney() {
        return paidMoney;
    }

    public int getStatus() {
        return status;
    }

    public int getOldStatus() {
        return oldStatus;
    }

    public boolean isPayAll() {
        return isPayAll;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public int getReason() {
        return reason;
    }

    public String getReasonName() {
        return reasonName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getExpiredAt() {
        return expiredAt;
    }

//    public double getActualMoney() {
//        return actualMoney;
//    }

    public boolean isHasException() {
        return hasException;
    }

    public CustomSetmeal getCustomSetmeal() {
        return customSetmeal;
    }

    public ArrayList<OrderPayHistory> getPayHistories() {
        return payHistories;
    }

    public long getRefundId() {
        return refundId;
    }

    public double getRefundPayMoney() {
        return refundPayMoney;
    }

    public int getRefundStatus() {
        return refundStatus;
    }

    public long getRefundReasonId() {
        return refundReasonId;
    }

    public String getRefundDesc() {
        return refundDesc;
    }

    public String getRefundRefuseReason() {
        return refundRefuseReason;
    }

    public Date getRefundCreateAt() {
        return refundCreateAt;
    }

    public String getRefundOrderNo() {
        return refundOrderNo;
    }

    public String getRefundReasonName() {
        return refundReasonName;
    }

    public String getStatusStr() {
        String str = "";
        switch (status) {
            case 10: //用户下单 等待接单              
                str = "待接单";
                break;
            case 11: //商家接单  等待付款
                str = "待付款";
                break;
            case 87://用户已付款
                str = "待服务";
                break;
            case 20: //用户申请退款
            case 21: //用户取消申请退款
            case 23: //退款被拒绝
                str = "退款中";
                break;
            case 24: //退款成功
                str = "退款成功";
                break;
            case 90:  // 用户确认服务,待评价
            case 92: //交易完成
                str = "交易成功";
                break;
            case 93: // 系统自动取消订单
            case 91: // 订单取消
                str = "交易关闭";
                break;
        }
        return str;
    }

    public void setWeddingTime(Date weddingTime) {
        this.weddingTime = weddingTime;
    }

    public void setProtocolPhotos(ArrayList<Photo> protocolPhotos) {
        this.protocolPhotos = protocolPhotos;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public void setEarnestMoney(double earnestMoney) {
        this.earnestMoney = earnestMoney;
    }

    public User getUser() {
        return user;
    }

    public boolean isInvalid() {
        return isInvalid;
    }

    public Date getRefundUpdateAt() {
        return refundUpdateAt;
    }
}
