package me.suncloud.marrymemo.model.orders;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by werther on 16/10/18.
 * 服务订单填写订单后提交订单的提交post body
 */

public class ServiceOrderSubmitBody implements Parcelable {
    @SerializedName(value = "buyer_name")
    String buyerName;
    @SerializedName(value = "buyer_phone")
    String buyerPhone;
    String message;
    @SerializedName(value = "pay_type")
    int payType; // 2: 定金支付 1： 全额支付 3：支付余款
    @SerializedName(value = "red_packet_no")
    String redPacketNo;
    @SerializedName(value = "set_meal_id")
    long setMealId;
    @SerializedName(value = "user_coupon_id")
    long userCouponId;
    @SerializedName(value = "wedding_time")
    DateTime weddingTime;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getRedPacketNo() {
        return redPacketNo;
    }

    public void setRedPacketNo(String redPacketNo) {
        this.redPacketNo = redPacketNo;
    }

    public long getSetMealId() {
        return setMealId;
    }

    public void setSetMealId(long setMealId) {
        this.setMealId = setMealId;
    }

    public long getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(long userCouponId) {
        this.userCouponId = userCouponId;
    }

    public DateTime getWeddingTime() {
        return weddingTime;
    }

    public void setWeddingTime(DateTime weddingTime) {
        this.weddingTime = weddingTime;
    }

    public ServiceOrderSubmitBody() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.buyerName);
        dest.writeString(this.buyerPhone);
        dest.writeString(this.message);
        dest.writeInt(this.payType);
        dest.writeString(this.redPacketNo);
        dest.writeLong(this.setMealId);
        dest.writeLong(this.userCouponId);
        dest.writeLong(this.weddingTime == null ? -1 : this.weddingTime.getMillis());
    }

    protected ServiceOrderSubmitBody(Parcel in) {
        this.buyerName = in.readString();
        this.buyerPhone = in.readString();
        this.message = in.readString();
        this.payType = in.readInt();
        this.redPacketNo = in.readString();
        this.setMealId = in.readLong();
        this.userCouponId = in.readLong();
        long millis = in.readLong();
        if (millis > 0) {
            this.weddingTime = new DateTime(millis);
        }
    }

    public static final Creator<ServiceOrderSubmitBody> CREATOR = new
            Creator<ServiceOrderSubmitBody>() {
        @Override
        public ServiceOrderSubmitBody createFromParcel(Parcel source) {
            return new ServiceOrderSubmitBody(source);
        }

        @Override
        public ServiceOrderSubmitBody[] newArray(int size) {return new
                ServiceOrderSubmitBody[size];}
    };
}
