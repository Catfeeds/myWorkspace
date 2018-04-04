package com.hunliji.hljquestionanswer.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.BasePostResult;
import com.hunliji.hljcommonlibrary.models.PostCollectBody;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.models.realm.PostAnswerBody;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljDialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.activities.QAAnswerEditActivity;
import com.hunliji.hljquestionanswer.activities.QuestionDetailActivity;
import com.hunliji.hljquestionanswer.adapters.QADetailAdapter;
import com.hunliji.hljquestionanswer.adapters.viewholder.OnQADetailActionInterface;
import com.hunliji.hljquestionanswer.adapters.viewholder.QADetailViewHolder;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.PostPraiseIdBody;
import com.hunliji.hljquestionanswer.models.QARxEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/9/25
 * 问答详情
 */

public class QADetailFragment extends RefreshFragment implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView>,
        OnQADetailActionInterface {

    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.tv_reply)
    TextView tvReply;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.et_content)
    EditText etContent;
    @BindView(R2.id.layout)
    RelativeLayout layout;
    private View footerView;
    private View endView;
    private View loadView;
    private QADetailAdapter adapter;
    private List<Answer> answers;

    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber followSubscriber;
    private HljHttpSubscriber praiseSubscriber;
    private HljHttpSubscriber replySubscriber;

    private Unbinder unbinder;
    private Dialog dialog;
    private long questionId;
    private Question question;
    private Subscription rxBus;
    private boolean isMerchant;
    private final static int INTENT_VALUE_EDIT_ANSWER = 1;

    public static QADetailFragment newInstance(long questionId, Question question) {
        Bundle args = new Bundle();
        QADetailFragment fragment = new QADetailFragment();
        args.putLong("questionId", questionId);
        args.putParcelable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFooterView();
        initValue();
        registerRxBus();
    }

    private void registerRxBus() {
        if (rxBus == null || rxBus.isUnsubscribed()) {
            rxBus = RxBus.getDefault()
                    .toObservable(QARxEvent.class)
                    .subscribe(new RxBusSubscriber<QARxEvent>() {
                        @Override
                        protected void onEvent(QARxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case COMPLAIN_SUCCESS:
                                    int position = (int) rxEvent.getObject();
                                    if (position == 0) {
                                        if (question != null) {
                                            question.setAppealStatus(0);
                                        }
                                    } else {
                                        if (answers.size() > position - 1) {
                                            answers.get(position - 1)
                                                    .setAppealStatus(0);
                                        }
                                    }
                                    adapter.notifyItemChanged(position);
                                    break;
                            }
                        }
                    });
        }
    }


    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qa_detail___qa, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initNetError();
        initView();
        onRefresh(recyclerView);
    }

    @Override
    public void refresh(Object... params) {
        onRefresh(recyclerView);
    }

    private void initValue() {
        isMerchant = HljQuestionAnswer.isMerchant(getContext());
        answers = new ArrayList<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            questionId = bundle.getLong("questionId", 0);
            question = bundle.getParcelable("question");
        }
    }

    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
    }

    private void initNetError() {
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    private void initView() {
        if (isMerchant) {
            etContent.setHint(R.string.hint_objective_answer_may_directly_bring_advice___qa);
        } else {
            etContent.setHint(R.string.hint_buy_or_experience_the_service_users_can_answer___qa);
        }
        adapter = new QADetailAdapter(getContext(), answers);
        recyclerView.setOnRefreshListener(this);
        adapter.setFooterView(footerView);
        adapter.setOnQADetailInterface(this);
        RecyclerView mRecyclerView = recyclerView.getRefreshableView();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyBoard();
                return false;
            }
        });
        initBottomStatus();
    }

    private void initBottomStatus() {
        if (!isMerchant && !question.isCanAnswer()) {
            etContent.setKeyListener(null);
            etContent.setCursorVisible(false);
            tvReply.setBackgroundResource(R.drawable.sp_r3_gray3);
            tvReply.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        }
    }

    @OnClick(R2.id.et_content)
    void onContentClick() {
        if (question == null) {
            return;
        }
        if (!isMerchant && !question.isCanAnswer()) {
            showReplyTipDialog();
        }
    }

    @OnClick(R2.id.tv_reply)
    void onReply() {
        if (!isMerchant) {
            if (!AuthUtil.loginBindCheck(getContext())) {
                return;
            }
        }
        if (question == null) {
            return;
        }
        if (!isMerchant && !question.isCanAnswer()) {
            showReplyTipDialog();
        } else {
            if (etContent.length() == 0) {
                ToastUtil.showToast(getContext(), "回答不能为空哦~", 0);
                return;
            }
            String content = etContent.getText()
                    .toString()
                    .trim();
            // 新建答案
            onCreatedNewAnswer(content);
        }
    }

    private void onCreatedNewAnswer(String content) {
        PostAnswerBody body = new PostAnswerBody();
        body.setQuestionId(questionId);
        body.setContent(content);
        Observable<HljHttpResult<BasePostResult>> observable = QuestionAnswerApi.postAnswerObb(body,
                QuestionAnswerApi.TYPE_MERCHANT_QA);
        replySubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<BasePostResult>>() {
                    @Override
                    public void onNext(HljHttpResult<BasePostResult> result) {
                        if (result.getStatus()
                                .getRetCode() == 0) {
                            onRefresh(recyclerView);
                            RxBus.getDefault()
                                    .post(new QARxEvent(QARxEvent.RxEventType
                                            .QUESTION_REPLY_SUCCESS,
                                            null));
                            ToastUtil.showCustomToast(getContext(),
                                    R.string.msg_thanks_for_your_answer___qa);
                        } else {
                            ToastUtil.showToast(getContext(),
                                    result.getStatus()
                                            .getMsg(),
                                    0);
                        }
                        hideKeyBoard();
                        etContent.setText(null);
                    }
                })
                .build();
        observable.subscribe(replySubscriber);
    }

    private void hideKeyBoard() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        ((QuestionDetailActivity) getActivity()).hideKeyboard(null);
    }

    /**
     * 用户暂时不可回答
     * 回答购买或体验过服务的用户才能回答
     */
    private void showReplyTipDialog() {
        if (dialog == null) {
            dialog = DialogUtil.createDialog(getContext(), R.layout.reply_dialog_confirm___qa);
            TextView tvReplyName = dialog.findViewById(R.id.tv_reply_tip);
            Button btnConfirm = dialog.findViewById(R.id.btn_confirm);
            String content = tvReplyName.getText()
                    .toString();
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(content);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(ContextCompat.getColor(
                    getContext(),
                    R.color.colorPrimary));
            String text = "1~60位已体验过";
            int index = content.indexOf(text);
            stringBuilder.setSpan(colorSpan,
                    index,
                    index + text.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvReplyName.setText(stringBuilder);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSubscriber);
        Observable<HljHttpCountData<List<Answer>>> observable = QuestionAnswerApi.getAnswerList(
                questionId,
                1,
                QuestionAnswerApi.TYPE_MERCHANT_QA);
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpCountData<List<Answer>>>() {
                    @Override
                    public void onNext(HljHttpCountData<List<Answer>> answersData) {
                        answers.clear();
                        int totalCount = 0;
                        if (answersData != null && !CommonUtil.isCollectionEmpty(answersData
                                .getData())) {
                            initPageAnswers(answersData.getPageCount());
                            answers.addAll(answersData.getData());
                            totalCount = answersData.getTotalCount();
                        }
                        adapter.setQuestion(question);
                        adapter.setTotalCount(totalCount);
                        adapter.setAnswers(answers);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setDataNullable(true)
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .setPullToRefreshBase(recyclerView)
                .build();

        observable.subscribe(refreshSubscriber);
    }

    private void initPageAnswers(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpCountData<List<Answer>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpCountData<List<Answer>>>() {
                    @Override
                    public Observable<HljHttpCountData<List<Answer>>> onNextPage(
                            int page) {
                        return QuestionAnswerApi.getAnswerList(questionId,
                                page,
                                QuestionAnswerApi.TYPE_MERCHANT_QA);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Answer>> data) {
                        answers.addAll(data.getData());
                        adapter.setAnswers(answers);
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    public void onComplain(final long id, final int position) {
        //type 1.问题申诉 2.回答申诉
        final int type = position == 0 ? 1 : 2;
        DialogUtil.showBottomDialog(getContext(),
                getContext().getString(R.string.label_complain___qa),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ARouter.getInstance()
                                .build(RouterPath.IntentPath.Merchant.QUESTION_COMPLAIN)
                                .withLong("id", id)
                                .withInt("type", type)
                                .withInt("position", position)
                                .withBoolean("is_question", true)
                                .withTransition(R.anim.slide_in_right, R.anim.activity_anim_default)
                                .navigation(getContext());
                    }
                })
                .show();
    }

    public void onEdit(final Answer answer) {
        if (answer == null) {
            return;
        }
        DialogUtil.showBottomDialog(getContext(),
                getContext().getString(R.string.label_edit___share),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), QAAnswerEditActivity.class);
                        intent.putExtra(QAAnswerEditActivity.ARG_ANSWER, answer);
                        intent.putExtra(QAAnswerEditActivity.ARG_QUESTION_ID, questionId);
                        startActivityForResult(intent, INTENT_VALUE_EDIT_ANSWER);
                    }
                })
                .show();
    }


    /**
     * 关注
     */
    @Override
    public void onFollowQuestion(
            final Question question, final int position) {
        if (AuthUtil.loginBindCheck(getContext())) {
            CommonUtil.unSubscribeSubs(followSubscriber);
            setFollowView(question, position);
            PostCollectBody body = new PostCollectBody();
            body.setId(question.getId());
            body.setFollowableType("QaQuestion");
            followSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            if (question.isFollow()) {
                                DialogUtil.createSingleButtonDialog(getContext(),
                                        null,
                                        "您已成功关注此问题，此问题新的回答我们将通知您，请注意关注消息提醒",
                                        null,
                                        null)
                                        .show();
                            } else {
                                ToastUtil.showCustomToast(getContext(),
                                        R.string.msg_success_to_un_follow___cm);
                            }
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            ToastUtil.showCustomToast(getContext(),
                                    question.isFollow() ? R.string.msg_fail_to_follow___cm : R
                                            .string.msg_fail_to_cancel_follow___cm);
                            // 失败的话变回原样
                            setFollowView(question, position);
                        }
                    })
                    .build();
            CommonApi.postCollectObb(body, !question.isFollow())
                    .subscribe(followSubscriber);
        }

    }

    private void setFollowView(final Question question, int position) {
        int count = question.getFollowCount();
        question.setFollow(!question.isFollow());
        question.setFollowCount(question.isFollow() ? count + 1 : count - 1);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onPraiseClickListener(
            final CheckableLinearLayout praiseView, final TextView upCount, final Answer item) {
        if (AuthUtil.loginBindCheck(getContext())) {
            CommonUtil.unSubscribeSubs(praiseSubscriber);
            //先变化 在请求
            setPraiseState(item);
            setPraiseView(item, praiseView, upCount);
            upCount.setText(item.getUpCount() <= 0 ? "有用" : String.valueOf(item.getUpCount()));
            PostPraiseIdBody body = new PostPraiseIdBody();
            body.setId(item.getId());
            body.setValue(item.getLikeType());
            Observable observable = QuestionAnswerApi.postPraiseAnswerObb(body);
            praiseSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
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
                            switch (item.getLikeType()) {
                                case QADetailViewHolder.ANSWER_PRAISED:
                                    ToastUtil.showCustomToast(getContext(),
                                            R.string.msg_success_to_praise___cm);
                                    break;
                                case QADetailViewHolder.ANSWER_CANCEL:
                                    ToastUtil.showCustomToast(getContext(),
                                            R.string.msg_success_to_un_praise___cm);
                                    break;
                            }
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            // 失败的话变回原样
                            setPraiseState(item);
                            setPraiseView(item, praiseView, upCount);
                            upCount.setText(item.getUpCount() <= 0 ? "有用" : String.valueOf(item
                                    .getUpCount()));
                        }
                    })
                    .build();
            observable.subscribe(praiseSubscriber);
        }
    }

    @Override
    public void onMoreOption(@Nullable Answer answer, int position) {
        if (HljQuestionAnswer.isMerchant(getContext())) {
            if (answer != null && answer.getUser()
                    .getKind() == 1) {
                // 商家回答，进行编辑
                onEdit(answer);
            } else {
                // 用户回答,进行申诉
                long id;
                if (answer == null) {
                    id = questionId;
                } else {
                    id = answer.getId();
                }
                onComplain(id, position);
            }
        } else {
            onEdit(answer);
        }
    }

    private void setPraiseState(Answer item) {
        switch (item.getLikeType()) {
            case QADetailViewHolder.ANSWER_PRAISED:
                item.setLikeType(0);
                item.setUpCount(item.getUpCount() - 1);
                break;
            case QADetailViewHolder.ANSWER_CANCEL:
                item.setLikeType(1);
                item.setUpCount(item.getUpCount() + 1);
                break;
        }
    }

    private void setPraiseView(
            Answer answer, CheckableLinearLayout praiseView, TextView upCount) {
        switch (answer.getLikeType()) {
            //未点赞 未反对
            case QADetailViewHolder.ANSWER_CANCEL:
                praiseView.setChecked(false);
                upCount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack3));
                break;
            //已点赞
            case QADetailViewHolder.ANSWER_PRAISED:
                praiseView.setChecked(true);
                upCount.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case INTENT_VALUE_EDIT_ANSWER:
                    refresh();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber,
                pageSubscriber,
                followSubscriber,
                praiseSubscriber,
                replySubscriber);
    }
}
