package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerAnswerViewHolder;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 16/12/20ot.问答通用样式
 */

public class CommonAnswerViewHolder extends TrackerAnswerViewHolder {

    @BindView(R2.id.tv_question_answer_title)
    TextView tvQuestionAnswerTitle;
    @BindView(R2.id.tv_answer_count)
    TextView tvAnswerCount;
    @BindView(R2.id.iv_question_answer)
    ImageView ivQuestionAnswer;
    @BindView(R2.id.iv_hot_question_answer)
    ImageView ivHotQuestionAnswer;
    @BindView(R2.id.tv_question_answer_content)
    TextView tvQuestionAnswerContent;
    @BindView(R2.id.tv_auth_name)
    TextView tvAuthName;
    @BindView(R2.id.tv_praise_count)
    TextView tvPraiseCount;
    @BindView(R2.id.ll_question_answer_view)
    LinearLayout llQuestionAnswerView;
    @BindView(R2.id.line_layout)
    View lineLayout;
    @BindView(R2.id.question_answer_view)
    LinearLayout questionAnswerView;
    @BindView(R2.id.iv_auth)
    RoundedImageView ivAuth;
    @BindView(R2.id.auth_view)
    LinearLayout authView;

    private int faceSize;
    private OnItemClickListener onItemClickListener;
    private int imageWidth;
    private int hotImageWidth;

    public CommonAnswerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        faceSize = CommonUtil.dp2px(itemView.getContext(), 20);
        imageWidth = CommonUtil.dp2px(itemView.getContext(), 115);
        int imageHeight = CommonUtil.dp2px(itemView.getContext(), 72);
        hotImageWidth = CommonUtil.getDeviceSize(itemView.getContext()).x - CommonUtil.dp2px(
                itemView.getContext(),
                24);
        ivQuestionAnswer.getLayoutParams().height = imageHeight;
        ivQuestionAnswer.getLayoutParams().width = imageWidth;
        ivHotQuestionAnswer.getLayoutParams().width = hotImageWidth;
        ivHotQuestionAnswer.getLayoutParams().height = hotImageWidth * 7 / 16;
    }

    @Override
    public View trackerView() {
        return questionAnswerView;
    }

    @Override
    protected void setViewData(
            final Context mContext, final Answer answer, final int position, int viewType) {
        if (answer == null) {
            return;
        }
        //问题摘要
        if (answer.getQuestion() != null) {
            //热门回答
            tvQuestionAnswerTitle.setVisibility(View.VISIBLE);
            tvQuestionAnswerTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                    answer.getQuestion()
                            .getTitle(),
                    faceSize));
            //回答数
            if (answer.getQuestion()
                    .getAnswerCount() == 0) {
                tvAnswerCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray2));
                tvAnswerCount.setText(mContext.getString(R.string.label_answer_count_none___cm));
            } else {
                tvAnswerCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                tvAnswerCount.setText(mContext.getString(R.string.label_answer_count___cm,
                        answer.getQuestion()
                                .getAnswerCount()));
            }
        }

        //回答摘要
        if (!TextUtils.isEmpty(answer.getSummary())) {
            //全部问题该字段为空

            tvQuestionAnswerContent.setVisibility(View.VISIBLE);
            tvQuestionAnswerContent.setText(EmojiUtil.parseEmojiByText2(mContext,
                    mContext.getString(R.string.label_answer_content___cm, answer.getSummary()),
                    faceSize));
        } else {
            tvQuestionAnswerContent.setVisibility(View.GONE);
        }
        String imageUrl = null;
        if (!answer.isRewriteStyle()) {
            //非热门回答图片显示
            if (!TextUtils.isEmpty(answer.getCoverPath())) {
                imageUrl = ImageUtil.getImagePath(answer.getCoverPath(), imageWidth);
            }
            ivHotQuestionAnswer.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(imageUrl)) {
                ivQuestionAnswer.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(imageUrl)
                        .apply(new RequestOptions().centerCrop()
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(ivQuestionAnswer);
            } else {
                ivQuestionAnswer.setVisibility(View.GONE);
                Glide.with(mContext)
                        .clear(ivQuestionAnswer);
                ivQuestionAnswer.setImageBitmap(null);
            }
        } else if (answer.getQuestion() != null) {
            //热门回答图片显示
            if (!TextUtils.isEmpty(answer.getCoverPath())) {
                imageUrl = ImageUtil.getImagePath(answer.getCoverPath(), hotImageWidth);
            }
            ivQuestionAnswer.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(imageUrl)) {
                ivHotQuestionAnswer.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(imageUrl)
                        .apply(new RequestOptions().centerCrop()
                                .placeholder(R.mipmap.icon_empty_image))
                        .into(ivHotQuestionAnswer);
            } else {
                ivHotQuestionAnswer.setVisibility(View.GONE);
                Glide.with(mContext)
                        .clear(ivHotQuestionAnswer);
                ivHotQuestionAnswer.setImageBitmap(null);
            }
        } else {
            ivQuestionAnswer.setVisibility(View.GONE);
            ivHotQuestionAnswer.setVisibility(View.GONE);
        }
        //回答
        authView.setVisibility(View.VISIBLE);
        int praiseCount;
        final QaAuthor author;
        praiseCount = answer.getUpCount();
        author = answer.getUser();

        if (author != null) {
            authView.setVisibility(View.VISIBLE);
            String url = author.getAvatar();
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().dontAnimate())
                    .into(ivAuth);
            tvAuthName.setText(author.getName());
            authView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (author.getKind() == 0) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                                .withLong("id", author.getId())
                                .navigation(mContext);
                    } else {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                                .withLong("id", author.getMerchantId())
                                .navigation(mContext);
                    }
                }
            });
            if (praiseCount == 0) {
                tvPraiseCount.setVisibility(View.GONE);
            } else {
                tvPraiseCount.setVisibility(View.VISIBLE);
                tvPraiseCount.setText(mContext.getString(R.string.label_answer_praise_count___cm,
                        praiseCount));
            }

        } else {
            authView.setVisibility(View.GONE);
        }
        questionAnswerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, answer);
                }
            }
        });
    }

    public void setShowBottomLineView(boolean showBottomLineView) {
        lineLayout.setVisibility(showBottomLineView ? View.VISIBLE : View.GONE);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
