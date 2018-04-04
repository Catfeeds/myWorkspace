package me.suncloud.marrymemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 1/15/16.
 * 兼容parcelable
 */
public class CustomSetmealOrder extends TypeModel implements Parcelable {

    public static final int TYPE = 0;
    static final long serialVersionUID = 6276654757247744671L;
    long id;
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName(value = "buyer_name")
    String buyerName;
    @SerializedName(value = "buyer_phone")
    String buyerPhone;
    @SerializedName(value = "wedding_time")
    DateTime weddingTime;
    @SerializedName(value = "set_meal_id")
    long setMealId;
    @SerializedName(value = "refund_id")
    long refundId;
    @SerializedName(value = "merchant_id")
    long merchantId;
    @SerializedName(value = "order_no")
    String orderNo;
    @SerializedName(value = "protocol_images")
    ArrayList<com.hunliji.hljcommonlibrary.models.Photo> protocolPhotos;
    String message;
    @SerializedName(value = "actual_price")
    double actualPrice;
    @SerializedName(value = "red_packet_money")
    double redPacketMoney;
    @SerializedName(value = "red_packet_no")
    String redPacketNo;
    @SerializedName(value = "earnest_money")
    double earnestMoney;
    @SerializedName(value = "paid_money")
    double paidMoney;
    int status;
    @SerializedName(value = "old_status")
    int oldStatus;
    @SerializedName(value = "is_pay_all")
    boolean isPayAll;
    @SerializedName(value = "is_finished")
    boolean isFinished;
    int reason;
    String reasonName;
    @SerializedName(value = "created_at")
    DateTime createdAt;
    @SerializedName(value = "expired_at")
    DateTime expiredAt;
    @SerializedName(value = "has_exception")
    boolean hasException;
    @SerializedName(value = "order_history")
    ArrayList<OrderPayHistory> payHistories;
    @SerializedName(value = "refund")
    CSORefundInfo csoRefundInfo;

    // 下面的是间接转换中间值
    @SerializedName(value = "set_meal")
    JsonElement setMealJsonElement;
    @SerializedName(value = "comment")
    JsonElement commentJsonElement;

    CustomSetmeal customSetmeal;
    Comment commentObj;

    public CustomSetmealOrder(JSONObject jsonObject) {
        if (jsonObject != null) {
            id = jsonObject.optLong("id", 0);
            userId = jsonObject.optLong("user_id", 0);
            buyerName = JSONUtil.getString(jsonObject, "buyer_name");
            buyerPhone = JSONUtil.getString(jsonObject, "buyer_phone");
            Date date = JSONUtil.getDateFromFormatLong(jsonObject, "wedding_time", true);
            if (date == null) {
                weddingTime = new DateTime();
            } else {
                weddingTime = new DateTime(date);
            }
            merchantId = jsonObject.optLong("merchant_id", 0);
            orderNo = JSONUtil.getString(jsonObject, "order_no");
            message = JSONUtil.getString(jsonObject, "message");
            actualPrice = jsonObject.optDouble("actual_price", 0);
            redPacketMoney = jsonObject.optDouble("red_packet_money", 0);
            redPacketNo = JSONUtil.getString(jsonObject, "red_packet_no");
            earnestMoney = jsonObject.optDouble("earnest_money", 0);
            paidMoney = jsonObject.optDouble("paid_money", 0);
            status = jsonObject.optInt("status");
            oldStatus = jsonObject.optInt("old_status");
            isPayAll = jsonObject.optInt("is_pay_all", 0) > 0;
            isFinished = jsonObject.optInt("is_finished", 0) > 0;
            reasonName = JSONUtil.getString(jsonObject, "reason_name");
            reason = jsonObject.optInt("reason", 0);
            Date createdAtDate = JSONUtil.getDateFromFormatLong(jsonObject, "created_at", true);
            createdAt = new DateTime(createdAtDate);
            Date expiredAtDate = JSONUtil.getDateFromFormatLong(jsonObject, "expired_at", true);
            expiredAt = new DateTime(expiredAtDate);
            hasException = jsonObject.optInt("has_exception", 0) > 0;
            customSetmeal = new CustomSetmeal(jsonObject.optJSONObject("set_meal"));
            protocolPhotos = new ArrayList<>();
            JSONArray imgArr = jsonObject.optJSONArray("protocol_images");
            if (imgArr != null && imgArr.length() > 0) {
                for (int i = 0; i < imgArr.length(); i++) {
                    Photo photo = new Photo(imgArr.optJSONObject(i));
                    com.hunliji.hljcommonlibrary.models.Photo p = new com.hunliji
                            .hljcommonlibrary.models.Photo();
                    p.setId(photo.getId());
                    p.setImagePath(photo.getPath());
                    p.setWidth(photo.getWidth());
                    p.setHeight(photo.getHeight());
                    protocolPhotos.add(p);
                }
            }
            payHistories = new ArrayList<>();
            JSONArray hisArr = jsonObject.optJSONArray("order_history");
            if (hisArr != null && hisArr.length() > 0) {
                for (int i = 0; i < hisArr.length(); i++) {
                    OrderPayHistory orderPayHistory = new OrderPayHistory(hisArr.optJSONObject(i));
                    payHistories.add(orderPayHistory);
                }
            }
            commentObj = new Comment(jsonObject.optJSONObject("comment"));
            JSONObject refundObj = jsonObject.optJSONObject("refund");
            if (refundObj != null) {
                csoRefundInfo = new CSORefundInfo(refundObj);
            }
        }
    }

