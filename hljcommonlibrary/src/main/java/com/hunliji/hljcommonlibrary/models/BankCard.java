package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by werther on 15/12/29.
 */
public class BankCard implements Parcelable {
    @SerializedName(value = "id",alternate = {"bank_id"})
    private long id;
    @SerializedName(value = "bank", alternate = {"bank_name","bank_desc"})
    private String bankName;
    @SerializedName("bank_code")
    private String bankCode;
    private String account;
    @SerializedName("card_type")
    private String cardType;
    @SerializedName("logo")
    private String logoPath;
    @SerializedName("short_fullname")
    private String shortName;
    private String cardId;
    @SerializedName("card_type_msg")
    private String cardTypeStr;
    @SerializedName(value = "acc_no")
    String accNo;//银行卡后四位
    @SerializedName(value = "bank_logo")
    String bankLogo;//银行logo
    public long getId() {
        return id;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccount() {
        return account;
    }

    public String getCardType() {
        return cardType;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public String getShortName() {
        return shortName;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardTypeStr() {
        return cardTypeStr;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setCardTypeStr(String cardTypeStr) {
        this.cardTypeStr = cardTypeStr;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getBankLogo() {
        return bankLogo;
    }

    public void setBankLogo(String bankLogo) {
        this.bankLogo = bankLogo;
    }

    public BankCard() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.bankName);
        dest.writeString(this.bankCode);
        dest.writeString(this.account);
        dest.writeString(this.cardType);
        dest.writeString(this.logoPath);
        dest.writeString(this.shortName);
        dest.writeString(this.cardId);
        dest.writeString(this.cardTypeStr);
        dest.writeString(this.accNo);
        dest.writeString(this.bankLogo);
    }

    protected BankCard(Parcel in) {
        this.id = in.readLong();
        this.bankName = in.readString();
        this.bankCode = in.readString();
        this.account = in.readString();
        this.cardType = in.readString();
        this.logoPath = in.readString();
        this.shortName = in.readString();
        this.cardId = in.readString();
        this.cardTypeStr = in.readString();
        this.accNo = in.readString();
        this.bankLogo = in.readString();
    }

    public static final Creator<BankCard> CREATOR = new Creator<BankCard>() {
        @Override
        public BankCard createFromParcel(Parcel source) {return new BankCard(source);}

        @Override
        public BankCard[] newArray(int size) {return new BankCard[size];}
    };
}
