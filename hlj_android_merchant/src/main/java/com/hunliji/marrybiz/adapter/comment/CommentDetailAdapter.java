package com.hunliji.marrybiz.adapter.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.viewholder.CommentDetailViewHolder;

import java.util.List;

/**
 * Created by hua_rong on 2017/4/17.
 * 评论详情--评论列表
 */

public class CommentDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<RepliedComment> replyList;
    private LayoutInflater inflater;
    private Context context;
    private final static int ITEM_TYPE_HEADER = 0;
    private final static int ITEM_TYPE_LIST = 1;

    private View headerView;

    public CommentDetailAdapter(Context context, List<RepliedComment> replyList) {
        this.context = context;
        this.replyList = replyList;
        inflater = LayoutInflater.from(context);
    }

    public void setReplyList(List<RepliedComment> replyList) {
        this.replyList = replyList;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_LIST:
                View itemView = inflater.inflate(R.layout.item_comment_detail, parent, false);
                CommentDetailViewHolder viewHolder = new CommentDetailViewHolder(itemView);
                viewHolder.setReplyList(replyList);
                viewHolder.setOnReplyUserListener(onCommentListener);
                return viewHolder;
            default:
                return new ExtraBaseViewHolder(headerView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) { 
            case ITEM_TYPE_LIST:
                if (holder instanceof CommentDetailViewHolder) {
                    CommentDetailViewHolder viewHolder = (CommentDetailViewHolder) holder;
                    viewHolder.setView(context,
                            replyList.get(position - 1),
                            position - 1,
                            viewType);
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_LIST;
        }
    }

    @Override
    public int getItemCount() {
        return (headerView == null ? 0 : 1) + replyList.size();
    }

    private OnCommentListener onCommentListener;

    public void setOnReplyUserListener(OnCommentListener onCommentListener) {
        this.onCommentListener = onCommentListener;
    }

    public interface OnCommentListener {

        void onReplyUser(RepliedComment repliedComment);

        void onDeleteClick(int position, RepliedComment reply);

        void onComplainClick(int position, RepliedComment reply, int type);

    }

}
