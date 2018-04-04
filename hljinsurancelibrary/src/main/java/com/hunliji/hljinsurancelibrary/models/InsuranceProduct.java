package com.hunliji.hljinsurancelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.Photo;

/**
 * 保险产品
 * Created by hua_rong on 2017/5/25.
 */
public class InsuranceProduct implements Parcelable {
    @SerializedName(value = "desc")
    private String desc;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "insurer_logo")
    private String insurerLogo;
    @SerializedName(value = "detail_url")
    private String detailUrl; //保险详情
    @SerializedName(value = "clause_url")
    private String clauseUrl;//保险条款
    @SerializedName(value = "notice_url")
    private String noticeUrl;//投保须知 投保规则
    @SerializedName(value = "company")
    private String company;
    @SerializedName(value = "price")
    private double price;
    @SerializedName(value = "limit_num")
    private int limitNum;
    @SerializedName(value = "type")
    private int type;
    @SerializedName(value = "claim_notice_url")
    String claimNoticeUrl;//理赔服务流程
    @SerializedName(value = "about_myb_image_url")
    String aboutMybImageUrl;//蜜月保顶部banner
    @SerializedName(value = "guarantees_image")
    Photo guaranteesImage;//保障项目详情图
    @SerializedName(value = "clause_url_abroad")
    String clauseUrlAbroad;//海外条款

    public transient static final int TYPE_GOLD = 1; //婚礼宝黄金计划
    public transient static final int TYPE_DIAMOND = 2; //婚礼宝钻石计划
    public transient static final int TYPE_HONEY_MOON = 3;//蜜月保

    public String getDesc() {
        return desc;
    }

    public String getTitle() {
        return title;
    }

    public String getInsurerLogo() {
        return insurerLogo;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public double getPrice() {
        return price;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public int getType() {
        return type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getClauseUrl() {
        return clauseUrl;
    }

    public String getNoticeUrl() {
        return noticeUrl;
    }

    public String getClaimNoticeUrl() {
        return claimNoticeUrl;
    }

    public void setClaimNoticeUrl(String claimNoticeUrl) {
        this.claimNoticeUrl = claimNoticeUrl;
    }

    public String getAboutMybImageUrl() {
        return aboutMybImageUrl;
    }

    public Photo getGuaranteesImage() {
        return guaranteesImage;
    }

    public String getClauseUrlAbroad() {
        return clauseUrlAbroad;
    }

    public InsuranceProduct() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.desc);
        dest.writeString(this.title);
        dest.writeString(this.insurerLogo);
        dest.writeString(this.detailUrl);
        dest.writeString(this.clauseUrl);
        dest.writeString(this.noticeUrl);
        dest.writeString(this.company);
        dest.writeDouble(this.price);
        dest.writeInt(this.limitNum);
        dest.writeInt(this.type);
        dest.writeString(this.claimNoticeUrl);
        dest.writeString(this.aboutMybImageUrl);
        dest.writeParcelable(this.guaranteesImage, flags);
        dest.writeString(this.clauseUrlAbroad);
    }

    protected InsuranceProduct(Parcel in) {
        this.desc = in.readString();
        this.title = in.readString();
        this.insurerLogo = in.readString();
        this.detailUrl = in.readString();
        this.clauseUrl = in.readString();
        this.noticeUrl = in.readString();
        this.company = in.readString();
        this.price = in.readDouble();
        this.limitNum = in.readInt();
        this.type = in.readInt();
        this.claimNoticeUrl = in.readString();
        this.aboutMybImageUrl = in.readString();
        this.guaranteesImage = in.readParcelable(Photo.class.getClassLoader());
        this.clauseUrlAbroad = in.readString();
    }

    public static final Creator<InsuranceProduct> CREATOR = new Creator<InsuranceProduct>() {
        @Override
        public InsuranceProduct createFromParcel(Parcel source) {return new InsuranceProduct(source);}

        @Override
        public InsuranceProduct[] newArray(int size) {return new InsuranceProduct[size];}
    };
}
