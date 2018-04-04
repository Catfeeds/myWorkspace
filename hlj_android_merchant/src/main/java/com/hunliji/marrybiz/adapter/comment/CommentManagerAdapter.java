package com.hunliji.marrybiz.adapter.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.ServiceComment;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.viewholder.CommentManagerViewHolder;

import java.util.List;

/**
 * Created by hua_rong on 2017/4/15.
 * 评价管理
 */

public class CommentManagerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<ServiceComment> comments;
    private LayoutInflater layoutInflater;
    private Context context;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;
    private View footerView;

    public CommentManagerAdapter(Context context, List<ServiceComment> comments) {
        this.context = context;
        this.comments = comments;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setComments(List<ServiceComment> comments) {
        this.comments = comments;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            default:
                View view = layoutInflater.inflate(R.layout.item_comment_manager, parent, false);
                return new CommentManagerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof CommentManagerViewHolder) {
                    CommentManagerViewHolder viewHolder = (CommentManagerViewHolder) holder;
                    viewHolder.setView(context, comments.get(position), position, viewType);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (comments == null ? 0 : comments.size()) + (footerView == null ? 0 : 1);
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
