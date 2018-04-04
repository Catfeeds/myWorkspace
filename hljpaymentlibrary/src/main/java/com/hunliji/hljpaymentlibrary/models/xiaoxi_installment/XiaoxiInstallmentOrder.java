package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.models.orders.HotelPeriodOrder;
import com.hunliji.hljcommonlibrary.models.orders.ServiceOrder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by chen_bin on 2017/8/25 0025.
 */
public class XiaoxiInstallmentOrder implements Parcelable {
    @SerializedName(value = "assetOrderId")
    private String assetOrderId;
    @SerializedName(value = "applyTime")
    private DateTime applyTime; //申请时间
    @SerializedName(value = "arrivalTime")
    private DateTime arrivalTime;  //放款时间
    @SerializedName(value = "contractAmount")
    private double contractAmount; //合同金额
    @SerializedName(value = "status")
    private int status; //订单状态
    @SerializedName(value = "period")
    private int period; //一共多少期还款计划
    @SerializedName(value = "currentStage")
    private int currentStage; //当前处于还款计划的第几期
    @SerializedName(value = "repaymentSchedules")
    private List<RepaymentSchedule> schedules; //还款计划列表
    @SerializedName(value = "order")
    private ServiceOrder serviceOrder;
    @SerializedName(value = "hotel_period_order")
    private HotelPeriodOrder hotelPeriodOrder;

    public transient final static int STATUS_REVIEWING = 1;  //审核中
    public transient final static int STATUS_REVIEW_FAIL = 2; //审核不通过
    public transient final static int STATUS_REVIEW_SUCCESS = 20; //审核通过
    public transient final static int STATUS_LOANING = 3; //放款中
    public transient final static int STATUS_REPAYING = 4; //还款中
    public transient final static int STATUS_DELAYED = 5; //已逾期
    public transient final static int STATUS_CLEAR = 6;  //已结清

    public String getAssetOrderId() {
        return assetOrderId;
    }

    public DateTime getApplyTime() {
        return applyTime;
    }

    public DateTime getArrivalTime() {
        return arrivalTime;
    }

    public double getContractAmount() {
        return contractAmount;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusStr() {
        switch (status) {
            case STATUS_REVIEWING:
                return "审核中";
            case STATUS_REVIEW_FAIL:
                return "审核不通过";
            case STATUS_REVIEW_SUCCESS:
            case STATUS_LOANING:
                return "放款中";
            case STATUS_REPAYING:
            case STATUS_DELAYED:
                double prepareRepayAmount = getPrepareRepayAmount();
                String str = prepareRepayAmount == 0 ? "当期已还" : "¥" + CommonUtil
                        .formatDouble2StringWithTwoFloat(
                        prepareRepayAmount);
                return "[" + currentStage + "/" + period + "期] " + str;
            case STATUS_CLEAR:
                return "已还清";
            default:
                return null;
        }
    }

    public String getDueAtStr() {
        if (status != STATUS_REPAYING && status != STATUS_DELAYED) {
            return null;
        }
        int earliestNotClearStageIndex = -1;
        int currentStageIndex = -1;
        if (!CommonUtil.isCollectionEmpty(schedules)) {
            for (int i = 0, size = schedules.size(); i < size; i++) {
                RepaymentSchedule schedule = schedules.get(i);
                if (earliestNotClearStageIndex == -1 && !schedule.isClear() && schedule.getStage
                        () < currentStage) {
                    earliestNotClearStageIndex = i;
                }
                if (schedule.getStage() == currentStage) {
                    currentStageIndex = i;
                    break;
                }
            }
            if (earliestNotClearStageIndex > -1) {
                RepaymentSchedule schedule = schedules.get(earliestNotClearStageIndex);
                int days = schedule.getDueDays();
                return days <= 0 ? null : "逾期" + days + "天";
            }
            if (currentStageIndex > -1) {
                RepaymentSchedule schedule = schedules.get(currentStageIndex);
                int days = schedule.getDueDays();
                if (!schedule.isClear()) {
                    return days == 0 ? "今天是还款日" : "距离还款日" + Math.abs(days) + "天";
                }
            }
        }
        return null;
    }

    public int getPeriod() {
        return period;
    }

    public int getCurrentStage() {
        return currentStage;
    }

    public List<RepaymentSchedule> getSchedules() {
        return schedules;
    }

    public double getPrepareRepayAmount() {
        double prepareRepayAmount = 0;
        if (!CommonUtil.isCollectionEmpty(schedules)) {
            for (RepaymentSchedule schedule : schedules) {
                if (!schedule.isClear() && schedule.getStartDays() >= 0) {
                    prepareRepayAmount += schedule.getAmount();
                }
            }
        }
        return prepareRepayAmount;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public HotelPeriodOrder getHotelPeriodOrder() {
        return hotelPeriodOrder;
    }

    public String getTitle() {
        if (serviceOrder != null) {
            return serviceOrder.getOrderSub()
                    .getWork()
                    .getTitle();
        } else if (hotelPeriodOrder != null) {
            return hotelPeriodOrder.getTitle();
        } else {
            return null;
        }
    }

    public long getEntityId() {
        if (serviceOrder != null) {
            return serviceOrder.getOrderSub()
                    .getWork()
                    .getId();
        } else if (hotelPeriodOrder != null) {
            return hotelPeriodOrder.getMerchant()
                    .getId();
        } else {
            return 0;
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.assetOrderId);
        HljTimeUtils.writeDateTimeToParcel(dest, this.applyTime);
        HljTimeUtils.writeDateTimeToParcel(dest, this.arrivalTime);
        dest.writeDouble(this.contractAmount);
        dest.writeInt(this.status);
        dest.writeInt(this.period);
        dest.writeInt(this.currentStage);
        dest.writeTypedList(this.schedules);
        dest.writeParcelable(this.serviceOrder, flags);
    }

    public XiaoxiInstallmentOrder() {}

    protected XiaoxiInstallmentOrder(Parcel in) {
        this.assetOrderId = in.readString();
        this.applyTime = HljTimeUtils.readDateTimeToParcel(in);
        this.arrivalTime = HljTimeUtils.readDateTimeToParcel(in);
        this.contractAmount = in.readDouble();
        this.status = in.readInt();
        this.period = in.readInt();
        this.currentStage = in.readInt();
        this.schedules = in.createTypedArrayList(RepaymentSchedule.CREATOR);
        this.serviceOrder = in.readParcelable(ServiceOrder.class.getClassLoader());
    }

    public static final Creator<XiaoxiInstallmentOrder> CREATOR = new
            Creator<XiaoxiInstallmentOrder>() {
        @Override
        public XiaoxiInstallmentOrder createFromParcel(Parcel source) {
            return new XiaoxiInstallmentOrder(source);
        }

        @Override
        public XiaoxiInstallmentOrder[] newArray(int size) {
            return new XiaoxiInstallmentOrder[size];
        }
    };
}
