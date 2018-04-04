package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by werther on 16/10/11.
 * 服务订单的子单信息
 */

public class ServiceOrderSub implements Parcelable {
    long id;
    @SerializedName(value = "order_no")
    String orderNo;
    @SerializedName(value = "actual_price")
    double actualPrice;
    @SerializedName(value = "aid_money")
    double aidMoney; // 优惠券金额
    @SerializedName(value = "earnest_money")
    double earnestMoney; // 需要支付的定金，也用于在待付款状态根据其值判断用户选择的付款方式
    @SerializedName(value = "expire_time")
    DateTime expireTime;
    @SerializedName(value = "is_gift")
    boolean isGift;
    @SerializedName(value = "money_status")
    int moneyStatus;
    @SerializedName(value = "paid_money")
    double paidMoney;
    @SerializedName(value = "pay_all_money")
    double payAllSavedMoney;
    @SerializedName(value = "prd_num")
    int prdNum;
    @SerializedName(value = "prdid")
    long prdId;
    @SerializedName(value = "reason_name")
    int reasonName;
    String reason;
    @SerializedName(value = "red_packet_money")
    double redPacketMoney;
    int status;
    @SerializedName(value = "status_name")
    String statusName; // 由服务器返回的状态描述，老版本的状态展示方式，新版的由用户端自己判断状态和展示
    @SerializedName(value = "is_finished")
    boolean isFinished; // 商家是否确认服务
    @SerializedName("product")
    Work work;
    Merchant merchant;
    @SerializedName("intent_money")
    double intentMoney; // v2订单接口新增字段，意向金金额
    @SerializedName("protocol_images")
    ArrayList<Photo> protocolImages;

    public static final int STATUS_WAITING_FOR_THE_PAYMENT = 10;
    public static final int STATUS_MERCHANT_ACCEPT_ORDER = 11;
    public static final int STATUS_WAITING_FOR_ACCEPT_ORDER = 14;
    public static final int STATUS_MERCHANT_REFUSE_ORDER = 15;
    public static final int STATUS_REFUND_REVIEWING = 20;
    public static final int STATUS_CANCEL_REFUND = 21;
    public static final int STATUS_REFUND_APPROVED = 22;
    public static final int STATUS_REFUSE_REFUND = 23;
    public static final int STATUS_REFUND_SUCCEED = 24;
    public static final int STATUS_SERVICE_COMPLETE = 90;
    public static final int STATUS_ORDER_CLOSED = 91;
    public static final int STATUS_ORDER_COMMENTED = 92;
    public static final int STATUS_ORDER_AUTO_CLOSED = 93;

    public static final int MONEY_STATUS_PAID_INTENT = 9; // 已付意向金
    public static final int MONEY_STATUS_PAID_DEPOSIT = 12; // 已付定金
    public static final int MONEY_STATUS_PAID_ALL = 13; // 已付全款
    // 10等待付款
    // 11商家已接单
    // 14等待商家接单
    // 15等待拒绝接单
    // 20退款审核中
    // 21取消申请退款
    // 22退款审核通过
    // 23拒绝退款
    // 24退款成功
    // 90交易成功
    // 91交易关闭
    // 92已评论
    // 93系统自动关闭


    public boolean isFinished() {
        return isFinished;
    }

