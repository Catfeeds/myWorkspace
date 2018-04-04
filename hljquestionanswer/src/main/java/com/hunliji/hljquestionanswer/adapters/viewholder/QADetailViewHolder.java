package com.hunliji.hljquestionanswer.adapters.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.makeramen.rounded.RoundedImageView;

import org.joda.time.DateTime;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/9/27
 * 问答详情 列表
 */

public class QADetailViewHolder extends BaseViewHolder<Answer> {

    @BindView(R2.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R2.id.tv_user_name)
    TextView tvUserName;
    @BindView(R2.id.tv_tag)
    TextView tvTag;
    @BindView(R2.id.tv_reply_time)
    TextView tvReplyTime;
    @BindView(R2.id.ib_complain)
    ImageButton ibComplain;
    @BindView(R2.id.tv_prefix_content)
    TextView tvPrefixContent;
    @BindView(R2.id.tv_suffix_content)
    TextView tvSuffixContent;
    @BindView(R2.id.tv_see_all)
    TextView tvSeeAll;
    @BindView(R2.id.suffix_content_layout)
    LinearLayout suffixContentLayout;
    @BindView(R2.id.tv_content)
    TextView tvContent;
    @BindView(R2.id.tv_pick_up_all)
    TextView tvPickUpAll;
    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R2.id.iv_praise)
    ImageView ivPraise;
    @BindView(R2.id.tv_praised_count)
    TextView tvPraisedCount;
    @BindView(R2.id.ll_praise)
    CheckableLinearLayout llPraise;
    @BindView(R2.id.tv_complain)
    TextView tvComplain;
    @BindView(R2.id.iv_qa_tag)
    ImageView ivQATag;
    @BindView(R2.id.view_line)
    View viewLine;

    private User customerUser;
    private ContentLayoutChangeListener listener;
    private OnQADetailActionInterface qaDetailActionInterface;
    private final static int EXPAND_STATE_INIT = 0;
    private final static int EXPAND_STATE_NORMAL = 1;
    private final static int EXPAND_STATE_EXPANDED = 2;
    private final static int EXPAND_STATE_PICK_UP = 3;

    public final static int ANSWER_PRAISED = 1;//已点赞
    public final static int ANSWER_CANCEL = 0;//未点赞，未反对

    private static final int LINE_COUNT = 6;

    private int width;
    private int height;

    private Context context;

    public QADetailViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        context = itemView.getContext();
        width = CommonUtil.dp2px(itemView.getContext(), 40);
        height = CommonUtil.dp2px(itemView.getContext(), 40);
        customerUser = UserSession.getInstance()
                .getUser(context);
    }

    public void setComplainView() {
        ivQATag.setVisibility(View.VISIBLE);
        ibComplain.setVisibility(View.GONE);
        tvComplain.setVisibility(View.GONE);
        viewLine.setVisibility(View.GONE);
    }

    public void setQaDetailActionInterface(OnQADetailActionInterface qaDetailActionInterface) {
        this.qaDetailActionInterface = qaDetailActionInterface;
    }

    @Override
    protected void setViewData(
            final Context context, final Answer answer, int position, int viewType) {
        itemView.setVisibility(View.VISIBLE);
        QaAuthor user = answer.getUser();
        boolean isMerchant = user.getKind() == 1;
        Glide.with(context)
                .load(ImagePath.buildPath(user.getAvatar())
                        .width(width)
                        .height(height)
                        .cropPath())
                .apply(new RequestOptions().placeholder(R.mipmap.icon_avatar_primary))
                .into(ivAvatar);
        tvUserName.setText(user.getName());
        DateTime createdAt = answer.getCreatedAt();
        if (createdAt != null) {
            tvReplyTime.setText(createdAt.toString("yyyy-MM-dd"));
        }
        if (isMerchant) {
            tvTag.setText("商家");
            tvTag.setBackgroundResource(R.drawable.sp_r2_color_link);
        } else {
            tvTag.setText("已体验服务");
            if (HljQuestionAnswer.isMerchant(context)) {
                tvTag.setBackgroundResource(R.drawable.sp_r2_color_accent);
            } else {
                tvTag.setBackgroundResource(R.drawable.sp_r2_primary);
            }
        }
        setAnswerContent(answer);
        ibComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qaDetailActionInterface != null) {
                    qaDetailActionInterface.onMoreOption(answer, getAdapterPosition());
                }
            }
        });
        ibComplain.setVisibility(View.GONE);
        if (HljQuestionAnswer.isMerchant(context)) {
            llPraise.setVisibility(View.GONE);
            Integer status = answer.getAppealStatus();
            if (isMerchant) {
                ibComplain.setVisibility(View.VISIBLE);
                tvComplain.setVisibility(View.GONE);
            } else if (status != null && status == 0) {
                ibComplain.setVisibility(View.GONE);
                tvComplain.setVisibility(View.VISIBLE);
            } else {
                ibComplain.setVisibility(View.VISIBLE);
                tvComplain.setVisibility(View.GONE);
            }
        } else {
            if (customerUser != null && customerUser.getId() == answer.getUser()
                    .getId()) {
                ibComplain.setVisibility(View.VISIBLE);
            }
            //点赞
            setPraiseView(answer.getLikeType(), tvPraisedCount);
            tvPraisedCount.setText(answer.getUpCount() <= 0 ? "有用" : String.valueOf(answer
                    .getUpCount()));
            llPraise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customerUser != null && customerUser.getId() == answer.getUser()
                            .getId()) {
                        // 当用户为答主时，显示小黑块：你不能给自己点赞哦~
                        ToastUtil.showToast(context, null, R.string.msg_answer_praise_self___qa);
                        llPraise.setChecked(false);
                    } else {
                        if (qaDetailActionInterface != null) {
                            qaDetailActionInterface.onPraiseClickListener(llPraise,
                                    tvPraisedCount,
                                    answer);
                        }
                    }
                }
            });
        }
    }

    private void setPraiseView(int likeType, TextView tvPraise) {
        switch (likeType) {
            //未点赞 未反对
            case ANSWER_CANCEL:
                llPraise.setChecked(false);
                tvPraise.setTextColor(ContextCompat.getColor(context, R.color.colorBlack3));
                break;
            //已点赞
            case ANSWER_PRAISED:
                llPraise.setChecked(true);
                tvPraise.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                break;
        }
    }


    private void setAnswerContent(Answer answer) {
        if (TextUtils.isEmpty(answer.getContent())) {
            contentLayout.setVisibility(View.GONE);
        } else {
            isExpanded(answer);
            contentLayout.setVisibility(View.VISIBLE);
            tvContent.setText(answer.getContent());
            tvPrefixContent.setVisibility(View.VISIBLE);
            tvPrefixContent.setText(answer.getContent());
            if (listener != null) {
                tvPrefixContent.removeOnLayoutChangeListener(listener);
            }
            listener = new ContentLayoutChangeListener(answer);
            tvPrefixContent.addOnLayoutChangeListener(listener);
            itemView.setOnClickListener(listener);
        }
    }

    private class ContentLayoutChangeListener implements View.OnLayoutChangeListener, View
            .OnClickListener {

        private Answer answer;

        ContentLayoutChangeListener(final Answer answer) {
            this.answer = answer;
            final Layout l = tvPrefixContent.getLayout();
            if (l != null) {
                final int lineCount = l.getLineCount();
                if (lineCount <= LINE_COUNT) {
                    answer.setExpandedState(EXPAND_STATE_NORMAL);
                } else if (answer.getExpandedState() == EXPAND_STATE_INIT || answer
                        .getExpandedState() == EXPAND_STATE_PICK_UP) {
                    answer.setExpandedState(EXPAND_STATE_PICK_UP);
                    tvSuffixContent.setText(answer.getContent()
                            .substring(l.getLineStart(LINE_COUNT - 1)));
                }
                isExpanded(answer);
            }
        }

        @Override
        public void onLayoutChange(
                final View v,
                int left,
                int top,
                int right,
                int bottom,
                int oldLeft,
                int oldTop,
                int oldRight,
                int oldBottom) {
            tvPrefixContent.removeOnLayoutChangeListener(this);
            final Layout l = tvPrefixContent.getLayout();
            if (l != null) {
                final int lineCount = l.getLineCount();
                tvPrefixContent.post(new Runnable() {
                    @Override
                    public void run() {
                        if (lineCount <= LINE_COUNT) {
                            answer.setExpandedState(EXPAND_STATE_NORMAL);
                        } else if (answer.getExpandedState() == EXPAND_STATE_INIT || answer
                                .getExpandedState() == EXPAND_STATE_PICK_UP) {
                            answer.setExpandedState(EXPAND_STATE_PICK_UP);
                            tvSuffixContent.setText(answer.getContent()
                                    .substring(l.getLineStart(LINE_COUNT - 1)));
                        }
                        isExpanded(answer);
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            if (answer.getExpandedState() == EXPAND_STATE_PICK_UP) {
                answer.setExpandedState(EXPAND_STATE_EXPANDED);
            } else if (answer.getExpandedState() == EXPAND_STATE_EXPANDED) {
                answer.setExpandedState(EXPAND_STATE_PICK_UP);
            }
            isExpanded(answer);
        }
    }

    private void isExpanded(Answer answer) {
        switch (answer.getExpandedState()) {
            case EXPAND_STATE_NORMAL:
                tvPrefixContent.setVisibility(View.GONE);
                suffixContentLayout.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvPickUpAll.setVisibility(View.GONE);
                break;
            case EXPAND_STATE_EXPANDED:
                tvPrefixContent.setVisibility(View.GONE);
                suffixContentLayout.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvPickUpAll.setVisibility(View.VISIBLE);
                break;
            case EXPAND_STATE_PICK_UP:
                tvPrefixContent.setVisibility(View.VISIBLE);
                suffixContentLayout.setVisibility(View.VISIBLE);
                tvContent.setVisibility(View.GONE);
                tvPickUpAll.setVisibility(View.GONE);
                break;
            default:
                tvPrefixContent.setVisibility(View.VISIBLE);
                suffixContentLayout.setVisibility(View.GONE);
                tvContent.setVisibility(View.GONE);
                tvPickUpAll.setVisibility(View.GONE);
                break;
        }
    }
}
