package com.hunliji.hljquestionanswer.adapters;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.ObjectBindAdapter;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/8/29.
 */
public class MyQuestionAdapter extends ObjectBindAdapter<Question> implements ObjectBindAdapter
        .ViewBinder<Question> {


    public MyQuestionAdapter(Context context, List<Question> list) {
        super(context, list, R.layout.my_question_item___qa);
        setViewBinder(this);
    }

    @Override
    public void setViewValue(
            View view, Question question, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder.line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        holder.tvTitle.setText(question.getTitle());
        holder.tvAnswerCount.setText(view.getContext()
                .getString(R.string.fmt_answer_count___qa,
                        NumberFormatUtil.formatThanThousand(question.getAnswerCount())));
        holder.tvWatchCount.setText(view.getContext()
                .getString(R.string.fmt_watch_count___qa,
                        NumberFormatUtil.formatThanThousand(question.getWatchCount())));
        holder.tvWatchCount.setVisibility(question.getType() == 1 ? View.VISIBLE : View.GONE);
    }

    static class ViewHolder {
        @BindView(R2.id.line)
        View line;
        @BindView(R2.id.tv_title)
        TextView tvTitle;
        @BindView(R2.id.tv_answer_count)
        TextView tvAnswerCount;
        @BindView(R2.id.tv_watch_count)
        TextView tvWatchCount;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
