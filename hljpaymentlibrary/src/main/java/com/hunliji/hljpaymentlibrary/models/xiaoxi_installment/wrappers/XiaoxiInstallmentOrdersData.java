package com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers;

import com.google.gson.annotations.SerializedName;
import com.hunliji.hljcommonlibrary.interfaces.HljRZData;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.RepaymentSchedule;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentOrder;

import java.util.List;

/**
 * 小犀分期-订单列表查询
 * Created by chen_bin on 16/7/18.
 */
public class XiaoxiInstallmentOrdersData implements HljRZData {
    @SerializedName(value = "orderList")
    private List<XiaoxiInstallmentOrder> orders;

    public List<XiaoxiInstallmentOrder> getOrders() {
        return orders;
    }

    public double getPrepareRepayAmount() {
        double prepareRepayAmount = 0;
        if (!CommonUtil.isCollectionEmpty(orders)) {
            for (XiaoxiInstallmentOrder order : orders) {
                if (!CommonUtil.isCollectionEmpty(order.getSchedules())) {
                    for (RepaymentSchedule schedule : order.getSchedules()) {
                        if (!schedule.isClear() && schedule.getStartDays() >= 0) {
                            prepareRepayAmount += schedule.getAmount();
                        }
                    }
                }
            }
        }
        return prepareRepayAmount;
    }

    @Override
    public boolean isEmpty() {
        return CommonUtil.isCollectionEmpty(orders);
    }
}