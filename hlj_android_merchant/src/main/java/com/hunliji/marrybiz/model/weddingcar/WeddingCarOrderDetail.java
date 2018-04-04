package com.hunliji.marrybiz.model.weddingcar;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.List;

/**
 * 婚车订单详情
 * Created by jinxin on 2018/1/4 0004.
 */

public class WeddingCarOrderDetail implements Parcelable {


    public static final int WAIT_TAKE_ORDER =10;//待接单
    public static final int WAIT_FOR_PAY =11;//等待用户付款
    public static final int BUYER_PAY_DEPOSIT = 12;//买家已付定金
    public static final int PAY_FINISHED = 13;//完成付款
    public static final int SELLER_REFUSE_ORDER = 15;//商家拒绝接单
    public static final int REFUSE_AUDIT = 20;//退款审核中
    public static final int REFUSE_AUDIT_PASSED = 22;//退款审核通过
    public static final int REFUSE_CANCEL = 21;//取消申请退款
    public static final int REFUSE_REFUSED = 23;//退款被拒绝
    public static final int REFUSE_SUCSSESS = 24;//退款成功
    public static final int BUYER_PAIED = 87;//买家已付款
    public static final int ORDER_SUCSSESS = 90;//交易成功
    public static final int BUYER_CANCEL = 91;//用户取消订单
    public static final int COMMEND = 92;//已评论
    public static final int SYSTEM_AUTO_CLOSE = 93;//系统自动关闭交易


    public static final int TAKER_ORDER_MONEY = 0;//定金
    public static final int ALL_PAY_MONEY = 1;//全款

