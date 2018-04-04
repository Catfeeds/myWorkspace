package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.RepaymentSchedule;

import java.util.List;

/**
 * 小犀分期-还款计划
 * Created by chen_bin on 16/7/18.
 */
public class XiaoxiInstallmentSchedulesData implements HljRZData {
    @SerializedName(value = "scheduleList")
    private List<RepaymentSchedule> schedules;

    public List<RepaymentSchedule> getSchedules() {
        return schedules;
    }

    public double getPrepareRepayAmount() {
        double prepareRepayAmount = 0;
        if (!CommonUtil.isCollectionEmpty(schedules)) {
            for (RepaymentSchedule schedule : schedules) {
                if (!schedule.isClear() && schedule.getStartDays() >= 0) {
                    prepareRepayAmount += schedule.getAmount(); //待还金额
                }
            }
        }
        return prepareRepayAmount;
    }

    public double getRepaidAmount() {
        double repaidAmount = 0;
        if (!CommonUtil.isCollectionEmpty(schedules)) {
            for (RepaymentSchedule schedule : schedules) {
                if (schedule.isClear()) {
                    repaidAmount += schedule.getAmount(); //已还金额
                }
            }
        }
        return repaidAmount;
    }

    public boolean isClear() {
        boolean isClear = false;
        if (!CommonUtil.isCollectionEmpty(schedules)) {
            for (RepaymentSchedule schedule : schedules) {
                if (!schedule.isClear()) {
                    isClear = false;
                    break;
                }
                isClear = true;
            }
        }
        return isClear;
    }

    @Override
    public boolean isEmpty() {
        return CommonUtil.isCollectionEmpty(schedules);
    }

}