/**
 *
 */
package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * @author iDay
 */
public class Detail implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5215403641349967330L;

    private String phone;
    private String qq;
    private String mail;
    private String business;
    private String address;
    private String city;

    public Detail(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.phone = JSONUtil.getString(jsonObject, "联系电话");
            this.qq = JSONUtil.getString(jsonObject, "QQ");
            this.mail = JSONUtil.getString(jsonObject, "邮箱");
            this.business = JSONUtil.getString(jsonObject, "经营范围");
            this.address = JSONUtil.getString(jsonObject, "地址");
            this.city = JSONUtil.getString(jsonObject, "城市");
        }
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the qq
     */
    public String getQQ() {
        return qq;
    }

    /**
     * @param qq the qq to set
     */
    public void setQQ(String qq) {
        this.qq = qq;
    }

    /**
     * @return the mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail the mail to set
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * @return the business
     */
    public String getBusiness() {
        return business;
    }

    /**
     * @param business the business to set
     */
    public void setBusiness(String business) {
        this.business = business;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }
}
