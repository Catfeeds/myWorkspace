package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 债权人
 * Created by chen_bin on 2017/11/3 0003.
 */
public class Debt implements Parcelable {
    @SerializedName(value = "date")
    private DateTime date;
    @SerializedName(value = "name")
    private String name;
    @SerializedName(value = "amount")
    private double amount;

    public DateTime getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        HljTimeUtils.writeDateTimeToParcel(dest, this.date);
        dest.writeString(this.name);
        dest.writeDouble(this.amount);
    }

    public Debt() {}

    protected Debt(Parcel in) {
        this.date = HljTimeUtils.readDateTimeToParcel(in);
        this.name = in.readString();
        this.amount = in.readDouble();
    }

    public static final Creator<Debt> CREATOR = new Creator<Debt>() {
        @Override
        public Debt createFromParcel(Parcel source) {return new Debt(source);}

        @Override
        public Debt[] newArray(int size) {return new Debt[size];}
    };
}
