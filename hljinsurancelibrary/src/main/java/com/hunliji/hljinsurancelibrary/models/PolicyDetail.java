package com.hunliji.hljinsurancelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;


/**
 * Created by hua_rong on 2017/5/25.
 * 保单详情
 */

public class PolicyDetail implements Parcelable {

    String id;
    @SerializedName(value = "actual_price")
    double actualPrice;//实际支付价格
    double price;//标准价格
    @SerializedName(value = "certi_no")
    String certiNo;//身份证号
    @SerializedName(value = "full_name")
    String fullName;
    @SerializedName(value = "policy_no")
    String policyNo;//保险单号
    int num;//购买份数
    String reason;//失败原因
    String phone;
    @SerializedName(value = "spouse_name")
    String spouseName;//配偶姓名
    @SerializedName(value = "created_at")
    DateTime createdAt;//填写时间
    @SerializedName(value = "trans_appl_date")
    DateTime transApplDate;//保险生效时间
    @SerializedName(value = "trans_end_date")
    DateTime transEndDate;//保险结束时间
    @SerializedName(value = "wedding_address")
    String weddingAddress;
    @SerializedName(value = "wedding_hotel")
    String weddingHotel;
    int status;
    @SerializedName(value = "user_id")
    long userId;
    @SerializedName(value = "epolicy_address")
    String epolicyAddress;//电子保单地址
    @SerializedName(value = "policy_address")
    String policyAddress;//无章保单地址*
    int type;//1黄金计划 2 钻石计划
    @SerializedName(value = "apply_policy_no")
    String applyPolicyNo;//投保单号
    ArrayList<Quarantees> guarantees;
    InsuranceProduct product;//保险信息
    @SerializedName(value = "trans_begin_date")
    DateTime transBeginDate;//保险有效开始时间
    @SerializedName(value = "giver_name")
    String giverName;
    @SerializedName(value = "spouse_certi_no")
    String spouseCertiNo;//配偶身份证
    @SerializedName(value = "spouse_phone")
    String spousePhone;//配偶手机号码

    public DateTime getTransBeginDate() {
        return transBeginDate;
    }

    public void setTransBeginDate(DateTime transBeginDate) {
        this.transBeginDate = transBeginDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCertiNo() {
        return certiNo;
    }

    public void setCertiNo(String certiNo) {
        this.certiNo = certiNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DateTime getTransApplDate() {
        return transApplDate;
    }

    public void setTransApplDate(DateTime transApplDate) {
        this.transApplDate = transApplDate;
    }

    public DateTime getTransEndDate() {
        return transEndDate;
    }

    public void setTransEndDate(DateTime transEndDate) {
        this.transEndDate = transEndDate;
    }

    public String getWeddingAddress() {
        return weddingAddress;
    }

    public void setWeddingAddress(String weddingDress) {
        this.weddingAddress = weddingDress;
    }

    public String getWeddingHotel() {
        return weddingHotel;
    }

    public void setWeddingHotel(String weddingHotel) {
        this.weddingHotel = weddingHotel;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEpolicyAddress() {
        return epolicyAddress;
    }

    public void setEpolicyAddress(String epolicyAddress) {
        this.epolicyAddress = epolicyAddress;
    }

    public String getPolicyAddress() {
        return policyAddress;
    }

    public void setPolicyAddress(String policyAddress) {
        this.policyAddress = policyAddress;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getApplyPolicyNo() {
        return applyPolicyNo;
    }

    public void setApplyPolicyNo(String applyPolicyNo) {
        this.applyPolicyNo = applyPolicyNo;
    }

    public ArrayList<Quarantees> getGuarantees() {
        return guarantees;
    }

    public void setGuarantees(ArrayList<Quarantees> guarantees) {
        this.guarantees = guarantees;
    }

    public InsuranceProduct getProduct() {
        if (this.product == null) {
            this.product = new InsuranceProduct();
        }
        return product;
    }

    public void setProduct(InsuranceProduct product) {
        this.product = product;
    }

    public String getGiverName() {
        return giverName;
    }

    public String getSpouseCertiNo() {
        return spouseCertiNo;
    }

    public String getSpousePhone() {
        return spousePhone;
    }

    public PolicyDetail() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDouble(this.actualPrice);
        dest.writeDouble(this.price);
        dest.writeString(this.certiNo);
        dest.writeString(this.fullName);
        dest.writeString(this.policyNo);
        dest.writeInt(this.num);
        dest.writeString(this.reason);
        dest.writeString(this.phone);
        dest.writeString(this.spouseName);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.transApplDate);
        dest.writeSerializable(this.transEndDate);
        dest.writeString(this.weddingAddress);
        dest.writeString(this.weddingHotel);
        dest.writeInt(this.status);
        dest.writeLong(this.userId);
        dest.writeString(this.epolicyAddress);
        dest.writeString(this.policyAddress);
        dest.writeInt(this.type);
        dest.writeString(this.applyPolicyNo);
        dest.writeTypedList(this.guarantees);
        dest.writeParcelable(this.product, flags);
        dest.writeSerializable(this.transBeginDate);
        dest.writeString(this.giverName);
        dest.writeString(this.spouseCertiNo);
        dest.writeString(this.spousePhone);
    }

    protected PolicyDetail(Parcel in) {
        this.id = in.readString();
        this.actualPrice = in.readDouble();
        this.price = in.readDouble();
        this.certiNo = in.readString();
        this.fullName = in.readString();
        this.policyNo = in.readString();
        this.num = in.readInt();
        this.reason = in.readString();
        this.phone = in.readString();
        this.spouseName = in.readString();
        this.createdAt = (DateTime) in.readSerializable();
        this.transApplDate = (DateTime) in.readSerializable();
        this.transEndDate = (DateTime) in.readSerializable();
        this.weddingAddress = in.readString();
        this.weddingHotel = in.readString();
        this.status = in.readInt();
        this.userId = in.readLong();
        this.epolicyAddress = in.readString();
        this.policyAddress = in.readString();
        this.type = in.readInt();
        this.applyPolicyNo = in.readString();
        this.guarantees = in.createTypedArrayList(Quarantees.CREATOR);
        this.product = in.readParcelable(InsuranceProduct.class.getClassLoader());
        this.transBeginDate = (DateTime) in.readSerializable();
        this.giverName = in.readString();
        this.spouseCertiNo = in.readString();
        this.spousePhone = in.readString();
    }

    public static final Creator<PolicyDetail> CREATOR = new Creator<PolicyDetail>() {
        @Override
        public PolicyDetail createFromParcel(Parcel source) {return new PolicyDetail(source);}

        @Override
        public PolicyDetail[] newArray(int size) {return new PolicyDetail[size];}
    };
}
