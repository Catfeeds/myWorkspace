package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentOrder;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment
        .RepaymentScheduleListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的账单列表viewHolder
 * Created by chen_bin on 2017/8/17 0017.
 */
public class MyBillViewHolder extends BaseViewHolder<XiaoxiInstallmentOrder> {
    @BindView(R2.id.tv_status)
    TextView tvStatus;
    @BindView(R2.id.tv_due_at)
    TextView tvDueAt;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.tv_apply_amount)
    TextView tvApplyAmount;
    @BindView(R2.id.tv_arrival_time)
    TextView tvArrivalTime;

    public MyBillViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XiaoxiInstallmentOrder order = getItem();
                if (order != null) {
                    Intent intent = new Intent(v.getContext(), RepaymentScheduleListActivity.class);
                    intent.putExtra(RepaymentScheduleListActivity.ARG_ORDER, order);
                    v.getContext()
                            .startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void setViewData(
            final Context mContext,
            final XiaoxiInstallmentOrder order,
            int position,
            int viewType) {
        if (order == null) {
            return;
        }
        tvStatus.setText(order.getStatusStr());
        tvDueAt.setText(order.getDueAtStr());
        int rightResId = 0;
        if (order.getStatus() == XiaoxiInstallmentOrder.STATUS_REPAYING || order.getStatus() ==
                XiaoxiInstallmentOrder.STATUS_DELAYED) {
            rightResId = R.mipmap.icon_arrow_right_primary_14_26;
        } else if (order.getStatus() == XiaoxiInstallmentOrder.STATUS_CLEAR) {
            rightResId = R.mipmap.icon_arrow_right_gray_14_26;
        }
        tvDueAt.setCompoundDrawablesWithIntrinsicBounds(0, 0, rightResId, 0);
        itemView.setClickable(rightResId > 0);
        tvTitle.setText(order.getTitle());
        tvApplyAmount.setText(mContext.getString(R.string.label_apply_amount___pay,
                CommonUtil.formatDouble2StringWithTwoFloat(order.getContractAmount())));
        tvArrivalTime.setText(mContext.getString(R.string.label_arrival_time___pay,
                order.getArrivalTime() == null ? mContext.getString(R.string
                        .label_no_available___pay) : order.getArrivalTime()
                        .toString(HljTimeUtils.DATE_FORMAT_SHORT)));
    }
}