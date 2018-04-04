package com.hunliji.hljhttplibrary.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/6/14.可提现请帖列表
 */

public class HljHttpCardData<T> extends HljHttpData<T> {

    @SerializedName("amount")
    private double amount;
    @SerializedName("can_allin")
    private int canAllin;//1可全部提现 0不显示 2显示，但提示请先设置提现账号
    @SerializedName("deposit_rate")
    private double depositRate;//服务费率
    @SerializedName("total_money")
    private double totalMoney;//总收入
    @SerializedName("cash_money")
    private double cashMoney;//礼金收入
    @SerializedName("gift_money")
    private double giftMoney;//	礼物收入


    public double getAmount() {
        return amount;
    }

    public int getCanAllin() {
        return canAllin;
    }

    public double getDepositRate() {
        return depositRate;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public double getCashMoney() {
        return cashMoney;
    }

    public double getGiftMoney() {
        return giftMoney;
    }
}
