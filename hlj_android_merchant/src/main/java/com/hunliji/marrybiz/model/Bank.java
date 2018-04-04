package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Suncloud on 2015/12/14.
 */
public class Bank implements Serializable {

    private int agent;    //账号类型 （0：银行卡 / 1：支付宝）
    private long cityId; //老的城市id；
    private long cid; //新的城市id；
    private long pid; //新的省份id；
    private String name; //支付宝实名（银行卡类型读取Certify realname）
    private String account;
    private String bank;
    private String bankName;
    private String cityName;
    private String province;

    public Bank(JSONObject jsonObject){
        if(jsonObject!=null){
            agent=jsonObject.optInt("agent");
            cityId=jsonObject.optLong("bank_cid");
            cid=jsonObject.optLong("cid");
            pid=jsonObject.optLong("pid");
            name= JSONUtil.getString(jsonObject,"name");
            account= JSONUtil.getString(jsonObject,"account");
            bank= JSONUtil.getString(jsonObject,"bank");
            bankName= JSONUtil.getString(jsonObject,"bank_name");
            cityName= JSONUtil.getString(jsonObject,"city_name");
            province= JSONUtil.getString(jsonObject,"province");
        }
    }

    public int getAgent() {
        return agent;
    }

    public long getCityId() {
        return cityId;
    }

    public long getCid() {
        return cid;
    }

    public long getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String getBank() {
        return bank;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getProvince() {
        return province;
    }
}
