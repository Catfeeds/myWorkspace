package com.hunliji.hljpaymentlibrary.models;

/**
 * 钱包支付方式
 * Created by wangtao on 2017/2/9.
 */

public class WalletPayment extends Payment {

    private double walletPrice; //钱包余额
    private double price; //支付金额

    public WalletPayment(String payAgent) {
        super(payAgent);
    }

    public void setWalletPrice(double walletPrice) {
        this.walletPrice = walletPrice;
    }

    public double getWalletPrice() {
        return walletPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean isSelected() {
        return isEnable()&&super.isSelected();
    }

    public boolean isEnable(){
        return walletPrice>=price;
    }
}
