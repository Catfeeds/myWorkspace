package com.hunliji.hljquestionanswer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.suncloud.hljweblibrary.views.widgets.CustomWebView;
import com.hunliji.hljcommonlibrary.adapters.BaseViewHolder;
import com.hunliji.hljcommonlibrary.adapters.ExtraBaseViewHolder;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HorizontalListView;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.activities.AnswerDetailActivity;
import com.makeramen.rounded.RoundedImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mo_yu on 2016/8/18.问题详情
 */
public class QuestionDetailAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private ArrayList<Answer> answers;
    private Question question;
    private User user;
    private long questionId;

    private final static int HEADER_TYPE = 0;
    private final static int ITEM_TYPE = 1;
    private final static int FOOTER_TYPE = 2;
    public final static int ANSWER_PRAISED = 1;//已点赞
    public final static int ANSWER_OPPOSED = -1;//已反对
    public final static int ANSWER_CANCEL = 0;//未点赞，未反对
    private View footerView;

    private int maxImageWidth;
    private int imageHeight;
    private int bottomMargin;
    private int avatarSize;
    private int faceSize;
    private int totalCount;
    private int topCount;

    private OnPraiseClickListener onPraiseClickListener;

    public QuestionDetailAdapter(Context context, long questionId) {
        this.context = context;
        this.questionId = questionId;
        user = UserSession.getInstance()
                .getUser(context);
        answers = new ArrayList<>();
        maxImageWidth = CommonUtil.getDeviceSize(context).x - CommonUtil.dp2px(context, 66);
        imageHeight = CommonUtil.dp2px(context, 180);
        bottomMargin = CommonUtil.dp2px(context, 14);
        avatarSize = CommonUtil.dp2px(context, 30);
        faceSize = CommonUtil.dp2px(context, 20);
    }


    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    public void setData(ArrayList<Answer> answerDetails) {
        if (answerDetails != null) {
            this.answers.clear();
            this.answers.addAll(answerDetails);
        }
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setTopCount(int topCount) {
        this.topCount = topCount;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_TYPE:
                return new QuestionDetailHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.question_detail_header___qa, parent, false));
            case FOOTER_TYPE:
                return new ExtraBaseViewHolder(footerView);
            default:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.question_detail_answer_list_item___qa, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_TYPE;
        } else if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return ITEM_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            holder.setView(context,
                    answers.get(question == null ? position : position - 1),
                    question == null ? position : position - 1,
                    getItemViewType(position));
        } else if (holder instanceof QuestionDetailHolder && question != null) {
            holder.setView(context, question, position, getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return answers.size() + (question == null ? 0 : 1) + (footerView == null ? 0 : 1);
    }

    class QuestionDetailHolder extends BaseViewHolder<Question> {
        @BindView(R2.id.mark_list)
        HorizontalListView markList;
        @BindView(R2.id.tv_question_detail_title)
        TextView tvQuestionDetailTitle;
        @BindView(R2.id.iv_question_auth_avatar)
        RoundedImageView ivQuestionAuthAvatar;
        @BindView(R2.id.tv_question_auth_name)
        TextView tvQuestionAuthName;
        @BindView(R2.id.tv_question_time)
        TextView tvQuestionTime;
        @BindView(R2.id.tv_question_watch_count)
        TextView tvQuestionWatchCount;
        @BindView(R2.id.web_view)
        CustomWebView webView;
        @BindView(R2.id.web_view_layout)
        LinearLayout webViewLayout;
        @BindView(R2.id.question_auth_view)
        LinearLayout questionAuthView;
        @BindView(R2.id.content_hint_view)
        LinearLayout contentHintView;
        @BindView(R2.id.answer_list_empty_layout)
        LinearLayout answerListEmptyLayout;
        @BindView(R2.id.tv_hint)
        TextView tvHint;
        @BindView(R2.id.tv_answer_count)
        TextView tvAnswerCount;
        @BindView(R2.id.question_detail_view)
        View questionDetailView;
        @BindView(R2.id.tv_question_summary)
        TextView tvQuestionSummary;
        @BindView(R2.id.answer_list_line)
        View answerListLine;
        @BindView(R2.id.rl_pack_up)
        RelativeLayout rlPackUp;
        @BindView(R2.id.tv_expansion)
        TextView tvExpansion;

        MarkAdapter markAdapter;

        QuestionDetailHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        protected void setViewData(
                final Context mContext, final Question item, int position, int viewType) {
            if (item != null) {
                questionDetailView.setVisibility(View.VISIBLE);
                tvQuestionDetailTitle.setText(EmojiUtil.parseEmojiByText2(mContext,
                        item.getTitle(),
                        faceSize));

                if (item.getUser() != null) {
                    tvQuestionAuthName.setText(mContext.getString(R.string
                                    .label_question_auth_name___qa,
                            item.getUser()
                                    .getName(),
                            HljTimeUtils.getShowTime(mContext, item.getCreatedAt()),
                            item.getWatchCount()));
                    String url = null;
                    if (!TextUtils.isEmpty(item.getUser()
                            .getAvatar())) {
                        url = ImageUtil.getAvatar(item.getUser()
                                .getAvatar(), avatarSize);
                    }
                    if (!TextUtils.isEmpty(url)) {
                        Glide.with(mContext)
                                .load(url)
                                .apply(new RequestOptions().dontAnimate()
                                        .placeholder(R.mipmap.icon_avatar_primary))
                                .into(ivQuestionAuthAvatar);
                    } else {
                        Glide.with(mContext)
                                .clear(ivQuestionAuthAvatar);
                        ivQuestionAuthAvatar.setImageBitmap(null);
                    }
                }
                if (item.getMarks() != null && item.getMarks()
                        .size() > 0) {
                    markList.setVisibility(View.VISIBLE);
                    markAdapter = new MarkAdapter(mContext, item.getMarks());
                    markList.setAdapter(markAdapter);
                } else {
                    markList.setVisibility(View.GONE);
                }


                if (!TextUtils.isEmpty(item.getContent())) {
                    webView.setVisibility(View.VISIBLE);
                    contentHintView.setVisibility(View.GONE);
                    String a = getContent(mContext);
                    String b = a.replace("回答内容", item.getContent());
                    webView.loadDataWithBaseURL(null, b, "text/html", "UTF-8", null);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                            tvQuestionDetailTitle.getLayoutParams();
                    params.bottomMargin = 0;
                } else {
                    webView.setVisibility(View.GONE);
                    contentHintView.setVisibility(View.GONE);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)
                            tvQuestionDetailTitle.getLayoutParams();
                    params.bottomMargin = bottomMargin;
                }
                if (answers == null || answers.size() == 0) {
                    answerListEmptyLayout.setVisibility(View.VISIBLE);
                    if (item.isQuestioner()) {
                        tvHint.setText(mContext.getString(R.string
                                .label_answer_list_empty_my___qa));
                    } else {
                        tvHint.setText(mContext.getString(R.string.label_answer_list_empty___qa));
                    }
                } else {
                    answerListEmptyLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    public static String getContent(Context context) {
        InputStreamReader inputReader = null;
        try {
            inputReader = new InputStreamReader(context.getResources()
                    .getAssets()
                    .open("question_content.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        BufferedReader bufReader = new BufferedReader(inputReader);
        StringBuffer stringBuffer = new StringBuffer("");
        String line;
        try {
            while ((line = bufReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    class ItemViewHolder extends BaseViewHolder<Answer> {

        @BindView(R2.id.iv_answer_header)
        ImageView ivAnswerHeader;
        @BindView(R2.id.tv_answer_header_count)
        TextView tvAnswerHeaderCount;
        @BindView(R2.id.answer_layout_header)
        LinearLayout answerLayoutHeader;
        @BindView(R2.id.img_hot_answer)
        ImageView imgHotAnswer;
        @BindView(R2.id.user_avatar)
        RoundedImageView userAvatar;
        @BindView(R2.id.iv_tag)
        ImageView ivTag;
        @BindView(R2.id.user_view)
        RelativeLayout userView;
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.btn_praise)
        ImageView btnPraise;
        @BindView(R2.id.up_count)
        TextView upCount;
        @BindView(R2.id.cl_praise_view)
        CheckableLinearLayout clPraiseView;
        @BindView(R2.id.btn_oppose)
        ImageView btnOppose;
        @BindView(R2.id.cl_oppose_view)
        CheckableLinearLayout clOpposeView;
        @BindView(R2.id.praise_layout)
        LinearLayout praiseLayout;
        @BindView(R2.id.content)
        TextView content;
        @BindView(R2.id.content_view)
        RelativeLayout contentView;
        @BindView(R2.id.img_answer_cover)
        ImageView imgAnswerCover;
        @BindView(R2.id.all_answer_layout)
        LinearLayout allAnswerLayout;
        @BindView(R2.id.answer_layout)
        RelativeLayout answerLayout;
        @BindView(R2.id.answer_bottom_line)
        View answerBottomLine;
        @BindView(R2.id.iv_hot_tag)
        ImageView ivHotTag;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) imgAnswerCover
                    .getLayoutParams();
            params.height = imageHeight;
        }

        @Override
        protected void setViewData(
                final Context mContext, final Answer item, final int position, int viewType) {
            if (position == 0) {
                answerLayoutHeader.setVisibility(View.VISIBLE);
                ivAnswerHeader.setImageResource(R.mipmap.icon_comment_round_gray_primary_32_32);
                tvAnswerHeaderCount.setText(mContext.getString(R.string
                                .label_answer_all_count_tip___qa,
                        totalCount));
            } else {
                answerLayoutHeader.setVisibility(View.GONE);
            }

            ivHotTag.setVisibility(item.isTop() ? View.VISIBLE : View.GONE);
            if (item.getUser() != null) {
                if (item.getUser()
                        .getKind() == 1) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(R.mipmap.icon_vip_blue_28_28);
                } else if (!TextUtils.isEmpty(item.getUser()
                        .getSpecialty()) && !"普通用户".equals(item.getUser()
                        .getSpecialty())) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(R.mipmap.icon_vip_yellow_28_28);
                } else if (item.getUser()
                        .getMember() != null && item.getUser()
                        .getMember()
                        .getId() > 0) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(R.mipmap.icon_member_28_28);
                } else {
                    ivTag.setVisibility(View.GONE);
                }
                name.setText(item.getUser()
                        .getName());
                String url = null;
                if (!TextUtils.isEmpty(item.getUser()
                        .getAvatar())) {
                    url = ImageUtil.getAvatar(item.getUser()
                            .getAvatar(), avatarSize);
                }
                if (!TextUtils.isEmpty(url)) {
                    Glide.with(mContext)
                            .load(url)
                            .apply(new RequestOptions().dontAnimate()
                                    .placeholder(R.mipmap.icon_avatar_primary))
                            .into(userAvatar);
                } else {
                    Glide.with(mContext)
                            .clear(userAvatar);
                    userAvatar.setImageBitmap(null);
                }
                userAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /**
                         * 商家端点击用户头像无跳转
                         */
                        if (!HljQuestionAnswer.isMerchant(mContext)) {
                            if (item.getUser()
                                    .getKind() == 0) {
                                ARouter.getInstance()
                                        .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                                        .withLong("id",
                                                item.getUser()
                                                        .getId())
                                        .navigation(context);
                            } else {
                                ARouter.getInstance()
                                        .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                                        .withLong("id",
                                                item.getUser()
                                                        .getMerchantId())
                                        .navigation(context);
                            }
                        } else {
                            Intent intent = new Intent();
                            intent.setClass(mContext, AnswerDetailActivity.class);
                            intent.putExtra("answerId", item.getId());
                            intent.putExtra("questionId", questionId);
                            intent.putExtra("position", position);
                            intent.putExtra("answers", answers);
                            mContext.startActivity(intent);
                        }
                    }
                });
            }
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!HljQuestionAnswer.isMerchant(mContext)) {
                        if (item.getUser()
                                .getKind() == 0) {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                                    .withLong("id",
                                            item.getUser()
                                                    .getId())
                                    .navigation(context);
                        } else {
                            ARouter.getInstance()
                                    .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                                    .withLong("id",
                                            item.getUser()
                                                    .getMerchantId())
                                    .navigation(context);
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(mContext, AnswerDetailActivity.class);
                        intent.putExtra("answerId", item.getId());
                        intent.putExtra("questionId", questionId);
                        intent.putExtra("position", position);
                        intent.putExtra("answers", answers);
                        mContext.startActivity(intent);
                    }
                }
            });

            //点赞，反对
            setPraiseView(item.getLikeType());
            upCount.setText(item.getUpCount() <= 0 ? "赞同" : String.valueOf(item.getUpCount()));
            clPraiseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user != null && user.getId() == item.getUser()
                            .getId()) {
                        // 当用户为答主时，显示小黑块：你不能给自己点赞哦~
                        ToastUtil.showToast(mContext, null, R.string.msg_answer_praise_self___qa);
                        clPraiseView.setChecked(false);
                    } else {
                        if (onPraiseClickListener != null) {
                            onPraiseClickListener.onPraiseClickListener(true,
                                    clPraiseView,
                                    clOpposeView,
                                    upCount,
                                    item);
                        }
                    }
                }
            });
            clOpposeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user != null && user.getId() == item.getUser()
                            .getId()) {
                        // 当用户为答主时，显示小黑块：你不能反对自己哦~
                        ToastUtil.showToast(mContext, null, R.string.msg_answer_oppose_self___qa);
                        clOpposeView.setChecked(false);
                    } else {
                        if (onPraiseClickListener != null) {
                            onPraiseClickListener.onPraiseClickListener(false,
                                    clPraiseView,
                                    clOpposeView,
                                    upCount,
                                    item);
                        }
                    }
                }
            });
            String a = item.getSummary();
            String summary = a.replaceAll("\\[图片\\]", "");
            content.setText(EmojiUtil.parseEmojiByText2(context, summary, faceSize));
            content.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(
                        View view,
                        int left,
                        int top,
                        int right,
                        int bottom,
                        int oldLeft,
                        int oldTop,
                        int oldRight,
                        int oldBottom) {
                    final Layout l = content.getLayout();
                    if (l != null) {
                        content.post(new Runnable() {
                            @Override
                            public void run() {
                                allAnswerLayout.setVisibility(isEllipsis(l) ? View.VISIBLE : View
                                        .GONE);
                            }
                        });
                        allAnswerLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setClass(mContext, AnswerDetailActivity.class);
                                intent.putExtra("answerId", item.getId());
                                intent.putExtra("questionId", questionId);
                                intent.putExtra("position", position);
                                intent.putExtra("answers", answers);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                }
            });

            //问答配图
            String url = null;
            if (!TextUtils.isEmpty(item.getCoverPath())) {
                url = ImagePath.buildPath(item.getCoverPath())
                        .height(imageHeight)
                        .width(maxImageWidth)
                        .path();
            }
            if (!TextUtils.isEmpty(url)) {
                imgAnswerCover.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(url)
                        .apply(new RequestOptions().override(maxImageWidth, imageHeight)
                                .placeholder(R.mipmap.icon_empty_image))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(
                                    @Nullable GlideException e,
                                    Object model,
                                    Target<Drawable> target,
                                    boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(
                                    Drawable resource,
                                    Object model,
                                    Target<Drawable> target,
                                    DataSource dataSource,
                                    boolean isFirstResource) {
                                int width = Math.round(resource.getIntrinsicWidth() * imageHeight
                                        / resource.getIntrinsicHeight());
                                if (width > maxImageWidth) {
                                    width = maxImageWidth;
                                }
                                imgAnswerCover.getLayoutParams().width = width;
                                return false;
                            }
                        })
                        .into(imgAnswerCover);
            } else {
                imgAnswerCover.setVisibility(View.GONE);
                Glide.with(mContext)
                        .clear(imgAnswerCover);
                imgAnswerCover.setImageBitmap(null);
            }

            //当为最后一条时，隐藏线
            if (position == answers.size() - 1) {
                answerBottomLine.setVisibility(View.INVISIBLE);
            } else {
                if (topCount != 0 && position == topCount - 1) {
                    answerBottomLine.setVisibility(View.INVISIBLE);
                } else {
                    answerBottomLine.setVisibility(View.VISIBLE);
                }
            }

            answerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, AnswerDetailActivity.class);
                    intent.putExtra("answerId", item.getId());
                    intent.putExtra("questionId", questionId);
                    intent.putExtra("position", position);
                    intent.putExtra("answers", answers);
                    view.getContext()
                            .startActivity(intent);
                    if (view.getContext() instanceof Activity) {
                        ((Activity) view.getContext()).overridePendingTransition(R.anim
                                        .slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
        }

        private void setPraiseView(int likeType) {
            switch (likeType) {
                //未点赞 未反对
                case ANSWER_CANCEL:
                    clPraiseView.setChecked(false);
                    clOpposeView.setChecked(false);
                    break;
                //已点赞
                case ANSWER_PRAISED:
                    clPraiseView.setChecked(true);
                    clOpposeView.setChecked(false);
                    break;
                //已反对
                case ANSWER_OPPOSED:
                    clPraiseView.setChecked(false);
                    clOpposeView.setChecked(true);
                    break;
            }
        }

        //是否超过六行
        private boolean isEllipsis(Layout l) {
            int lines = l.getLineCount();
            if (lines > 6) {
                return true;
            } else if (lines > 1) {
                for (int i = lines; i > 0; i--) {
                    if (l.getEllipsisCount(i - 1) > 0) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public void setOnPraiseClickListener(
            OnPraiseClickListener onPraiseClickListener) {
        this.onPraiseClickListener = onPraiseClickListener;
    }

    public interface OnPraiseClickListener {
        void onPraiseClickListener(
                boolean isPraise,
                CheckableLinearLayout btnPraise,
                CheckableLinearLayout btnOppose,
                TextView upCount,
                Answer answer);
    }
}
