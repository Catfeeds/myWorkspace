package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import com.google.gson.annotations.SerializedName;

/**
 * Created by luohanlin on 2017/9/6.
 */

public class InstallmentData {
    Installment2 installment;
    @SerializedName("period_limit")
    int periodLimit;//分期时间限制

    public Installment2 getInstallment() {
        return installment;
    }

    public int getPeriodLimit() {
        return periodLimit;
    }

    public void setPeriodLimit(int periodLimit) {
        this.periodLimit = periodLimit;
    }
}
