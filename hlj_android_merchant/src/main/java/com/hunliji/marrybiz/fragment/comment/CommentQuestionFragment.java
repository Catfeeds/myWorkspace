package com.hunliji.marrybiz.fragment.comment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.questionanswer.Question;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;
import com.hunliji.hljquestionanswer.models.HljHttpQuestion;
import com.hunliji.hljquestionanswer.models.QARxEvent;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.comment.CommentQuestionAdapter;
import com.hunliji.marrybiz.util.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hua_rong on 2017/9/21
 * 问大家
 */

public class CommentQuestionFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private View footerView;
    private TextView endView;
    private View loadView;
    private CommentQuestionAdapter adapter;
    private List<Question> questions;
    private long merchantId;
    private Subscription rxBus;

    public static CommentQuestionFragment newInstance() {
        Bundle args = new Bundle();
        CommentQuestionFragment fragment = new CommentQuestionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFooter();
        initValue();
        registerRxBus();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initNetError();
        onRefresh(recyclerView);
    }

    private void initNetError() {
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_message);
        emptyView.setHintId(R.string.hint_no_related_questions);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
    }

    private void initValue() {
        questions = new ArrayList<>();
        merchantId = Session.getInstance()
                .getCurrentUser(getContext())
                .getMerchantId();
    }

    private void initView() {
        RecyclerView mRecyclerView = recyclerView.getRefreshableView();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOnRefreshListener(this);
        adapter = new CommentQuestionAdapter(getContext(), questions);
        adapter.setFooterView(footerView);
        mRecyclerView.setAdapter(adapter);
    }

    private void initFooter() {
        footerView = View.inflate(getContext(), R.layout.list_foot_no_more_2, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
    }

    @Override
    public void refresh(Object... params) {

    }

    /**
     * 当回答成功后 刷新当前条目的评论数量
     */
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
                20);
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpQuestion<List<Question>>>() {
                    @Override
                    public void onNext(HljHttpQuestion<List<Question>> hljHttpQuestion) {
                        initPage(hljHttpQuestion.getPageCount());
                        questions.clear();
                        questions.addAll(hljHttpQuestion.getData());
                        adapter.setQuestions(questions);
                        adapter.setSize(hljHttpQuestion.getTotalCount());
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
                        return QuestionAnswerApi.getQAList(merchantId, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
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
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, rxBus);
    }
}
