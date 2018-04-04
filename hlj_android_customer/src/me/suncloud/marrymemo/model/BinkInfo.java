package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 16/1/7.
 */
public class BinkInfo implements Identifiable{

    private static final long serialVersionUID = 5251908833690610889L;
    private String bankCode;
    private String bankName;
    private int bankStatus;
    private int cardType;
    private double dayAmt;
    private double monthAmt;
    private double singleAmt;
    private String cardTypeStr;
    private String logoPath;


    public BinkInfo(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.bankCode = JSONUtil.getString(jsonObject, "bank_code");
            this.bankName = JSONUtil.getString(jsonObject, "bank_name");
            this.bankStatus = jsonObject.optInt("bank_status");
            this.cardType = jsonObject.optInt("card_type", 0);
            this.cardTypeStr = JSONUtil.getString(jsonObject, "card_type_msg");
            this.dayAmt = jsonObject.optDouble("day_amt", 0);
            this.monthAmt = jsonObject.optDouble("month_amt", 0);
            this.singleAmt = jsonObject.optDouble("single_amt");
            this.logoPath = JSONUtil.getString(jsonObject, "logo");
        }
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public int getBankStatus() {
        return bankStatus;
    }

    public int getCardType() {
        return cardType;
    }

    public double getDayAmt() {
        return dayAmt;
    }

    public double getMonthAmt() {
        return monthAmt;
    }

    public double getSingleAmt() {
        return singleAmt;
    }

    public String getCardTypeStr() {
        return cardTypeStr;
    }

    public String getLogoPath() {
        return logoPath;
    }

    @Override
    public Long getId() {
        return serialVersionUID;
    }
}
