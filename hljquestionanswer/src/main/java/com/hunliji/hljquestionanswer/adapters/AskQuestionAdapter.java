package com.hunliji.hljquestionanswer.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljimagelibrary.adapters.viewholders.ExtraViewHolder;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.adapters.viewholder.AskQuestionViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/9/22
 * 用户端 问大家
 */

public class AskQuestionAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private View footerView;
    private Context context;
    private List<Question> questions;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private final LayoutInflater inflater;

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    private int totalCount;

    private Merchant merchant;

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public AskQuestionAdapter(Context context, List<Question> questions) {
        this.context = context;
        this.questions = questions;
        inflater = LayoutInflater.from(context);
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                View headerView = inflater.inflate(R.layout.ask_quesdtion_header___qa,
                        parent,
                        false);
                return new AskQuestionHeadViewHolder(headerView);
            case TYPE_FOOTER:
                return new ExtraViewHolder(footerView);
            default:
                View itemView = inflater.inflate(R.layout.ask_quesdtion_item___qa, parent, false);
                AskQuestionViewHolder viewHolder = new AskQuestionViewHolder(itemView);
                viewHolder.setOnItemClickListener(onItemClickListener);
                return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof AskQuestionViewHolder) {
            AskQuestionViewHolder viewHolder = (AskQuestionViewHolder) holder;
            viewHolder.setView(context, getItem(position), position - 1, getItemViewType(position));
        } else if (holder instanceof AskQuestionHeadViewHolder) {
            AskQuestionHeadViewHolder viewHolder = (AskQuestionHeadViewHolder) holder;
            viewHolder.setView(context, merchant, position, getItemViewType(position));
        }
    }

    private Question getItem(int position) {
        return questions.get(position - 1);
    }

    @Override
    public int getItemCount() {
        return (questions == null ? 0 : questions.size()) + 1 + (footerView == null ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position == getItemCount() - 1 && footerView != null) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    class AskQuestionHeadViewHolder extends BaseViewHolder<Merchant> {

        @BindView(R2.id.tv_title)
        TextView tvTitle;

        public AskQuestionHeadViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void setViewData(
                Context mContext, Merchant merchant, int position, int viewType) {
            if (merchant != null) {
                itemView.setVisibility(View.VISIBLE);
                String name = merchant.getName();
                if (!TextUtils.isEmpty(name) && name.length() > 12) {
                    name = name.substring(0, 12) + "...";
                }
                String title = String.format("关于 %1$s 服务相关的%2$s个问题", name, totalCount);
                int index = title.indexOf(name);
                StyleSpan span = new StyleSpan(Typeface.BOLD);
                SpannableStringBuilder builder = new SpannableStringBuilder(title);
                builder.setSpan(span,
                        index,
                        index + name.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvTitle.setText(builder);
            }
        }
    }


    public void setOnItemClickListener(OnItemClickListener<Question> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener<Question> onItemClickListener;


}
