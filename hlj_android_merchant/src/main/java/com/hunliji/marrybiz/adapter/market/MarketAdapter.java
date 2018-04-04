package com.hunliji.marrybiz.adapter.market;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.market.viewholder.MarketGroupViewHolder;
import com.hunliji.marrybiz.model.merchantservice.MarketGroup;

import java.util.List;

/**
 * Created by hua_rong on 2017/12/25
 * 营销
 */

public class MarketAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private LayoutInflater inflater;
    private Context context;
    private List<MarketGroup> records;
    private View headerView;
    private OnItemClickListener onItemClickListener;

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public MarketAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setRecords(List<MarketGroup> records) {
        this.records = records;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            default:
                View itemView = inflater.inflate(R.layout.market_item_layout, parent, false);
                return new MarketGroupViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof MarketGroupViewHolder) {
            MarketGroupViewHolder viewHolder = (MarketGroupViewHolder) holder;
            viewHolder.setOnItemClickListener(onItemClickListener);
            viewHolder.setView(context,
                    records.get(position - 1),
                    position - 1,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return CommonUtil.getCollectionSize(records) + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
