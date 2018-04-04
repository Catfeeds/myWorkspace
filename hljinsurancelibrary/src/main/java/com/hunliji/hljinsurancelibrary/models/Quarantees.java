package com.hunliji.hljinsurancelibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hua_rong on 2017/5/25.
 * 保障项目
 */

public class Quarantees implements Parcelable {

    String desc;//描述
    String money; //理赔金额

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }


    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.desc);
        dest.writeString(this.money);
    }

    public Quarantees() {}

    protected Quarantees(Parcel in) {
        this.desc = in.readString();
        this.money = in.readString();
    }

    public static final Parcelable.Creator<Quarantees> CREATOR = new Parcelable
            .Creator<Quarantees>() {
        @Override
        public Quarantees createFromParcel(Parcel source) {return new Quarantees(source);}

        @Override
        public Quarantees[] newArray(int size) {return new Quarantees[size];}
    };
}
