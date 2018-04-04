package com.hunliji.hljquestionanswer.activities;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.adapters.FragmentAdapter;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.fragments.AnswerDetailFragment;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/8/25.回答详情
 */
public class AnswerDetailActivity extends HljBaseNoBarActivity {

    @BindView(R2.id.tv_question_title)
    TextView tvQuestionTitle;
    @BindView(R2.id.action_more_menu)
    ImageButton actionMoreMenu;
    @BindView(R2.id.action_layout)
    LinearLayout actionLayout;
    @BindView(R2.id.viewpager)
    ViewPager viewpager;
    @BindView(R2.id.tv_answer_count)
    TextView tvAnswerCount;
    @BindView(R2.id.question_title_view)
    LinearLayout questionTitleView;
    @BindView(R2.id.tv_answer_title)
    TextView tvAnswerTitle;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private long answerId;
    private long questionId;
    private ArrayList<Answer> answers;
    private ArrayList<Question> questions;
    private int position;
    private Dialog menuDialog;
    private User user;
    private Answer answer;
    private boolean isEnd;
    private AnswerDetailFragment answerDetailFragment;
    private final static int ANSWER_EDIT = 1;
    private boolean isLoad;
    private boolean isShow;
    ObjectAnimator showAnimator;
    ObjectAnimator hideAnimator;

    FragmentAdapter fragmentAdapter;
    View bottomView;

    private HljHttpSubscriber initSubscriber;//初始加载
    private HljHttpSubscriber qaSubscriber;//相关问题加载

