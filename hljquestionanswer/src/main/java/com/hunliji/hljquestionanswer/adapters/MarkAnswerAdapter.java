package com.hunliji.hljquestionanswer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.MarkAnswerViewHolder;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;

import java.util.List;

/**
 * Created by mo_yu on 2016/8/29.类标签回答样式
 */
public class MarkAnswerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<Answer> answers;
    private boolean dividerAtTop;
    private static final int ITEM_TYPE = 1;
    private static final int FOOTER_TYPE = 2;
    private static final int HEADER_TYPE = 3;
    private View footerView;
    private View headerView;

    public MarkAnswerAdapter(Context context, List<Answer> answers) {
        this.context = context;
        this.answers = answers;
    }

    public void setDividerAtTop(boolean dividerAtTop) {
        this.dividerAtTop = dividerAtTop;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            case HEADER_TYPE:
                return new ExtraBaseViewHolder(headerView);
            default:
                MarkAnswerViewHolder holder = new MarkAnswerViewHolder(LayoutInflater.from(parent
                        .getContext())
                        .inflate(R.layout.mark_answer_item___cv, parent, false));
                holder.setDividerAtTop(dividerAtTop);
                final Activity activity = (Activity) context;
                holder.setOnItemClickListener(new OnItemClickListener<Question>() {
                    @Override
                    public void onItemClick(int position, Question question) {
                        if (question.getId() == 0) {
                            return;
                        }
                        Intent intent = new Intent(activity, QuestionDetailActivity.class);
                        intent.putExtra("questionId", question.getId());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                holder.setOnUserClickListener(new MarkAnswerViewHolder.OnUserClickListener() {
                    @Override
                    public void onUserClick(int position, Object object) {
                        QaAuthor author = (QaAuthor) object;
                        if (HljQuestionAnswer.isMerchant(activity)) {
                            return;
                        }
                        if (author.getKind() == 1) {
                            //跳转到商家主页
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                                    .withLong("id", author.getMerchantId())
                                    .navigation(context);
                        } else if (author.getId() > 0) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                                    .withLong("id", author.getId())
                                    .navigation(context);
                        }
                    }
                });
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof MarkAnswerViewHolder) {
            holder.setView(context,
                    answers.get(headerView == null ? position : position - 1),
                    headerView == null ? position : position - 1,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return answers.size() + (footerView == null ? 0 : 1) + (headerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return HEADER_TYPE;
        } else if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }
}
