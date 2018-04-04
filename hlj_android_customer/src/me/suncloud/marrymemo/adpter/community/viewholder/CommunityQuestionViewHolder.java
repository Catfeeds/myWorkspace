package me.suncloud.marrymemo.adpter.community.viewholder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonviewlibrary.adapters.viewholders.tracker.TrackerQuestionViewHolder;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.R;

/**
 * Created by mo_yu on 2018/3/13.推荐问题列表
 */

public class CommunityQuestionViewHolder extends TrackerQuestionViewHolder {
    @BindView(R.id.tv_question_title)
    TextView tvQuestionTitle;
    @BindView(R.id.riv_auth_avatar)
    RoundedImageView rivAuthAvatar;
    @BindView(R.id.tv_auth_name)
    TextView tvAuthName;
    @BindView(R.id.tv_answer_content)
    TextView tvAnswerContent;
    @BindView(R.id.img_single_img)
    ImageView imgSingleImg;
    @BindView(R.id.tv_answer_count)
    TextView tvAnswerCount;
    @BindView(R.id.auth_view)
    View authView;
    @BindView(R.id.tv_answer_up_count)
    TextView tvAnswerUpCount;
    @BindView(R.id.tv_watch_count)
    TextView tvWatchCount;
    private int faceSize;
    private int avatarSize;
    private int imgWidth;
    private int imgHeight;

    public CommunityQuestionViewHolder(ViewGroup parent) {
        this(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.community_question_list_item___cv, parent, false));
    }

    public CommunityQuestionViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        faceSize = CommonUtil.dp2px(view.getContext(), 18);
        avatarSize = CommonUtil.dp2px(view.getContext(), 20);
        imgHeight = CommonUtil.dp2px(view.getContext(), 120);
        imgWidth = CommonUtil.dp2px(view.getContext(), 160);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Question question = getItem();
                Intent intent = new Intent(v.getContext(), QuestionDetailActivity.class);
                intent.putExtra(QuestionDetailActivity.ARG_QUESTION_ID, question.getId());
                v.getContext()
                        .startActivity(intent);
            }
        });
    }

    @Override
    public View trackerView() {
        return itemView;
    }

    @Override
    protected void setViewData(
            final Context mContext, final Question question, final int position, int viewType) {
        String imageUrl = null;
        if (question != null) {
            tvQuestionTitle.setVisibility(View.VISIBLE);
            tvQuestionTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                    question.getTitle(),
                    faceSize));
            tvAnswerCount.setText(String.valueOf(question.getAnswerCount()));
            tvWatchCount.setText(String.valueOf(question.getWatchCount()));
            Answer answer = question.getAnswer();
            if (answer != null) {
                if (!TextUtils.isEmpty(answer.getSummary())) {
                    tvAnswerContent.setVisibility(View.VISIBLE);
                    tvAnswerContent.setText(EmojiUtil.parseEmojiByText2(mContext,
                            answer.getSummary(),
                            faceSize));
                } else {
                    tvAnswerContent.setVisibility(View.GONE);
                }
                tvAnswerUpCount.setText(String.valueOf(answer.getUpCount()));
                //回答者头像
                if (answer.getUser() != null) {
                    authView.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(ImagePath.buildPath(answer.getUser()
                                    .getAvatar())
                                    .width(avatarSize)
                                    .height(avatarSize)
                                    .cropPath())
                            .apply(new RequestOptions().dontAnimate())
                            .into(rivAuthAvatar);
                    tvAuthName.setText(answer.getUser()
                            .getName());
                } else {
                    authView.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(question.getCoverPath())) {
                    imageUrl = question.getCoverPath();
                } else if (!TextUtils.isEmpty(answer.getCoverPath())) {
                    imageUrl = answer.getCoverPath();
                }
            } else {
                authView.setVisibility(View.GONE);
                tvAnswerContent.setVisibility(View.GONE);
            }
        }
        if (!CommonUtil.isEmpty(imageUrl)) {
            imgSingleImg.setVisibility(View.VISIBLE);
            // 1.最多2行标题；
            // 2.一行标题，需要显示最多2行的答案；
            // 3.两行标题，需要显示最多1行的答案
            //            hasImageTitleWidth = tvQuestionTitle.getMeasuredWidth() - tvQuestionTitle
            //                    .getPaddingLeft() - tvQuestionTitle.getPaddingRight();
            //            StaticLayout layout = new StaticLayout(tvQuestionTitle.getText(),
            //                    tvQuestionTitle.getPaint(),
            //                    hasImageTitleWidth,
            //                    Layout.Alignment.ALIGN_NORMAL,
            //                    0,
            //                    0,
            //                    true);
            //            if (layout.getLineCount() > 1) {
            //                tvAnswerContent.setMaxLines(1);
            //            } else {
            //                tvAnswerContent.setMaxLines(1);
            //            }
            tvQuestionTitle.setMaxLines(4);
            tvQuestionTitle.getViewTreeObserver()
                    .addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            tvQuestionTitle.getViewTreeObserver()
                                    .removeOnPreDrawListener(this);
                            int titleLineCount = tvQuestionTitle.getLineCount();
                            tvAnswerContent.setMaxLines(5 - titleLineCount);
                            tvAnswerContent.invalidate();
                            return false;
                        }
                    });
            Glide.with(mContext)
                    .load(ImagePath.buildPath(imageUrl)
                            .width(imgWidth)
                            .height(imgHeight)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgSingleImg);
        } else {
            //无图标题2行，内容3行
            tvQuestionTitle.setMaxLines(2);
            tvAnswerContent.setMaxLines(3);
            imgSingleImg.setVisibility(View.GONE);
        }
    }
}
