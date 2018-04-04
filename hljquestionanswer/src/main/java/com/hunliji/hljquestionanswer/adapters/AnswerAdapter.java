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
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonAnswerViewHolder;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;

import java.util.ArrayList;

/**
 * Created by mo_yu on 2016/12/20 回答列表
 */
public class AnswerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<Answer> answers;

    private static final int ITEM_TYPE = 1;
    private static final int FOOTER_TYPE = 2;
    private static final int HEADER_TYPE = 3;
    private View footerView;
    private View headerView;

    public AnswerAdapter(
            Context context, ArrayList<Answer> data) {
        this.context = context;
        answers = data;
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
                CommonAnswerViewHolder holder = new CommonAnswerViewHolder(LayoutInflater.from(
                        parent.getContext())
                        .inflate(R.layout.common_question_answer_list_item___cv, parent, false));
                holder.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, Object object) {
                        Activity activity = (Activity) context;
                        Answer item = (Answer) object;
                        Intent intent = new Intent();
                        intent.setClass(activity, QuestionDetailActivity.class);
                        intent.putExtra("questionId",
                                item.getQuestion()
                                        .getId());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                return holder;
        }
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

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof CommonAnswerViewHolder) {
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
}
