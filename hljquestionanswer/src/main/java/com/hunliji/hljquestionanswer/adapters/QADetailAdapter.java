package com.hunliji.hljquestionanswer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljimagelibrary.adapters.viewholders.ExtraViewHolder;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.adapters.viewholder.OnQADetailActionInterface;
import com.hunliji.hljquestionanswer.adapters.viewholder.QADetailHeaderViewHolder;
import com.hunliji.hljquestionanswer.adapters.viewholder.QADetailViewHolder;

import java.util.List;

/**
 * Created by hua_rong on 2017/9/21
 * 问答详情
 */

public class QADetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private View footerView;
    private Context context;
    private List<Answer> answers;
    private int totalCount;
    private Question question;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final LayoutInflater inflater;
    private OnQADetailActionInterface onQADetailInterface;

    public void setOnQADetailInterface(OnQADetailActionInterface onQADetailInterface) {
        this.onQADetailInterface = onQADetailInterface;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public QADetailAdapter(Context context, List<Answer> answers) {
        this.context = context;
        this.answers = answers;
        inflater = LayoutInflater.from(context);
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                View view = inflater.inflate(R.layout.qa_detail_header___qa, parent, false);
                QADetailHeaderViewHolder headerViewHolder = new QADetailHeaderViewHolder(view);
                headerViewHolder.setActionInterface(onQADetailInterface);
                return headerViewHolder;
            case TYPE_FOOTER:
                return new ExtraViewHolder(footerView);
            default:
                View itemView = inflater.inflate(R.layout.qa_detail_item___qa, parent, false);
                QADetailViewHolder viewHolder = new QADetailViewHolder(itemView);
                viewHolder.setQaDetailActionInterface(onQADetailInterface);
                return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof QADetailViewHolder) {
            QADetailViewHolder viewHolder = (QADetailViewHolder) holder;
            viewHolder.setView(context, getItem(position), position - 1, getItemViewType(position));
        } else if (holder instanceof QADetailHeaderViewHolder) {
            QADetailHeaderViewHolder viewHolder = (QADetailHeaderViewHolder) holder;
            viewHolder.setTotalCount(totalCount);
            viewHolder.setView(context, question, position, getItemViewType(position));
        }
    }

    private Answer getItem(int position) {
        return answers.get(position - 1);
    }


    @Override
    public int getItemCount() {
        return (answers == null ? 0 : answers.size()) + 1 + (footerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

}
