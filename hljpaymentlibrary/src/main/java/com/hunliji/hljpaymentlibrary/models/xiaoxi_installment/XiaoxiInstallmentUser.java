package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.hunliji.hljhttplibrary.utils.GsonUtil;

import java.util.List;

/**
 * 小犀分期user
 * Created by chen_bin on 2017/11/9 0009.
 */
public class XiaoxiInstallmentUser implements Parcelable {
    @SerializedName(value = "assetUserId")
    private String assetUserId; //婚礼纪的用户id,不经过加密
    @SerializedName(value = "province")
    private String province;
    @SerializedName(value = "city")
    private String city;
    @SerializedName(value = "area")
    private String area;
    @SerializedName(value = "address")
    private String address;
    @SerializedName(value = "companyName")
    private String companyName;
    @SerializedName(value = "salary")
    private int salary;
    @SerializedName(value = "_riskDataOff")
    private int riskDataOff;
    @SerializedName(value = "emergencyContacts")
    private List<EmergencyContact> contacts;
    @SerializedName(value = "extInfo")
    private ExtInfo extInfo;
    @SerializedName(value = "_riskData")
    private JsonElement riskData;

    public String getAssetUserId() {
        return assetUserId;
    }

    public void setAssetUserId(String assetUserId) {
        this.assetUserId = assetUserId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public int getRiskDataOff() {
        return riskDataOff;
    }

    public void setRiskDataOff(int riskDataOff) {
        this.riskDataOff = riskDataOff;
    }

    public List<EmergencyContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<EmergencyContact> contacts) {
        this.contacts = contacts;
    }

    public ExtInfo getExtInfo() {
        if (extInfo == null) {
            extInfo = new ExtInfo();
        }
        return extInfo;
    }

    public void setExtInfo(ExtInfo extInfo) {
        this.extInfo = extInfo;
    }

    public JsonElement getRiskData() {
        return riskData;
    }

    public void setRiskData(JsonElement riskData) {
        this.riskData = riskData;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.assetUserId);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.area);
        dest.writeString(this.address);
        dest.writeString(this.companyName);
        dest.writeInt(this.salary);
        dest.writeInt(this.riskDataOff);
        dest.writeTypedList(this.contacts);
        dest.writeParcelable(this.extInfo, flags);
        dest.writeString(GsonUtil.getGsonInstance()
                .toJson(this.riskData));
    }

    public XiaoxiInstallmentUser() {

    }

    protected XiaoxiInstallmentUser(Parcel in) {
        this.assetUserId = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.area = in.readString();
        this.address = in.readString();
        this.companyName = in.readString();
        this.salary = in.readInt();
        this.riskDataOff = in.readInt();
        this.contacts = in.createTypedArrayList(EmergencyContact.CREATOR);
        this.extInfo = in.readParcelable(ExtInfo.class.getClassLoader());
        this.riskData = GsonUtil.getGsonInstance()
                .fromJson(in.readString(), JsonElement.class);
    }

    public static final Creator<XiaoxiInstallmentUser> CREATOR = new
            Creator<XiaoxiInstallmentUser>() {
        @Override
        public XiaoxiInstallmentUser createFromParcel(Parcel source) {
            return new XiaoxiInstallmentUser(source);
        }

        @Override
        public XiaoxiInstallmentUser[] newArray(int size) {return new XiaoxiInstallmentUser[size];}
    };
}
