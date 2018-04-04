package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/1/3.类标签回答cell样式
 */

public class MarkAnswerViewHolder extends BaseViewHolder<Answer> {
    @BindView(R2.id.top_divider)
    View topDivider;
    @BindView(R2.id.tv_question_title)
    TextView tvQuestionTitle;
    @BindView(R2.id.user_avatar)
    RoundedImageView userAvatar;
    @BindView(R2.id.up_count)
    TextView upCount;
    @BindView(R2.id.user_layout)
    RelativeLayout userLayout;
    @BindView(R2.id.content)
    TextView content;
    @BindView(R2.id.answer_view)
    View answerView;

    private int avatarSize;
    private int faceSize;
    private boolean dividerAtTop;
    private OnItemClickListener<Question> onItemClickListener;
    private OnQuestionClickListener onQuestionClickListener;
    private OnUserClickListener onUserClickListener;

    public MarkAnswerViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mark_answer_item___cv, parent, false));
    }

    public MarkAnswerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        avatarSize = CommonUtil.dp2px(itemView.getContext(), 24);
        faceSize = CommonUtil.dp2px(itemView.getContext(), 20);
    }

    @Override
    protected void setViewData(
            Context mContext, final Answer answer, final int position, int viewType) {
        if (answer.getUser() != null) {
            final QaAuthor author = answer.getUser();
            String avatarPath = ImageUtil.getAvatar(author.getAvatar(), avatarSize);
            if (!TextUtils.isEmpty(avatarPath)) {
                Glide.with(mContext)
                        .load(avatarPath)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(userAvatar);
            } else {
                Glide.with(mContext)
                        .clear(userAvatar);
                userAvatar.setImageResource(R.mipmap.icon_avatar_primary);
            }
            userLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onUserClickListener != null) {
                        onUserClickListener.onUserClick(position, author);
                    }
                }
            });
        }
        upCount.setText(NumberFormatUtil.formatThanThousand(answer.getUpCount()));
        content.setText(EmojiUtil.parseEmojiByText2(mContext, answer.getSummary(), faceSize));
        if (answer.getQuestion() != null) {
            final Question question = answer.getQuestion();
            tvQuestionTitle.setText(question.getTitle());
            answerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(position, answer.getQuestion());
                    }
                }
            });
        }
    }

    public void setDividerAtTop(boolean dividerAtTop) {
        this.dividerAtTop = dividerAtTop;
    }

    public void setOnItemClickListener(OnItemClickListener<Question> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnQuestionClickListener(OnQuestionClickListener onQuestionClickListener) {
        this.onQuestionClickListener = onQuestionClickListener;
    }

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public interface OnQuestionClickListener {
        void onQuestionClick(int position, Question question);
    }

    public interface OnUserClickListener {
        void onUserClick(int position, Object object);
    }

}
