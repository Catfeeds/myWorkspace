package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 15/9/23.
 */
public class CarOrder implements Identifiable {

    private static final long serialVersionUID = 2531804966416750784L;
    private long id;
    private String orderNo;
    private String redPacketNo;
    private double redPacketMoney;
    private double aidMoney;
    private double originActualMoney;
    private double paidMoney;
    private int status;
    private ArrayList<CarSubOrder> subOrders;
    private Date expireTime;
    private Date createdAt;
    private boolean isPayAll;
    private double earnestPercent; // 定金百分比
    private double payAllSavedMoneyExpect; // 预计全额支付优惠金额
    private double payAllSavedMoney; // 实际得到的全款优惠金额,如果使用定金支付的话就没有全款优惠了
    private double refundedMoney; // 实际退款的金额
    private String buyerName;
    private String buyerPhone;
    private Date carUseDate;
    private String carUseAddr;
    private String memoGroomAddress;
    private String memoBrideAddress;
    private String memoHotel;
    private String memoWay;
    private String memoExtra;
    private long carInsuranceId;
    private boolean isOfflinePay;

    public CarOrder(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            orderNo = JSONUtil.getString(jsonObject, "order_no");
            redPacketNo = JSONUtil.getString(jsonObject, "red_packet_no");
            redPacketMoney = jsonObject.optDouble("red_packet_money", 0);
            aidMoney = jsonObject.optDouble("aid_money", 0);
            originActualMoney = jsonObject.optDouble("original_money", 0);
            paidMoney = jsonObject.optDouble("paid_money", 0);
            status = jsonObject.optInt("status", 10);
            expireTime = JSONUtil.getDateFromFormatLong(jsonObject, "expire_time", true);
            createdAt = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            isPayAll = jsonObject.optInt("is_pay_all", 0) > 0;
            earnestPercent = jsonObject.optDouble("earnest_percent", 0);
            payAllSavedMoneyExpect = jsonObject.optDouble("pay_all_money_discount_money_expect", 0);
            payAllSavedMoney = jsonObject.optDouble("pay_all_money_discount_money", 0);
            buyerName = JSONUtil.getString(jsonObject, "buyer_name");
            buyerPhone = JSONUtil.getString(jsonObject, "buyer_phone");
            carUseDate = JSONUtil.getDateFromFormatLong(jsonObject, "start_at", false);
            carUseAddr = JSONUtil.getString(jsonObject, "address_detail");
            JSONObject memoObj = jsonObject.optJSONObject("memo");
            if (memoObj != null) {
                memoGroomAddress = JSONUtil.getString(memoObj, "groom_address");
                memoBrideAddress = JSONUtil.getString(memoObj, "bride_address");
                memoHotel = JSONUtil.getString(memoObj, "hotel");
                memoWay = JSONUtil.getString(memoObj, "way");
                memoExtra = JSONUtil.getString(memoObj, "extra");
            }
            JSONArray subsArray = jsonObject.optJSONArray("ordersubs");
            subOrders = new ArrayList<>();
            if (subsArray != null && subsArray.length() > 0) {
                for (int i = 0; i < subsArray.length(); i++) {
                    CarSubOrder subOrder = new CarSubOrder(subsArray.optJSONObject(i));
                    subOrders.add(subOrder);
                }
            }
            JSONObject refundObj = jsonObject.optJSONObject("recent_refund");
            if (refundObj != null) {
                refundedMoney = refundObj.optDouble("pay_money", 0);
            }

            JSONObject carInsuranceObj = jsonObject.optJSONObject("car_insurance");
            if (carInsuranceObj != null) {
                carInsuranceId = carInsuranceObj.optLong("id", 0);
            }
            isOfflinePay = jsonObject.optInt("is_offline_pay", 0) > 0;
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getRedPacketNo() {
        return redPacketNo;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public double getAidMoney() {
        return aidMoney;
    }

    public double getOriginActualMoney() {
        return originActualMoney;
    }

    public int getStatus() {
        return status;
    }

    public ArrayList<CarSubOrder> getSubOrders() {
        return subOrders;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public double getPaidMoney() {
        return paidMoney;
    }

    public boolean isPayAll() {
        return isPayAll;
    }

    public String getStatusStr() {
        switch (status) {
            case 10:
                return "等待商家接单";
            case 11:
                return "待付款";
            case 87:
                return "已付款";
            case 90:
            case 92:
                return "交易成功";
            case 15:
            case 91: // 用户取消订单
            case 93: // 系统自动关闭交易
                return "订单已关闭";
            case 22:
                return "退款中";
            case 24:
                return "退款成功";
            default:
                return "";
        }
    }

    public String getStatusStr2() {
        switch (status) {
            case 10:
                return "待接单";
            case 11:
                return "待付款";
            case 87:
                return "已付款";
            case 90:
            case 92:
                return "交易成功";
            case 15:
            case 91: // 用户取消订单
            case 93: // 系统自动关闭交易
                return "订单关闭";
            case 24:
                return "退款详情";
            default:
                return "";
        }
    }


    /**
     * 倒计时截止,自动关闭订单,对应服务器自动关闭
     */
    public void autoCancelOrder() {
        status = 93;
    }

    public double getEarnestPercent() {
        return earnestPercent;
    }

    public double getPayAllSavedMoneyExpect() {
        return payAllSavedMoneyExpect;
    }

    public double getRefundedMoney() {
        return refundedMoney;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public Date getCarUseDate() {
        return carUseDate;
    }

    public String getCarUseAddr() {
        return carUseAddr;
    }

    public String getMemoGroomAddress() {
        return memoGroomAddress;
    }

    public String getMemoBrideAddress() {
        return memoBrideAddress;
    }

    public String getMemoHotel() {
        return memoHotel;
    }

    public String getMemoWay() {
        return memoWay;
    }

    public String getMemoExtra() {
        return memoExtra;
    }

    public long getCarInsuranceId() {
        return carInsuranceId;
    }

    public double getPayAllSavedMoney() {
        return payAllSavedMoney;
    }

    public boolean isOfflinePay() {
        return isOfflinePay;
    }
}
