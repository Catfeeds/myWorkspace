package me.suncloud.marrymemo.fragment.finder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.view_tracker.HljVTTagger;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.HljHttp;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpResultFunc;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.FinderCaseRecyclerAdapter;
import me.suncloud.marrymemo.adpter.finder.viewholder.BaseFinderCaseViewHolder;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.api.finder.FinderService;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.model.finder.FinderFeed;
import me.suncloud.marrymemo.util.CaseTogglesUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.finder.FinderCaseMediaPagerActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2018/2/7. 发现页-案例-案例列表
 */

public class FinderCaseListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener, BaseFinderCaseViewHolder.OnCollectCaseListener,
        BaseFinderCaseViewHolder.OnSearchSamesListener, FinderCaseRecyclerAdapter
                .OnCaseImageClickListener {

    public static final String FIND_CASE_LIST = "find_case_list";
    public static final String ARG_PROPERTY_IDS = "property_ids";
    public static final int RESULT_CASE_MEDIA = 1;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;

    private City city;
    private String ids;
    private List<Work> cases;
    private Work currentWork;
    private View endView;
    private View loadView;
    private FinderCaseRecyclerAdapter adapter;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private HljHttpSubscriber sameSubscriber;

    public static FinderCaseListFragment newInstance(String ids) {
        Bundle args = new Bundle();
        FinderCaseListFragment fragment = new FinderCaseListFragment();
        args.putString(ARG_PROPERTY_IDS, ids);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cases = new ArrayList<>();
        city = Session.getInstance()
                .getMyCity(getContext());
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___qa, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new FinderCaseRecyclerAdapter(getContext());
        adapter.setFooterView(footerView);
        adapter.setCases(cases);
        adapter.setOnCollectCaseListener(this);
        adapter.setOnSearchSamesListener(this);
        adapter.setOnCaseImageClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hlj_common_fragment_ptr_recycler_view___cm,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            ids = getArguments().getString(ARG_PROPERTY_IDS);
        }
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                onRefresh(recyclerView);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTracker();
        onRefresh(recyclerView);
    }

    private void initTracker() {
        String parentTagName = "find_case_list?propertyIds=" + ids;
        HljVTTagger.tagViewParentName(recyclerView, parentTagName);
        HljVTTagger.buildTagger(recyclerView)
                .tagName(parentTagName)
                .tag();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                            CommonUtil.unSubscribeSubs(sameSubscriber);
                            cases.clear();
                            cases.addAll(listHljHttpData.getData());
                            adapter.notifyDataSetChanged();
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            getFinderCasesObb(ids, 1).subscribe(refreshSubscriber);
        }
    }


    private Observable<HljHttpData<List<Work>>> getFinderCasesObb(
            String ids, final int page) {
        return FinderApi.getFinderCasesObb(ids, page, HljCommon.PER_PAGE)
                .map(new Func1<HljHttpData<List<Work>>, HljHttpData<List<Work>>>() {
                    @Override
                    public HljHttpData<List<Work>> call(
                            HljHttpData<List<Work>> listHljHttpData) {
                        if (listHljHttpData != null) {
                            if (CommonUtil.isCollectionEmpty(listHljHttpData.getData())) {
                                listHljHttpData.setPageCount(page - 1);
                                if (page == 1) {
                                    listHljHttpData.setTotalCount(0);
                                }
                            }
                        }
                        return listHljHttpData;
                    }
                });
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        final Observable<HljHttpData<List<Work>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Work>>>() {
                    @Override
                    public Observable<HljHttpData<List<Work>>> onNextPage(final int page) {
                        return getFinderCasesObb(ids, page);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                    @Override
                    public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                        adapter.addCases(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    public void cityRefresh(City c) {
        if (city == null || city.getId()
                .equals(c.getId())) {
            return;
        }
        city = c;
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber, sameSubscriber);
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
        recyclerView.setRefreshing(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            cityRefresh(Session.getInstance()
                    .getMyCity(getContext()));
        }
    }

    @Override
    public void refresh(Object... params) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(0);
            recyclerView.setRefreshing(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(refreshSubscriber, sameSubscriber, pageSubscriber);
        CaseTogglesUtil.onDestroy();
    }

    @Override
    public void onCollectCase(
            int position,
            Object object,
            final ImageView imgCollectCase,
            final TextView tvCollectState) {
        if (!Util.loginBindChecked(getContext(), Constants.Login.LIKE_LOGIN)) {
            return;
        }
        final Work finderCase = (Work) object;
        if (finderCase == null || getContext() == null) {
            return;
        }
        if (!finderCase.isCollected()) {
            imgCollectCase.setImageResource(R.mipmap.icon_collect_primary_32_32_selected);
            tvCollectState.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        } else {
            imgCollectCase.setImageResource(R.mipmap.icon_collect_black2_32_32);
            tvCollectState.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack3));
        }
        CaseTogglesUtil.onCollectCase(getContext(),
                finderCase,
                new CaseTogglesUtil.onCollectCompleteListener() {
                    @Override
                    public void onCollectComplete(boolean isSuccess, String msg) {
                        if (!isSuccess) {
                            if (CommonUtil.isEmpty(msg)) {
                                ToastUtil.showToast(getContext(), "请稍后再试", 0);
                            } else {
                                ToastUtil.showToast(getContext(), msg, 0);
                            }
                            if (finderCase.isCollected()) {
                                imgCollectCase.setImageResource(R.mipmap
                                        .icon_collect_primary_32_32_selected);
                                tvCollectState.setTextColor(ContextCompat.getColor(getContext(),
                                        R.color.colorPrimary));
                            } else {
                                imgCollectCase.setImageResource(R.mipmap.icon_collect_black2_32_32);
                                tvCollectState.setTextColor(ContextCompat.getColor(getContext(),
                                        R.color.colorBlack3));
                            }
                        }
                    }
                });
    }

    @Override
    public void onSearchSames(
            final int position, Object object) {
        if (!CommonUtil.isUnsubscribed(sameSubscriber)) {
            return;
        }
        final Work finderCase = (Work) object;
        if (finderCase == null) {
            return;
        }
        sameSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<List<Work>>() {
                    @Override
                    public void onNext(List<Work> works) {
                        finderCase.setHideSearch(true);
                        int position = cases.indexOf(finderCase);
                        adapter.notifyItemChanged(position + adapter.getHeaderViewCount());
                        if (!CommonUtil.isCollectionEmpty(works)) {
                            adapter.addSamesCases(position, works);
                        } else {
                            ToastUtil.showToast(getContext(), null, R.string.hint_no_more_similar);
                        }
                    }
                })
                .setDataNullable(true)
                .build();
        FinderApi.getFinderCaseSamesObb(position,
                cases,
                finderCase.getId(),
                FinderFeed.TYPE_CASE,
                ids)
                .subscribe(sameSubscriber);
    }

    @Override
    public void onImageClick(
            Context context, Work work, long mediaId) {
        currentWork = work;
        Intent intent = new Intent(context, FinderCaseMediaPagerActivity.class);
        intent.putExtra(FinderCaseMediaPagerActivity.ARG_MEDIA_ID, mediaId);
        intent.putExtra(FinderCaseMediaPagerActivity.ARG_CASE, currentWork);
        intent.putExtra(FinderCaseMediaPagerActivity.ARG_ATTR_TYPE, ids);
        startActivityForResult(intent, RESULT_CASE_MEDIA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RESULT_CASE_MEDIA:
                    Work work = data.getParcelableExtra(FinderCaseMediaPagerActivity.ARG_CASE);
                    currentWork.setCollected(work.isCollected());
                    int position = cases.indexOf(currentWork);
                    adapter.notifyItemChanged(position);
                    break;
            }
        }
    }
}
