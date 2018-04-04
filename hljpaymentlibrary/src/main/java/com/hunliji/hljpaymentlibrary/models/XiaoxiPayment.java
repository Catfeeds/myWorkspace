package com.hunliji.hljpaymentlibrary.models;

/**
 * Created by luohanlin on 2017/9/19.
 * 小犀分期
 */

public class XiaoxiPayment extends Payment {

    private double installmentMoneyStartAt;

    public XiaoxiPayment(String payAgent) {
        super(payAgent);
    }

    public double getInstallmentMoneyStartAt() {
        return installmentMoneyStartAt;
    }

    public void setInstallmentMoneyStartAt(double installmentMoneyStartAt) {
        this.installmentMoneyStartAt = installmentMoneyStartAt;
    }
}
