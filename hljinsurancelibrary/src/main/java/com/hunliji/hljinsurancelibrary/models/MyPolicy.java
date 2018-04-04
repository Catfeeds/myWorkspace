package com.hunliji.hljinsurancelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * Created by hua_rong on 2017/5/25.
 * 我的保单
 */

public class MyPolicy implements Parcelable {

    //0未支付 1未提交保单 2待生效 3保障中 4已终止 5投保失败 6已退款
    public static final int STATUS_UNPAY = 0;
    public static final int STATUS_UNSUBMITTED = 1;
    public static final int STATUS_TO_BE_EFFECTIVE = 2;
    public static final int STATUS_PROTECT = 3;
    public static final int STATUS_FINISHED = 4;
    public static final int STATUS_FAILED = 5;
    public static final int STATUS_REFUND = 6;

    String id;
    String beneficiary;//受益人
    int num;//保单份数
    String party;//被保险人
    double price;
    String reason;//失败原因
    int status;//0未支付 1未提交保单 2待生效 3保障中 4已终止 5投保失败 6已退款
    @SerializedName(value = "trans_begin_date")
    DateTime transBeginDate;//保险有效开始时间
    @SerializedName(value = "trans_end_date")
    DateTime transEndDate;//保险有效结束时间
    @SerializedName(value = "created_at")
    DateTime createdAt;
    int type;//1.婚礼保黄金，2婚礼宝钻石 // 蜜月保
    @SerializedName(value = "user_id")
    long userId;
    InsuranceProduct product;
    PolicyDetail detail;
    PolicyCard card;//请帖
    @SerializedName(value = "giver_name")
    String giverName;

    public transient static final int TYPE_GOLD = 1; //婚礼宝黄金计划
    public transient static final int TYPE_DIAMOND = 2; //婚礼宝钻石计划
    public transient static final int TYPE_MI_YUE_BAO = 3;//蜜月保

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DateTime getTransBeginDate() {
        return transBeginDate;
    }

    public void setTransBeginDate(DateTime transBeginDate) {
        this.transBeginDate = transBeginDate;
    }

    public DateTime getTransEndDate() {
        return transEndDate;
    }

    public void setTransEndDate(DateTime transEndDate) {
        this.transEndDate = transEndDate;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public InsuranceProduct getProduct() {
        if (this.product == null) {
            this.product = new InsuranceProduct();
        }
        return this.product;
    }

    public void setProduct(InsuranceProduct product) {
        this.product = product;
    }

    public PolicyDetail getDetail() {
        return detail;
    }

    public void setDetail(PolicyDetail detail) {
        this.detail = detail;
    }

    public PolicyCard getCard() {
        return card;
    }

    public String getGiverName() {
        return giverName;
    }

    public MyPolicy() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.beneficiary);
        dest.writeInt(this.num);
        dest.writeString(this.party);
        dest.writeDouble(this.price);
        dest.writeString(this.reason);
        dest.writeInt(this.status);
        HljTimeUtils.writeDateTimeToParcel(dest, this.transBeginDate);
        HljTimeUtils.writeDateTimeToParcel(dest, this.transEndDate);
        HljTimeUtils.writeDateTimeToParcel(dest, this.createdAt);
        dest.writeInt(this.type);
        dest.writeLong(this.userId);
        dest.writeParcelable(this.product, flags);
        dest.writeParcelable(this.detail, flags);
        dest.writeParcelable(this.card, flags);
        dest.writeString(this.giverName);
    }

    protected MyPolicy(Parcel in) {
        this.id = in.readString();
        this.beneficiary = in.readString();
        this.num = in.readInt();
        this.party = in.readString();
        this.price = in.readDouble();
        this.reason = in.readString();
        this.status = in.readInt();
        this.transBeginDate = HljTimeUtils.readDateTimeToParcel(in);
        this.transEndDate = HljTimeUtils.readDateTimeToParcel(in);
        this.createdAt = HljTimeUtils.readDateTimeToParcel(in);
        this.type = in.readInt();
        this.userId = in.readLong();
        this.product = in.readParcelable(InsuranceProduct.class.getClassLoader());
        this.detail = in.readParcelable(PolicyDetail.class.getClassLoader());
        this.card = in.readParcelable(PolicyCard.class.getClassLoader());
        this.giverName = in.readString();
    }

    public static final Creator<MyPolicy> CREATOR = new Creator<MyPolicy>() {
        @Override
        public MyPolicy createFromParcel(Parcel source) {return new MyPolicy(source);}

        @Override
        public MyPolicy[] newArray(int size) {return new MyPolicy[size];}
    };
}
