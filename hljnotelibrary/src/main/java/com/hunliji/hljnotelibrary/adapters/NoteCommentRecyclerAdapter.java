package com.hunliji.hljnotelibrary.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.RepliedComment;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.adapters.viewholder.CommonNoteCommentViewHolder;

import java.util.ArrayList;

/**
 * Created by mo_yu on 2017/6/29.用户笔记评论adapter
 */

public class NoteCommentRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private final static int FOOTER_TYPE = 0;
    private final static int ITEM_TYPE = 1;

    private Context context;
    private ArrayList<RepliedComment> repliedComments;
    private View footerView;
    private LayoutInflater inflater;
    private OnCommentReplyListener onCommentReplyListener;
    private String entityType;

    public NoteCommentRecyclerAdapter(Context context, ArrayList<RepliedComment> repliedComments) {
        this.context = context;
        this.repliedComments = repliedComments;
        this.inflater = LayoutInflater.from(context);
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            default:
                CommonNoteCommentViewHolder noteViewHolder = new CommonNoteCommentViewHolder(
                        inflater.inflate(R.layout.note_comment_list_item___note, parent, false),
                        entityType);
                noteViewHolder.setOnCommentListener(new CommonNoteCommentViewHolder
                        .OnCommentListener() {
                    @Override
                    public void onComment(RepliedComment comment) {
                        if (onCommentReplyListener != null) {
                            onCommentReplyListener.onCommentItemClick(comment);
                        }
                    }
                });
                return noteViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE:
                holder.setView(context, repliedComments.get(position), position, viewType);
                break;
        }
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    @Override
    public int getItemViewType(int position) {
        if (footerView != null && position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return (repliedComments != null ? repliedComments.size() : 0) + (footerView != null ? 1 :
                0);
    }

    public interface OnCommentReplyListener {
        void onCommentItemClick(RepliedComment comment);
    }


    public void setOnCommentReplyListener(OnCommentReplyListener onCommentReplyListener) {
        this.onCommentReplyListener = onCommentReplyListener;
    }
}
