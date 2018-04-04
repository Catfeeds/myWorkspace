package me.suncloud.marrymemo.adpter.community;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.Mark;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.QaVipMerchant;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerAnswerViewHolder;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerQuestionViewHolder;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljquestionanswer.activities.QAMarkDetailActivity;
import com.makeramen.rounded.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.community.QaHomeListFragment;

import static android.support.v7.widget.LinearLayoutManager.HORIZONTAL;

/**
 * Created by luohanlin on 2017/10/24.
 */

public class QaHomeListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<Question> questionList;
    private List<QaVipMerchant> vipMerchants;
    private OnItemClickListener onItemClickListener;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;
    public static final int TYPE_VIPS = 3;
    private View footerView;
    private View hintView;
    private int faceSize;
    private int bigImgWidth;
    private int bigImgHeight;
    private int listType;

    public QaHomeListAdapter(
            Context context, List<Question> questionList, int listType) {
        this.context = context;
        this.questionList = questionList;
        this.listType = listType;
        faceSize = CommonUtil.dp2px(context, 20);
        bigImgWidth = CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 32);
        bigImgHeight = CommonUtil.dp2px(context, 150);
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setVipMerchants(List<QaVipMerchant> vipMerchants) {
        this.vipMerchants = vipMerchants;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_FOOTER:
                return new ExtraBaseViewHolder(footerView);
            case TYPE_VIPS:
                return new QaVipsViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.qa_home_vips_item, parent, false));
            default:
                return new QaHomeQuestionViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.qa_home_list_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof QaHomeQuestionViewHolder) {
            if (CommonUtil.isCollectionEmpty(questionList)) {
                return;
            }
            int index = position;
            if (position > 1 && !CommonUtil.isCollectionEmpty(vipMerchants)) {
                index = position - 1;
            }
            holder.setView(context, questionList.get(index), position, getItemViewType(position));
        } else {
            holder.setView(context, null, position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size() + (footerView == null ? 0 : 1) + (CommonUtil.isCollectionEmpty(
                vipMerchants) ? 0 : 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else if (!CommonUtil.isCollectionEmpty(vipMerchants) && position == 1) {
            return TYPE_VIPS;
        } else {
            return TYPE_ITEM;
        }
    }

    class QaHomeQuestionViewHolder extends TrackerQuestionViewHolder {
        @BindView(R.id.content_layout)
        View contentLayout;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.img_avatar)
        RoundedImageView imgAvatar;
        @BindView(R.id.tv_answer_auth_name)
        TextView tvAnswerAuthName;
        @BindView(R.id.answer_auth_layout)
        LinearLayout answerAuthLayout;
        @BindView(R.id.img_cover_big)
        ImageView imgCoverBig;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_answer_count)
        TextView tvAnswerCount;
        @BindView(R.id.img_cover_mini)
        ImageView imgCoverMini;
        @BindView(R.id.img_cover_mini_2)
        ImageView imgCoverMini2;
        @BindView(R.id.marks_layout)
        LinearLayout marksLayout;
        @BindView(R.id.tv_praise_count)
        TextView tvPraiseCount;
        @BindView(R.id.divider)
        View divider;
        @BindView(R.id.tv_no_answer)
        TextView tvNoAnswer;
        @BindView(R.id.bottom_layout)
        LinearLayout bottomLayout;
        @BindView(R.id.img_hint)
        ImageView imgHint;

        QaHomeQuestionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public View trackerView() {
            return contentLayout;
        }
        @Override
        protected void setViewData(
                Context mContext, final Question question, final int position, int viewType) {
            divider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, question);
                    }
                }
            });
            tvTitle.setText(EmojiUtil.parseEmojiByText2(context, question.getTitle(), faceSize));
            QaAuthor author = null;
            if (question.getAnswer() != null) {
                tvNoAnswer.setVisibility(View.GONE);
                String imageUrl = null;
                if (!TextUtils.isEmpty(question.getCoverPath())) {
                    imageUrl = question.getCoverPath();
                } else if (question.getAnswer() != null && !TextUtils.isEmpty(question.getAnswer()
                        .getCoverPath())) {
                    imageUrl = question.getAnswer()
                            .getCoverPath();
                }
                if (question.getAnswer() != null && question.getAnswer()
                        .isRewriteStyle()) {
                    // 精编答案
                    imgCoverMini.setVisibility(View.GONE);
                    imgCoverMini2.setVisibility(View.GONE);
                    author = question.getAnswer()
                            .getUser();

                    if (!TextUtils.isEmpty(imageUrl)) {
                        imgCoverBig.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(ImagePath.buildPath(question.getAnswer()
                                        .getCoverPath())
                                        .width(bigImgWidth)
                                        .height(bigImgHeight)
                                        .cropPath())
                                .into(imgCoverBig);
                    } else {
                        imgCoverBig.setVisibility(View.GONE);
                    }
                    tvContent.setVisibility(View.VISIBLE);
                    tvAnswerCount.setVisibility(View.GONE);
                    tvContent.setText(EmojiUtil.parseEmojiByText2(context,
                            question.getAnswer()
                                    .getSummary(),
                            faceSize));
                } else {
                    // 非精编
                    if (question.getAnswer() != null && !TextUtils.isEmpty(question.getAnswer()
                            .getSummary())) {
                        // 有回答摘要
                        tvContent.setVisibility(View.VISIBLE);
                        tvAnswerCount.setVisibility(View.GONE);
                        author = question.getAnswer()
                                .getUser();
                        tvContent.setText(EmojiUtil.parseEmojiByText2(context,
                                question.getAnswer()
                                        .getSummary(),
                                faceSize));
                        if (!TextUtils.isEmpty(imageUrl)) {
                            imgCoverMini.setVisibility(View.VISIBLE);
                            imgCoverMini2.setVisibility(View.GONE);
                            Glide.with(context)
                                    .load(ImagePath.buildPath(imageUrl)
                                            .width(CommonUtil.dp2px(context, 92))
                                            .height(CommonUtil.dp2px(context, 92))
                                            .cropPath())
                                    .into(imgCoverMini);
                        } else {
                            imgCoverMini.setVisibility(View.GONE);
                            imgCoverMini2.setVisibility(View.GONE);
                        }
                    } else if (question.getAnswerCount() > 0) {
                        // 无回答摘要
                        tvContent.setVisibility(View.GONE);
                        tvAnswerCount.setVisibility(View.VISIBLE);
                        tvAnswerCount.setText(context.getString(R.string.label_answer_count___cm,
                                question.getAnswerCount()));
                        if (!TextUtils.isEmpty(imageUrl)) {
                            imgCoverMini2.setVisibility(View.VISIBLE);
                            imgCoverMini.setVisibility(View.GONE);
                            Glide.with(context)
                                    .load(ImagePath.buildPath(imageUrl)
                                            .width(CommonUtil.dp2px(context, 92))
                                            .height(CommonUtil.dp2px(context, 92))
                                            .cropPath())
                                    .into(imgCoverMini2);
                        } else {
                            imgCoverMini.setVisibility(View.GONE);
                            imgCoverMini2.setVisibility(View.GONE);
                        }
                    } else {
                        // 暂无回答
                        imgCoverBig.setVisibility(View.GONE);
                        imgCoverMini.setVisibility(View.GONE);
                        imgCoverMini2.setVisibility(View.GONE);
                        tvContent.setVisibility(View.GONE);
                        tvAnswerCount.setVisibility(View.GONE);
                        tvNoAnswer.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                // 暂无回答
                imgCoverBig.setVisibility(View.GONE);
                imgCoverMini.setVisibility(View.GONE);
                imgCoverMini2.setVisibility(View.GONE);
                tvContent.setVisibility(View.GONE);
                tvAnswerCount.setVisibility(View.GONE);
                tvNoAnswer.setVisibility(View.VISIBLE);
            }

            // 回答者
            if (author != null) {
                answerAuthLayout.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(ImagePath.buildPath(question.getAnswer()
                                .getUser()
                                .getAvatar())
                                .width(CommonUtil.dp2px(context, 20))
                                .height(CommonUtil.dp2px(context, 20))
                                .cropPath())
                        .apply(new RequestOptions().dontAnimate())
                        .into(imgAvatar);
                tvAnswerAuthName.setText(question.getAnswer()
                        .getUser()
                        .getName());
            } else {
                answerAuthLayout.setVisibility(View.GONE);
            }

            // 底部
            int praiseCount = 0;
            if (question.getAnswer() != null) {
                praiseCount = question.getAnswer()
                        .getUpCount();
            }
            imgHint.setVisibility(View.GONE);
            if ((question.getMarks() == null || question.getMarks()
                    .isEmpty()) && praiseCount == 0) {
                bottomLayout.setVisibility(View.GONE);
            } else {
                bottomLayout.setVisibility(View.VISIBLE);
                if (!CommonUtil.isCollectionEmpty(question.getMarks())) {
                    showHintView(imgHint, position);
                    marksLayout.setVisibility(View.VISIBLE);
                    marksLayout.removeAllViews();
                    for (int i = 0; i < question.getMarks()
                            .size() && i < 2; i++) {
                        final Mark mark = question.getMarks()
                                .get(i);
                        View view = LayoutInflater.from(context)
                                .inflate(R.layout.qa_home_list_mark_text_view, null, false);
                        TextView tvMark = view.findViewById(R.id.tv_title);
                        tvMark.setText("#" + mark.getName());
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideHintView();
                                Intent intent = new Intent(context, QAMarkDetailActivity.class);
                                intent.putExtra("id", mark.getId());
                                context.startActivity(intent);
                            }
                        });
                        marksLayout.addView(view);
                    }
                } else {
                    marksLayout.setVisibility(View.INVISIBLE);
                }
                if (praiseCount > 0) {
                    tvPraiseCount.setVisibility(View.VISIBLE);
                    tvPraiseCount.setText(context.getString(R.string.label_answer_praise_count___cm,
                            praiseCount));
                } else {
                    tvPraiseCount.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    private void showHintView(final View view, final int position) {
        this.hintView = view;
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREF_FILE,
                Context.MODE_PRIVATE);
        boolean hasShowed = preferences.getBoolean(Constants.PREF_QA_HOME_MARK_HINT_SHOWED, false);
        if (position == 0 && listType == QaHomeListFragment.TYPE_HOTEST && !hasShowed) {
            hintView.setVisibility(View.VISIBLE);
            hintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideHintView();
                }
            });
        }
    }

    private void hideHintView() {
        if (hintView != null) {
            context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(Constants.PREF_QA_HOME_MARK_HINT_SHOWED, true)
                    .apply();
            hintView.setVisibility(View.GONE);
            notifyItemChanged(0);
        }
    }

    class QaVipsViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.recycler_view)
        RecyclerView recyclerView;

        QaVipsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            HljVTTagger.tagViewParentName(recyclerView, "qa_active_merchant_list");
        }

        @Override
        protected void setViewData(Context mContext, Object item, int position, int viewType) {
            if (CommonUtil.isCollectionEmpty(vipMerchants)) {
                return;
            }
            LinearLayoutManager prizeLayoutManager = new LinearLayoutManager(context,
                    HORIZONTAL,
                    false);
            recyclerView.setFocusable(false);
            recyclerView.setLayoutManager(prizeLayoutManager);
            QaVipsAdapter adapter = new QaVipsAdapter(mContext, vipMerchants);
            recyclerView.setAdapter(adapter);
        }
    }
}
