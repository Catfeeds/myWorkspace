package com.hunliji.hljcarlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.adapter.viewholder.WeddingCarCommentContentViewHolder;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.wedding_car.WeddingCarComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

/**
 * Created by jinxin on 2018/1/11 0011.
 */

public class WeddingCarCommentListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;

    private Context mContext;
    private LayoutInflater inflater;
    private View headerView;
    private View footerView;
    private List<WeddingCarComment> comments;
    private int firstSixMonthAgoIndex;
    private OnItemClickListener onItemClickListener;

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public int getFooterViewCount() {
        return footerView != null ? 1 : 0;
    }

    public int getHeaderViewCount() {
        return headerView != null ? 1 : 0;
    }

    public int getFirstSixMonthAgoIndex() {
        return firstSixMonthAgoIndex;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setFirstSixMonthAgoIndex(int firstSixMonthAgoIndex) {
        this.firstSixMonthAgoIndex = firstSixMonthAgoIndex;
    }


    public WeddingCarCommentListAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
    }

    public List<WeddingCarComment> getComments() {
        return comments;
    }

    public void setComments(List<WeddingCarComment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComments(List<WeddingCarComment> comments) {
        if (!CommonUtil.isCollectionEmpty(comments)) {
            int start = getItemCount() - getFooterViewCount();
            this.comments.addAll(comments);
            notifyItemRangeInserted(start, comments.size());
            if (start - getHeaderViewCount() > 0) {
                notifyItemChanged(start - 1);
            }
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
                WeddingCarCommentContentViewHolder holder = new WeddingCarCommentContentViewHolder(
                        inflater.inflate(R.layout.wedding_car_comment_list_item___car,
                                parent,
                                false));
                holder.setOnItemClickListener(onItemClickListener);
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                WeddingCarCommentContentViewHolder commentViewHolder = (WeddingCarCommentContentViewHolder)
                        holder;
                int index = position - getHeaderViewCount();
                commentViewHolder.setDividerLineVisible(index != comments.size()-1);
                commentViewHolder.setView(mContext, comments.get(index), index, viewType);
                break;
        }
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
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + (firstSixMonthAgoIndex > 0 ?
                firstSixMonthAgoIndex : CommonUtil.getCollectionSize(
                comments));
    }
}
