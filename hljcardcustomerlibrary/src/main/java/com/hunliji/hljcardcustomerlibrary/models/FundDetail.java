package com.hunliji.hljcardcustomerlibrary.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

/**
 * Created by mo_yu on 2017/11/27.理财明细
 */

public class FundDetail implements Parcelable {

    @SerializedName("id")
    long id;
    @SerializedName("created_at")
    DateTime createdAt;
    @SerializedName("fee")
    double fee;//礼金转入时收取的服务费
    @SerializedName("title")
    String title;//	标题
    @SerializedName("type")
    int type;//1礼金转入 2银行卡转入 3收益 4转出 5婚品消费
    @SerializedName("value")
    double value;//金额

    public long getId() {
        return id;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public double getFee() {
        return fee;
    }

    public String getTitle() {
        return title;
    }

    public int getType() {
        return type;
    }

    public double getValue() {
        return value;
    }


    public FundDetail() {}

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeSerializable(this.createdAt);
        dest.writeDouble(this.fee);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeDouble(this.value);
    }

    protected FundDetail(Parcel in) {
        this.id = in.readLong();
        this.createdAt = (DateTime) in.readSerializable();
        this.fee = in.readDouble();
        this.title = in.readString();
        this.type = in.readInt();
        this.value = in.readDouble();
    }

    public static final Creator<FundDetail> CREATOR = new Creator<FundDetail>() {
        @Override
        public FundDetail createFromParcel(Parcel source) {return new FundDetail(source);}

        @Override
        public FundDetail[] newArray(int size) {return new FundDetail[size];}
    };
}
