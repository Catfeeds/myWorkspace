package com.hunliji.marrybiz.model.merchant;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hua_rong on 2017/12/26
 * 营销部分 icon跳转地址
 */

public class DescriptionLink implements Parcelable {

    @SerializedName(value = "EyeSystem")
    private String eyeSystem;//天眼系统
    @SerializedName(value = "MicroFunctionalNetwork")
    private String microWebsite;//微官网
    @SerializedName(value = "MultiStoreManagement")
    private String shopManage;//多店铺管理
    @SerializedName(value = "SmallProgram")
    private String smallProgram;//小程序
    @SerializedName(value = "ExperienceStoreRecommendation")
    private String ExperienceStoreRecommendation;//推荐橱窗

    public String getEyeSystem() {
        return eyeSystem;
    }

    public void setEyeSystem(String eyeSystem) {
        this.eyeSystem = eyeSystem;
    }

    public String getMicroWebsite() {
        return microWebsite;
    }

    public void setMicroWebsite(String microWebsite) {
        this.microWebsite = microWebsite;
    }

    public String getShopManage() {
        return shopManage;
    }

    public void setShopManage(String shopManage) {
        this.shopManage = shopManage;
    }

    public String getSmallProgram() {
        return smallProgram;
    }

    public void setSmallProgram(String smallProgram) {
        this.smallProgram = smallProgram;
    }

    public String getExperienceStoreRecommendation() {
        return ExperienceStoreRecommendation;
    }

    public void setExperienceStoreRecommendation(String experienceStoreRecommendation) {
        ExperienceStoreRecommendation = experienceStoreRecommendation;
    }

    public DescriptionLink() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.eyeSystem);
        dest.writeString(this.microWebsite);
        dest.writeString(this.shopManage);
        dest.writeString(this.smallProgram);
        dest.writeString(this.ExperienceStoreRecommendation);
    }

    protected DescriptionLink(Parcel in) {
        this.eyeSystem = in.readString();
        this.microWebsite = in.readString();
        this.shopManage = in.readString();
        this.smallProgram = in.readString();
        this.ExperienceStoreRecommendation = in.readString();
    }

    public static final Creator<DescriptionLink> CREATOR = new Creator<DescriptionLink>() {
        @Override
        public DescriptionLink createFromParcel(Parcel source) {return new DescriptionLink(source);}

        @Override
        public DescriptionLink[] newArray(int size) {return new DescriptionLink[size];}
    };
}
