package com.hunliji.hljquestionanswer.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.widgets.CustomWebView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.PostCollectBody;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.modelwrappers.QaAuthor;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljDialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.HljImageSpan;
import com.hunliji.hljemojilibrary.EmojiUtil;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.activities.AnswerCommentListActivity;
import com.hunliji.hljquestionanswer.activities.AnswerDetailActivity;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.PostPraiseIdBody;
import com.hunliji.hljquestionanswer.widgets.AnswerAuthHeadView;
import com.hunliji.hljquestionanswer.widgets.AnswerDetailHintView;
import com.hunliji.hljquestionanswer.widgets.CustScrollView;
import com.hunliji.hljsharelibrary.HljShare;
import com.hunliji.hljsharelibrary.utils.ShareDialogUtil;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2016/8/17.回答详情
 */
public class AnswerDetailFragment extends RefreshFragment {

    private final static int ANSWER_PRAISED = 1;//已点赞
    private final static int ANSWER_OPPOSED = -1;//已反对
    private final static int ANSWER_CANCEL = 0;//未点赞，未反对
    @BindView(R2.id.tv_question_summary)
    TextView tvQuestionSummary;
    @BindView(R2.id.tv_answer_count)
    TextView tvAnswerCount;
    @BindView(R2.id.question_view)
    RelativeLayout questionView;
    @BindView(R2.id.web_view)
    CustomWebView webView;
    @BindView(R2.id.scroll_view)
    CustScrollView scrollView;
    @BindView(R2.id.bottom_line)
    View bottomLine;
    @BindView(R2.id.img_collect)
    ImageView imgCollect;
    @BindView(R2.id.tv_collect)
    TextView tvCollect;
    @BindView(R2.id.check_collected)
    LinearLayout checkCollected;
    @BindView(R2.id.tv_comment_count)
    TextView tvCommentCount;
    @BindView(R2.id.comment_layout)
    LinearLayout commentLayout;
    @BindView(R2.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.hint_answer_detail)
    AnswerDetailHintView hintAnswerDetail;
    @BindView(R2.id.answer_header_view)
    AnswerAuthHeadView answerHeaderView;
    @BindView(R2.id.answer_header_view2)
    AnswerAuthHeadView answerHeaderView2;

    private long answerId;
    private ArrayList<Answer> answers;
    private ArrayList<Question> questions;
    private int position;
    private boolean hideTitle;
    private boolean showTitle;

    private Unbinder unbinder;
    private Answer answer;
    private int topHeight;
    private int faceSize;
    private int redColor;
    private int blackColor;
    private long questionId;

    private HljHttpSubscriber initSubscriber;//初始加载
    private HljHttpSubscriber refreshSubscriber;//刷新加载
    private HljHttpSubscriber praiseSubscriber;//点赞
    private HljHttpSubscriber collectSubscriber;//收藏
    private HljHttpSubscriber questionSubscriber;//相关问题列表

    public static AnswerDetailFragment newInstance(
            int position, ArrayList<Answer> answers) {
        Bundle args = new Bundle();
        AnswerDetailFragment fragment = new AnswerDetailFragment();
        args.putInt("position", position);
        args.putSerializable("answers", answers);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answer_detail___qa, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initValues();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initLoad();
    }

    public void refreshView() {
        refreshLoad();
    }

    private void initValues() {
        if (getArguments() != null) {
            answers = getArguments().getParcelableArrayList("answers");
            position = getArguments().getInt("position", 0);
        }
        if (answers != null && answers.size() >= position) {
            answerId = answers.get(position)
                    .getId();
        } else {
            answerId = getActivity().getIntent()
                    .getLongExtra("answerId", 0);
        }
        questions = new ArrayList<>();
        questionId = getActivity().getIntent()
                .getLongExtra("questionId", 0);
        faceSize = CommonUtil.dp2px(getContext(), 20);
        hideTitle = true;
        showTitle = false;
    }

