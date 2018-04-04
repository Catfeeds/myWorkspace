package com.hunliji.marrybiz.adapter.comment.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by hua_rong on 2017/10/9 0009
 */

public class CommentQuestionViewHolder extends BaseViewHolder<Question> {

    @BindView(R.id.tv_question_title)
    TextView tvQuestionTitle;
    @BindView(R.id.tv_answer_time)
    TextView tvAnswerTime;
    @BindView(R.id.tv_answer_count)
    TextView tvAnswerCount;
    @BindView(R.id.tv_is_answered)
    TextView tvIsAnswered;
    @BindView(R.id.line)
    View line;
    private int size;


    public void setSize(int size) {
        this.size = size;
    }

    public CommentQuestionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question question = getItem();
                if (question != null && question.getId() > 0) {
                    Activity activity = (Activity) view.getContext();
                    Intent intent = new Intent(activity, QuestionDetailActivity.class);
                    intent.putExtra("questionId", question.getId());
                    intent.putExtra("position", getAdapterPosition());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            }
        });
    }

    @OnClick(R.id.tv_create_answer)
    void onCreateClick(View view) {
        Question question = getItem();
        if (question != null && question.getId() > 0) {
            Activity activity = (Activity) view.getContext();
            Intent intent = new Intent(activity, QuestionDetailActivity.class);
            intent.putExtra("is_show_key_board", true);
            intent.putExtra("questionId", question.getId());
            intent.putExtra("position", getAdapterPosition());
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    protected void setViewData(
            Context context, final Question question, int position, int viewType) {
        if (question != null && question.getId() > 0) {
            itemView.setVisibility(View.VISIBLE);
            tvQuestionTitle.setText(question.getTitle());
            DateTime createdAt = question.getCreatedAt();
            if (createdAt != null) {
                tvAnswerTime.setText(createdAt.toString(Constants.DATE_FORMAT_SHORT));
            }
            int answerCount = question.getAnswerCount();
            tvAnswerCount.setText(answerCount == 0 ? context.getString(R.string
                    .label_no_answer_yet) : context.getString(
                    R.string.label_answer_count,
                    answerCount));
            tvIsAnswered.setVisibility(question.isAnswered() ? View.VISIBLE : View.GONE);
            line.setVisibility(position == size - 1 ? View.GONE : View.VISIBLE);
        }
    }
}
