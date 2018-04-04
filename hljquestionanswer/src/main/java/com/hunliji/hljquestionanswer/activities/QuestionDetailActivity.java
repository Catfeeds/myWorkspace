package com.hunliji.hljquestionanswer.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.fragments.QADetailFragment;
import com.hunliji.hljquestionanswer.fragments.QuestionDetailFragment;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by mo_yu on 2016/8/17.问题详情
 */
public class QuestionDetailActivity extends HljBaseNoBarActivity {

    public static final String ARG_QUESTION_ID = "questionId";

    @BindView(R2.id.fl_container)
    FrameLayout flContainer;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_question_title)
    TextView tvQuestionTitle;
    @BindView(R2.id.action_share)
    ImageButton actionShare;
    @BindView(R2.id.action_more_menu)
    ImageButton actionMoreMenu;

    private long questionId;
    private User user;

    private HljHttpSubscriber initSubscriber;
    private Subscription backSubscription;
    private String site;
    private Question question;
    private Dialog menuDialog;

    private QuestionDetailFragment questionDetailFragment;

    private static final int QUESTION_EDIT = 1;

    private Handler shareCallbackHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                    trackerShare("QQZone");
                    break;
                case HljShare.RequestCode.SHARE_TO_QQ:
                    trackerShare("QQ");
                    break;
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                    trackerShare("Timeline");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    trackerShare("Session");
                    break;
            }
            return false;
        }
    });

    private void trackerShare(String shareInfo) {
        new HljTracker.Builder(this).eventableId(questionId)
                .eventableType("Question")
                .action("share")
                .additional(shareInfo)
                .build()
                .add();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail___qa);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        questionId = getIntent().getLongExtra("questionId", 0);
        boolean showKeyBoard = getIntent().getBooleanExtra("is_show_key_board", false);
        if (!showKeyBoard) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        site = getIntent().getStringExtra("site");
        initValue();
        onLoad();
        initError();
    }

    private void initValue() {
        user = UserSession.getInstance()
                .getUser(this);
        new HljTracker.Builder(this).eventableId(questionId)
                .eventableType("Question")
                .action("hit")
                .site(site)
                .build()
                .add();
    }

    private void initError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
    }

    @OnClick(R2.id.action_back)
    void actionBack() {
        hideKeyboard(null);
        super.onBackPressed();
    }

    @OnClick(R2.id.action_share)
    void actionShare() {
        if (question != null) {
            ShareDialogUtil.onCommonShare(this, question.getShareInfo(), shareCallbackHandler);

        }
    }

    @OnClick(R2.id.action_more_menu)
    public void onActionMoreClick(View view) {
        if (question == null) {
            return;
        }
        if (AuthUtil.loginBindCheck(this)) {
            showMenu(question);
        }
    }

    private void showMenu(final Question item) {
        if (question == null) {
            return;
        }
        menuDialog = new Dialog(this, R.style.BubbleDialogTheme);
        menuDialog.setContentView(R.layout.dialog_answer_comment_menu___qa);
        Button actionBtn = menuDialog.findViewById(R.id.action_delete_reply);
        if (user != null && user.getId() != item.getUser()
                .getId()) {
            //当用户非题主时，第一个按钮为：举报问题。点击后，执行举报流程。
            actionBtn.setText(getString(R.string.label_report___qa));
        } else {
            //当用户是题主时，第一个按钮为：编辑问题
            actionBtn.setText(getString(R.string.label_modify___qa));
        }
        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && user.getId() != item.getUser()
                        .getId()) {
                    //当用户非题主时，第一个按钮为：举报问题。点击后，执行举报流程。
                    Intent intent = new Intent(QuestionDetailActivity.this, ReportActivity.class);
                    intent.putExtra("id", item.getId());
                    intent.putExtra("kind", HljQuestionAnswer.REPORT_QUESTION);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                } else {
                    //当用户是题主时，第一个按钮为：编辑问题
                    if (item.isAllowModify()) {
                        Intent intent = new Intent(QuestionDetailActivity.this,
                                CreateQuestionTitleActivity.class);
                        intent.putExtra("question", question);
                        startActivityForResult(intent, QUESTION_EDIT);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else {
                        ToastUtil.showToast(QuestionDetailActivity.this,
                                null,
                                R.string.msg_question_un_modify___qa);
                    }
                }
                menuDialog.cancel();
            }
        });
        menuDialog.findViewById(R.id.action_cancel)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        menuDialog.cancel();
                    }
                });
        menuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Animation animation = AnimationUtils.loadAnimation(QuestionDetailActivity.this,
                        R.anim.slide_in_up);
                animation.setFillAfter(true);
                if (questionDetailFragment != null) {
                    questionDetailFragment.startBottomAnimation(animation);
                }
            }
        });
        menuDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Animation animation = AnimationUtils.loadAnimation(QuestionDetailActivity.this,
                        R.anim.slide_out_up);
                animation.setFillAfter(true);
                if (questionDetailFragment != null) {
                    questionDetailFragment.startBottomAnimation(animation);
                }
            }
        });
        Window window = menuDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = CommonUtil.getDeviceSize(this);
        params.width = point.x;
        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.dialog_anim_rise_style);
        menuDialog.show();
    }


    private void onLoad() {
        CommonUtil.unSubscribeSubs(initSubscriber);
        Observable<Question> observable = QuestionAnswerApi.getQuestionDetail(questionId);
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setContentView(flContainer)
                .setOnNextListener(new SubscriberOnNextListener<Question>() {
                    @Override
                    public void onNext(Question question) {
                        QuestionDetailActivity.this.question = question;
                        if (question.isDeleted()) {
                            //该问题已被删除，2秒后，页面返回至上一页
                            ToastUtil.showToast(QuestionDetailActivity.this,
                                    null,
                                    R.string.msg_question_hidden___qa);
                            CommonUtil.unSubscribeSubs(backSubscription);
                            backSubscription = Observable.timer(2, TimeUnit.SECONDS)
                                    .subscribe(new Action1<Long>() {
                                        @Override
                                        public void call(Long aLong) {
                                            QuestionDetailActivity.this.onBackPressed();
                                        }
                                    });
                            emptyView.setHintId(R.string.msg_question_hidden___qa);
                            emptyView.showEmptyView();
                        } else {
                            showFragment(question);
                        }
                    }
                })
                .build();
        observable.subscribe(initSubscriber);
    }

    private void showFragment(Question question) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (question.getType() != 2) {
            actionShare.setVisibility(View.VISIBLE);
            actionMoreMenu.setVisibility(View.VISIBLE);
            tvQuestionTitle.setText(getString(R.string.label_question_detail___qa));
            questionDetailFragment = (QuestionDetailFragment) manager.findFragmentByTag(
                    "questionDetailFragment");
            if (questionDetailFragment == null) {
                questionDetailFragment = QuestionDetailFragment.newInstance(questionId, question);
                transaction.add(R.id.fl_container,
                        questionDetailFragment,
                        "questionDetailFragment");
            } else {
                questionDetailFragment.refresh(question);
            }
        } else {
            tvQuestionTitle.setText(R.string.label_qa_detail___qa);
            if (!HljQuestionAnswer.isMerchant(QuestionDetailActivity.this)) {
                actionShare.setVisibility(View.VISIBLE);
            }
            QADetailFragment qaDetailFragment = QADetailFragment.newInstance(questionId, question);
            transaction.add(R.id.fl_container, qaDetailFragment, "qaDetailFragment");
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case QUESTION_EDIT:
                    onLoad();
                    break;
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                    trackerShare("TXWeibo");
                    break;
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    trackerShare("Weibo");
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber, backSubscription);
    }
}
