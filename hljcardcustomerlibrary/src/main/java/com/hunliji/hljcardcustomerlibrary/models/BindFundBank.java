package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mo_yu on 2017/11/24.理财银行卡绑定回调
 */
public class BindFundBank implements Parcelable {


    private long id;
    @SerializedName("can_use_card_money")
    private boolean canUseCardMoney;//是否能使用请帖余额 1能 0不能
    private String message;

    public long getId() {
        return id;
    }

    public boolean isCanUseCardMoney() {
        return canUseCardMoney;
    }

    public String getMessage() {
        return message;
    }

    public BindFundBank() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeByte(this.canUseCardMoney ? (byte) 1 : (byte) 0);
        dest.writeString(this.message);
    }

    protected BindFundBank(Parcel in) {
        this.id = in.readLong();
        this.canUseCardMoney = in.readByte() != 0;
        this.message = in.readString();
    }

    public static final Creator<BindFundBank> CREATOR = new Creator<BindFundBank>() {
        @Override
        public BindFundBank createFromParcel(Parcel source) {return new BindFundBank(source);}

        @Override
        public BindFundBank[] newArray(int size) {return new BindFundBank[size];}
    };
}
