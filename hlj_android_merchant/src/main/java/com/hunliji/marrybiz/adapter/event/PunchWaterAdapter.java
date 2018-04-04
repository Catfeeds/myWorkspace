package com.hunliji.marrybiz.adapter.event;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.event.viewholders.PunchWaterViewHolder;
import com.hunliji.marrybiz.model.event.RechargeRecord;

import java.util.List;

/**
 * Created by hua_rong on 2017/12/27 充扣流水
 */

public class PunchWaterAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private Context context;
    private View footerView;
    private List<RechargeRecord> records;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;
    private LayoutInflater inflater;

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public PunchWaterAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setRecords(List<RechargeRecord> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                View itemView = inflater.inflate(R.layout.recharge_record_list_item, parent, false);
                return new PunchWaterViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof PunchWaterViewHolder) {
            PunchWaterViewHolder viewHolder = (PunchWaterViewHolder) holder;
            viewHolder.setRecords(records);
            viewHolder.setView(context, records.get(position), position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(records) + (footerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }
}
