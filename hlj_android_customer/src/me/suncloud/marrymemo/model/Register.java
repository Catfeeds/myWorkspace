package me.suncloud.marrymemo.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class Register implements Identifiable{

    private static final long serialVersionUID = 6468645596736353380L;

    public long id;//结婚登记处ID
    public String name;//结婚登记处名称
    public String address;//结婚登记处地址
    public String business_time;//结婚登记处营业时间
    public String contact;//结婚登记处联系方式
    public double latitude;//结婚登记处纬度
    public double longitude;//结婚登记处经度
    public JSONArray contacts;//结婚登记处联系方式列表

    public Register(JSONObject json) {
        if (json != null) {
            this.id = json.optLong("id", 0);
            this.name = json.optString("name");
            this.address = json.optString("address");
            this.business_time = json.optString("business_time");
            this.contact = json.optString("contact");
            this.latitude = json.optDouble("latitude",0);
            this.longitude = json.optDouble("longitude",0);
            this.contacts = json.optJSONArray("only_contact_phone");

        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBusiness_time() {
        return business_time;
    }

    public void setBusiness_time(String business_time) {
        this.business_time = business_time;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public JSONArray getContacts() {
        return contacts;
    }

    public void setContacts(JSONArray contacts) {
        this.contacts = contacts;
    }
}