    private void initViews() {

    }

    private void initLoad() {
        progressBar.setVisibility(View.VISIBLE);
        Observable<Answer> hqaObservable = QuestionAnswerApi.getAnswerDetail(answerId);
        initSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setContentView(scrollView)
                .setEmptyView(emptyView)
                .setOnNextListener(new SubscriberOnNextListener<Answer>() {
                    @Override
                    public void onNext(Answer item) {
                        if (item != null && item.isDeleted()) {
                            progressBar.setVisibility(View.GONE);
                            //该回答已被删除，2秒后，页面返回至上一页
                            ToastUtil.showToast(getActivity(),
                                    null,
                                    R.string.msg_answer_hidden___qa);
                            emptyView.setHintId(R.string.msg_answer_hidden___qa);
                            emptyView.showEmptyView();
                            if (answers == null || answers.size() == 1) {
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        if (getActivity() == null) {
                                            return;
                                        }
                                        getActivity().onBackPressed();
                                    }
                                }, 2000);
                            }
                        } else {
                            answer = item;
                            if (questionId == 0) {
                                assert item != null;
                                if (item.getQuestion() != null) {
                                    questionId = item.getQuestion()
                                            .getId();
                                }
                                getQuestionList();
                            } else {
                                AnswerDetailActivity activity = (AnswerDetailActivity)
                                        getActivity();
                                questions = activity.getQuestions();
                                setAnswerDetailView();
                            }
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .build();
        hqaObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(initSubscriber);
    }

    private void getQuestionList() {
        questionSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
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
                        setAnswerDetailView();
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        setAnswerDetailView();
                    }
                })
                .setDataNullable(true)
                .build();
        QuestionAnswerApi.getRelatedQuestionObb(questionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(questionSubscriber);
    }

    private void refreshLoad() {
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<Answer>() {
                    @Override
                    public void onNext(Answer item) {
                        answer = item;
                        setAnswerDetailView();
                    }
                })
                .build();
        QuestionAnswerApi.getAnswerDetail(answerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(refreshSubscriber);
    }

    /**
     * 获取本地js配置
     */
    public static String getContent(Context context) {
        InputStreamReader inputReader = null;
        try {
            inputReader = new InputStreamReader(context.getResources()
                    .getAssets()
                    .open("answer_content.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        assert inputReader != null;
        BufferedReader bufReader = new BufferedReader(inputReader);
        StringBuilder sb = new StringBuilder("");
        String line;
        try {
            while ((line = bufReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void setAnswerDetailView() {
        progressBar.setVisibility(View.GONE);
        if (answer != null) {
            Question question = answer.getQuestion();
            if (question.getType() != 2) {
                bottomLayout.setVisibility(View.VISIBLE);
            } else {
                bottomLayout.setVisibility(View.GONE);
            }
            answerHeaderView.setVisibility(View.VISIBLE);
            answerHeaderView.setAnswerAuthView(answer);
            answerHeaderView2.setAnswerAuthView(answer);
            answerHeaderView.setOnPraiseCallback(new AnswerAuthHeadView.OnPraiseCallback() {
                @Override
                public void onPraise(Object object, boolean isPraise) {
                    Answer answer = (Answer) object;
                    onAnswerPraise(answer, isPraise);
                }
            });
            answerHeaderView2.setOnPraiseCallback(new AnswerAuthHeadView.OnPraiseCallback() {
                @Override
                public void onPraise(Object object, boolean isPraise) {
                    Answer answer = (Answer) object;
                    onAnswerPraise(answer, isPraise);
                }
            });
            answerHeaderView.showBottomLine(false);
            answerHeaderView2.showBottomLine(true);
            if (!TextUtils.isEmpty(answer.getContent())) {
                webView.loadDataWithBaseURL(null, getContent(), "text/html", "UTF-8", null);
            }

            if (answer.getQuestion() != null && !TextUtils.isEmpty(answer.getQuestion()
                    .getTitle())) {
                tvAnswerCount.setText(getActivity().getString(R.string
                                .label_answer_detail_count_tip___qa,
                        answer.getQuestion()
                                .getAnswerCount()));

                String summary = "    " + answer.getQuestion()
                        .getTitle();
                SpannableStringBuilder builder = EmojiUtil.parseEmojiByText2(getActivity(),
                        summary,
                        faceSize);
                if (builder != null) {
                    Drawable drawable = ContextCompat.getDrawable(getActivity(),
                            R.mipmap.icon_question_answer_tag_primary);
                    drawable.setBounds(0,
                            0,
                            CommonUtil.dp2px(getContext(), 36),
                            CommonUtil.dp2px(getContext(), 18));
                    HljImageSpan span = new HljImageSpan(drawable);
                    builder.setSpan(span, 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                tvQuestionSummary.setText(builder);
                questionView.setVisibility(View.VISIBLE);

                //回答详情引导
                if (!getActivity().getSharedPreferences(HljCommon.FileNames.PREF_FILE,
                        Context.MODE_PRIVATE)
                        .getBoolean(HljQuestionAnswer.ANSWER_DETAIL_HINT,
                                false) && question.getType() != 2) {
                    hintAnswerDetail.setTargetView(answerHeaderView.getClPraiseView());
                    hintAnswerDetail.setVisibility(View.VISIBLE);
                    hintAnswerDetail.showHint();
                }

            } else {
                questionView.setVisibility(View.GONE);
            }
            questionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answers != null) {
                        getActivity().finish();
                    } else {
                        if (answer != null) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), QuestionDetailActivity.class);
                            intent.putExtra("questionId", answer.getQuestionId());
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                }
            });
            questionView.post(new Runnable() {
                @Override
                public void run() {
                    if (questionView != null) {
                        topHeight = questionView.getMeasuredHeight();
                    }
                }
            });
            final AnswerDetailActivity activity = (AnswerDetailActivity) getActivity();
            activity.setQuestionTitle(answer);
            scrollView.setOnScrollListener(new CustScrollView.OnScrollListener() {
                @Override
                public void onScrollChanged(int l, int t, int oldl, int oldt) {
                    if (t > topHeight) {
                        answerHeaderView2.setVisibility(View.VISIBLE);
                        if (!showTitle) {
                            hideTitle = false;
                            showTitle = true;
                            activity.showQuestionTitle(true);
                        }
                    } else {
                        answerHeaderView2.setVisibility(View.GONE);
                        if (!hideTitle) {
                            hideTitle = true;
                            showTitle = false;
                            activity.showQuestionTitle(false);
                        }

                    }
                }
            });

            redColor = ContextCompat.getColor(getActivity(), R.color.colorPrimary);
            blackColor = ContextCompat.getColor(getActivity(), R.color.colorBlack2);
            //已收藏
            if (answer.isFollow()) {
                imgCollect.setImageResource(R.mipmap.icon_star_primary_40_40);
                tvCollect.setTextColor(redColor);
                tvCollect.setText(getString(R.string.label_collected___cm));
            }
            //未收藏
            else {
                imgCollect.setImageResource(R.mipmap.icon_star_black_40_40);
                tvCollect.setTextColor(blackColor);
                tvCollect.setText(getString(R.string.label_collect_answer___cm));
            }

            //评论数
            if (answer.getCommentCount() == 0) {
                tvCommentCount.setText(getString(R.string.label_comment___qa));
            } else {
                tvCommentCount.setText(getString(R.string.label_comment_count_bottom___qa,
                        answer.getCommentCount()));
            }
        }
    }

    private String getContent() {
        String a = getContent(getActivity());
        String b = a.replace("问答内容", answer.getContent());
        String c = b.replace("创建于",
                "创建于" + answer.getCreatedAt()
                        .toString(HljQuestionAnswer.DATE_FORMAT_SHORT));
        String d;
        QaAuthor author = answer.getUser();
        if (author != null && answer.isTop()) {
            String id = "0";
            String avatar = ImageUtil.getImagePath2(author.getAvatar(),
                    CommonUtil.dp2px(getContext(), 36));
            if (author.getKind() == 1) {
                if (author.getMerchant() != null) {
                    id = String.valueOf(author.getMerchant()
                            .getId());
                }
            } else {
                id = String.valueOf(author.getId());
            }
            /**
             * 需要动态设置的字段 答主头像iv_answer_avatar 答主名称user_name
             * 答主回答数user_answer_count 答主被点赞数user_praise_count 答主类型kind 答主id user_id
             */
            assert avatar != null;
            String f;
            f = c.replace("iv_answer_avatar", avatar)
                    .replace("user_name", author.getName())
                    .replace("user_answer_count", author.getUserAnswerCount() + "回答")
                    .replace("user_praise_count", "获得" + author.getUserLikesCount() + "个赞")
                    //商家端点击用户无跳转
                    .replace("kind",
                            HljQuestionAnswer.isMerchant(getContext()) ? "-1" : String.valueOf(
                                    author.getKind()))
                    //为商家时id取商家id
                    .replace("user_id", id);
            if (HljQuestionAnswer.isMerchant(getContext())) {
                String userStr = f.substring(f.indexOf("<!--去看看 start-->"),
                        f.indexOf("<!--去看看 end-->"));
                d = f.replace(userStr, "");
            } else {
                d = f;
            }
        } else {
            String userStr = c.substring(c.indexOf("<!--优质答主 start-->"),
                    c.indexOf("<!--优质答主 end-->"));
            d = c.replace(userStr, "");
        }
        String e;
        if (questions != null && questions.size() > 0) {
            Question question1 = questions.get(0);
            String q1 = d.replace("question_id_1", String.valueOf(question1.getId()))
                    .replace("question_title_1", question1.getTitle())
                    .replace("question_answer_count_1",
                            String.valueOf(question1.getAnswerCount()) + "个回答");
            String q2;
            if (questions.size() > 1) {
                Question question2 = questions.get(1);
                q2 = q1.replace("question_id_2", String.valueOf(question2.getId()))
                        .replace("question_title_2", question2.getTitle())
                        .replace("question_answer_count_2",
                                String.valueOf(question2.getAnswerCount()) + "个回答");
            } else {
                String q2Str = q1.substring(q1.indexOf(" <!--问题2 start-->"),
                        q1.indexOf("<!--问题2 end-->"));
                q2 = q1.replace(q2Str, "");
            }
            String q3;
            if (questions.size() > 2) {
                Question question3 = questions.get(2);
                q3 = q2.replace("question_id_3", String.valueOf(question3.getId()))
                        .replace("question_title_3", question3.getTitle())
                        .replace("question_answer_count_3",
                                String.valueOf(question3.getAnswerCount()) + "个回答");
            } else {
                String q3Str = q2.substring(q2.indexOf(" <!--问题3 start-->"),
                        q2.indexOf(" <!--问题3 end-->"));
                q3 = q2.replace(q3Str, "");
            }
            e = q3;
        } else {
            String q1Str = d.substring(d.indexOf("<!--相关问题 start-->"),
                    d.indexOf("<!--相关问题 end-->"));
            e = d.replace(q1Str, "");
        }
        return e;
    }

    public boolean showTitleView() {
        return showTitle;
    }

    @OnClick({R2.id.check_collected, R2.id.comment_layout, R2.id.action_share})
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.check_collected) {
            if (answer != null) {
                onAnswerCollect(answer);
            }
        } else if (i == R.id.comment_layout) {
            if (answer == null || answerId == 0) {
                return;
            }
            Intent intent = new Intent();
            intent.setClass(getActivity(), AnswerCommentListActivity.class);
            intent.putExtra("answerId", answerId);
            intent.putExtra("questionAuthId",
                    answer.getUser()
                            .getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        } else if (i == R.id.action_share) {
            shareHelper();
        }
    }

    /**
     * 分享
     */
    public void shareHelper() {
        if (answer != null && answer.getShareInfo() != null) {
            ShareDialogUtil.onCommonShare(getContext(),
                    answer.getShareInfo(),
                    shareCallbackHandler);
        }
    }

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
        TrackerHelper.postShareAction(getContext(), answerId, "answer");
        new HljTracker.Builder(getContext()).eventableId(answerId)
                .eventableType("Answer")
                .action("share")
                .additional(shareInfo)
                .build()
                .add();
    }


    /**
     * 回答详情点赞
     */
    public void onAnswerPraise(final Answer item, final boolean isPraise) {
        boolean isCancel = false;//是否先取消，再操作
        if (AuthUtil.loginBindCheck(getActivity())) {
            // 先变化,再进行网络请求
            switch (item.getLikeType()) {
                case ANSWER_PRAISED:
                    if (isPraise) {
                        // 已经赞过,取消点赞
                        item.setLikeType(0);
                    } else {
                        // 已经赞过,取消点赞，进行反对
                        isCancel = true;
                        item.setLikeType(-1);
                    }
                    item.setUpCount(item.getUpCount() - 1);
                    break;
                case ANSWER_OPPOSED:
                    if (isPraise) {
                        // 已经反对,取消反对,进行点赞
                        isCancel = true;
                        item.setLikeType(1);
                        item.setUpCount(item.getUpCount() + 1);
                    } else {
                        // 已经反对,取消反对
                        item.setLikeType(0);
                    }
                    break;
                case ANSWER_CANCEL:
                    if (isPraise) {
                        // 没有赞过,变为赞
                        item.setLikeType(1);
                        item.setUpCount(item.getUpCount() + 1);
                    } else {
                        // 没有反对,变为反对
                        item.setLikeType(-1);
                    }
                    break;
            }
            PostPraiseIdBody body = new PostPraiseIdBody();
            body.setId(item.getId());
            body.setValue(item.getLikeType());
            Observable observable = QuestionAnswerApi.postPraiseAnswerObb(body);
            final boolean finalIsCancel = isCancel;
            praiseSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            //首次点赞问答判断
                            if (HljDialogUtil.isNewFirstCollect(getContext(),
                                    HljDialogUtil.QUESTION_ANSWER)) {
                                HljDialogUtil.showFirstCollectNoticeDialog(getContext(),
                                        HljDialogUtil.QUESTION_ANSWER);
                            }
                            RxBus.getDefault()
                                    .post(new RxEvent(RxEvent.RxEventType.ANSWER_PRAISE, null));
                            answerHeaderView.setPraiseView(item);
                            answerHeaderView2.setPraiseView(item);
                            switch (item.getLikeType()) {
                                case ANSWER_PRAISED:
                                    ToastUtil.showCustomToast(getActivity(),
                                            R.string.msg_success_to_praise___cm);
                                    break;
                                case ANSWER_OPPOSED:
                                    ToastUtil.showCustomToast(getActivity(),
                                            R.string.msg_success_to_oppose___cm);
                                    break;
                                case ANSWER_CANCEL:
                                    if (isPraise) {
                                        ToastUtil.showCustomToast(getActivity(),
                                                R.string.msg_success_to_un_praise___cm);
                                    } else {
                                        ToastUtil.showCustomToast(getActivity(),
                                                R.string.msg_success_to_un_oppose___cm);
                                    }
                                    break;
                            }
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            // 失败的话变回原样
                            switch (item.getLikeType()) {
                                case ANSWER_PRAISED:
                                    if (!finalIsCancel) {
                                        item.setLikeType(0);
                                    } else {
                                        item.setLikeType(-1);
                                    }
                                    item.setUpCount(item.getUpCount() - 1);
                                    break;
                                case ANSWER_OPPOSED:
                                    if (finalIsCancel) {
                                        item.setLikeType(1);
                                        item.setUpCount(item.getUpCount() + 1);
                                    } else {
                                        item.setLikeType(0);
                                    }
                                    break;
                                case ANSWER_CANCEL:
                                    if (isPraise) {
                                        item.setLikeType(1);
                                        item.setUpCount(item.getUpCount() + 1);
                                    } else {
                                        item.setLikeType(-1);
                                    }
                                    break;
                            }
                            answerHeaderView.setPraiseView(item);
                            answerHeaderView2.setPraiseView(item);
                        }
                    })
                    .build();
            observable.subscribe(praiseSubscriber);
        }
    }

    /**
     * 回答详情收藏
     */
    public void onAnswerCollect(final Answer item) {
        if (AuthUtil.loginBindCheck(getActivity())) {
            checkCollected.setClickable(false);
            // 先变化,再进行网络请求
            if (item.isFollow()) {
                // 已收藏,变为未收藏
                item.setFollow(false);
                tvCollect.setTextColor(blackColor);
                imgCollect.setImageResource(R.mipmap.icon_star_black_40_40);
                tvCollect.setText(getString(R.string.label_collect___cm));
            } else {
                // 没有收藏,变为收藏
                item.setFollow(true);
                tvCollect.setTextColor(redColor);
                imgCollect.setImageResource(R.mipmap.icon_star_primary_40_40);
                tvCollect.setText(getString(R.string.label_collected___cm));
            }

            PostCollectBody body = new PostCollectBody();
            body.setId(item.getId());
            body.setFollowableType("QaAnswer");
            collectSubscriber = HljHttpSubscriber.buildSubscriber(getActivity())
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            ToastUtil.showCustomToast(getActivity(),
                                    item.isFollow() ? R.string.msg_success_to_collect___cm : R
                                            .string.msg_success_to_un_collect___cm);
                            checkCollected.setClickable(true);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            ToastUtil.showCustomToast(getActivity(),
                                    item.isFollow() ? R.string.msg_fail_to_collect___cm : R
                                            .string.msg_fail_to_cancel_collect___cm);
                            // 失败的话变回原样
                            if (item.isFollow()) {
                                // 之前已经收藏,现在取消
                                item.setFollow(false);
                                imgCollect.setImageResource(R.mipmap.icon_star_black_40_40);
                                tvCollect.setTextColor(blackColor);
                                tvCollect.setText(getString(R.string.label_collect___cm));
                            } else {
                                // 没有收藏过,变为收藏
                                item.setFollow(true);
                                imgCollect.setImageResource(R.mipmap.icon_star_primary_40_40);
                                tvCollect.setTextColor(redColor);
                                tvCollect.setText(getString(R.string.label_collected___cm));
                            }
                            checkCollected.setClickable(true);
                        }
                    })
                    .build();
            CommonApi.postCollectObb(body, !item.isFollow())
                    .subscribe(collectSubscriber);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(initSubscriber,
                praiseSubscriber,
                collectSubscriber,
                refreshSubscriber,
                questionSubscriber);
    }

    private void hideToolbar() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(bottomLayout,
                View.TRANSLATION_Y,
                0,
                bottomLayout.getHeight());
        animator.setDuration(500);
        animator.start();
    }

    private void showToolbar() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(bottomLayout,
                View.TRANSLATION_Y,
                bottomLayout.getHeight(),
                0);
        animator.setDuration(500);
        animator.start();
    }

    @Override
    public void refresh(Object... params) {
    }

    public LinearLayout getBottomLayout() {
        return bottomLayout;
    }

    public Answer getAnswer() {
        return answer;
    }

}
