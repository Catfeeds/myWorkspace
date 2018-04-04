package com.hunliji.hljquestionanswer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.VTMetaData;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.AuthUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.adapters.AskQuestionAdapter;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;
import com.hunliji.hljquestionanswer.models.QARxEvent;
import com.hunliji.hljquestionanswer.models.wrappers.PostQuestionBody;
import com.hunliji.hljquestionanswer.models.wrappers.PostQuestionResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/9/22
 * 用户 问大家
 */
@Route(path = RouterPath.IntentPath.QuestionAnswer.ASK_QUESTION_LIST)
public class AskQuestionListActivity extends HljBaseNoBarActivity implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener<RecyclerView>,
        OnItemClickListener<Question> {

    @Override
    public String pageTrackTagName() {
        return "问大家";
    }

    @Override
    public VTMetaData pageTrackData() {
        merchantId = getIntent().getLongExtra(ARG_MERCHANT_ID, 0);
        return new VTMetaData(merchantId,"Merchant");
    }

    @BindView(R2.id.tv_question_title)
    TextView tvQuestionTitle;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.et_content)
    EditText etContent;

    private View footerView;
    private View endView;
    private View loadView;
    private AskQuestionAdapter adapter;
    private List<Question> questions;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber postSubscriber;
    private Subscription rxBus;
    private long merchantId;
    private PostQuestionBody body;

    public static final String ARG_MERCHANT_ID = "merchant_id";
    public static final String ARG_WORK_ID = "work_id";
    public static final String ARG_SHOW_KEYBOARD = "is_show_key_board";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question_list___qa);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValue();
        initFooterView();
        initNetError();
        initView();
        onRefresh(recyclerView);
        registerRxBus();
    }

    private void initValue() {
        body = new PostQuestionBody();
        questions = new ArrayList<>();
        merchantId = getIntent().getLongExtra(ARG_MERCHANT_ID, 0);
        long setMealId = getIntent().getLongExtra(ARG_WORK_ID, 0);
        body.setSetMealId(setMealId > 0 ? setMealId : null);
        body.setMerchantId(merchantId);
        boolean showKeyBoard = getIntent().getBooleanExtra(ARG_SHOW_KEYBOARD, false);
        if (!showKeyBoard) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    private void initFooterView() {
        footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
    }

    private void initNetError() {
        emptyView.setHintId(R.string.hint_answer_content_empty___qa);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    @OnClick(R2.id.tv_ask_question)
    void onAskQuestion() {
        if (!AuthUtil.loginBindCheck(this)) {
            return;
        }
        if (etContent.length() < 5) {
            ToastUtil.showToast(this, "好问题不能少于5个字哦~", 0);
            return;
        }
        body.setTitle(etContent.getText()
                .toString()
                .trim());
        Observable<PostQuestionResult> observable = QuestionAnswerApi.postQuestionObb(body, 2);
        postSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<PostQuestionResult>() {
                    @Override
                    public void onNext(final PostQuestionResult result) {
                        //商家问答 提问完成
                        RxBus.getDefault()
                                .post(new QARxEvent(QARxEvent.RxEventType.ASK_QUESTION_SUCCESS,
                                        null));
                        if (result != null) {
                            if (result.isExist()) {
                                // 已有相同问题
                                ToastUtil.showToast(AskQuestionListActivity.this,
                                        null,
                                        R.string.msg_question_is_exist___qa);
                            } else {
                                ToastUtil.showCustomToast(AskQuestionListActivity.this,
                                        R.string.msg_to_experience_the_service_of_people_ask___qa);
                            }
                            hideKeyboard(null);
                            onRefresh(recyclerView);
                            etContent.setText(null);
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        observable.subscribe(postSubscriber);
    }

    private void initView() {
        tvQuestionTitle.setText(R.string.label_activity_ask_question___qa);
        adapter = new AskQuestionAdapter(this, questions);
        recyclerView.setOnRefreshListener(this);
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        RecyclerView mRecyclerView = recyclerView.getRefreshableView();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideKeyboard(null);
                return false;
            }
        });
    }

    @OnClick(R2.id.action_back)
    void actionBack() {
        super.onBackPressed();
    }

    private void registerRxBus() {
        if (rxBus == null || rxBus.isUnsubscribed()) {
            rxBus = RxBus.getDefault()
                    .toObservable(QARxEvent.class)
                    .subscribe(new RxBusSubscriber<QARxEvent>() {
                        @Override
                        protected void onEvent(QARxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case QUESTION_REPLY_SUCCESS:
                                    onRefresh(recyclerView);
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSubscriber);
        Observable<HljHttpQuestion<List<Question>>> observable = QuestionAnswerApi.getQAList(
                merchantId,
                1,
                HljCommon.PER_PAGE);
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpQuestion<List<Question>>>() {
                    @Override
                    public void onNext(HljHttpQuestion<List<Question>> hljHttpQuestion) {
                        initPage(hljHttpQuestion.getPageCount());
                        questions.clear();
                        questions.addAll(hljHttpQuestion.getData());
                        adapter.setTotalCount(hljHttpQuestion.getTotalCount());
                        adapter.setMerchant(hljHttpQuestion.getMerchant());
                        adapter.setQuestions(questions);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .build();
        observable.subscribe(refreshSubscriber);
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpQuestion<List<Question>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpQuestion<List<Question>>>() {
                    @Override
                    public Observable<HljHttpQuestion<List<Question>>> onNextPage(int page) {
                        return QuestionAnswerApi.getQAList(merchantId, page, HljCommon.PER_PAGE);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpQuestion<List<Question>>>() {

                    @Override
                    public void onNext(HljHttpQuestion<List<Question>> hljHttpQuestion) {
                        questions.addAll(hljHttpQuestion.getData());
                        adapter.setQuestions(questions);
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void onItemClick(int position, Question question) {
        if (question != null) {
            Intent intent = new Intent(this, QuestionDetailActivity.class);
            intent.putExtra("questionId", question.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, rxBus, postSubscriber);
    }

}