    @Override
    public int getType() {
        return TYPE;
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

    public DateTime getWeddingTime() {
        return weddingTime;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public ArrayList<com.hunliji.hljcommonlibrary.models.Photo> getProtocolPhotos() {
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

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getExpiredAt() {
        return expiredAt;
    }

    public boolean isHasException() {
        return hasException;
    }

    public CustomSetmeal getCustomSetmeal() {
        if (customSetmeal == null && setMealJsonElement != null) {
            try {
                customSetmeal = new CustomSetmeal(new JSONObject(setMealJsonElement.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return customSetmeal;
    }

    /**
     * 倒计时截止,自动关闭订单,对应服务器自动关闭
     */
    public void autoCancelOrder() {
        status = 93;
    }

    public String getStatusStr() {
        String str = "";
        switch (status) {
            case 10:
                str = "等待商家接单";
                break;
            case 11:
                str = "等待付款";
                break;
            case 15:
                str = "商家拒绝接单";
                break;
            case 87:
                str = "已付款";
                break;
            case 90:
                // 用户确认服务,待评价
                str = "交易成功";
                break;
            case 91:
                // 订单取消
                str = "交易关闭";
                break;
            case 92:
                // 已评价
                str = "交易成功";
                break;
            case 93:
                // 系统自动取消订单
                str = "交易关闭";
                break;
            case 20:
                str = "退款中";
                break;
            case 24:
                str = "退款成功";
                break;
        }

        return str;
    }

    public ArrayList<OrderPayHistory> getPayHistories() {
        return payHistories;
    }

    public boolean isEarnest() {
        return earnestMoney > 0;
    }

    public Comment getComment() {
        if (commentObj == null && commentJsonElement != null) {
            try {
                commentObj = new Comment(new JSONObject(commentJsonElement.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return commentObj;
    }

    public CSORefundInfo getCsoRefundInfo() {
        return csoRefundInfo;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeString(this.buyerName);
        dest.writeString(this.buyerPhone);
        HljTimeUtils.writeDateTimeToParcel(dest,this.weddingTime);
        dest.writeLong(this.setMealId);
        dest.writeLong(this.refundId);
        dest.writeLong(this.merchantId);
        dest.writeString(this.orderNo);
        dest.writeTypedList(this.protocolPhotos);
        dest.writeString(this.message);
        dest.writeDouble(this.actualPrice);
        dest.writeDouble(this.redPacketMoney);
        dest.writeString(this.redPacketNo);
        dest.writeDouble(this.earnestMoney);
        dest.writeDouble(this.paidMoney);
        dest.writeInt(this.status);
        dest.writeInt(this.oldStatus);
        dest.writeByte(this.isPayAll ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFinished ? (byte) 1 : (byte) 0);
        dest.writeInt(this.reason);
        dest.writeString(this.reasonName);
        HljTimeUtils.writeDateTimeToParcel(dest,this.createdAt);
        HljTimeUtils.writeDateTimeToParcel(dest,this.expiredAt);
        dest.writeByte(this.hasException ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.payHistories);
        dest.writeSerializable(this.csoRefundInfo);
        dest.writeSerializable(this.customSetmeal);
        dest.writeSerializable(this.commentObj);
    }

    protected CustomSetmealOrder(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.buyerName = in.readString();
        this.buyerPhone = in.readString();
        this.weddingTime = HljTimeUtils.readDateTimeToParcel(in);
        this.setMealId = in.readLong();
        this.refundId = in.readLong();
        this.merchantId = in.readLong();
        this.orderNo = in.readString();
        this.protocolPhotos = in.createTypedArrayList(com.hunliji.hljcommonlibrary.models.Photo
                .CREATOR);
        this.message = in.readString();
        this.actualPrice = in.readDouble();
        this.redPacketMoney = in.readDouble();
        this.redPacketNo = in.readString();
        this.earnestMoney = in.readDouble();
        this.paidMoney = in.readDouble();
        this.status = in.readInt();
        this.oldStatus = in.readInt();
        this.isPayAll = in.readByte() != 0;
        this.isFinished = in.readByte() != 0;
        this.reason = in.readInt();
        this.reasonName = in.readString();
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.expiredAt = HljTimeUtils.readDateTimeToParcel(in);
        this.hasException = in.readByte() != 0;
        this.payHistories = in.createTypedArrayList(OrderPayHistory.CREATOR);
        this.csoRefundInfo = (CSORefundInfo) in.readSerializable();
        this.customSetmeal = (CustomSetmeal) in.readSerializable();
        this.commentObj = (Comment) in.readSerializable();
    }

    public static final Creator<CustomSetmealOrder> CREATOR = new Creator<CustomSetmealOrder>() {
        @Override
        public CustomSetmealOrder createFromParcel(Parcel source) {
            return new CustomSetmealOrder(source);
        }

        @Override
        public CustomSetmealOrder[] newArray(int size) {return new CustomSetmealOrder[size];}
    };
}
