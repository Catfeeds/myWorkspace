package com.hunliji.hljquestionanswer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.questionanswer.Answer;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
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
 * Created by Suncloud on 2016/8/30.问答标签
 */
public class MarkAnswersFragment extends ScrollAbleFragment {

    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private ArrayList<Answer> answers;
    private MarkAnswerAdapter adapter;
    private View endView;
    private View loadView;
    private View footerView;

    private Subscriber refreshSubscriber;
    private Subscriber pageSubscriber;
    private long markId;
    private Unbinder unbinder;
    private LinearLayoutManager layoutManager;
    private boolean isRefresh;

    public static MarkAnswersFragment newInstance(long markId) {
        Bundle bundle = new Bundle();
        bundle.putLong("markId", markId);
        MarkAnswersFragment fragment = new MarkAnswersFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answers = new ArrayList<>();
        adapter = new MarkAnswerAdapter(getContext(), answers);
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
        adapter.setFooterView(footerView);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            markId = getArguments().getLong("markId", 0);
        }
        if (answers.isEmpty()) {
            refresh();
        }
    }

    @Override
    public void refresh(Object... params) {
        if (params != null && params.length > 0) {
            isRefresh = (boolean) params[0];
        }
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Answer>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Answer>> listHljHttpData) {
                            answers.clear();
                            answers.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setProgressBar(isRefresh ? null : progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView.getRefreshableView())
                    .build();

            QuestionAnswerApi.getMarkAnswersObb(markId, 1)
                    .subscribe(refreshSubscriber);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Answer>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Answer>>>() {
                    @Override
                    public Observable<HljHttpData<List<Answer>>> onNextPage(int page) {
                        return QuestionAnswerApi.getMarkAnswersObb(markId, page);
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
    public View getScrollableView() {
        return recyclerView.getRefreshableView();
    }

    @Override
    public void onDestroy() {
        if (refreshSubscriber != null && !refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber.unsubscribe();
        }
        if (pageSubscriber != null && !pageSubscriber.isUnsubscribed()) {
            pageSubscriber.unsubscribe();
        }
        unbinder.unbind();
        super.onDestroy();
    }
}
