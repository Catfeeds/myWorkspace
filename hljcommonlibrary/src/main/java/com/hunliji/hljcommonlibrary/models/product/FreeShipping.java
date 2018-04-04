package com.hunliji.hljcommonlibrary.models.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * 运费模板
 * Created by jinxin on 2017/10/19 0019.
 */

public class FreeShipping implements Parcelable {
    @SerializedName(value = "express_template_id")
    long id;//运费模板id
    double money;//满金额包邮
    int num;//满件包邮
    int type;//类型 0金额 1件数

    public long getId() {
        return id;
    }

    public double getMoney() {
        return money;
    }

    public int getNum() {
        return num;
    }

    public int getType() {
        return type;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeDouble(this.money);
        dest.writeInt(this.num);
        dest.writeInt(this.type);
    }

    public FreeShipping() {}

    protected FreeShipping(Parcel in) {
        this.id = in.readLong();
        this.money = in.readDouble();
        this.num = in.readInt();
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<FreeShipping> CREATOR = new Parcelable
            .Creator<FreeShipping>() {
        @Override
        public FreeShipping createFromParcel(Parcel source) {return new FreeShipping(source);}

        @Override
        public FreeShipping[] newArray(int size) {return new FreeShipping[size];}
    };
}
