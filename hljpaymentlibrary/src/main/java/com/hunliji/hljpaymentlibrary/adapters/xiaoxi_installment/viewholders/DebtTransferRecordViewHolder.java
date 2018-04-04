package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.DebtTransferRecord;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 订单债权记录viewHolder
 * Created by chen_bin on 2017/11/3 0003.
 */
public class DebtTransferRecordViewHolder extends BaseViewHolder<DebtTransferRecord> {
    @BindView(R2.id.top_line_layout)
    View topLineLayout;
    @BindView(R2.id.tv_time)
    TextView tvTime;
    @BindView(R2.id.tv_date)
    TextView tvDate;
    @BindView(R2.id.tv_from_name)
    TextView tvFromName;
    @BindView(R2.id.tv_to_name)
    TextView tvToName;
    @BindView(R2.id.tv_amount)
    TextView tvAmount;

    public DebtTransferRecordViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void setViewData(
            Context mContext, DebtTransferRecord record, int position, int viewType) {
        if (record == null) {
            return;
        }
        if (record.getDate() == null) {
            tvTime.setText(null);
            tvDate.setText(null);
        } else {
            tvTime.setText(record.getDate()
                    .toString("HH:mm"));
            tvDate.setText(record.getDate()
                    .toString("yyyy.MM.dd"));
        }
        tvFromName.setText(record.getFromName());
        tvToName.setText(record.getToName());
        tvAmount.setText(CommonUtil.formatDouble2String(record.getAmount()));
    }

    public void setTopLineView(boolean showTopLineView) {
        topLineLayout.setVisibility(showTopLineView ? View.VISIBLE : View.GONE);
    }

}