    public Work getWork() {
        return work;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public long getId() {
        return id;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    /**
     * 优惠券金额
     *
     * @return
     */
    public double getAidMoney() {
        return aidMoney;
    }

    public double getEarnestMoney() {
        return earnestMoney;
    }

    public DateTime getExpireTime() {
        return expireTime;
    }

    public boolean isGift() {
        return isGift;
    }

    public int getMoneyStatus() {
        return moneyStatus;
    }

    public double getPaidMoney() {
        return paidMoney;
    }

    public double getPayAllSavedMoney() {
        return payAllSavedMoney;
    }

    public int getPrdNum() {
        return prdNum;
    }

    public long getPrdId() {
        return prdId;
    }

    public int getReasonName() {
        return reasonName;
    }

    public String getReason() {
        return reason;
    }

    public double getRedPacketMoney() {
        return redPacketMoney;
    }

    public int getStatus() {
        return status;
    }

    public String getOrderNo() {
        return orderNo;
    }


    public double getIntentMoney() {
        return intentMoney;
    }


    /**
     * 由服务器返回的状态描述，老版本的状态展示方式，新版的由用户端自己判断状态和展示
     * 使用getStatusStr
     *
     * @return
     */
    private String getStatusName() {
        return statusName;
    }

    public String getStatusStr() {
        switch (status) {
            case STATUS_WAITING_FOR_THE_PAYMENT:
                return "等待付款";
            case STATUS_WAITING_FOR_ACCEPT_ORDER:
                //                return "等待商家接单";
                return orderPaymentStatus();
            case STATUS_MERCHANT_ACCEPT_ORDER:
                //                if (isFinished) {
                //                    return "待确认服务";
                //                }else {
                return orderPaymentStatus();
            //            return "商家已接单";
            //                }
            case STATUS_MERCHANT_REFUSE_ORDER:
                return "商家拒绝接单";
            case STATUS_SERVICE_COMPLETE:
                return "交易成功";
            case STATUS_ORDER_COMMENTED:
                return "已评价";
            case STATUS_ORDER_CLOSED:
            case STATUS_ORDER_AUTO_CLOSED:
                return "订单关闭";
            // 下面的是退款专挑
            case STATUS_REFUND_REVIEWING:
                return "退款审核中";
            case STATUS_REFUND_APPROVED:
                return "退款审核通过";
            case STATUS_CANCEL_REFUND:
                return "取消退款";
            case STATUS_REFUSE_REFUND:
                return "拒绝退款";
            case STATUS_REFUND_SUCCEED:
                return "退款成功";
            default:
                return "订单状态";
        }
    }

    private String orderPaymentStatus() {
        if (moneyStatus == MONEY_STATUS_PAID_ALL) {
            return "已付款";
        } else {
            return "已付部分款";
        }
    }

    /**
     * 是不是"已付部分款"这个状态
     *
     * @return
     */
    public boolean isPayedSomeStatus() {
        if (status == STATUS_WAITING_FOR_ACCEPT_ORDER || status == STATUS_MERCHANT_ACCEPT_ORDER) {
            if (moneyStatus != MONEY_STATUS_PAID_ALL) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Photo> getProtocolImages() {
        return protocolImages;
    }

    public ServiceOrderSub() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.orderNo);
        dest.writeDouble(this.actualPrice);
        dest.writeDouble(this.aidMoney);
        dest.writeDouble(this.earnestMoney);
        dest.writeSerializable(this.expireTime);
        dest.writeByte(this.isGift ? (byte) 1 : (byte) 0);
        dest.writeInt(this.moneyStatus);
        dest.writeDouble(this.paidMoney);
        dest.writeDouble(this.payAllSavedMoney);
        dest.writeInt(this.prdNum);
        dest.writeLong(this.prdId);
        dest.writeInt(this.reasonName);
        dest.writeString(this.reason);
        dest.writeDouble(this.redPacketMoney);
        dest.writeInt(this.status);
        dest.writeString(this.statusName);
        dest.writeParcelable(this.work, flags);
        dest.writeParcelable(this.merchant, flags);
        dest.writeDouble(this.intentMoney);
        dest.writeTypedList(this.protocolImages);
    }

    protected ServiceOrderSub(Parcel in) {
        this.id = in.readLong();
        this.orderNo = in.readString();
        this.actualPrice = in.readDouble();
        this.aidMoney = in.readDouble();
        this.earnestMoney = in.readDouble();
        this.expireTime = (DateTime) in.readSerializable();
        this.isGift = in.readByte() != 0;
        this.moneyStatus = in.readInt();
        this.paidMoney = in.readDouble();
        this.payAllSavedMoney = in.readDouble();
        this.prdNum = in.readInt();
        this.prdId = in.readLong();
        this.reasonName = in.readInt();
        this.reason = in.readString();
        this.redPacketMoney = in.readDouble();
        this.status = in.readInt();
        this.statusName = in.readString();
        this.work = in.readParcelable(Work.class.getClassLoader());
        this.merchant = in.readParcelable(Merchant.class.getClassLoader());
        this.intentMoney = in.readDouble();
        this.protocolImages = in.createTypedArrayList(Photo.CREATOR);
    }

    public static final Creator<ServiceOrderSub> CREATOR = new Creator<ServiceOrderSub>() {
        @Override
        public ServiceOrderSub createFromParcel(Parcel source) {return new ServiceOrderSub(source);}

        @Override
        public ServiceOrderSub[] newArray(int size) {return new ServiceOrderSub[size];}
    };

    public void setExpireTime(DateTime expireTime) {
        this.expireTime = expireTime;
    }
}
