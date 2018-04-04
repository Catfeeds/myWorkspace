package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.RepaymentSchedule;

import java.util.List;

/**
 * Created by luohanlin on 2017/9/11.
 */

public class XiaoxiInstallmentPreviewSchedulesData implements HljRZData {
    @SerializedName("previewScheduleList")
    private List<RepaymentSchedule> schedules; //还款计划预览列表
    @SerializedName(value = "capitalizedCost")
    private double capitalizedCost; //资金年化成本

    public List<RepaymentSchedule> getSchedules() {
        return schedules;
    }

    public double getCapitalizedCost() {
        return capitalizedCost;
    }

    @Override
    public boolean isEmpty() {
        return CommonUtil.isCollectionEmpty(schedules);
    }
}
