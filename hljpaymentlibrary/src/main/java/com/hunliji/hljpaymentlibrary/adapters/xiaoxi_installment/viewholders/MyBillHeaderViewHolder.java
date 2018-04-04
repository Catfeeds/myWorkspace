package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers.XiaoxiInstallmentOrdersData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的账单headerViewHolder
 * Created by chen_bin on 2017/8/25 0025.
 */
public class MyBillHeaderViewHolder extends BaseViewHolder<XiaoxiInstallmentOrdersData> {
    @BindView(R2.id.tv_prepare_repay_amount)
    TextView tvPrepareRepayAmount;
    @BindView(R2.id.tv_prepare_repay_amount_decimal)
    TextView tvPrepareRepayAmountDecimal;

    public MyBillHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, XiaoxiInstallmentOrdersData ordersData, int position, int viewType) {
        if (ordersData == null) {
            return;
        }
        String str = CommonUtil.formatDouble2StringWithTwoFloat(ordersData.getPrepareRepayAmount());
        String[] s = str.split("\\.");
        tvPrepareRepayAmount.setText(s.length > 0 ? s[0] : "0");
        tvPrepareRepayAmountDecimal.setText(s.length > 1 ? s[1] : "00");
    }
}
