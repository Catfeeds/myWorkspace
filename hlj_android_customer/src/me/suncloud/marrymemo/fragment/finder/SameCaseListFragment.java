package me.suncloud.marrymemo.fragment.finder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.finder.SameCaseRecyclerAdapter;
import me.suncloud.marrymemo.api.finder.FinderApi;
import me.suncloud.marrymemo.model.finder.FinderFeed;
import me.suncloud.marrymemo.util.CaseTogglesUtil;
import me.suncloud.marrymemo.util.Util;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2018/2/7.案例预览-同主题案例列表
 */

public class SameCaseListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener, SameCaseRecyclerAdapter.OnCollectCaseListener {

    public static final String ARG_CASE_ID = "case_id";
    public static final String ARG_CASE_TYPE = "case_type";
    public static final String ARG_ATTR_TYPE = "attr_type";
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    Unbinder unbinder;

    private long type;
    private long id;
    private String attrType;
    private List<Work> cases;
    private View endView;
    private View loadView;
    private SameCaseRecyclerAdapter adapter;
    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;

    public static SameCaseListFragment newInstance(long id, int type, String attrType) {
        Bundle args = new Bundle();
        SameCaseListFragment fragment = new SameCaseListFragment();
        args.putLong(ARG_CASE_ID, id);
        args.putInt(ARG_CASE_TYPE, type);
        args.putString(ARG_ATTR_TYPE, attrType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cases = new ArrayList<>();
        View footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new SameCaseRecyclerAdapter(getContext());
        adapter.setFooterView(footerView);
        adapter.setCases(cases);
        adapter.setOnCollectCaseListener(this);
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
            id = getArguments().getLong(ARG_CASE_ID);
            type = getArguments().getInt(ARG_CASE_TYPE, SameCasesHomeFragment.TAB_SAME);
            attrType = getArguments().getString(ARG_ATTR_TYPE);
        }
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(recyclerView);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(recyclerView);
    }

    @Override
    public void onRefresh(final PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<Work>>>() {
                        @Override
                        public void onNext(HljHttpData<List<Work>> listHljHttpData) {
                            cases.clear();
                            //找相似不做分页，固定取30条
                            if (type == SameCasesHomeFragment.TAB_SAME) {
                                if (CommonUtil.isCollectionEmpty(listHljHttpData.getData())) {
                                    emptyView.showEmptyView();
                                    recyclerView.setVisibility(View.GONE);
                                    if (getParentFragment() instanceof SameCasesHomeFragment) {
                                        SameCasesHomeFragment fragment = (SameCasesHomeFragment)
                                                getParentFragment();
                                        fragment.onTabChanged(SameCasesHomeFragment.TAB_HOT);
                                    }
                                } else {
                                    emptyView.hideEmptyView();
                                    recyclerView.setVisibility(View.VISIBLE);
                                    cases.addAll(listHljHttpData.getData());
                                }
                            } else {
                                cases.addAll(listHljHttpData.getData());
                            }
                            initPagination(listHljHttpData.getPageCount());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setDataNullable(type == SameCasesHomeFragment.TAB_SAME)
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            if (type == SameCasesHomeFragment.TAB_SAME) {
                FinderApi.getSameCasesObb(id, FinderFeed.TYPE_CASE, 30, attrType)
                        .subscribe(refreshSubscriber);
            } else {
                FinderApi.getHotCasesObb(id, "case", "like", 1, 10)
                        .subscribe(refreshSubscriber);
            }

        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<Work>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<Work>>>() {
                    @Override
                    public Observable<HljHttpData<List<Work>>> onNextPage(int page) {
                        return FinderApi.getHotCasesObb(id, "case", "like", page, 10);
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

    public void cityRefresh() {
        refresh();
    }

    @Override
    public void refresh(Object... params) {
        CommonUtil.unSubscribeSubs(refreshSubscriber, pageSubscriber);
        onRefresh(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

}
