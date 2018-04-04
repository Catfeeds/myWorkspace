package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.viewholders
        .RepaymentScheduleViewHolder;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.RepaymentSchedule;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.XiaoxiInstallmentOrder;

import java.util.List;


/**
 * 还款计划adapter
 * Created by chen_bin on 2017/8/17 0017.
 */
public class RepaymentScheduleListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private View headerView;
    private View footerView;

    private List<RepaymentSchedule> schedules;
    private int currentStage;

    private LayoutInflater inflater;

    private RepaymentScheduleViewHolder.OnRepayListener onRepayListener;

    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;

    public RepaymentScheduleListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setSchedules(List<RepaymentSchedule> schedules) {
        this.schedules = schedules;
        notifyDataSetChanged();
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public void setOnRepayListener(RepaymentScheduleViewHolder.OnRepayListener onRepayListener) {
        this.onRepayListener = onRepayListener;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize
                (schedules);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                RepaymentScheduleViewHolder scheduleViewHolder = new RepaymentScheduleViewHolder(
                        inflater.inflate(R.layout.repayment_schedule_list_item___pay,
                                parent,
                                false));
                scheduleViewHolder.setCurrentStage(currentStage);
                scheduleViewHolder.setOnRepayListener(onRepayListener);
                return scheduleViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof RepaymentScheduleViewHolder) {
                    RepaymentScheduleViewHolder scheduleViewHolder =
                            (RepaymentScheduleViewHolder) holder;
                    int index = position - getHeaderViewCount();
                    RepaymentSchedule schedule = schedules.get(index);
                    scheduleViewHolder.setView(context, schedule, index, viewType);
                    setShowRepayBtn(scheduleViewHolder, schedule);
                    setShowStageLineView(scheduleViewHolder, index);
                    setStageBottomLineBackgroundColor(scheduleViewHolder, index);
                }
                break;
        }
    }

    private void setShowStageLineView(RepaymentScheduleViewHolder scheduleViewHolder, int index) {
        scheduleViewHolder.setShowStageTopLineView(index > 0);
        scheduleViewHolder.setShowStageBottomLineView(index < schedules.size() - 1);
    }

    private void setStageBottomLineBackgroundColor(
            RepaymentScheduleViewHolder scheduleViewHolder, int index) {
        if (index >= schedules.size() - 1) {
            scheduleViewHolder.setShowStageBottomLineView(false);
        } else {
            RepaymentSchedule schedule = schedules.get(index + 1);
            scheduleViewHolder.setStageBottomLineColor(ContextCompat.getColor(context,
                    schedule.getStage() <= currentStage || schedule.isClear() ? R.color
                            .colorPrimary : R.color.colorLine));
        }
    }

    private void setShowRepayBtn(
            RepaymentScheduleViewHolder scheduleViewHolder, RepaymentSchedule schedule) {
        scheduleViewHolder.setShowRepayBtn(!schedule.isClear() && (schedule.getDueDays() > 0 ||
                currentStage == schedule.getStage()));
    }
}