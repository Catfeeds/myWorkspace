package com.hunliji.hljcardcustomerlibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2017/2/7.我的余额
 */

public class Balance implements Parcelable {

    @SerializedName(value = "created_at")
    DateTime createdAt;
    String message;
    int type;//类型
    double value;//金额
    @SerializedName(value = "type_msg")
    String typeMsg;//礼物类型文案
    @SerializedName("withdraw_data")
    WithdrawData withDrawData;

    public String getTypeMsg() {
        return typeMsg;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }

    public WithdrawData getWithDrawData() {
        return withDrawData;
    }

    public int getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public Balance() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.createdAt);
        dest.writeString(this.message);
        dest.writeInt(this.type);
        dest.writeDouble(this.value);
        dest.writeString(this.typeMsg);
    }

    protected Balance(Parcel in) {
        this.createdAt = (DateTime) in.readSerializable();
        this.message = in.readString();
        this.type = in.readInt();
        this.value = in.readDouble();
        this.typeMsg = in.readString();
    }

    public static final Creator<Balance> CREATOR = new Creator<Balance>() {
        @Override
        public Balance createFromParcel(Parcel source) {return new Balance(source);}

        @Override
        public Balance[] newArray(int size) {return new Balance[size];}
    };
}
