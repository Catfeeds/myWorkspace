package me.suncloud.marrymemo.adpter.experienceshop;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.Comment;

import java.util.List;

import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.viewholder.experienceshop.CommentViewHolder;

/**
 * Created by hua_rong on 2017/3/28.
 * 体验店评论列表
 */

public class CommentAdapter extends RecyclerView.Adapter<BaseViewHolder<Comment>> {

    private View headerView;
    private View footerView;

    private Context context;

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    private List<Comment> commentList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final LayoutInflater inflater;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        inflater = LayoutInflater.from(context);
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder<Comment> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return new ExtraViewHolder(headerView);
            case TYPE_FOOTER:
                return new ExtraViewHolder(footerView);
            default:
                View itemView = inflater.inflate(R.layout.comment_recycler_item, parent, false);
                return new CommentViewHolder(itemView,commentList);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Comment> holder, int position) {
        if (holder instanceof CommentViewHolder) {
            holder.setView(context,
                    getItem(position),
                    position -(headerView == null?0:1),
                    getItemViewType(position));
        }
    }

    private Comment getItem(int position) {
        return commentList.get(headerView == null ? position : position - 1);
    }

    private class ExtraViewHolder extends BaseViewHolder<Comment> {
        public ExtraViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void setViewData(Context mContext, Comment item, int position, int viewType) {

        }
    }

    @Override
    public int getItemCount() {
        return commentList.size() + (headerView == null ? 0 : 1) + (footerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && headerView != null) {
            return TYPE_HEADER;
        } else if (position == getItemCount()-1 && footerView != null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }
}
