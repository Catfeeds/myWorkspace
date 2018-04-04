package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentOrder;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentSchedulesData;
import com.hunliji.hljpaymentlibrary.views.widgets.xiaoxi_installment
        .XiaoxiInstallmentAgreementsDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 还还款计划headerViewHolder
 * Created by chen_bin on 2017/8/23 0023.
 */
public class RepaymentScheduleHeaderViewHolder extends BaseViewHolder<XiaoxiInstallmentOrder> {

    @BindView(R2.id.tv_prepare_repay_amount)
    TextView tvPrepareRepayAmount;
    @BindView(R2.id.tv_prepare_repay_amount_decimal)
    TextView tvPrepareRepayAmountDecimal;
    @BindView(R2.id.tv_arrival_time)
    TextView tvArrivalTime;
    @BindView(R2.id.tv_apply_amount)
    TextView tvApplyAmount;
    @BindView(R2.id.tv_repaid_amount)
    TextView tvRepaidAmount;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.agreement_layout)
    LinearLayout agreementLayout;

    private XiaoxiInstallmentSchedulesData schedulesData;

    public RepaymentScheduleHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XiaoxiInstallmentOrder order = getItem();
                if (order == null) {
                    return;
                }
                String path = null;
                if (order.getServiceOrder() != null) {
                    path = RouterPath.IntentPath.Customer.WORK_ACTIVITY;
                } else if (order.getHotelPeriodOrder() != null) {
                    path = RouterPath.IntentPath.Customer.MERCHANT_HOME;
                }
                if (CommonUtil.isEmpty(path)) {
                    return;
                }
                ARouter.getInstance()
                        .build(path)
                        .withLong("id", order.getEntityId())
                        .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                        .navigation(view.getContext());
            }
        });
        agreementLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XiaoxiInstallmentOrder order = getItem();
                if (order == null || CommonUtil.isEmpty(order.getAssetOrderId())) {
                    return;
                }
                new XiaoxiInstallmentAgreementsDialog(view.getContext()).setAssetOrderId(order
                        .getAssetOrderId())
                        .show();
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, XiaoxiInstallmentOrder order, int position, int viewType) {
        if (order == null) {
            return;
        }
        String str = CommonUtil.formatDouble2StringWithTwoFloat(schedulesData
                .getPrepareRepayAmount());
        String[] s = str.split("\\.");
        tvPrepareRepayAmount.setText(s.length > 0 ? s[0] : "0");
        tvPrepareRepayAmountDecimal.setText(s.length > 1 ? s[1] : "00");
        tvArrivalTime.setText(mContext.getString(R.string.label_arrival_time___pay,
                order.getArrivalTime() == null ? mContext.getString(R.string
                        .label_no_available___pay) : order.getArrivalTime()
                        .toString(HljTimeUtils.DATE_FORMAT_SHORT)));
        tvApplyAmount.setText(mContext.getString(R.string.label_amount___pay,
                CommonUtil.formatDouble2StringWithTwoFloat(order.getContractAmount())));
        tvRepaidAmount.setText(mContext.getString(R.string.label_amount___pay,
                CommonUtil.formatDouble2StringWithTwoFloat(schedulesData.getRepaidAmount())));
        tvTitle.setText(order.getTitle());
    }

    public void setSchedulesData(XiaoxiInstallmentSchedulesData schedulesData) {
        this.schedulesData = schedulesData;
    }
}