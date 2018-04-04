package com.hunliji.hljquestionanswer.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.makeramen.rounded.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2016/12/23.
 */

public class AnswerAuthHeadView extends FrameLayout {
    @BindView(R2.id.riv_answer_auth_avatar)
    RoundedImageView rivAnswerAuthAvatar;
    @BindView(R2.id.iv_tag)
    ImageView ivTag;
    @BindView(R2.id.tv_answer_auth_name)
    TextView tvAnswerAuthName;
    @BindView(R2.id.tv_answer_auth_description)
    TextView tvAnswerAuthDescription;
    @BindView(R2.id.tv_answer_wedding_date)
    TextView tvAnswerWeddingDate;
    @BindView(R2.id.auth_view)
    LinearLayout authView;
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
    @BindView(R2.id.img_hot_answer)
    ImageView imgHotAnswer;
    @BindView(R2.id.auth_bottom_line)
    View authBottomLine;

    private Context context;
    private Answer answer;
    private User user;
    private OnPraiseCallback onPraiseCallback;

    private final static int ANSWER_PRAISED = 1;//已点赞
    private final static int ANSWER_OPPOSED = -1;//已反对
    private final static int ANSWER_CANCEL = 0;//未点赞，未反对

    public AnswerAuthHeadView(Context context) {
        this(context, null);
    }

