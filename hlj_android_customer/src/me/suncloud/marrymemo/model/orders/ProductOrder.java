package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by werther on 17/1/3.
 * 婚品订单
 */

public class ProductOrder implements Parcelable {
    long id;
    @SerializedName("order_no")
    String orderNo;
    @SerializedName("express_id")
    long expressId;
    @SerializedName("red_packet_no")
    String redPacketNo;
    @SerializedName("red_packet_money")
    double redPacketMoney;
    @SerializedName("shipping_fee")
    double shippingFee;
    @SerializedName("price_changed")
    boolean priceChanged;
    int status = 10; // 默认10
    @SerializedName("actual_money")
    double actualMoney;
    @SerializedName("expire_time")
    DateTime expireTime;
    @SerializedName("created_at")
    DateTime createdAt;
    @SerializedName("ordersubs")
    List<ProductSubOrder> subOrders;
    Merchant merchant;
    @SerializedName("express")
    ProductOrderExpressInfo expressInfo;
    int reason;
    String remark; // 用户提交订单是的备注
    @SerializedName("address")
    ShippingAddress shippingAddress;
    @SerializedName("user_coupon_id")
    String userCouponId;
    @SerializedName("aid_money")
    double aidMoney;// 优惠券金额

    public static final int STATUS_WAITING_FOR_THE_PAYMENT = 10; // 待付款
    public static final int STATUS_WAITING_FOR_SHIPPING = 88; // 代发货
    public static final int STATUS_WAITING_FOR_ACCEPT_SHIPPING = 89; // 待收货
    public static final int STATUS_ORDER_SUCCED = 90; // 交易成功
    public static final int STATUS_ORDER_COMMENTED = 92; // 交易成功,已评价
    public static final int STATUS_ORDER_CLOSED = 91; // 用户关闭订单
    public static final int STATUS_AUTO_CLOSED = 93; // 系统自动关闭

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

    public boolean isPriceChanged() {
        return priceChanged;
    }

    public int getStatus() {
        return status;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public DateTime getExpireTime() {
        return expireTime;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public List<ProductSubOrder> getSubOrders() {
        return subOrders;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public ProductOrderExpressInfo getExpressInfo() {
        return expressInfo;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public int getReason() {
        return reason;
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

    /**
     * 将订单状态改为"交易关闭"
     */
    public void setToCommented() {
        this.status = 92;
    }

    /**
     * 倒计时截止,自动关闭订单,对应服务器自动关闭
     */
    public void autoCancelOrder() {
        status = 93;
    }

    public String getRemark() {
        return remark;
    }

    public String getUserCouponId() {
        return userCouponId;
    }

    public ProductOrder() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.orderNo);
        dest.writeLong(this.expressId);
        dest.writeString(this.redPacketNo);
        dest.writeDouble(this.redPacketMoney);
        dest.writeDouble(this.shippingFee);
        dest.writeDouble(this.aidMoney);
        dest.writeByte(this.priceChanged ? (byte) 1 : (byte) 0);
        dest.writeInt(this.status);
        dest.writeDouble(this.actualMoney);
        dest.writeSerializable(this.expireTime);
        dest.writeSerializable(this.createdAt);
        dest.writeTypedList(this.subOrders);
        dest.writeParcelable(this.merchant, flags);
        dest.writeParcelable(this.expressInfo, flags);
        dest.writeInt(this.reason);
        dest.writeString(this.remark);
        dest.writeParcelable(this.shippingAddress, flags);
        dest.writeString(this.userCouponId);
        dest.writeDouble(this.aidMoney);
    }

    protected ProductOrder(Parcel in) {
        this.id = in.readLong();
        this.orderNo = in.readString();
        this.expressId = in.readLong();
        this.redPacketNo = in.readString();
        this.redPacketMoney = in.readDouble();
        this.shippingFee = in.readDouble();
        this.aidMoney = in.readDouble();
        this.priceChanged = in.readByte() != 0;
        this.status = in.readInt();
        this.actualMoney = in.readDouble();
        this.expireTime = (DateTime) in.readSerializable();
        this.createdAt = (DateTime) in.readSerializable();
        this.subOrders = in.createTypedArrayList(ProductSubOrder.CREATOR);
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.expressInfo = in.readParcelable(ProductOrderExpressInfo.class.getClassLoader());
        this.reason = in.readInt();
        this.remark = in.readString();
        this.shippingAddress = in.readParcelable(ShippingAddress.class.getClassLoader());
        this.userCouponId = in.readString();
        this.aidMoney = in.readDouble();
    }

    public static final Creator<ProductOrder> CREATOR = new Creator<ProductOrder>() {
        @Override
        public ProductOrder createFromParcel(Parcel source) {return new ProductOrder(source);}

        @Override
        public ProductOrder[] newArray(int size) {return new ProductOrder[size];}
    };
}
