package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/11/27.理财首页
 */

public class FundIndex implements Parcelable {

    @SerializedName("fund_profit")
    double fundProfit;//总收益
    @SerializedName("fund_rate")
    double fundRate;//理财基础年化，小数
    @SerializedName("fund_rate_bonus")
    double fundRateBonus;//理财奖励年化，小数
    @SerializedName("fund_total")
    double fundTotal;//	总金额
    @SerializedName("gift_cash_money")
    double giftCashMoney;//礼物礼金余额
    @SerializedName("fund_income_max")
    double fundIncomeMax;//转入最大值
    @SerializedName("fund_income_min")
    double fundIncomeMin;//转入最小值
    @SerializedName("fund_outcome_max")
    double fundOutcomeMax;//转出最大值
    @SerializedName("fund_outcome_min")
    double fundOutcomeMin;//转出最小值
    @SerializedName("fund_rate_bonus_logo")
    String fundRateBonusLogo;//	理财奖励年化图标
    @SerializedName("fund_protocol_url")
    String fundProtocolUrl;//礼金理财服务协议
    @SerializedName("fund_qa_url")
    String fundQaUrl;//常见问题的地址

    public double getFundProfit() {
        return fundProfit;
    }

    public double getFundRate() {
        return fundRate;
    }

    public double getFundRateBonus() {
        return fundRateBonus;
    }

    public double getFundTotal() {
        return fundTotal;
    }

    public double getGiftCashMoney() {
        return giftCashMoney;
    }

    public double getFundIncomeMax() {
        return fundIncomeMax;
    }

    public double getFundIncomeMin() {
        return fundIncomeMin;
    }

    public double getFundOutcomeMax() {
        return fundOutcomeMax;
    }

    public double getFundOutcomeMin() {
        return fundOutcomeMin;
    }

    public String getFundRateBonusLogo() {
        return fundRateBonusLogo;
    }

    public String getFundProtocolUrl() {
        return fundProtocolUrl;
    }

    public String getFundQaUrl() {
        return fundQaUrl;
    }

    public FundIndex() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.fundProfit);
        dest.writeDouble(this.fundRate);
        dest.writeDouble(this.fundRateBonus);
        dest.writeDouble(this.fundTotal);
        dest.writeDouble(this.giftCashMoney);
        dest.writeDouble(this.fundIncomeMax);
        dest.writeDouble(this.fundIncomeMin);
        dest.writeDouble(this.fundOutcomeMax);
        dest.writeDouble(this.fundOutcomeMin);
        dest.writeString(this.fundRateBonusLogo);
        dest.writeString(this.fundProtocolUrl);
        dest.writeString(this.fundQaUrl);
    }

    protected FundIndex(Parcel in) {
        this.fundProfit = in.readDouble();
        this.fundRate = in.readDouble();
        this.fundRateBonus = in.readDouble();
        this.fundTotal = in.readDouble();
        this.giftCashMoney = in.readDouble();
        this.fundIncomeMax = in.readDouble();
        this.fundIncomeMin = in.readDouble();
        this.fundOutcomeMax = in.readDouble();
        this.fundOutcomeMin = in.readDouble();
        this.fundRateBonusLogo = in.readString();
        this.fundProtocolUrl = in.readString();
        this.fundQaUrl = in.readString();
    }

    public static final Creator<FundIndex> CREATOR = new Creator<FundIndex>() {
        @Override
        public FundIndex createFromParcel(Parcel source) {return new FundIndex(source);}

        @Override
        public FundIndex[] newArray(int size) {return new FundIndex[size];}
    };
}
