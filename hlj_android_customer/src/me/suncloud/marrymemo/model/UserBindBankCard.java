package me.suncloud.marrymemo.model;

import org.json.JSONObject;

import me.suncloud.marrymemo.util.JSONUtil;

/**
 * Created by werther on 15/12/29.
 */
public class UserBindBankCard implements Identifiable {
    private static final long serialVersionUID = -2230084432643335646L;
    private long id;
    private String bankName;
    private String bankCode;
    private String account;
    private String cardType;
    private String logoPath;
    private String shortName;

    public UserBindBankCard(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.id = jsonObject.optLong("id", 0);
            this.bankName = JSONUtil.getString(jsonObject, "bank");
            this.account = JSONUtil.getString(jsonObject, "account");
            this.cardType = JSONUtil.getString(jsonObject, "card_type");
            this.bankCode = JSONUtil.getString(jsonObject, "bank_code");
            this.logoPath = JSONUtil.getString(jsonObject, "logo");
            this.shortName = JSONUtil.getString(jsonObject, "short_fullname");
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getAccount() {
        return account;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public String getShortName() {
        return shortName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setId(long id) {
        this.id = id;
    }
}
