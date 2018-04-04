package me.suncloud.marrymemo.adpter.work_case;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.work_case.viewholder.WeddingCompereViewHolder;

/**
 * Created by hua_rong on 2017/7/31.
 * 婚礼司仪
 */

public class WeddingCompereAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private View headerView;
    private View footerView;
    private List<Work> works;
    private Context context;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private LayoutInflater inflater;

    public WeddingCompereAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setWorks(List<Work> works) {
        this.works = works;
        notifyDataSetChanged();
    }

    public void addWorks(List<Work> works) {
        if (!CommonUtil.isCollectionEmpty(works)) {
            int start = getItemCount() - getFooterViewCount();
            this.works.addAll(works);
            notifyItemRangeInserted(start, works.size());
        }
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

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                View itemView = inflater.inflate(R.layout.property_wedding_compere_item,
                        parent,
                        false);
                return new WeddingCompereViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof WeddingCompereViewHolder) {
            WeddingCompereViewHolder viewHolder = (WeddingCompereViewHolder) holder;
            viewHolder.setView(context, getItem(position), position, getItemViewType(position));
        }
    }

    private Work getItem(int position) {
        return works.get(position - getHeaderViewCount());
    }


    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(works);
    }

    @Override
    public int getItemViewType(int position) {
        if (getHeaderViewCount() > 0 && position == 0) {
            return TYPE_HEADER;
        } else if (getFooterViewCount() > 0 && position == getItemCount() - getFooterViewCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

}