package com.hunliji.marrybiz.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by mo_yu on 2017/12/13.商家订单（Bd）
 */

public class MerchantOrder implements Parcelable {

    long id;
    @SerializedName("merchant_id")
    long merchantId;
    @SerializedName("trade_no")
    String tradeNo;//订单号
    @SerializedName("is_multi_pay")
    boolean isMultiPay;//1分笔 0不分
    int channel;//1线上 2bd线下
    @SerializedName("cash_flow_id")
    long cashFlowId;//线上支付时对应的流水ID
    @SerializedName("bd_id")
    long bdId;//BD人员的ID
    @SerializedName("bd_name")
    String bdName;//BD人员的姓名
    String remark;//备注
    String attach;//附件
    @SerializedName("actual_money")
    double actualMoney;//订单总价
    @SerializedName("paid_money")
    double paidMoney;//已支付金额
    int status;//0待支付、1已支付、2已关闭、3审批中、4审批拒绝
    @SerializedName("created_at")
    DateTime createAt;//创建时间
    @SerializedName("expire_time")
    DateTime expireTime;//过期时间
    @SerializedName("pay_time")
    DateTime payTime;//支付时间
    @SerializedName("order_subs")
    List<MerchantOrderSub> merchantOrderSubs;

    public static final int ORDER_WAIT_FOR_PAY = 0;//待支付
    public static final int ORDER_HAVE_PAID = 1;//已支付
    public static final int ORDER_PAY_CLOSED = 2;//已关闭
    public static final int ORDER_IN_REVIEW = 3;//审核中
    public static final int ORDER_REFUSE = 4;//审核拒绝

    public static final int ONLINE_CHANNEL = 1;//线上渠道
    public static final int OFFLINE_BD_CHANNEL = 2;//BD线下

    public long getId() {
        return id;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public boolean isMultiPay() {
        return isMultiPay;
    }

    public int getChannel() {
        return channel;
    }

    public long getCashFlowId() {
        return cashFlowId;
    }

    public long getBdId() {
        return bdId;
    }

    public String getBdName() {
        return bdName;
    }

    public String getRemark() {
        return remark;
    }

    public String getAttach() {
        return attach;
    }

    public double getActualMoney() {
        return actualMoney;
    }

    public double getPaidMoney() {
        return paidMoney;
    }

    public int getStatus() {
        return status;
    }

    public DateTime getCreateAt() {
        return createAt;
    }

    public DateTime getExpireTime() {
        return expireTime;
    }

    public DateTime getPayTime() {
        return payTime;
    }

    public List<MerchantOrderSub> getMerchantOrderSubs() {
        return merchantOrderSubs;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.merchantId);
        dest.writeString(this.tradeNo);
        dest.writeByte(this.isMultiPay ? (byte) 1 : (byte) 0);
        dest.writeInt(this.channel);
        dest.writeLong(this.cashFlowId);
        dest.writeLong(this.bdId);
        dest.writeString(this.bdName);
        dest.writeString(this.remark);
        dest.writeString(this.attach);
        dest.writeDouble(this.actualMoney);
        dest.writeDouble(this.paidMoney);
        dest.writeInt(this.status);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createAt);
        HljTimeUtils.writeDateTimeToParcel(dest, this.expireTime);
        HljTimeUtils.writeDateTimeToParcel(dest, this.payTime);
        dest.writeTypedList(this.merchantOrderSubs);
    }

    public MerchantOrder() {}

    protected MerchantOrder(Parcel in) {
        this.id = in.readLong();
        this.merchantId = in.readLong();
        this.tradeNo = in.readString();
        this.isMultiPay = in.readByte() != 0;
        this.channel = in.readInt();
        this.cashFlowId = in.readLong();
        this.bdId = in.readLong();
        this.bdName = in.readString();
        this.remark = in.readString();
        this.attach = in.readString();
        this.actualMoney = in.readDouble();
        this.paidMoney = in.readDouble();
        this.status = in.readInt();
        this.createAt = HljTimeUtils.readDateTimeToParcel(in);
        this.expireTime = HljTimeUtils.readDateTimeToParcel(in);
        this.payTime = HljTimeUtils.readDateTimeToParcel(in);
        this.merchantOrderSubs = in.createTypedArrayList(MerchantOrderSub.CREATOR);
    }

    public static final Parcelable.Creator<MerchantOrder> CREATOR = new Parcelable
            .Creator<MerchantOrder>() {
        @Override
        public MerchantOrder createFromParcel(Parcel source) {return new MerchantOrder(source);}

        @Override
        public MerchantOrder[] newArray(int size) {return new MerchantOrder[size];}
    };
}
