package me.suncloud.marrymemo.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 15/8/10.
 */
public class ShippingAddress implements Identifiable {

    private static final long serialVersionUID = -777518414789912313L;
    private long id;
    private String buyerName;
    private String mobilePhone;
    private String street;
    private long regionId;
    private boolean isDefault;
    private String country;
    private String province;
    private String city;
    private String district;
    private String zip;

    public ShippingAddress(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.buyerName = JSONUtil.getString(jsonObject, "buyer_name");
            this.mobilePhone = JSONUtil.getString(jsonObject, "mobile");
            this.street = JSONUtil.getString(jsonObject, "street");
            this.regionId = jsonObject.optLong("ragion_id", 0);
            this.isDefault = jsonObject.optInt("is_default", 0) > 0;
            this.country = JSONUtil.getString(jsonObject, "country");
            this.province = JSONUtil.getString(jsonObject, "province");
            this.city = JSONUtil.getString(jsonObject, "city");
            this.district = JSONUtil.getString(jsonObject, "district");
            this.zip = JSONUtil.getString(jsonObject, "zip");
        }
    }

    public ShippingAddress(JsonObject jsonObject) {
        if (jsonObject != null) {
            this.id = CommonUtil.getAsLong(jsonObject, "id");
            this.buyerName = CommonUtil.getAsString(jsonObject, "buyer_name");
            this.mobilePhone = CommonUtil.getAsString(jsonObject, "mobile");
            this.street = CommonUtil.getAsString(jsonObject, "street");
            this.regionId = CommonUtil.getAsLong(jsonObject, "ragion_id");
            this.isDefault = CommonUtil.getAsInt(jsonObject, "is_default") > 0;
            this.country = CommonUtil.getAsString(jsonObject, "country");
            this.province = CommonUtil.getAsString(jsonObject, "province");
            this.city = CommonUtil.getAsString(jsonObject, "city");
            this.district = CommonUtil.getAsString(jsonObject, "district");
            this.zip = CommonUtil.getAsString(jsonObject, "zip");
        }
    }

    public ShippingAddress(long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getStreet() {
        return street;
    }

    public long getRegionId() {
        return regionId;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getDistrict() {
        return district;
    }

    public String getZip() {
        return zip;
    }

    @Override
    public String toString() {
        return (JSONUtil.isEmpty(country) ? "" : country) + province + city + (JSONUtil.isEmpty(
                district) ? "" : district) + street;
    }

    public String getCity() {
        return city;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
