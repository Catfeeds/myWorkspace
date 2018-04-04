package com.hunliji.hljquestionanswer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljquestionanswer.R;
import com.hunliji.hljquestionanswer.R2;
import com.hunliji.hljquestionanswer.adapters.MarkAnswerAdapter;
import com.hunliji.hljquestionanswer.api.QuestionAnswerApi;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Suncloud on 2016/8/29.
 */
public class MyAnswersFragment extends ScrollAbleFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;

    private Subscriber refreshSubscriber;
    private Subscriber pageSubscriber;

    private ArrayList<Answer> answers;
    private MarkAnswerAdapter adapter;
    private View endView;
    private View loadView;
    private View footerView;
    private boolean isForHomePage;

    private Unbinder unbinder;
    private long userId;
    private LinearLayoutManager layoutManager;

    public static MyAnswersFragment newInstance() {
        Bundle args = new Bundle();
        MyAnswersFragment fragment = new MyAnswersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            isForHomePage = getArguments().getBoolean("is_for_user_profile", false);
            userId = getArguments().getLong("user_id", 0);
        }
        if (userId == 0) {
            userId = UserSession.getInstance()
                    .getUser(getActivity())
                    .getId();
        }

        answers = new ArrayList<>();
        adapter = new MarkAnswerAdapter(getContext(), answers);
        adapter.setDividerAtTop(isForHomePage);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        setEmptyView();

        adapter.setFooterView(footerView);
        recyclerView.setOnRefreshListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        if (isForHomePage) {
            recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        return rootView;
    }

    private void setEmptyView() {
        User user = UserSession.getInstance()
                .getUser(getContext());
        if (user != null && user.getId() == userId) {
            // 是自己的主页
            emptyView.setHintId(R.string.label_my_answer_empty___qa);
            emptyView.setHint2Id(R.string.label_my_answer_empty_hint___qa);
        } else {
            // 别人的主页
            emptyView.setHintId(R.string.label_other_answer_empty___qa);
            emptyView.setHint2Id(0);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CommonUtil.isCollectionEmpty(answers)) {
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSubscriber);
        refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Answer>> listHljHttpData) {
                        answers.clear();
                        answers.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                        initPagination(listHljHttpData.getPageCount());
                        if (onTabTextChangeListener != null) {
                            onTabTextChangeListener.onTabTextChange(listHljHttpData.getTotalCount
                                    ());
                        }
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                .build();

        QuestionAnswerApi.getUserAnswersObb(userId, 1)
                .subscribe(refreshSubscriber);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Answer>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public Observable<HljHttpData<List<Answer>>> onNextPage(int page) {
                        return QuestionAnswerApi.getUserAnswersObb(userId, page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();

        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Answer>> listHljHttpData) {
                        answers.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    @Override
    public void refresh(Object... params) {
        if (params != null) {
            userId = (long) params[0];
        }
        setEmptyView();
        answers.clear();
        adapter.notifyDataSetChanged();
        onRefresh(recyclerView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (recyclerView != null) {
            recyclerView.getRefreshableView()
                    .setAdapter(null);
        }
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }
}
