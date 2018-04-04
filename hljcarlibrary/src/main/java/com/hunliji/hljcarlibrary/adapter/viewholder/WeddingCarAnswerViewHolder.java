package com.hunliji.hljcarlibrary.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcarlibrary.R;
import com.hunliji.hljcarlibrary.R2;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljquestionanswer.adapters.viewholder.AskQuestionViewHolder;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jinxin on 2017/12/26 0026.
 */

public class WeddingCarAnswerViewHolder extends BaseViewHolder<HljHttpQuestion<List<Question>>> {

    @BindView(R2.id.tv_question_count)
    TextView tvQuestionCount;
    @BindView(R2.id.question_brief_info_layout)
    RelativeLayout questionBriefInfoLayout;
    @BindView(R2.id.layout_answer_content)
    LinearLayout layoutAnswerContent;
    @BindView(R2.id.layout_answer_empty)
    LinearLayout layoutAnswerEmpty;

    private Context mContext;
    private long merchantId;
    private onWeddingCarAnswerViewHolderClickListener onWeddingCarAnswerViewHolderClickListener;

    public WeddingCarAnswerViewHolder(View itemView,long merchantId) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
        this.merchantId = merchantId;
    }

    public void setOnWeddingCarAnswerViewHolderClickListener(
            WeddingCarAnswerViewHolder.onWeddingCarAnswerViewHolderClickListener
                    onWeddingCarAnswerViewHolderClickListener) {
        this.onWeddingCarAnswerViewHolderClickListener = onWeddingCarAnswerViewHolderClickListener;
    }

    @Override
    protected void setViewData(
            Context mContext,
            HljHttpQuestion<List<Question>> questionListData,
            int position,
            int viewType) {
        setQuestions(questionListData);
    }

    private void setQuestions(HljHttpQuestion<List<Question>> questionListData) {
        if (questionListData == null || questionListData.getData() == null || questionListData
                .getData()
                .isEmpty()) {
            layoutAnswerEmpty.setVisibility(View.VISIBLE);
            layoutAnswerContent.setVisibility(View.GONE);
        } else {
            layoutAnswerEmpty.setVisibility(View.GONE);
            layoutAnswerContent.setVisibility(View.VISIBLE);
            tvQuestionCount.setText(mContext.getString(R.string.label_ask_question_count___car,
                    String.valueOf(questionListData.getTotalCount())));
            if (questionBriefInfoLayout.getChildCount() == 0) {
                View.inflate(mContext, R.layout.ask_quesdtion_item___qa, questionBriefInfoLayout);
            }
            View questionView = questionBriefInfoLayout.getChildAt(questionBriefInfoLayout
                    .getChildCount() - 1);
            AskQuestionViewHolder holder = (AskQuestionViewHolder) questionView.getTag();
            if (holder == null) {
                holder = new AskQuestionViewHolder(questionView);
                holder.setOnItemClickListener(new OnItemClickListener<Question>() {
                    @Override
                    public void onItemClick(int position, Question object) {
                        layoutAnswerContent.performClick();
                    }
                });
                questionView.setTag(holder);
            }
            holder.setWorkWithMerchantVisibility(true);
            Question question = questionListData.getData().get(0);
            holder.setView(mContext,
                    question,
                    0,
                    0);
        }
        layoutAnswerEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAskQuestionList(merchantId,true);
            }
        });
        layoutAnswerContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAskQuestionList(merchantId,false);
            }
        });
    }

    //问大家列表
    private void onAskQuestionList(long merchantId,boolean isShowKeyboard) {
        if (onWeddingCarAnswerViewHolderClickListener != null) {
            onWeddingCarAnswerViewHolderClickListener.onAskQuestionList(merchantId, isShowKeyboard);
        }
    }

    public interface onWeddingCarAnswerViewHolderClickListener {
        void onAskQuestionList(long merchantId, boolean isShowKeyboard);
    }
}
