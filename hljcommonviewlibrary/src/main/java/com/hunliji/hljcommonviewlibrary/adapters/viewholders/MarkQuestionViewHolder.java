package com.hunliji.hljcommonviewlibrary.adapters.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;
import com.hunliji.hljimagelibrary.utils.ImageUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2017/1/3.类标签问题cell样式
 */

public class MarkQuestionViewHolder extends BaseViewHolder<Question> {
    @BindView(R2.id.line)
    View line;
    @BindView(R2.id.tv_watch_count)
    TextView tvWatchCount;
    @BindView(R2.id.tv_title)
    TextView tvTitle;
    @BindView(R2.id.answerers_layout)
    LinearLayout answerersLayout;
    @BindView(R2.id.tv_answerers)
    TextView tvAnswerers;
    @BindView(R2.id.question_view)
    View questionView;

    private int avatarSize;
    private OnItemClickListener onItemClickListener;

    public MarkQuestionViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        avatarSize = CommonUtil.dp2px(itemView.getContext(), 24);
    }

    @Override
    protected void setViewData(
            Context mContext, final Question question, final int position, int viewType) {
        line.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        tvWatchCount.setText(NumberFormatUtil.formatThanThousand(question.getWatchCount()));
        tvTitle.setText(question.getTitle());
        if (question.getAnswerUsers() != null && question.getAnswerUsers()
                .getUserCount() > 0) {
            if (question.getAnswerUsers()
                    .getUserCount() > 4) {
                tvAnswerers.setText(CommonUtil.fromHtml(tvAnswerers.getContext(),
                        mContext.getString(R.string.html_fmt_answerer_count___cv),
                        question.getAnswerUsers()
                                .getUserCount()));
            } else {
                tvAnswerers.setText(R.string.label_user_answer_count___cv);
            }
            List<QaAuthor> users = question.getAnswerUsers()
                    .getUsers();
            if (users != null && !users.isEmpty()) {
                answerersLayout.setVisibility(View.VISIBLE);
                int userSize = Math.min(users.size(), 4);
                int count = answerersLayout.getChildCount();
                if (count > userSize) {
                    answerersLayout.removeViews(userSize, count - userSize);
                }
                for (int i = 0; i < userSize; i++) {
                    if (i >= count) {
                        View.inflate(mContext,
                                R.layout.mark_question_answer_item___cv,
                                answerersLayout);
                    }
                    QaAuthor user = users.get(i);
                    ImageView avatarView = (ImageView) answerersLayout.getChildAt(i);
                    String avatarPath = ImageUtil.getAvatar(user.getAvatar(), avatarSize);
                    if (!TextUtils.isEmpty(avatarPath)) {
                        Glide.with(mContext)
                                .load(avatarPath)
                                .apply(new RequestOptions().dontAnimate()
                                        .placeholder(R.mipmap.icon_avatar_primary))
                                .into(avatarView);
                    } else {
                        Glide.with(mContext)
                                .clear(avatarView);
                        avatarView.setImageResource(R.mipmap.icon_avatar_primary);
                    }
                }
            } else {
                answerersLayout.setVisibility(View.GONE);
            }
        } else {
            answerersLayout.setVisibility(View.GONE);
            tvAnswerers.setText(R.string.label_no_answer___cv);
        }
        questionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position, question);
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
