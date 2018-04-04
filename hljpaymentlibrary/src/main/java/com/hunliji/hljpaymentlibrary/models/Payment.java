package com.hunliji.hljpaymentlibrary.models;

/**
 * 支付方式model
 * Created by wangtao on 2017/2/9.
 */

public class Payment {
    private String payAgent; //支付类型
    private boolean isSelected; //选中的支付方式

    public Payment(String payAgent) {
        this.payAgent = payAgent;
    }

    public String getPayAgent() {
        return payAgent;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
