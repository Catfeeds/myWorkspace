package com.hunliji.hljquestionanswer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.PostCollectBody;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.merchant.MerchantUser;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.models.realm.PostAnswerBody;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljDialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpCountData;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.HljQuestionAnswer;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.activities.AnswerDetailActivity;
import com.hunliji.hljquestionanswer.activities.CreateAnswerActivity;
import com.hunliji.hljquestionanswer.adapters.AnswerPopupRule;
import com.hunliji.hljquestionanswer.adapters.QuestionDetailAdapter;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.PostPraiseIdBody;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/9/22
 * 问题详情
 */

public class QuestionDetailFragment extends RefreshFragment implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView>, QuestionDetailAdapter
        .OnPraiseClickListener {


    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.bottom_layout)
    RelativeLayout bottomLayout;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.tv_add_answer)
    TextView tvAddAnswer;
    @BindView(R2.id.merchant_bottom)
    RelativeLayout merchantBottom;
    @BindView(R2.id.tv_merchant_follow)
    TextView tvMerchantFollow;
    @BindView(R2.id.tv_merchant_add_answer)
    TextView tvMerchantAddAnswer;
    @BindView(R2.id.create_answer_view)
    LinearLayout createAnswerView;
    @BindView(R2.id.follow_question_view)
    LinearLayout followQuestionView;
    @BindView(R2.id.icon_follow)
    ImageView iconFollow;

    private long questionId;
    private QuestionDetailAdapter adapter;
    private ArrayList<Answer> answers;
    private Question question;
    private Realm realm;
    private User user;
    private View endView;
    private View loadView;
    private HljHttpSubscriber praiseSubscriber;//点赞，反对
    private HljHttpSubscriber followSubscriber;//关注
    private HljHttpSubscriber pageSubscriber;//分页
    private HljHttpSubscriber refreshSubscriber;//刷新
    private Subscription rxBusEventSub;
    private final int ANSWER_CREATE = 2;
    private Unbinder unBinder;


    public static QuestionDetailFragment newInstance(
            long questionId, Question question) {
        Bundle args = new Bundle();
        QuestionDetailFragment fragment = new QuestionDetailFragment();
        args.putLong("questionId", questionId);
        args.putParcelable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initValues();
        registerRxBusEvent();
    }

    private void initValues() {
        user = UserSession.getInstance()
                .getUser(getContext());
        Bundle bundle = getArguments();
        if (bundle != null) {
            questionId = bundle.getLong("questionId", 0);
            question = bundle.getParcelable("question");
        }
        answers = new ArrayList<>();
    }

    //编辑完成后刷新
    @Override
    public void refresh(Object... params) {
        Object object = params[0];
        if (object instanceof Question) {
            this.question = (Question) object;
            onRefresh(recyclerView);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_detail___qa, container, false);
        unBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        onRefresh(recyclerView);
    }

    private void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new QuestionDetailAdapter(getContext(), questionId);
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter.setFooterView(footerView);
        adapter.setOnPraiseClickListener(this);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        /**
         * 区分商家端与用户端底部视图
         */
        if (HljQuestionAnswer.isMerchant(getContext())) {
            merchantBottom.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
        } else {
            merchantBottom.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
        }
    }

    public void startBottomAnimation(Animation animation) {
        if (bottomLayout != null) {
            bottomLayout.startAnimation(animation);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSubscriber);
        Observable<HljHttpCountData<List<Answer>>> observable = QuestionAnswerApi.getAnswerList(
                questionId,
                1,
                QuestionAnswerApi.TYPE_COMMUNITY_QA);
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpCountData<List<Answer>>>() {
                    @Override
                    public void onNext(HljHttpCountData<List<Answer>> answersData) {
                        answers.clear();
                        if (answersData != null && !CommonUtil.isCollectionEmpty(answersData
                                .getData())) {
                            initPageAnswers(answersData.getPageCount());
                            answers.addAll(answersData.getData());
                            adapter.setTopCount(answersData.getTopCount());
                            adapter.setTotalCount(answersData.getTotalCount());
                        }
                        adapter.setQuestion(question);
                        adapter.setData(answers);
                        adapter.notifyDataSetChanged();
                        setQuestionView();
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
                        Log.d("pagination tool", "on load: " + page);
                        return QuestionAnswerApi.getAnswerList(questionId,
                                page,
                                QuestionAnswerApi.TYPE_COMMUNITY_QA);
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
                        adapter.setData(answers);
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private void setQuestionView() {
        if (question == null) {
            return;
        }
        iconFollow.setVisibility(question.isFollow() ? View.GONE : View.VISIBLE);
        tvMerchantFollow.setText(question.isFollow() ? getString(R.string.label_followed___cm) :
                getString(
                R.string.label_follow___cm));
        if (question.getAnswerId() == 0) {
            if (questionId > 0) {
                long count = 0;
                if (user != null && user.getId() != 0) {
                    realm = Realm.getDefaultInstance();
                    count = realm.where(PostAnswerBody.class)
                            .equalTo("userId", user.getId())
                            .equalTo("questionId", questionId)
                            .count();
                }
                if (count > 0) {
                    //当用户已保存该问题下的回答草稿时，按钮内容变为“添加回答（草稿）”
                    tvAddAnswer.setText(R.string.action_add_answer_draft___qa);
                    tvMerchantAddAnswer.setText(R.string.action_add_answer_draft___qa);
                } else {
                    //当用户未回答该问题时，点击添加回答按钮
                    tvAddAnswer.setText(R.string.action_add_answer___qa);
                    tvMerchantAddAnswer.setText(R.string.action_add_answer___qa);
                }
                bottomLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Activity activity = (Activity) view.getContext();
                        if (!AuthUtil.loginBindCheck(getContext())) {
                            return;
                        }
                        Intent intent = new Intent(getContext(), CreateAnswerActivity.class);
                        intent.putExtra("questionId", questionId);
                        startActivityForResult(intent, ANSWER_CREATE);
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                createAnswerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Activity activity = (Activity) view.getContext();
                        MerchantUser merchantUser = (MerchantUser) UserSession.getInstance()
                                .getUser(getContext());
                        if (merchantUser == null) {
                            return;
                        }
                        if (merchantUser.getExamine() != 1 || merchantUser.getCertifyStatus() !=
                                3) {
                            AnswerPopupRule.getDefault()
                                    .showHintDialog(activity);
                        } else if (!merchantUser.isPro()) {
                            AnswerPopupRule.getDefault()
                                    .showProDialog(activity);
                        } else {
                            Intent intent = new Intent(activity, CreateAnswerActivity.class);
                            intent.putExtra("questionId", questionId);
                            startActivityForResult(intent, ANSWER_CREATE);
                            activity.overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    }
                });
            }
        } else {
            //当用户已回答该问题时，按钮内容变为“我的回答”
            tvAddAnswer.setText(R.string.title_activity_my_qa___qa);
            tvMerchantAddAnswer.setText(R.string.title_activity_my_qa___qa);
            bottomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (AuthUtil.loginBindCheck(getContext())) {
                        Intent intent = new Intent();
                        intent.setClass(getContext(), AnswerDetailActivity.class);
                        intent.putExtra("answerId", question.getAnswerId());
                        startActivity(intent);
                        Activity activity = (Activity) view.getContext();
                        activity.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                }
            });
            createAnswerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), AnswerDetailActivity.class);
                    intent.putExtra("answerId", question.getAnswerId());
                    startActivity(intent);
                    Activity activity = (Activity) view.getContext();
                    activity.overridePendingTransition(R.anim.slide_in_right,
                            R.anim.activity_anim_default);
                }
            });
        }
    }


    @OnClick(R2.id.follow_question_view)
    public void onClick(View view) {
        Activity activity = (Activity) view.getContext();
        if (question == null) {
            return;
        }
        MerchantUser merchantUser = (MerchantUser) UserSession.getInstance()
                .getUser(getContext());
        if (merchantUser == null) {
            return;
        }
        if (merchantUser.getExamine() != 1 || merchantUser.getCertifyStatus() != 3) {
            AnswerPopupRule.getDefault()
                    .showHintDialog(activity);
        } else if (!merchantUser.isPro()) {
            AnswerPopupRule.getDefault()
                    .showProDialog(activity);
        } else {
            onFollowQuestion();
        }
    }


    private void onFollowQuestion() {
        if (question == null) {
            return;
        }
        if (followSubscriber == null || followSubscriber.isUnsubscribed()) {
            PostCollectBody body = new PostCollectBody();
            body.setId(question.getId());
            body.setFollowableType("QaQuestion");
            followQuestionView.setClickable(false);
            setFollowView();
            followSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            ToastUtil.showCustomToast(getContext(),
                                    question.isFollow() ? R.string.msg_success_to_follow___cm : R
                                            .string.msg_success_to_un_follow___cm);
                            followQuestionView.setClickable(true);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            ToastUtil.showCustomToast(getContext(),
                                    question.isFollow() ? R.string.msg_fail_to_follow___cm : R
                                            .string.msg_fail_to_cancel_follow___cm);
                            // 失败的话变回原样
                            setFollowView();
                            followQuestionView.setClickable(true);
                        }
                    })
                    .build();
            CommonApi.postCollectObb(body, !question.isFollow())
                    .subscribe(followSubscriber);
        }
    }

    private void setFollowView() {
        // 先变化,再进行网络请求
        if (question.isFollow()) {
            // 已关注,变为未关注
            question.setFollow(false);
            iconFollow.setVisibility(View.VISIBLE);
            tvMerchantFollow.setText(getString(R.string.label_follow___cm));
        } else {
            // 没有关注,变为关注
            question.setFollow(true);
            iconFollow.setVisibility(View.GONE);
            tvMerchantFollow.setText(getString(R.string.label_followed___cm));
        }
    }

    @Override
    public void onPraiseClickListener(
            final boolean isPraise,
            final CheckableLinearLayout praiseView,
            final CheckableLinearLayout opposeView,
            final TextView upCount,
            final Answer item) {
        boolean isCancel = false;//是否先取消，再操作
        if (AuthUtil.loginBindCheck(getContext())) {
            // 先变化,再进行网络请求
            switch (item.getLikeType()) {
                case QuestionDetailAdapter.ANSWER_PRAISED:
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
                case QuestionDetailAdapter.ANSWER_OPPOSED:
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
                case QuestionDetailAdapter.ANSWER_CANCEL:
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
            setPraiseView(item.getLikeType(), praiseView, opposeView, upCount);
            upCount.setText(item.getUpCount() <= 0 ? "赞同" : String.valueOf(item.getUpCount()));

            PostPraiseIdBody body = new PostPraiseIdBody();
            body.setId(item.getId());
            body.setValue(item.getLikeType());
            Observable observable = QuestionAnswerApi.postPraiseAnswerObb(body);
            final boolean finalIsCancel = isCancel;
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
                                case QuestionDetailAdapter.ANSWER_PRAISED:
                                    ToastUtil.showCustomToast(getContext(),
                                            R.string.msg_success_to_praise___cm);
                                    break;
                                case QuestionDetailAdapter.ANSWER_OPPOSED:
                                    ToastUtil.showCustomToast(getContext(),
                                            R.string.msg_success_to_oppose___cm);
                                    break;
                                case QuestionDetailAdapter.ANSWER_CANCEL:
                                    if (isPraise) {
                                        ToastUtil.showCustomToast(getContext(),
                                                R.string.msg_success_to_un_praise___cm);
                                    } else {
                                        ToastUtil.showCustomToast(getContext(),
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
                                case QuestionDetailAdapter.ANSWER_PRAISED:
                                    if (!finalIsCancel) {
                                        item.setLikeType(0);
                                    } else {
                                        item.setLikeType(-1);
                                    }
                                    item.setUpCount(item.getUpCount() - 1);
                                    break;
                                case QuestionDetailAdapter.ANSWER_OPPOSED:
                                    if (finalIsCancel) {
                                        item.setLikeType(1);
                                        item.setUpCount(item.getUpCount() + 1);
                                    } else {
                                        item.setLikeType(0);
                                    }
                                    break;
                                case QuestionDetailAdapter.ANSWER_CANCEL:
                                    if (isPraise) {
                                        item.setLikeType(1);
                                        item.setUpCount(item.getUpCount() + 1);
                                    } else {
                                        item.setLikeType(-1);
                                    }
                                    break;
                            }
                            setPraiseView(item.getLikeType(), praiseView, opposeView, upCount);
                            upCount.setText(item.getUpCount() <= 0 ? "赞同" : String.valueOf(item
                                    .getUpCount()));
                        }
                    })
                    .build();
            observable.subscribe(praiseSubscriber);
        }
    }

    private void setPraiseView(
            int likeType,
            CheckableLinearLayout praiseView,
            CheckableLinearLayout opposeView,
            TextView upCount) {
        switch (likeType) {
            //未点赞 未反对
            case QuestionDetailAdapter.ANSWER_CANCEL:
                praiseView.setChecked(false);
                opposeView.setChecked(false);
                break;
            //已点赞
            case QuestionDetailAdapter.ANSWER_PRAISED:
                praiseView.setChecked(true);
                opposeView.setChecked(false);
                break;
            //已反对
            case QuestionDetailAdapter.ANSWER_OPPOSED:
                praiseView.setChecked(false);
                opposeView.setChecked(true);
                break;
        }
    }


    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ANSWER_CREATE:
                    if (data != null) {
                        long answerId = data.getLongExtra("answerId", 0);
                        if (answerId != 0) {
                            question.setAnswerId(answerId);
                            onRefresh(recyclerView);
                        } else {
                            setQuestionView();
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unBinder != null) {
            unBinder.unbind();
        }
        CommonUtil.unSubscribeSubs(pageSubscriber,
                refreshSubscriber,
                praiseSubscriber,
                followSubscriber,
                rxBusEventSub);
        if (realm != null) {
            realm.close();
        }
    }


    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case ANSWER_PRAISE:
                                    onRefresh(recyclerView);
                                    break;
                            }
                        }
                    });
        }
    }


}
