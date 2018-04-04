package com.hunliji.hljquestionanswer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wangtao on 2016/12/21.
 */

public class SameQuestionAdapter extends RecyclerView.Adapter<BaseViewHolder<Question>> {

    private List<Question> questions;
    private Context context;

    public SameQuestionAdapter(Context context) {
        this.context = context;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        notifyDataSetChanged();
    }

    public void clear() {
        if (questions != null) {
            questions.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder<Question> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SameQuestionViewHolder(View.inflate(parent.getContext(),
                R.layout.same_question_item___qa,
                null));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Question> holder, int position) {
        holder.setView(context, getItem(position), position, getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return questions == null ? 0 : questions.size();
    }

    public Question getItem(int position) {
        return questions.get(position);
    }

    public class SameQuestionViewHolder extends BaseViewHolder<Question> {

        @BindView(R2.id.tv_title)
        TextView tvTitle;
        @BindView(R2.id.tv_watch_count)
        TextView tvWatchCount;
        @BindView(R2.id.tv_answer_count)
        TextView tvAnswerCount;

        public SameQuestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), QuestionDetailActivity.class);
                    intent.putExtra("questionId", getItem().getId());
                    v.getContext()
                            .startActivity(intent);
                }
            });
        }

        @Override
        protected void setViewData(
                Context mContext, Question item, int position, int viewType) {
            tvTitle.setText(item.getTitle());
            tvWatchCount.setText(tvWatchCount.getContext()
                    .getString(R.string.label_watch_count___qa) + item.getWatchCount());
            tvAnswerCount.setText(tvWatchCount.getContext()
                    .getString(R.string.label_answer___qa) + item.getAnswerCount());
        }
    }
}
