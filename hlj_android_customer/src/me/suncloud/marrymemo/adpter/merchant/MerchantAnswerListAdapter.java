package me.suncloud.marrymemo.adpter.merchant;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.MarkAnswerViewHolder;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;

import java.util.List;

/**
 * Created by wangtao on 2017/1/6.
 */

public class MerchantAnswerListAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        OnItemClickListener<Question>, MarkAnswerViewHolder.OnQuestionClickListener {

    private Context context;
    private List<Answer> answers;
    private View headerView;
    private View footerView;

    private final int HEADER = -1;
    private final int FOOTER = -2;
    private final int ANSWER = 1;

    public MerchantAnswerListAdapter(Context context) {
        this.context = context;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
        notifyDataSetChanged();
    }

    public void addAnswers(List<Answer> answers) {
        int start = getItemCount();
        if (footerView != null) {
            start--;
        }
        this.answers.addAll(answers);
        notifyItemRangeInserted(start, answers.size());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ANSWER:
                MarkAnswerViewHolder holder = new MarkAnswerViewHolder(parent);
                holder.setOnItemClickListener(this);
                return holder;
            case HEADER:
                return new ExtraBaseViewHolder(headerView);
            case FOOTER:
                return new ExtraBaseViewHolder(footerView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case ANSWER:
                holder.setView(context,
                        getAnswer(position),
                        position - (headerView != null ? 1 : 0),
                        type);
                break;
        }
    }

    private Answer getAnswer(int position) {
        if (headerView != null) {
            position--;
        }
        if (position >= 0 && answers != null && answers.size() > position) {
            return answers.get(position);
        }
        return null;

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (headerView != null) {
            count++;
        }
        if (answers != null) {
            count += answers.size();
        }
        if (footerView != null) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null) {
            if (position == 0) {
                return HEADER;
            }
            position--;
        }
        if (answers != null && answers.size() > position) {
            return ANSWER;
        }
        return FOOTER;
    }

    @Override
    public void onItemClick(
            int position, Question question) {
        if (context == null || question.getId() == 0) {
            return;
        }
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        intent.putExtra("questionId", question.getId());
        context.startActivity(intent);


    }

    @Override
    public void onQuestionClick(int position, Question question) {
        if (context == null || question.getId() == 0) {
            return;
        }
        Intent intent = new Intent(context, QuestionDetailActivity.class);
        intent.putExtra("questionId", question.getId());
        context.startActivity(intent);
    }
}
