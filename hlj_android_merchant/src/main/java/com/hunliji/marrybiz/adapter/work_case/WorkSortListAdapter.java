package com.hunliji.marrybiz.adapter.work_case;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.marrybiz.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 套餐排序列表适配器
 * Created by chen_bin on 2017/2/3 0003.
 */
public class WorkSortListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private List<Work> works;
    private LayoutInflater inflater;
    private int selectedPosition;
    private static final int ITEM_TYPE_HEADER = 0;
    private static final int ITEM_TYPE_LIST = 1;
    private static final int ITEM_TYPE_FOOTER = 2;
    private OnItemClickListener onItemClickListener;
    private OnMoveListener onMoveListener;
    private OnMoveTopListener onMoveTopListener;

    public WorkSortListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<Work> getWorks() {
        return works;
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
            if (start - getHeaderViewCount() > 0) {
                notifyItemChanged(start - 1);
            }
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnMoveListener(OnMoveListener onMoveListener) {
        this.onMoveListener = onMoveListener;
    }

    public void setOnMoveTopListener(OnMoveTopListener onMoveTopListener) {
        this.onMoveTopListener = onMoveTopListener;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(works);
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
                return new WorkSortViewHolder(inflater.inflate(R.layout.work_sort_list_item,
                        parent,
                        false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                int index = position - getHeaderViewCount();
                holder.setView(context, works.get(index), index, viewType);
                break;
        }
    }

    public class WorkSortViewHolder extends BaseViewHolder<Work> {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.btn_move_up)
        ImageButton btnMoveUp;
        @BindView(R.id.btn_move_down)
        ImageButton btnMoveDown;
        @BindView(R.id.btn_move_top)
        ImageButton btnMoveTop;
        @BindView(R.id.line_layout)
        View lineLayout;

        public WorkSortViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnMoveUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMoveListener != null) {
                        onMoveListener.onMove(0, getAdapterPosition(), getItem());
                    }
                }
            });
            btnMoveDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMoveListener != null) {
                        onMoveListener.onMove(1, getAdapterPosition(), getItem());
                    }
                }
            });
            btnMoveTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMoveTopListener != null) {
                        onMoveTopListener.onMoveTop(getAdapterPosition(), getItem());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getItem());
                    }
                }
            });
        }

        @Override
        protected void setViewData(
                Context context, final Work work, int position, int viewType) {
            if (work == null) {
                return;
            }
            lineLayout.setVisibility(position < works.size() - 1 ? View.VISIBLE : View.GONE);
            tvTitle.setText(work.getTitle());
            if (selectedPosition == position) {
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                btnMoveUp.setVisibility(View.VISIBLE);
                btnMoveUp.setEnabled(isCanMoveUp(selectedPosition, works.size()));
                btnMoveDown.setVisibility(View.VISIBLE);
                btnMoveDown.setEnabled(isCanMoveDown(selectedPosition, works.size()));
                btnMoveTop.setVisibility(View.VISIBLE);
                btnMoveTop.setEnabled(isCanMoveTop(selectedPosition, works.size()));
            } else {
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.colorBlack2));
                btnMoveUp.setVisibility(View.INVISIBLE);
                btnMoveDown.setVisibility(View.INVISIBLE);
                btnMoveTop.setVisibility(View.INVISIBLE);
            }
        }
    }

    //是否能够向上
    private boolean isCanMoveUp(int selectedPosition, int size) {
        return size != 1 && selectedPosition != 0;
    }

    //是够能够向下
    private boolean isCanMoveDown(int selectedPosition, int size) {
        return size != 1 && selectedPosition < size - 1;
    }

    //是否能够置顶
    private boolean isCanMoveTop(int selectedPosition, int size) {
        return size != 1 && selectedPosition != 0;
    }

    public interface OnMoveListener {
        void onMove(int direction, int position, Work work);
    }

    public interface OnMoveTopListener {
        void onMoveTop(int position, Work work);
    }

}
