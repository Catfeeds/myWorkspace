package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chen_bin on 2017/11/10 0010.
 */
public class ExtInfo implements Parcelable {
    @SerializedName(value = "industries")
    private int industries;
    @SerializedName(value = "natureOfWork")
    private int natureOfWork;
    @SerializedName(value = "bulkProperty")
    private int bulkProperty;
    @SerializedName(value = "paymentSrc")
    private int paymentSrc;
    @SerializedName(value = "othersLoan")
    private int othersLoan;
    @SerializedName(value = "debts")
    private int debts;
    @SerializedName(value = "creditInfoNearlySixMonth")
    private int creditInfoNearlySixMonth;

    public transient final static int DEFAULT_INDUSTRIES = 21;
    public transient final static int DEFAULT_NATURE_OF_WORK = 1;
    public transient final static int DEFAULT_BULK_PROPERTY = 1;
    public transient final static int DEFAULT_PAYMENT_SRC = 1;
    public transient final static int DEFAULT_OTHERS_LOAN = 1;
    public transient final static int DEFAULT_DEBTS = 1;
    public transient final static int DEFAULT_CREDIT_INFO_NEARLY_SIX_MONTH = 1;

    public int getIndustries() {
        return industries;
    }

    public void setIndustries(int industries) {
        this.industries = industries;
    }

    public int getNatureOfWork() {
        return natureOfWork;
    }

    public void setNatureOfWork(int natureOfWork) {
        this.natureOfWork = natureOfWork;
    }

    public int getBulkProperty() {
        return bulkProperty;
    }

    public void setBulkProperty(int bulkProperty) {
        this.bulkProperty = bulkProperty;
    }

    public int getPaymentSrc() {
        return paymentSrc;
    }

    public void setPaymentSrc(int paymentSrc) {
        this.paymentSrc = paymentSrc;
    }

    public int getOthersLoan() {
        return othersLoan;
    }

    public void setOthersLoan(int othersLoan) {
        this.othersLoan = othersLoan;
    }

    public int getDebts() {
        return debts;
    }

    public void setDebts(int debts) {
        this.debts = debts;
    }

    public int getCreditInfoNearlySixMonth() {
        return creditInfoNearlySixMonth;
    }

    public void setCreditInfoNearlySixMonth(int creditInfoNearlySixMonth) {
        this.creditInfoNearlySixMonth = creditInfoNearlySixMonth;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.industries);
        dest.writeInt(this.natureOfWork);
        dest.writeInt(this.bulkProperty);
        dest.writeInt(this.paymentSrc);
        dest.writeInt(this.othersLoan);
        dest.writeInt(this.debts);
        dest.writeInt(this.creditInfoNearlySixMonth);
    }

    public ExtInfo() {

    }

    protected ExtInfo(Parcel in) {
        this.industries = in.readInt();
        this.natureOfWork = in.readInt();
        this.bulkProperty = in.readInt();
        this.paymentSrc = in.readInt();
        this.othersLoan = in.readInt();
        this.debts = in.readInt();
        this.creditInfoNearlySixMonth = in.readInt();
    }

    public static final Creator<ExtInfo> CREATOR = new Creator<ExtInfo>() {
        @Override
        public ExtInfo createFromParcel(Parcel source) {return new ExtInfo(source);}

        @Override
        public ExtInfo[] newArray(int size) {return new ExtInfo[size];}
    };
}