    long id;
    @SerializedName("actual_money")
    double actualMoney;//订单价格
    @SerializedName("address_detail")
    String addressDetail;//用车地点
    @SerializedName("aid_money")
    double aidMoney;
    @SerializedName("buyer_name")
    String buyerName;//买家名称
    @SerializedName("buyer_phone")
    String buyerPhone;//买家手机
    @SerializedName("car_insurance")
    WeddingCarInsurance carInsurance;
    @SerializedName("created_at")
    DateTime createdAt;
    @SerializedName("earnest_percent")
    double earnestPercent;
    @SerializedName("expire_time")
    DateTime expireTime;
    @SerializedName("is_offline_pay")
    boolean isOfflinePay;
    @SerializedName("offline_pay_time")
    DateTime offLinePayTime;
    @SerializedName("is_pay_all")
    int isPayAll;//0为订金，1全额
    Merchant merchant;
    @SerializedName("merchant_id")
    long  merchantId;
    String message;
    @SerializedName("order_no")
    String orderNo;//订单号
    @SerializedName("original_money")
    double originalMoney;
    @SerializedName("paid_money")
    double paidMoney;//跟据is_pay_all来显示（实付）
    @SerializedName("pay_all_money")
    double payAllMoney;//全额支付优惠金额l
    @SerializedName("pay_all_money_discount_money")
    double payAllMoneyDiscountMoney;
    @SerializedName("pay_all_money_discount_money_expect")
    int payAllMoneyDiscountMoneyExpect;
    @SerializedName("price_changed")
    boolean priceChanged;
    @SerializedName("recent_refund")
    JsonElement recentRefund;
    @SerializedName("red_packet_money")
    double redPacketMoney;//红包金额
    @SerializedName("red_packet_no")
    String redPacketNo;
    @SerializedName("red_packet_name")
    String redPacketName;//红包名称
    @SerializedName("refund_id")
    long refundId;
    String reminded;
    @SerializedName("start_at")
    DateTime startAt;
    int status;
    @SerializedName("status_name")
    String statusName;
    @SerializedName("ordersubs")
    List<WeddingCarOrderSub> orderSubs;
    @SerializedName("user")
    User user;
    @SerializedName("user_id")
    long userId;
    @SerializedName("deposit_money")
    double depositMoney;//定金

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public void setActualMoney(double actualMoney) {
        this.actualMoney = actualMoney;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public double getAidMoney() {
        return aidMoney;
    }

    public void setAidMoney(double aidMoney) {
        this.aidMoney = aidMoney;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public WeddingCarInsurance getCarInsurance() {
        return carInsurance;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getEarnestPercent() {
        return earnestPercent;
    }

    public void setEarnestPercent(double earnestPercent) {
        this.earnestPercent = earnestPercent;
    }

    public DateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(DateTime expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isOfflinePay() {
        return isOfflinePay;
    }

    public void setOfflinePay(boolean offlinePay) {
        isOfflinePay = offlinePay;
    }

    public int getIsPayAll() {
        return isPayAll;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public double getOriginalMoney() {
        return originalMoney;
    }

    public void setOriginalMoney(double originalMoney) {
        this.originalMoney = originalMoney;
    }

    public double getPaidMoney() {
        return paidMoney;
    }

    public void setPaidMoney(double paidMoney) {
        this.paidMoney = paidMoney;
    }

    public double getPayAllMoney() {
        return payAllMoney;
    }

    public void setPayAllMoney(double payAllMoney) {
        this.payAllMoney = payAllMoney;
    }

    public double getPayAllMoneyDiscountMoney() {
        return payAllMoneyDiscountMoney;
    }

    public void setPayAllMoneyDiscountMoney(double payAllMoneyDiscountMoney) {
        this.payAllMoneyDiscountMoney = payAllMoneyDiscountMoney;
    }

    public int getPayAllMoneyDiscountMoneyExpect() {
        return payAllMoneyDiscountMoneyExpect;
    }

    public void setPayAllMoneyDiscountMoneyExpect(int payAllMoneyDiscountMoneyExpect) {
        this.payAllMoneyDiscountMoneyExpect = payAllMoneyDiscountMoneyExpect;
    }

    public boolean isPriceChanged() {
        return priceChanged;
    }

    public void setPriceChanged(boolean priceChanged) {
        this.priceChanged = priceChanged;
    }


    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public void setRedPacketMoney(double redPacketMoney) {
        this.redPacketMoney = redPacketMoney;
    }

    public String getRedPacketNo() {
        return redPacketNo;
    }

    public void setRedPacketNo(String redPacketNo) {
        this.redPacketNo = redPacketNo;
    }

    public long getRefundId() {
        return refundId;
    }

    public void setRefundId(long refundId) {
        this.refundId = refundId;
    }

    public String getReminded() {
        return reminded;
    }

    public void setReminded(String reminded) {
        this.reminded = reminded;
    }

    public DateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(DateTime startAt) {
        this.startAt = startAt;
    }

    public int getStauts() {
        return status;
    }

    public void setStauts(int status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<WeddingCarOrderSub> getOrderSubs() {
        return orderSubs;
    }

    public void setOrderSubs(List<WeddingCarOrderSub> orderSubs) {
        this.orderSubs = orderSubs;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getRedPacketName() {
        return redPacketName;
    }

    public double getDepositMoney() {
        return depositMoney;
    }

    public DateTime getOffLinePayTime() {
        return offLinePayTime;
    }

    public WeddingCarOrderDetail() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeDouble(this.actualMoney);
        dest.writeString(this.addressDetail);
        dest.writeDouble(this.aidMoney);
        dest.writeString(this.buyerName);
        dest.writeString(this.buyerPhone);
        dest.writeParcelable(this.carInsurance, flags);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        dest.writeDouble(this.earnestPercent);
        HljTimeUtils.writeDateTimeToParcel(dest,this.expireTime);
        dest.writeByte(this.isOfflinePay ? (byte) 1 : (byte) 0);
        dest.writeInt(this.isPayAll);
        dest.writeParcelable(this.merchant, flags);
        dest.writeLong(this.merchantId);
        dest.writeString(this.message);
        dest.writeString(this.orderNo);
        dest.writeDouble(this.originalMoney);
        dest.writeDouble(this.paidMoney);
        dest.writeDouble(this.payAllMoney);
        dest.writeDouble(this.payAllMoneyDiscountMoney);
        dest.writeInt(this.payAllMoneyDiscountMoneyExpect);
        dest.writeByte(this.priceChanged ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.redPacketMoney);
        dest.writeString(this.redPacketNo);
        dest.writeString(this.redPacketName);
        dest.writeLong(this.refundId);
        dest.writeString(this.reminded);
        HljTimeUtils.writeDateTimeToParcel(dest,this.startAt);
        dest.writeInt(this.status);
        dest.writeString(this.statusName);
        dest.writeTypedList(this.orderSubs);
        dest.writeParcelable(this.user, flags);
        dest.writeLong(this.userId);
        dest.writeDouble(this.depositMoney);
        HljTimeUtils.writeDateTimeToParcel(dest, this.offLinePayTime);
    }

    protected WeddingCarOrderDetail(Parcel in) {
        this.id = in.readLong();
        this.actualMoney = in.readDouble();
        this.addressDetail = in.readString();
        this.aidMoney = in.readDouble();
        this.buyerName = in.readString();
        this.buyerPhone = in.readString();
        this.carInsurance = in.readParcelable(WeddingCarInsurance.class.getClassLoader());
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.earnestPercent = in.readDouble();
        this.expireTime = HljTimeUtils.readDateTimeToParcel(in);
        this.isOfflinePay = in.readByte() != 0;
        this.isPayAll = in.readInt();
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.merchantId = in.readLong();
        this.message = in.readString();
        this.orderNo = in.readString();
        this.originalMoney = in.readDouble();
        this.paidMoney = in.readDouble();
        this.payAllMoney = in.readDouble();
        this.payAllMoneyDiscountMoney = in.readDouble();
        this.payAllMoneyDiscountMoneyExpect = in.readInt();
        this.priceChanged = in.readByte() != 0;
        this.redPacketMoney = in.readDouble();
        this.redPacketNo = in.readString();
        this.redPacketName = in.readString();
        this.refundId = in.readLong();
        this.reminded = in.readString();
        this.startAt = HljTimeUtils.readDateTimeToParcel(in);
        this.status = in.readInt();
        this.statusName = in.readString();
        this.orderSubs = in.createTypedArrayList(WeddingCarOrderSub.CREATOR);
        this.user = in.readParcelable(User.class.getClassLoader());
        this.userId = in.readLong();
        this.depositMoney = in.readDouble();
        this.offLinePayTime = HljTimeUtils.readDateTimeToParcel(in);
    }

    public static final Creator<WeddingCarOrderDetail> CREATOR = new Creator<WeddingCarOrderDetail>() {
        @Override
        public WeddingCarOrderDetail createFromParcel(Parcel source) {
            return new WeddingCarOrderDetail(source);
        }

        @Override
        public WeddingCarOrderDetail[] newArray(int size) {return new WeddingCarOrderDetail[size];}
    };
}
