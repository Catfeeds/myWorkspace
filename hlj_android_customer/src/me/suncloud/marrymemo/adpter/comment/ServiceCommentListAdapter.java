package me.suncloud.marrymemo.adpter.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceCommentViewHolder;

/**
 * 服务评价列表adapter
 * Created by chen_bin on 2017/4/14 0014.
 */
public class ServiceCommentListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private View questionLayout;
    private List<ServiceComment> comments;
    private LayoutInflater inflater;
    private int firstSixMonthAgoIndex;
    private OnItemClickListener onItemClickListener;
    private ServiceCommentViewHolder.OnPraiseListener onPraiseListener;
    private ServiceCommentViewHolder.OnCommentListener onCommentListener;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;
    private final static int ITEM_TYPE_QUESTION = 3;

    public ServiceCommentListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public List<ServiceComment> getComments() {
        return comments;
    }

    public void setComments(List<ServiceComment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void setQuestionLayout(View questionLayout) {
        this.questionLayout = questionLayout;
    }

    public void addComments(List<ServiceComment> comments) {
        if (!CommonUtil.isCollectionEmpty(comments)) {
            int start = getItemCount() - getFooterViewCount();
            this.comments.addAll(comments);
            notifyItemRangeInserted(start, comments.size());
            if (start - getHeaderViewCount() - getQuestionLayoutCount() > 0) {
                notifyItemChanged(start - 1);
            }
        }
    }

    private int getQuestionLayoutCount() {
        return questionLayout != null ? 1 : 0;
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

    public int getFirstSixMonthAgoIndex() {
        return firstSixMonthAgoIndex;
    }

    public void setFirstSixMonthAgoIndex(int firstSixMonthAgoIndex) {
        this.firstSixMonthAgoIndex = firstSixMonthAgoIndex;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnPraiseListener(
            ServiceCommentViewHolder.OnPraiseListener onPraiseListener) {
        this.onPraiseListener = onPraiseListener;
    }

    public void setOnCommentListener(
            ServiceCommentViewHolder.OnCommentListener onCommentListener) {
        this.onCommentListener = onCommentListener;
    }

    @Override
    public int getItemCount() {
        return getQuestionLayoutCount() + getHeaderViewCount() + getFooterViewCount() +
                (firstSixMonthAgoIndex > 0 ? firstSixMonthAgoIndex : CommonUtil.getCollectionSize(
                comments));
    }

    @Override
    public int getItemViewType(int position) {
        if (getQuestionLayoutCount() > 0 && position == 0) {
            return ITEM_TYPE_QUESTION;
        } else if (getHeaderViewCount() > 0 && position == 1) {
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
            case ITEM_TYPE_QUESTION:
                return new ExtraBaseViewHolder(questionLayout);
            case ITEM_TYPE_HEADER:
                return new ExtraBaseViewHolder(headerView);
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                ServiceCommentViewHolder holder = new ServiceCommentViewHolder(inflater
                        .inflate(
                        R.layout.service_comment_list_item,
                        parent,
                        false));
                holder.setOnItemClickListener(onItemClickListener);
                holder.setOnPraiseListener(onPraiseListener);
                holder.setOnCommentListener(onCommentListener);
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                ServiceCommentViewHolder commentViewHolder = (ServiceCommentViewHolder) holder;
                int index = position - getHeaderViewCount() - getQuestionLayoutCount();
                commentViewHolder.setShowTopLineView(index != 0);
                commentViewHolder.setView(context, comments.get(index), index, viewType);
                break;
        }
    }

}