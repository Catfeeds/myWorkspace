package com.hunliji.marrybiz.adapter.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.viewholder.CommentQuestionViewHolder;

import java.util.List;

/**
 * Created by hua_rong on 2017/9/21
 * 商家--评论问大家
 */

public class CommentQuestionAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Question> questions;
    private LayoutInflater layoutInflater;
    private Context context;
    private final static int ITEM_TYPE_LIST = 0;
    private final static int ITEM_TYPE_FOOTER = 1;
    private View footerView;
    private int size;

    public void setSize(int size) {
        this.size = size;
    }

    public CommentQuestionAdapter(Context context, List<Question> questions) {
        this.context = context;
        this.questions = questions;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
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
                View view = layoutInflater.inflate(R.layout.item_comment_question, parent, false);
                return new CommentQuestionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_LIST:
                if (holder instanceof CommentQuestionViewHolder) {
                    CommentQuestionViewHolder viewHolder = (CommentQuestionViewHolder) holder;
                    viewHolder.setSize(size);
                    viewHolder.setView(context, questions.get(position), position, viewType);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (questions == null ? 0 : questions.size()) + (footerView == null ? 0 : 1);
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
