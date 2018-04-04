package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import com.google.gson.annotations.SerializedName;

/**
 * 借款使用情况
 * Created by jinxin on 2017/11/10 0010.
 */
public class DebtInfo {
    @SerializedName(value = "useOfFunds")
    private int useOfFunds;
    @SerializedName(value = "repaymentAbility")
    private int repaymentAbility;
    @SerializedName(value = "litigation")
    private int litigation;
    @SerializedName(value = "situationStatus")
    private int situationStatus;

    private String assetOrderId;

    public transient final static int DEFAULT_USE_OF_FUNDS = 3;
    public transient final static int DEFAULT_REPAYMENT_ABILITY = 1;
    public transient final static int DEFAULT_LITIGATION = 1;
    public transient final static int DEFAULT_SITUATION_STATUS = 1;

    public int getUseOfFunds() {
        return useOfFunds;
    }

    public void setUseOfFunds(int useOfFunds) {
        this.useOfFunds = useOfFunds;
    }

    public int getRepaymentAbility() {
        return repaymentAbility;
    }

    public void setRepaymentAbility(int repaymentAbility) {
        this.repaymentAbility = repaymentAbility;
    }

    public int getLitigation() {
        return litigation;
    }

    public void setLitigation(int litigation) {
        this.litigation = litigation;
    }

    public int getSituationStatus() {
        return situationStatus;
    }

    public void setSituationStatus(int situationStatus) {
        this.situationStatus = situationStatus;
    }

    public String getAssetOrderId() {
        return assetOrderId;
    }

    public void setAssetOrderId(String assetOrderId) {
        this.assetOrderId = assetOrderId;
    }
}