    public AnswerAuthHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnswerAuthHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.answer_detail_header___qa, this);
        ButterKnife.bind(this, view);
        this.context = context;
        user = UserSession.getInstance()
                .getUser(context);
    }

    public void showBottomLine(boolean isShow) {
        authBottomLine.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setAnswerAuthView(final Answer item) {
        this.answer = item;
        if (answer.getUser() != null) {
            setPraiseView(answer);
            String url = null;
            if (!TextUtils.isEmpty(answer.getUser()
                    .getAvatar())) {
                url = ImageUtil.getAvatar(answer.getUser()
                        .getAvatar(), CommonUtil.dp2px(context, 36));
            }
            if (!TextUtils.isEmpty(url)) {
                Glide.with(context)
                        .load(url)
                        .apply(new RequestOptions().dontAnimate()
                                .placeholder(R.mipmap.icon_avatar_primary))
                        .into(rivAnswerAuthAvatar);
            } else {
                Glide.with(context)
                        .clear(rivAnswerAuthAvatar);
            }
            tvAnswerAuthName.setText(answer.getUser()
                    .getName());
            if (answer.getUser()
                    .getKind() == 1) {
                ivTag.setVisibility(View.VISIBLE);
                ivTag.setImageResource(R.mipmap.icon_vip_blue_28_28);
                tvAnswerAuthDescription.setVisibility(View.VISIBLE);
                if (answer.getUser()
                        .getMerchant() != null) {
                    tvAnswerAuthDescription.setText(answer.getUser()
                            .getMerchant()
                            .getPropertyName());
                }

            } else if (!TextUtils.isEmpty(answer.getUser()
                    .getSpecialty()) && !"普通用户".equals(answer.getUser()
                    .getSpecialty())) {
                ivTag.setVisibility(View.VISIBLE);
                ivTag.setImageResource(R.mipmap.icon_vip_yellow_28_28);
                tvAnswerAuthDescription.setVisibility(View.VISIBLE);
                tvAnswerAuthDescription.setText(answer.getUser()
                        .getSpecialty());
                tvAnswerWeddingDate.setVisibility(View.VISIBLE);
                tvAnswerWeddingDate.setText(HljTimeUtils.getWeddingDate(answer.getUser()
                                .getWeddingDay(),
                        answer.getUser()
                                .getIsPending(),
                        answer.getUser()
                                .isGender()));
            } else {
                if (item.getUser()
                        .getMember() != null && item.getUser()
                        .getMember()
                        .getId() > 0) {
                    ivTag.setVisibility(View.VISIBLE);
                    ivTag.setImageResource(R.mipmap.icon_member_28_28);
                } else {
                    ivTag.setVisibility(View.GONE);
                }
                tvAnswerWeddingDate.setVisibility(View.VISIBLE);
                tvAnswerWeddingDate.setText(HljTimeUtils.getWeddingDate(answer.getUser()
                                .getWeddingDay(),
                        answer.getUser()
                                .getIsPending(),
                        answer.getUser()
                                .isGender()));
            }
            authView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (HljQuestionAnswer.isMerchant(context)) {
                        return;
                    }
                    if (answer.getUser()
                            .getKind() == 0) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.USER_PROFILE)
                                .withLong("id",
                                        answer.getUser()
                                                .getId())
                                .navigation(context);
                    } else {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Customer.MERCHANT_HOME)
                                .withLong("id",
                                        answer.getUser()
                                                .getMerchantId())
                                .navigation(context);
                    }
                }
            });
        }
        //优质回答,本版本不显示
        if (answer.isTop()) {
            imgHotAnswer.setVisibility(View.GONE);
        } else {
            imgHotAnswer.setVisibility(View.GONE);
        }
        Question question = answer.getQuestion();
        if (question.getType() != 2) {
            praiseLayout.setVisibility(View.VISIBLE);
        } else {
            praiseLayout.setVisibility(View.INVISIBLE);
        }
    }

    public View getClPraiseView() {
        return clPraiseView;
    }

    /**
     * 点赞视图设置
     */
    public void setPraiseView(Answer answer) {
        upCount.setText(answer.getUpCount() <= 0 ? "赞同" : String.valueOf(answer.getUpCount()));
        switch (answer.getLikeType()) {
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

    @OnClick({R2.id.cl_praise_view, R2.id.cl_oppose_view})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.cl_praise_view) {
            if (answer != null) {
                if (user != null && user.getId() == answer.getUser()
                        .getId()) {
                    // 当用户为答主时，显示小黑块：你不能给自己点赞哦~
                    ToastUtil.showToast(context, null, R.string.msg_answer_praise_self___qa);
                    clPraiseView.setChecked(false);
                    //                } else if (answer.getLikeType() == ANSWER_PRAISED) {
                    //                    // “已点赞”点击点赞按钮或反对按钮，系统弹出小黑块：你已经赞同过。
                    //                    ToastUtil.showToast(context, null, R.string
                    // .msg_answer_praised___qa);
                    //                    clPraiseView.setChecked(true);
                    //                } else if (answer.getLikeType() == ANSWER_OPPOSED) {
                    //                    // “已反对用户”点击点赞按钮或反对按钮，系统弹出小黑块：你已经反对过.
                    //                    ToastUtil.showToast(context, null, R.string
                    // .msg_answer_opposed___qa);
                    //                    clPraiseView.setChecked(false);
                } else {
                    if (onPraiseCallback != null) {
                        onPraiseCallback.onPraise(answer, true);
                    }
                }
            }
        } else if (i == R.id.cl_oppose_view) {
            if (answer != null) {
                if (user != null && user.getId() == answer.getUser()
                        .getId()) {
                    // 当用户为答主时，显示小黑块：你不能反对自己哦~
                    ToastUtil.showToast(context, null, R.string.msg_answer_oppose_self___qa);
                    clOpposeView.setChecked(false);
                    //                } else if (answer.getLikeType() == ANSWER_PRAISED) {
                    //                    // “已点赞”点击点赞按钮或反对按钮，系统弹出小黑块：你已经赞同过。
                    //                    ToastUtil.showToast(context, null, R.string
                    // .msg_answer_praised___qa);
                    //                    clOpposeView.setChecked(false);
                    //                } else if (answer.getLikeType() == ANSWER_OPPOSED) {
                    //                    // “已反对用户”点击点赞按钮或反对按钮，系统弹出小黑块：你已经反对过.
                    //                    ToastUtil.showToast(context, null, R.string
                    // .msg_answer_opposed___qa);
                    //                    clOpposeView.setChecked(true);
                } else {
                    if (onPraiseCallback != null) {
                        onPraiseCallback.onPraise(answer, false);
                    }
                }
            }
        }
    }

    /**
     * 刷新回调
     */
    public void setOnPraiseCallback(OnPraiseCallback onPraiseCallback) {
        this.onPraiseCallback = onPraiseCallback;
    }

    public interface OnPraiseCallback {
        void onPraise(Object object, boolean isPraise);
    }
}
