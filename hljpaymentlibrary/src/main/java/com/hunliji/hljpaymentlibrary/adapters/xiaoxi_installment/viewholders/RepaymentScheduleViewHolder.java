package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.RepaymentSchedule;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentOrder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 还款计划viewHolder
 * Created by chen_bin on 2017/8/17 0017.
 */
public class RepaymentScheduleViewHolder extends BaseViewHolder<RepaymentSchedule> {

    @BindView(R2.id.stage_top_line_layout)
    View stageTopLineLayout;
    @BindView(R2.id.tv_stage)
    TextView tvStage;
    @BindView(R2.id.stage_bottom_line_layout)
    View stageBottomLineLayout;
    @BindView(R2.id.tv_due_at)
    TextView tvDueAt;
    @BindView(R2.id.tv_amount)
    TextView tvAmount;
    @BindView(R2.id.tv_status)
    TextView tvStatus;
    @BindView(R2.id.btn_repay)
    Button btnRepay;

    private int currentStage;

    private OnRepayListener onRepayListener;

    public RepaymentScheduleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        btnRepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRepayListener != null) {
                    onRepayListener.onRepay(getAdapterPosition(), getItem());
                }
            }
        });
    }

    @Override
    protected void setViewData(
            Context mContext, RepaymentSchedule schedule, int position, int viewType) {
        if (schedule == null) {
            return;
        }
        int stageLineColor;
        Drawable stageRoundDrawable;
        int dueAtColor;
        int statusColor;
        int amountColor;
        if (schedule.getStage() <= currentStage || schedule.isClear()) {
            stageLineColor = ContextCompat.getColor(mContext, R.color.colorPrimary);
            stageRoundDrawable = ContextCompat.getDrawable(mContext, R.drawable.sp_oval_primary);
            dueAtColor = ContextCompat.getColor(mContext, R.color.colorBlack2);
            statusColor = ContextCompat.getColor(mContext, R.color.colorBlack2);
            amountColor = ContextCompat.getColor(mContext,
                    !schedule.isClear() && schedule.getDueDays() > 0 ? R.color.colorPrimary : R
                            .color.colorBlack2);
        } else {
            stageLineColor = ContextCompat.getColor(mContext, R.color.colorLine);
            stageRoundDrawable = ContextCompat.getDrawable(mContext, R.drawable.sp_oval_line);
            dueAtColor = ContextCompat.getColor(mContext, R.color.colorGray);
            statusColor = ContextCompat.getColor(mContext, R.color.colorGray);
            amountColor = ContextCompat.getColor(mContext, R.color.colorGray);
        }
        stageTopLineLayout.setBackgroundColor(stageLineColor);
        stageBottomLineLayout.setBackgroundColor(stageLineColor);
        tvStage.setBackground(stageRoundDrawable);
        tvStage.setText(String.valueOf(schedule.getStage()));
        tvDueAt.setTextColor(dueAtColor);
        tvDueAt.setText(schedule.getDueAt() == null ? null : schedule.getDueAt()
                .toString(HljTimeUtils.DATE_FORMAT_SHORT));
        tvStatus.setTextColor(statusColor);
        tvStatus.setText(schedule.getStatusStr());
        tvAmount.setTextColor(amountColor);
        tvAmount.setText(mContext.getString(R.string.label_amount___pay,
                CommonUtil.formatDouble2StringWithTwoFloat(schedule.getAmount())));
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public void setShowStageTopLineView(boolean showStageTopLineView) {
        stageTopLineLayout.setVisibility(showStageTopLineView ? View.VISIBLE : View.INVISIBLE);
    }

    public void setShowStageBottomLineView(boolean showStageBottomLineView) {
        stageBottomLineLayout.setVisibility(showStageBottomLineView ? View.VISIBLE : View
                .INVISIBLE);
    }

    public void setStageBottomLineColor(int color) {
        stageBottomLineLayout.setBackgroundColor(color);
    }

    public void setShowRepayBtn(boolean showRepayBtn) {
        btnRepay.setVisibility(showRepayBtn ? View.VISIBLE : View.GONE);
    }

    public void setOnRepayListener(OnRepayListener onRepayListener) {
        this.onRepayListener = onRepayListener;
    }

    public interface OnRepayListener {
        void onRepay(int position, RepaymentSchedule schedule);
    }
}