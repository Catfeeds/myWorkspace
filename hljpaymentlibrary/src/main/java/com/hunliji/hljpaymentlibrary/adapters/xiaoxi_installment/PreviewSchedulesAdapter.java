package com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.RepaymentSchedule;

import java.util.List;

/**
 * 还款计划预览列表
 * Created by luohanlin on 2017/9/11.
 */
public class PreviewSchedulesAdapter extends BaseAdapter {

    private List<RepaymentSchedule> schedules;

    public PreviewSchedulesAdapter(List<RepaymentSchedule> schedules) {
        this.schedules = schedules;
    }

    @Override
    public int getCount() {
        return CommonUtil.getCollectionSize(schedules);
    }

    @Override
    public Object getItem(int position) {
        return schedules == null ? null : schedules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.installment_preview_schedules_item, viewGroup, false);
            ViewHolder holder = new ViewHolder();
            holder.tvDate = view.findViewById(R.id.tv_date);
            holder.tvAmount = view.findViewById(R.id.tv_amount);
            holder.tvInterestAndGratuity = view.findViewById(R.id.tv_interest_and_gratuity);
            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        RepaymentSchedule schedule = (RepaymentSchedule) getItem(position);
        holder.tvDate.setText(schedule.getDueAt()
                .toString(HljTimeUtils.DATE_FORMAT_SHORT));
        holder.tvAmount.setText(viewGroup.getContext()
                .getString(R.string.label_price,
                        CommonUtil.formatDouble2String(schedule.getAmount())));
        holder.tvInterestAndGratuity.setText(viewGroup.getContext()
                .getString(R.string.label_interest_and_gratuity___pay,
                        schedule.getInterest(),
                        schedule.getGratuity()));
        return view;
    }

    private class ViewHolder {
        TextView tvDate;
        TextView tvAmount;
        TextView tvInterestAndGratuity;
    }
}
