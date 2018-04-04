package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.CreditLimit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提升额度-额度授信headerViewHolder
 * Created by chen_bin on 2017/8/11 0011.
 */
public class LimitAuthItemHeaderViewHolder extends BaseViewHolder<CreditLimit> {
    @BindView(R2.id.tv_available_limit)
    TextView tvAvailableLimit;
    @BindView(R2.id.tv_available_limit_decimal)
    TextView tvAvailableLimitDecimal;
    @BindView(R2.id.btn_continue_pay)
    Button btnContinuePay;

    public LimitAuthItemHeaderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        btnContinuePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getDefault()
                        .post(new PayRxEvent(PayRxEvent.RxEventType.LIMIT_CONTINUE_PAY, getItem()));
                if (v.getContext() instanceof Activity) {
                    ((Activity) v.getContext()).finish();
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, CreditLimit creditLimit, int position, int viewType) {
        if (creditLimit == null) {
            return;
        }
        String str = CommonUtil.formatDouble2StringWithTwoFloat(creditLimit.getAvailableLimit());
        String[] s = str.split("\\.");
        tvAvailableLimit.setText(s.length > 0 ? s[0] : "0");
        tvAvailableLimitDecimal.setText(s.length > 1 ? s[1] : "00");
    }

    public void setShowPayBtn(boolean showPayBtn) {
        btnContinuePay.setVisibility(showPayBtn ? View.VISIBLE : View.GONE);
    }
}
