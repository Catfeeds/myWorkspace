package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 订单债权记录
 * Created by chen_bin on 2017/11/3 0003.
 */
public class DebtTransferRecord implements Parcelable {
    @SerializedName(value = "date")
    private DateTime date;
    @SerializedName(value = "fromName")
    private String fromName;
    @SerializedName(value = "toName")
    private String toName;
    @SerializedName(value = "amount")
    private double amount;

    public DateTime getDate() {
        return date;
    }

    public String getFromName() {
        return fromName;
    }

    public String getToName() {
        return toName;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        HljTimeUtils.writeDateTimeToParcel(dest, this.date);
        dest.writeString(this.fromName);
        dest.writeString(this.toName);
        dest.writeDouble(this.amount);
    }

    public DebtTransferRecord() {}

    protected DebtTransferRecord(Parcel in) {
        this.date = HljTimeUtils.readDateTimeToParcel(in);
        this.fromName = in.readString();
        this.toName = in.readString();
        this.amount = in.readDouble();
    }

    public static final Creator<DebtTransferRecord> CREATOR = new Creator<DebtTransferRecord>() {
        @Override
        public DebtTransferRecord createFromParcel(Parcel source) {
            return new DebtTransferRecord(source);
        }

        @Override
        public DebtTransferRecord[] newArray(int size) {return new DebtTransferRecord[size];}
    };
}
