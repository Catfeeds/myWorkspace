package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

/**
 * 还款计划
 * Created by chen_bin on 2017/8/18 0018.
 */
public class RepaymentSchedule implements Parcelable {
    @SerializedName(value = "startAt")
    private DateTime startAt; //本期开始日
    @SerializedName(value = "dueAt")
    private DateTime dueAt; //本期结束日/用户应还日
    @SerializedName(value = "repayAt")
    private DateTime repayAt; //用户实还日
    @SerializedName(value = "penalty")
    private double penalty; //罚息
    @SerializedName(value = "amount")
    private double amount; //本期还款总金额
    @SerializedName(value = "interest")
    private double interest; //利息
    @SerializedName(value = "gratuity")
    private double gratuity; //服务费
    @SerializedName(value = "isClear")
    private boolean isClear; //是否还清
    @SerializedName(value = "startDays")
    private int startDays;
    @SerializedName(value = "dueDays")
    private int dueDays; //现在时间跟dueAt的差值
    @SerializedName(value = "status")
    private int status; //4：还款成功 5：还款失败 14:提前结清
    @SerializedName(value = "stage")
    private int stage; //所属第几期

    public static transient final int STATUS_INIT = 1;//初始化
    public static transient final int STATUS_PREPARE_REPAY = 2;//待还
    public static transient final int STATUS_REPAY_SUCCESS = 4; //还款成功
    public static transient final int STATUS_REPAY_FAIL = 5; //还款失败
    public static transient final int STATUS_SETTLE_UP = 14; //提前结清

    public DateTime getStartAt() {
        return startAt;
    }

    public DateTime getDueAt() {
        return dueAt;
    }

    public DateTime getRepayAt() {
        return repayAt;
    }

    public double getPenalty() {
        return penalty;
    }

    public double getAmount() {
        return amount;
    }

    public double getInterest() {
        return interest;
    }

    public double getGratuity() {
        return gratuity;
    }

    public boolean isClear() {
        return isClear;
    }

    public void setClear(boolean clear) {
        this.isClear = clear;
    }

    public int getStartDays() {
        return startDays;
    }

    public int getDueDays() {
        return dueDays;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusStr() {
        if (isClear) {
            return "已还";
        } else {
            return dueDays <= 0 ? "待还" : "逾期" + dueDays + "天";
        }
    }

    public int getStage() {
        return stage;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        HljTimeUtils.writeDateTimeToParcel(dest, this.startAt);
        HljTimeUtils.writeDateTimeToParcel(dest, this.dueAt);
        HljTimeUtils.writeDateTimeToParcel(dest, this.repayAt);
        dest.writeDouble(this.penalty);
        dest.writeDouble(this.amount);
        dest.writeDouble(this.interest);
        dest.writeDouble(this.gratuity);
        dest.writeByte(this.isClear ? (byte) 1 : (byte) 0);
        dest.writeInt(this.startDays);
        dest.writeInt(this.dueDays);
        dest.writeInt(this.status);
        dest.writeInt(this.stage);
    }

    public RepaymentSchedule() {}

    protected RepaymentSchedule(Parcel in) {
        this.startAt = HljTimeUtils.readDateTimeToParcel(in);
        this.dueAt = HljTimeUtils.readDateTimeToParcel(in);
        this.repayAt = HljTimeUtils.readDateTimeToParcel(in);
        this.penalty = in.readDouble();
        this.amount = in.readDouble();
        this.interest = in.readDouble();
        this.gratuity = in.readDouble();
        this.isClear = in.readByte() != 0;
        this.startDays = in.readInt();
        this.dueDays = in.readInt();
        this.status = in.readInt();
        this.stage = in.readInt();
    }

    public static final Creator<RepaymentSchedule> CREATOR = new Creator<RepaymentSchedule>() {
        @Override
        public RepaymentSchedule createFromParcel(Parcel source) {
            return new RepaymentSchedule(source);
        }

        @Override
        public RepaymentSchedule[] newArray(int size) {return new RepaymentSchedule[size];}
    };
}
