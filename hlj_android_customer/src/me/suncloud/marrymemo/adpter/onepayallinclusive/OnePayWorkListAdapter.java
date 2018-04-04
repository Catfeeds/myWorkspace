package me.suncloud.marrymemo.adpter.onepayallinclusive;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.onepayallinclusive.viewholder.OnePayAllInclusiveViewHolder;

/**
 * Created by jinxin on 2018/3/14 0014.
 */

public class OnePayWorkListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final int FOOTER = 4;
    private final int ITEM = 3;
    private List<Work> works;
    private View footerView;
    private Context mContext;
    private LayoutInflater inflater;
    private OnItemClickListener<Work> onItemClickListener;

    public OnePayWorkListAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void setOnItemClickListener(OnItemClickListener<Work> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setWorks(List<Work> works) {
        this.works = works;
        notifyDataSetChanged();
    }

    public void addWorks(List<Work> works) {
        if (!CommonUtil.isCollectionEmpty(works)) {
            int start = getItemCount() - getFooterCount();
            this.works.addAll(works);
            notifyItemRangeInserted(start, works.size());
        }
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case ITEM:
                View itemView = inflater.inflate(R.layout
                                .one_pay_all_inclusive_work_item,
                        parent,
                        false);
                OnePayAllInclusiveViewHolder h = new OnePayAllInclusiveViewHolder(itemView);
                return h;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder h, final int position) {
        int type = getItemViewType(position);
        if (type == ITEM) {
            OnePayAllInclusiveViewHolder holder = (OnePayAllInclusiveViewHolder) h;
            holder.setView(mContext, works.get(position), position, type);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if(onItemClickListener != null){
                            onItemClickListener.onItemClick(position,works.get(position));
                        }
                }
            });
        }
    }

    private int getWorkListCount() {
        return CommonUtil.getCollectionSize(works);
    }

    private int getFooterCount() {
        return CommonUtil.isCollectionEmpty(works) ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return getWorkListCount() <= 0 ? 0 : getWorkListCount() + getFooterCount();
    }

    @Override
    public int getItemViewType(int position) {
        int type = -1;
        if (position == getItemCount() - 1) {
            type = FOOTER;
        } else {
            type = ITEM;
        }
        return type;
    }
}