    private Handler shareCallbackHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                case HljShare.RequestCode.SHARE_TO_QQZONE:
                case HljShare.RequestCode.SHARE_TO_QQ:
                case HljShare.RequestCode.SHARE_TO_PENGYOU:
                case HljShare.RequestCode.SHARE_TO_WEIXIN:
                    trackerShare(HljShare.getShareTypeName(msg.what));
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_detail_fragment___qa);
        ButterKnife.bind(this);
        setActionBarPadding(actionLayout);
        initValue();
    }

    private void initValue() {
        answerId = getIntent().getLongExtra("answerId", 0);
        questionId = getIntent().getLongExtra("questionId", 0);
        answers = getIntent().getParcelableArrayListExtra("answers");
        position = getIntent().getIntExtra("position", 0);
        user = UserSession.getInstance()
                .getUser(this);
        questions = new ArrayList<>();
        isEnd = true;
        isLoad = false;
        isShow = false;
        if (questionId != 0) {
            initLoad();
        } else {
            initView();
        }
        new HljTracker.Builder(this).eventableId(answerId)
                .eventableType("Answer")
                .action("hit")
                .build()
                .add();
    }

    /**
     * 初始化View
     */
    private void initView() {
        if (answers != null && answers.size() > 0) {
            //判断当前列表是否有下一页，当能整除时，表示有下一页
            if (answers.size() % 20 == 0) {
                isEnd = false;
            } else {
                isEnd = true;
            }
            fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), answers);
            viewpager.setAdapter(fragmentAdapter);
            viewpager.setCurrentItem(position);
            fragmentAdapter.getItem(viewpager.getCurrentItem());

            if (!isEnd) {
                if (answers != null && answers.size() >= 20 && position > answers.size() - 3) {
                    int page = (answers.size() / 20) + 1;
                    updateList(page);
                }
            }
            viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(
                        int position, float positionOffset, int positionOffsetPixels) {
                    if (!isEnd) {
                        if (answers != null && answers.size() >= 20 && position > answers.size()
                                - 5 && !isLoad) {
                            int page = (answers.size() / 20) + 1;
                            isLoad = true;
                            updateList(page);
                        }
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    AnswerDetailFragment fragment = (AnswerDetailFragment) fragmentAdapter.getItem(
                            position);
                    if (isShow != fragment.showTitleView()) {
                        showQuestionTitle(fragment.showTitleView());
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            isEnd = true;
            fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), null);
            viewpager.setAdapter(fragmentAdapter);
        }
    }

    private void initLoad() {
        qaSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<HljHttpData<List<Question>>>>() {
                    @Override
                    public void onNext(HljHttpResult<HljHttpData<List<Question>>> data) {
                        if (data.getStatus()
                                .getRetCode() == 0) {
                            questions.clear();
                            if (data.getData() != null) {
                                questions.addAll(data.getData()
                                        .getData());
                            }
                        }
                        initView();
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        initView();
                    }
                })
                .setProgressBar(progressBar)
                .setDataNullable(true)
                .build();
        QuestionAnswerApi.getRelatedQuestionObb(questionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(qaSubscriber);
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }


    public void setQuestionTitle(final Answer answer) {
        if (answer.getQuestion() != null) {
            actionMoreMenu.setVisibility(answer.getQuestion()
                    .getType() == 2 ? View.GONE : View.VISIBLE);
            tvQuestionTitle.setText(answer.getQuestion()
                    .getTitle());
            tvAnswerCount.setText(getString(R.string.label_answer_detail_count_tip___qa,
                    answer.getQuestion()
                            .getAnswerCount()));
        }
        questionTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answers != null) {
                    finish();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(AnswerDetailActivity.this, QuestionDetailActivity.class);
                    intent.putExtra("questionId", answer.getQuestionId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            }
        });
    }

    public void showQuestionTitle(boolean isShow) {
        this.isShow = isShow;
        if (!isShow) {
            showToolbar();
        } else {
            questionTitleView.setVisibility(View.VISIBLE);
            hideToolbar();
        }
    }

    private void hideToolbar() {
        if (hideAnimator != null && hideAnimator.isRunning()) {
            return;
        }
        hideAnimator = ObjectAnimator.ofFloat(questionTitleView,
                View.TRANSLATION_Y,
                CommonUtil.dp2px(AnswerDetailActivity.this, 45),
                0);
        hideAnimator.setDuration(300);
        hideAnimator.start();
    }

    private void showToolbar() {
        if (showAnimator != null && showAnimator.isRunning()) {
            return;
        }
        showAnimator = ObjectAnimator.ofFloat(questionTitleView,
                View.TRANSLATION_Y,
                0,
                CommonUtil.dp2px(AnswerDetailActivity.this, 45));
        showAnimator.setDuration(300);
        showAnimator.start();
    }

    private void updateList(int page) {
        Observable<HljHttpCountData<List<Answer>>> observable = QuestionAnswerApi.getAnswerList(
                questionId,
                page,
                QuestionAnswerApi.TYPE_COMMUNITY_QA);
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpCountData<List<Answer>>>() {
                    @Override
                    public void onNext(HljHttpCountData<List<Answer>> data) {
                        answers.addAll(data.getData());
                        fragmentAdapter.notifyDataSetChanged();
                        isLoad = false;
                        //判断当前列表是否有下一页，当能整除时，表示有下一页
                        if (answers.size() % 20 == 0) {
                            isEnd = false;
                        } else {
                            isEnd = true;
                        }
                    }
                })
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(initSubscriber);
    }


    @OnClick({R2.id.action_more_menu})
    public void onClick(View view) {
        int i = view.getId();
        answerDetailFragment = (AnswerDetailFragment) fragmentAdapter.getItem(viewpager
                .getCurrentItem());
        answer = answerDetailFragment.getAnswer();
        bottomView = answerDetailFragment.getBottomLayout();
        if (i == R.id.action_more_menu) {
            if (AuthUtil.loginBindCheck(this)) {
                showMenu();
            }
        }
    }

    public void showMenu() {
        if (answer == null) {
            return;
        }
        menuDialog = new Dialog(this, R.style.BubbleDialogTheme);
        menuDialog.setContentView(R.layout.dialog_answer_comment_menu___qa);
        Button actionBtn = (Button) menuDialog.findViewById(R.id.action_delete_reply);
        if (user != null && user.getId() != answer.getUser()
                .getId()) {
            //当用户非题主时，第一个按钮为：举报回答。点击后，执行举报流程。
            actionBtn.setText("举报");
        } else {
            //当用户是答主时，第一个按钮为：编辑回答
            actionBtn.setText("编辑");
        }

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null && user.getId() != answer.getUser()
                        .getId()) {
                    //当用户非答主时，第一个按钮为：举报回答。点击后，执行举报流程。
                    Intent intent = new Intent(AnswerDetailActivity.this, ReportActivity.class);
                    intent.putExtra("id", answer.getId());
                    intent.putExtra("kind", HljQuestionAnswer.REPORT_ANSWER);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                } else {
                    if (answer.isAllowModify()) {
                        // 当用户是答主时，第一个按钮为：编辑回答
                        if (HljQuestionAnswer.isMerchant(AnswerDetailActivity.this)) {
                            // 商家端编辑时不需要资料完整检测
                            Intent intent = new Intent(AnswerDetailActivity.this,
                                    CreateAnswerActivity.class);
                            intent.putExtra("answer", answer);
                            startActivityForResult(intent, ANSWER_EDIT);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        } else {
                            Intent intent = new Intent(AnswerDetailActivity.this,
                                    CreateAnswerActivity.class);
                            intent.putExtra("answer", answer);
                            startActivityForResult(intent, ANSWER_EDIT);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    } else {
                        //如果回答已被运营在后台限制编，弹出小黑块：该回答已被限制编辑，2秒后自动消失。
                        ToastUtil.showToast(AnswerDetailActivity.this,
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
                Animation animation = AnimationUtils.loadAnimation(AnswerDetailActivity.this,
                        R.anim.slide_in_up);
                animation.setFillAfter(true);
                if (bottomView != null)
                    bottomView.startAnimation(animation);
            }
        });
        menuDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Animation animation = AnimationUtils.loadAnimation(AnswerDetailActivity.this,
                        R.anim.slide_out_up);
                animation.setFillAfter(true);
                if (bottomView != null)
                    bottomView.startAnimation(animation);
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

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        if (initSubscriber != null && !initSubscriber.isUnsubscribed()) {
            initSubscriber.unsubscribe();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ANSWER_EDIT:
                    if (data == null) {
                        answerDetailFragment.refreshView();
                    }
                    break;
                case HljShare.RequestCode.SHARE_TO_TXWEIBO:
                case HljShare.RequestCode.SHARE_TO_WEIBO:
                    shareCallbackHandler.sendEmptyMessage(requestCode);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void trackerShare(String shareInfo) {
        TrackerHelper.postShareAction(this, answerId, "answer");
        new HljTracker.Builder(this).eventableId(answerId)
                .eventableType("Answer")
                .action("share")
                .additional(shareInfo)
                .build()
                .add();
    }
}
