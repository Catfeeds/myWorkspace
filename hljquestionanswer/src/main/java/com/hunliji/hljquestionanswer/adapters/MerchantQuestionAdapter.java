package com.hunliji.hljquestionanswer.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.activities.CreateAnswerActivity;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljquestionanswer.models.wrappers.RecQaWrappers;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2016/8/18.商家-推荐问答列表
 */
public class MerchantQuestionAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<RecQaWrappers> recQaAnswers;

    private static final int ITEM_TYPE = 1;
    private static final int FOOTER_TYPE = 2;
    private View footerView;

    private int faceSize;

    public MerchantQuestionAdapter(Context context) {
        this.context = context;
        recQaAnswers = new ArrayList<>();
        DisplayMetrics dm = context.getResources()
                .getDisplayMetrics();
        faceSize = Math.round(dm.density * 20);
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setData(ArrayList<RecQaWrappers> recQaWrapperses) {
        if (recQaWrapperses != null) {
            int oldSize = this.recQaAnswers.size();
            int size = recQaWrapperses.size();
            this.recQaAnswers.clear();
            this.recQaAnswers.addAll(recQaWrapperses);
            if (Math.min(oldSize, size) > 0) {
                notifyItemRangeChanged(0, Math.min(oldSize, size) - 1);
            }
            if (oldSize > size) {
                notifyItemRangeRemoved(size, oldSize - size - 1);
            } else if (oldSize < size) {
                notifyItemRangeInserted(oldSize, size - oldSize - 1);
            }
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.merchant_question_list_item, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            holder.setView(context,
                    recQaAnswers.get(position),
                    position,
                    getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return recQaAnswers.size() + (footerView == null ? 0 : 1);
    }

    class ItemViewHolder extends BaseViewHolder<RecQaWrappers> {
        @BindView(R2.id.tv_answer_count)
        TextView tvAnswerCount;
        @BindView(R2.id.iv_tag)
        ImageView ivTag;
        @BindView(R2.id.tv_mark_1)
        TextView tvMark1;
        @BindView(R2.id.tv_mark_2)
        TextView tvMark2;
        @BindView(R2.id.tv_create_answer)
        TextView tvCreateAnswer;
        @BindView(R2.id.tv_question_title)
        TextView tvQuestionTitle;
        @BindView(R2.id.rec_question_view)
        View recQuestionView;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

        @Override
        protected void setViewData(
                final Context mContext, final RecQaWrappers item, int position, int viewType) {

            tvQuestionTitle.setVisibility(View.VISIBLE);
            tvQuestionTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                    item.getTitle(),
                    faceSize));

            tvAnswerCount.setText(mContext.getString(R.string.fmt_watch_count___qa,
                    String.valueOf(item.getWatchCount())));
            tvMark1.setText("");
            tvMark2.setText("");
            if (item.getMarkList() != null && item.getMarkList()
                    .size() > 0) {
                ivTag.setVisibility(View.VISIBLE);
                tvMark1.setText(item.getMarkList()
                        .get(0)
                        .getName());
                if (item.getMarkList()
                        .size() > 1) {
                    tvMark2.setText(item.getMarkList()
                            .get(1)
                            .getName());
                }
            } else {
                ivTag.setVisibility(View.GONE);
            }
            final Activity activity = (Activity) mContext;
            tvCreateAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MerchantUser merchantUser = (MerchantUser) UserSession.getInstance()
                            .getUser(activity);
                    if (merchantUser == null) {
                        return;
                    }
                    if (merchantUser.getExamine() != 1 || merchantUser.getCertifyStatus() !=
                            3) {
                        AnswerPopupRule.getDefault().showHintDialog(activity);
                    } else if (!merchantUser.isPro()) {
                        AnswerPopupRule.getDefault().showProDialog(activity);
                    } else {
                        Intent intent = new Intent(activity, CreateAnswerActivity.class);
                        intent.putExtra("questionId", item.getId());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
            recQuestionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (item.getId() != 0) {
                        Intent intent = new Intent(activity, QuestionDetailActivity.class);
                        intent.putExtra("questionId", item.getId());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        }
    }

    private Dialog proDialog;
    private Dialog exaDialog;
}
