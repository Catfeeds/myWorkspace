package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.WorkRule;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static me.suncloud.marrymemo.model.orders.ServiceOrderSub.MONEY_STATUS_PAID_ALL;
import static me.suncloud.marrymemo.model.orders.ServiceOrderSub.MONEY_STATUS_PAID_DEPOSIT;

/**
 * Created by werther on 16/10/11.
 * 普通套餐(本地服务)订单
 */

public class ServiceOrder implements Parcelable {
    long id;
    @SerializedName(value = "order_no")
    String orderNo;
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "buyer_name")
    String buyerName;
    @SerializedName(value = "buyer_phone")
    String buyerPhone;
    @SerializedName(value = "address")
    int addressInt;
    @SerializedName(value = "has_exception")
    boolean hasException;
    @SerializedName(value = "wedding_time")
    DateTime weddingTime;
    String message;
    @SerializedName(value = "order_sub")
    ServiceOrderSub orderSub;
    WorkRule rule; // 活动信息
    // 下订单是选择的支付方式 1：全款支付，2：定金支付, 5：意向金支付
    // 这里的pay_type是指订单的付款方式类型，与支付接口的pay_type没有直接关系，一旦订单生成，这个值就确定而且不会再改变
    @SerializedName(value = "pay_type")
    int orderPayType;
    @SerializedName(value = "free_order_link")
    String freeOrderLink;
    @SerializedName("is_installment")
    boolean isInstallment;

    public static final int ORDER_PAY_TYPE_PAY_ALL = 1;
    public static final int ORDER_PAY_TYPE_DEPOSIT = 2;
    public static final int ORDER_PAY_TYPE_INTENT = 5;

    public String getFreeOrderLink() {
        return freeOrderLink;
    }

    public String getMessage() {
        return message;
    }

    public long getId() {
        return id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public long getUserId() {
        return userId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public int getAddressInt() {
        return addressInt;
    }

    public boolean isHasException() {
        return hasException;
    }

    public DateTime getWeddingTime() {
        return weddingTime;
    }

    public void setWeddingTime(DateTime weddingTime) {
        this.weddingTime = weddingTime;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public ServiceOrderSub getOrderSub() {
        return orderSub;
    }

    public WorkRule getRule() {
        return rule;
    }

    /**
     * 计算用户实付金额
     *
     * @return
     */
    public double getCustomerRealPayMoney() {
        double customerRealPay;
        if (getOrderPayType() == ORDER_PAY_TYPE_INTENT) {
            // 意向金支付，需要判断是否经过定金支付
            if (orderSub.getMoneyStatus() == MONEY_STATUS_PAID_DEPOSIT || (orderSub
                    .getMoneyStatus() == MONEY_STATUS_PAID_ALL && orderSub.getEarnestMoney() > 0)) {
                // 确定正处于已付定金状态，或者已经处于付完全款且定金数额大于零（如果直接通过全款支付的话，服务器会把原来的定金earnestMoney设置为0）
                customerRealPay = orderSub.getIntentMoney() + CommonUtil.positive(orderSub
                        .getEarnestMoney() - orderSub.getIntentMoney()) + CommonUtil.positive(
                        orderSub.getActualPrice() - orderSub.getEarnestMoney() - orderSub
                                .getAidMoney() - orderSub.getRedPacketMoney());
            } else if (orderSub.getMoneyStatus() == MONEY_STATUS_PAID_ALL && orderSub
                    .getEarnestMoney() <= 0) {
                customerRealPay = orderSub.getIntentMoney() + CommonUtil.positive(orderSub
                        .getActualPrice() - orderSub.getIntentMoney() - orderSub
                        .getRedPacketMoney() - orderSub.getAidMoney() - orderSub
                        .getPayAllSavedMoney());
            } else {
                customerRealPay = orderSub.getIntentMoney() + CommonUtil.positive(orderSub
                        .getActualPrice() - orderSub.getIntentMoney() - orderSub
                        .getRedPacketMoney() - orderSub.getAidMoney());
            }
        } else if (getOrderPayType() == ORDER_PAY_TYPE_DEPOSIT) {
            // 定金支付
            customerRealPay = orderSub.getEarnestMoney() + CommonUtil.positive(orderSub
                    .getActualPrice() - orderSub.getEarnestMoney() - orderSub.getAidMoney() -
                    orderSub.getRedPacketMoney());
        } else {
            customerRealPay = orderSub.getActualPrice() - orderSub.getAidMoney() - orderSub
                    .getRedPacketMoney() - orderSub.getPayAllSavedMoney();
        }

        return customerRealPay;
    }


    /**
     * 下订单是选择的支付方式 1：全款支付，2：定金支付, 5：意向金支付
     * 这里的pay_type是指订单的付款方式类型，与支付接口的pay_type没有直接关系，一旦订单生成，这个值就确定而且不会再改变
     *
     * @return
     */
    public int getOrderPayType() {
        return orderPayType;
    }

    public boolean isInstallment() {
        return isInstallment;
    }

    /**
     * 兼容之前的订单在支付的时候需要的一种json object数据
     *
     * @return
     */
    public String getPrds() {
        JSONObject jsonObject = new JSONObject();
        JSONArray orderArray = new JSONArray();
        JSONObject orderJson = new JSONObject();
        try {
            orderJson.put("id", orderSub.getPrdId());
            orderJson.put("num", 1);
            orderJson.put("type", 0);
            orderArray.put(orderJson);
            jsonObject.put("prds", orderArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public ServiceOrder() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.orderNo);
        dest.writeLong(this.userId);
        dest.writeSerializable(this.createdAt);
        dest.writeString(this.buyerName);
        dest.writeString(this.buyerPhone);
        dest.writeInt(this.addressInt);
        dest.writeByte(this.hasException ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.weddingTime);
        dest.writeString(this.message);
        dest.writeParcelable(this.orderSub, flags);
        dest.writeParcelable(this.rule, flags);
        dest.writeInt(this.orderPayType);
        dest.writeString(this.freeOrderLink);
        dest.writeByte(this.isInstallment ? (byte) 1 : (byte) 0);
    }

    protected ServiceOrder(Parcel in) {
        this.id = in.readLong();
        this.orderNo = in.readString();
        this.userId = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.buyerName = in.readString();
        this.buyerPhone = in.readString();
        this.addressInt = in.readInt();
        this.hasException = in.readByte() != 0;
        this.weddingTime = (DateTime) in.readSerializable();
        this.message = in.readString();
        this.orderSub = in.readParcelable(ServiceOrderSub.class.getClassLoader());
        this.rule = in.readParcelable(WorkRule.class.getClassLoader());
        this.orderPayType = in.readInt();
        this.freeOrderLink = in.readString();
        this.isInstallment = in.readByte() != 0;
    }

    public static final Creator<ServiceOrder> CREATOR = new Creator<ServiceOrder>() {
        @Override
        public ServiceOrder createFromParcel(Parcel source) {return new ServiceOrder(source);}

        @Override
        public ServiceOrder[] newArray(int size) {return new ServiceOrder[size];}
    };
}
