package com.hunliji.marrybiz.model;

import com.hunliji.marrybiz.util.JSONUtil;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Suncloud on 2015/12/14.
 */
public class Certify implements Serializable {

    public static final int PERSONAL_TYPE = 1;
    public static final int COMPANY_TYPE = 2;

    private int type;//1 个人 2企业
    private int status;//1 待审核 2认证失败 3成功
    private String realname;
    private String certify;
    private String certifyFront;
    private String certifyBack;
    private String companyLicense;
    private String companyName;
    private String failReason;

    public Certify(JSONObject jsonObject) {
        if (jsonObject != null) {
            type = jsonObject.optInt("type");
            status = jsonObject.optInt("status");
            realname = JSONUtil.getString(jsonObject, "realname");
            certify = JSONUtil.getString(jsonObject, "certify");
            certifyFront = JSONUtil.getString(jsonObject, "certify_front");
            certifyBack = JSONUtil.getString(jsonObject, "certify_back");
            companyLicense = JSONUtil.getString(jsonObject, "company_license");
            companyName = JSONUtil.getString(jsonObject, "company_name");
            failReason = JSONUtil.getString(jsonObject, "fail_reason");
        }
    }

    public int getType() {
        return type;
    }

    public String getRealname() {
        return realname;
    }

    public String getCertify() {
        return certify;
    }

    public String getCertifyFront() {
        return certifyFront;
    }

    public String getCertifyBack() {
        return certifyBack;
    }

    public int getStatus() {
        return status;
    }

    public String getCompanyLicense() {
        return companyLicense;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public void setCertify(String certify) {
        this.certify = certify;
    }

    public void setCertifyFront(String certifyFront) {
        this.certifyFront = certifyFront;
    }

    public void setCertifyBack(String certifyBack) {
        this.certifyBack = certifyBack;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCompanyLicense(String companyLicense) {
        this.companyLicense = companyLicense;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getFailReason() {
        return JSONUtil.isEmpty(failReason) ? "" : failReason;
    }
}
