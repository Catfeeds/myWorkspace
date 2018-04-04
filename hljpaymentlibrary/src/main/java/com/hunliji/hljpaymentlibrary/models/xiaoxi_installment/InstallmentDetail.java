package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by luohanlin on 2017/9/5.
 */

public class InstallmentDetail implements Serializable {
    @SerializedName("each_pay")
    private double eachPay;
    @SerializedName("stage_num")
    private int stageNum;

    public InstallmentDetail(JSONObject jsonObject) {
        this.eachPay = jsonObject.optDouble("each_pay");
        this.stageNum = jsonObject.optInt("stage_num");
    }

    public InstallmentDetail(){}

    public double getEachPay() {
        return eachPay;
    }

    public int getStageNum() {
        return stageNum;
    }

    public String getShowText() {
        return "￥" + eachPay + "x" + stageNum + "期";
    }

    public String getStageNumString() {
        return "分" + stageNum + "期";
    }

    public void setStageNum(int stageNum) {
        this.stageNum = stageNum;
    }
}
