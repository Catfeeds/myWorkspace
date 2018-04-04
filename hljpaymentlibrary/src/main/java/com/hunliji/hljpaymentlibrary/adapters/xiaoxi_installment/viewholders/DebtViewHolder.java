package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Debt;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 订单债权人viewHolder
 * Created by chen_bin on 2017/11/3 0003.
 */
public class DebtViewHolder extends BaseViewHolder<Debt> {
    @BindView(R2.id.top_line_layout)
    View topLineLayout;
    @BindView(R2.id.tv_time)
    TextView tvTime;
    @BindView(R2.id.tv_date)
    TextView tvDate;
    @BindView(R2.id.tv_name)
    TextView tvName;
    @BindView(R2.id.tv_amount)
    TextView tvAmount;

    public DebtViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(Context mContext, Debt debt, int position, int viewType) {
        if (debt == null) {
            return;
        }
        if (debt.getDate() == null) {
            tvTime.setText(null);
            tvDate.setText(null);
        } else {
            tvTime.setText(debt.getDate().toString("HH:mm"));
            tvDate.setText(debt.getDate().toString("yyyy.MM.dd"));
        }
        tvName.setText(debt.getName());
        tvAmount.setText(CommonUtil.formatDouble2String(debt.getAmount()));
    }

    public void setTopLineView(boolean showTopLineView) {
        topLineLayout.setVisibility(showTopLineView ? View.VISIBLE : View.GONE);
    }

}
