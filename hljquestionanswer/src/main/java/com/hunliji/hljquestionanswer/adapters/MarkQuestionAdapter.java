package com.hunliji.hljquestionanswer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.MarkQuestionViewHolder;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;

import java.util.List;

/**
 * Created by Suncloud on 2016/8/30.类标签问题adapter
 */
public class MarkQuestionAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<Question> questions;
    private static final int ITEM_TYPE = 1;
    private static final int FOOTER_TYPE = 2;
    private static final int HEADER_TYPE = 3;
    private View footerView;
    private View headerView;

    public MarkQuestionAdapter(Context context, List<Question> answers) {
        this.context = context;
        this.questions = answers;
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
                MarkQuestionViewHolder holder = new MarkQuestionViewHolder(LayoutInflater.from(
                        parent.getContext())
                        .inflate(R.layout.mark_question_item___cv, parent, false));
                final Activity activity = (Activity) context;
                holder.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Object object) {
                        Question question = (Question) object;
                        if (question != null && question.getId() > 0) {
                            Intent intent = new Intent(activity, QuestionDetailActivity.class);
                            intent.putExtra("questionId", question.getId());
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
                return holder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof MarkQuestionViewHolder) {
            holder.setView(context,
                    questions.get(headerView == null ? position : position - 1),
                    headerView == null ? position : position - 1,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return questions.size() + (footerView == null ? 0 : 1) + (headerView == null ? 0 : 1);
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

