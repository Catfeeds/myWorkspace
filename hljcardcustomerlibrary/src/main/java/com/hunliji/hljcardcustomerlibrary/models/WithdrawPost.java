package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/2/10.提现
 */

public class WithdrawPost implements Parcelable {

    @SerializedName(value = "code")
    private String code;
    @SerializedName(value = "money")
    private String money;
    @SerializedName(value = "openid")
    private String openid;
    @SerializedName(value = "insurance")
    private int insurance;

    public String getCode() {
        return code;
    }

    public String getMoney() {
        return money;
    }

    public String getOpenid() {
        return openid;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.money);
        dest.writeString(this.openid);
    }

    public WithdrawPost(String smsCode, String money, String openid, int insurance) {
        this.code = smsCode;
        this.money = money;
        this.openid = openid;
        this.insurance = insurance;
    }

    public WithdrawPost(String smsCode, String money, int insurance) {
        this.code = smsCode;
        this.money = money;
        this.insurance = insurance;
    }

    protected WithdrawPost(Parcel in) {
        this.code = in.readString();
        this.money = in.readString();
        this.openid = in.readString();
        this.insurance = in.readInt();
    }

    public static final Creator<WithdrawPost> CREATOR = new Creator<WithdrawPost>() {
        @Override
        public WithdrawPost createFromParcel(Parcel source) {return new WithdrawPost(source);}

        @Override
        public WithdrawPost[] newArray(int size) {return new WithdrawPost[size];}
    };
}
