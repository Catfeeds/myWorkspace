package com.hunliji.hljcommonlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/3/40.绑定信息(银行卡或微信)
 */

public class BindInfo implements Parcelable {

    public final static int BIND_BANK = 1;
    public final static int BIND_WX = 2;

    @SerializedName(value = "acc_no", alternate = "acc_no_tail")
    private String accNo;//银行卡号后四位
    @SerializedName(value = "bank_desc")
    private String bankDesc;//银行信息
    @SerializedName(value = "bank_id")
    private String bankId;
    @SerializedName(value = "bank_logo",alternate = "logo")
    private String bankLogo;
    @SerializedName(value = "id_holder")
    private String idHolder;//姓名
    @SerializedName(value = "type")
    private int type;//1银行卡 2微信
    @SerializedName(value = "openid")
    private String openid;
    @SerializedName(value = "city")
    private City city;
    @SerializedName("can_modify")
    private boolean canModify;

    public String getBankLogo() {
        return bankLogo;
    }

    public String getAccNo() {
        return accNo;
    }

    public String getBankDesc() {
        return bankDesc;
    }

    public String getBankId() {
        return bankId;
    }

    public String getIdHolder() {
        return idHolder;
    }

    public int getType() {
        return type;
    }

    public String getOpenid() {
        return openid;
    }

    public City getCity() {
        return city;
    }

    public boolean isCanModify() {
        return canModify;
    }

    public BindInfo() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.accNo);
        dest.writeString(this.bankDesc);
        dest.writeString(this.bankId);
        dest.writeString(this.bankLogo);
        dest.writeString(this.idHolder);
        dest.writeInt(this.type);
        dest.writeString(this.openid);
        dest.writeParcelable(this.city, flags);
        dest.writeByte(this.canModify ? (byte) 1 : (byte) 0);
    }

    protected BindInfo(Parcel in) {
        this.accNo = in.readString();
        this.bankDesc = in.readString();
        this.bankId = in.readString();
        this.bankLogo = in.readString();
        this.idHolder = in.readString();
        this.type = in.readInt();
        this.openid = in.readString();
        this.city = in.readParcelable(City.class.getClassLoader());
        this.canModify = in.readByte() != 0;
    }

    public static final Creator<BindInfo> CREATOR = new Creator<BindInfo>() {
        @Override
        public BindInfo createFromParcel(Parcel source) {return new BindInfo(source);}

        @Override
        public BindInfo[] newArray(int size) {return new BindInfo[size];}
    };
}
