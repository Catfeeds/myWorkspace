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
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 16/12/20ot.问答通用样式
 */

public class CommonQuestionViewHolder extends BaseViewHolder<Question> {

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

    public CommonQuestionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        faceSize = CommonUtil.dp2px(itemView.getContext(), 20);
        imageWidth = CommonUtil.dp2px(itemView.getContext(), 115);
        int imageHeight = CommonUtil.dp2px(itemView.getContext(), 72);
        ivQuestionAnswer.getLayoutParams().height = imageHeight;
        ivQuestionAnswer.getLayoutParams().width = imageWidth;
    }

    @Override
    protected void setViewData(
            final Context mContext, final Question item, final int position, int viewType) {
        if (item == null) {
            return;
        }
        //问题摘要
        if (!TextUtils.isEmpty(item.getTitle())) {
            tvQuestionAnswerTitle.setVisibility(View.VISIBLE);
            tvQuestionAnswerTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                    item.getTitle(),
                    faceSize));
        } else {
            tvQuestionAnswerTitle.setVisibility(View.GONE);
        }
        //回答数
        if (item.getAnswerCount() == 0) {
            tvAnswerCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray2));
            tvAnswerCount.setText(mContext.getString(R.string.label_answer_count_none___cm));
        } else {
            tvAnswerCount.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            tvAnswerCount.setText(mContext.getString(R.string.label_answer_count___cm,
                    item.getAnswerCount()));
        }

        //回答摘要
        if (item.getAnswer() != null && !TextUtils.isEmpty(item.getAnswer()
                .getSummary())) {
            tvQuestionAnswerContent.setVisibility(View.VISIBLE);
            tvQuestionAnswerContent.setText(EmojiUtil.parseEmojiByText2(mContext,
                    mContext.getString(R.string.label_answer_content___cm,
                            item.getAnswer()
                                    .getSummary()),
                    faceSize));
        } else {
            tvQuestionAnswerContent.setVisibility(View.GONE);
        }
        String imageUrl = null;
        //非热门回答图片显示
        if (!TextUtils.isEmpty(item.getCoverPath())) {
            imageUrl = ImageUtil.getImagePath(item.getCoverPath(), imageWidth);
        } else if (item.getAnswer() != null && !TextUtils.isEmpty(item.getAnswer()
                .getCoverPath())) {
            imageUrl = ImageUtil.getImagePath(item.getAnswer()
                    .getCoverPath(), imageWidth);
        }
        ivHotQuestionAnswer.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(imageUrl)) {
            ivQuestionAnswer.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(imageUrl)
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(ivQuestionAnswer);
        } else {
            ivQuestionAnswer.setVisibility(View.GONE);
            Glide.with(mContext)
                    .clear(ivQuestionAnswer);
            ivQuestionAnswer.setImageBitmap(null);
        }
        //回答
        authView.setVisibility(View.VISIBLE);
        int praiseCount = 0;
        QaAuthor author = null;
        if (item.getAnswer() != null) {
            praiseCount = item.getAnswer()
                    .getUpCount();
            author = item.getAnswer()
                    .getUser();
        }

        if (author != null) {
            authView.setVisibility(View.VISIBLE);
            String url = author.getAvatar();
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().dontAnimate())
                    .into(ivAuth);
            tvAuthName.setText(author.getName());
            final QaAuthor finalAuthor = author;
            authView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (finalAuthor.getKind() == 0) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                                .withLong("id", finalAuthor.getId())
                                .navigation(mContext);
                    } else {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                                .withLong("id", finalAuthor.getMerchantId())
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
                    onItemClickListener.onItemClick(position, item);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
