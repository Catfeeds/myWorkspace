package com.hunliji.hljquestionanswer.adapters.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/9/27 0027
 * 问大家列表
 */

public class AskQuestionViewHolder extends BaseViewHolder<Question> {

    @BindView(R2.id.tv_author)
    TextView tvAuthor;
    @BindView(R2.id.ll_author)
    LinearLayout llAuthor;
    @BindView(R2.id.tv_question)
    TextView tvQuestion;
    @BindView(R2.id.tv_total_answer)
    TextView tvTotalAnswer;
    @BindView(R2.id.tv_answer)
    TextView tvAnswer;
    @BindView(R2.id.tv_time)
    TextView tvTime;
    @BindView(R2.id.tv_link)
    TextView tvLink;
    @BindView(R2.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R2.id.iv_answer_tag)
    ImageView ivAnswerTag;

    private boolean visibility;

    public void setOnItemClickListener(OnItemClickListener<Question> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener<Question> onItemClickListener;

    public AskQuestionViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ask_quesdtion_item___qa, parent, false));
    }

    public AskQuestionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(getAdapterPosition() - 1, getItem());
                }
            }
        });
    }

    /**
     * 商家主页和套餐问答相关
     */
    public void setWorkWithMerchantVisibility(boolean visibility) {
        this.visibility = visibility;
    }


    @Override
    protected void setViewData(Context context, Question question, int position, int viewType) {
        if (question != null) {
            itemView.setVisibility(View.VISIBLE);
            QaAuthor user = question.getUser();
            if (user != null) {
                tvAuthor.setText(context.getString(R.string.label_author_question___qa,
                        user.getName()));
            }
            int answerCount = question.getAnswerCount();
            tvQuestion.setText(question.getTitle());
            tvLink.setText(context.getString(R.string.label_look_all_answer___qa, answerCount));
            tvLink.setVisibility(answerCount > 0 ? View.VISIBLE : View.GONE);
            ivAnswerTag.setImageResource(answerCount == 0 ? R.mipmap.icon_answer_gray___qa : R
                    .mipmap.icon_answer_green___qa);
            Answer answer = question.getAnswer();
            String content = answer.getContent();
            if (TextUtils.isEmpty(content)) {
                content = context.getString(R.string.label_answer_count_none___cm);
            }
            tvAnswer.setText(content);
            DateTime createdAt = question.getCreatedAt();
            if (createdAt != null) {
                tvTime.setText(createdAt.toString("yyyy-MM-dd"));
            }
            if (visibility) {
                rlBottom.setVisibility(View.GONE);
                llAuthor.setVisibility(View.GONE);
                if (answerCount > 0) {
                    tvTotalAnswer.setVisibility(View.VISIBLE);
                    tvTotalAnswer.setText(context.getString(R.string
                                    .label_answer_detail_count_tip___qa,
                            answerCount));
                }
            }
        }
    }

}
