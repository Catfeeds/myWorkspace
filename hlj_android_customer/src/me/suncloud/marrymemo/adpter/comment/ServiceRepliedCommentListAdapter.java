package me.suncloud.marrymemo.adpter.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.comment.viewholder.ServiceRepliedCommentViewHolder;

/**
 * 服务评价回复列表
 * Created by chen_bin on 2017/6/8 0008.
 */
public class ServiceRepliedCommentListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private View headerView;
    private View footerView;
    private ArrayList<RepliedComment> repliedComments;
    private LayoutInflater inflater;
    private ServiceRepliedCommentViewHolder.OnRepliedCommentListener onRepliedCommentListener;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;
    private final static int ITEM_TYPE_FOOTER = 2;

    public ServiceRepliedCommentListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setRepliedComments(ArrayList<RepliedComment> repliedComments) {
        this.repliedComments = repliedComments;
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

    public void setOnRepliedCommentListener(
            ServiceRepliedCommentViewHolder.OnRepliedCommentListener
                    onRepliedCommentListener) {
        this.onRepliedCommentListener = onRepliedCommentListener;
    }

    @Override
    public int getItemCount() {
        return getHeaderViewCount() + getFooterViewCount() + CommonUtil.getCollectionSize(
                repliedComments);
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
                ServiceRepliedCommentViewHolder repliedCommentHolder = new ServiceRepliedCommentViewHolder(
                        inflater.inflate(R.layout.service_replied_comment_list_item,
                                parent,
                                false));
                repliedCommentHolder.setOnRepliedCommentListener(onRepliedCommentListener);
                return repliedCommentHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (holder instanceof ServiceRepliedCommentViewHolder) {
            int index = position - getHeaderViewCount();
            ServiceRepliedCommentViewHolder repliedCommentViewHolder =
                    (ServiceRepliedCommentViewHolder) holder;
            repliedCommentViewHolder.setItemBottomMargin(CommonUtil.dp2px(context,
                    index < repliedComments.size() - 1 ? 0 : CommonUtil.dp2px(context, 4)));
            repliedCommentViewHolder.setView(context, repliedComments.get(index), index, viewType);
        }
    }
}
