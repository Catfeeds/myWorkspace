package com.hunliji.marrybiz.model.revenue;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by hua_rong on 2017/8/17 0017
 * 绑定银行账号
 */

public class Bank implements Parcelable {

    private long id;
    private String account;//	银行账号或支付宝号
    private int agent;    //账号类型 （0：银行卡 / 1：支付宝）
    private String bank;//开户银行支行名称
    @SerializedName(value = "bank_cid")
    private long bankCid;//开户行所在地
    @SerializedName(value = "bank_name")
    private String bankName;//银行名
    @SerializedName(value = "created_at")
    private DateTime createdAt;
    @SerializedName(value = "merchant_id")
    private long merchantId;
    private String name;//户名
    private String reason;
    private int status;//1：通过 2失败
    private int type;//	1个人 2企业
    @SerializedName(value = "updated_at")
    private DateTime updatedAt;
    private String code;//提交银行卡信息的验证码
    @SerializedName(value = "city_name")
    private String cityName;
    private String province;
    @SerializedName(value = "change_protocol")
    private String changeProtocol;

    public String getChangeProtocol() {
        return changeProtocol;
    }

    public void setChangeProtocol(String changeProtocol) {
        this.changeProtocol = changeProtocol;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getAgent() {
        return agent;
    }

    public void setAgent(int agent) {
        this.agent = agent;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public long getBankCid() {
        return bankCid;
    }

    public void setBankCid(long bankCid) {
        this.bankCid = bankCid;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(long merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.account);
        dest.writeInt(this.agent);
        dest.writeString(this.bank);
        dest.writeLong(this.bankCid);
        dest.writeString(this.bankName);
        dest.writeSerializable(this.createdAt);
        dest.writeLong(this.merchantId);
        dest.writeString(this.name);
        dest.writeString(this.reason);
        dest.writeInt(this.status);
        dest.writeInt(this.type);
        dest.writeSerializable(this.updatedAt);
        dest.writeString(this.code);
        dest.writeString(this.cityName);
        dest.writeString(this.province);
        dest.writeString(this.changeProtocol);
    }

    public Bank() {}

    protected Bank(Parcel in) {
        this.id = in.readLong();
        this.account = in.readString();
        this.agent = in.readInt();
        this.bank = in.readString();
        this.bankCid = in.readLong();
        this.bankName = in.readString();
        this.createdAt = (DateTime) in.readSerializable();
        this.merchantId = in.readLong();
        this.name = in.readString();
        this.reason = in.readString();
        this.status = in.readInt();
        this.type = in.readInt();
        this.updatedAt = (DateTime) in.readSerializable();
        this.code = in.readString();
        this.cityName = in.readString();
        this.province = in.readString();
        this.changeProtocol = in.readString();
    }

    public static final Creator<Bank> CREATOR = new Creator<Bank>() {
        @Override
        public Bank createFromParcel(Parcel source) {return new Bank(source);}

        @Override
        public Bank[] newArray(int size) {return new Bank[size];}
    };
}
