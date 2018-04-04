package me.suncloud.marrymemo.adpter.newsearch;

import android.content.Context;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.CommonAnswerViewHolder;

import java.util.ArrayList;

import me.suncloud.marrymemo.R;

/**
 * Created by werther on 16/12/7.
 * 问答结果页
 */

public class NewSearchQaResultAdapter extends NewBaseSearchResultAdapter {

    public NewSearchQaResultAdapter(Context context, ArrayList<? extends Object> data) {
        super(context, data);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_HEADER:
            case ITEM_TYPE_FOOTER:
                return super.onCreateViewHolder(parent, viewType);
            default:
                CommonAnswerViewHolder answerViewHolder = new CommonAnswerViewHolder
                        (layoutInflater.inflate(
                        R.layout.common_question_answer_list_item___cv,
                        parent,
                        false));
                answerViewHolder.setOnItemClickListener(onItemClickListener);
                return answerViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_ITEM:
                int index = getItemIndex(position);
                if (holder instanceof CommonAnswerViewHolder) {
                    CommonAnswerViewHolder answerViewHolder = (CommonAnswerViewHolder) holder;
                    answerViewHolder.setShowBottomLineView(index < data.size() - 1);
                    answerViewHolder.setView(context, (Answer) data.get(index), index, viewType);
                }
                break;
        }
    }
}
