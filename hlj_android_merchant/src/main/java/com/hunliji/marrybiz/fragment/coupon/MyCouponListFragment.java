package com.hunliji.marrybiz.fragment.coupon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.coupon.MyCouponRecyclerAdapter;
import com.hunliji.marrybiz.api.coupon.CouponApi;
import com.hunliji.marrybiz.view.coupon.CreateCouponActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的优惠券列表页
 * Created by chen_bin on 2016/10/13 0013.
 */
public class MyCouponListFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, OnItemClickListener<CouponInfo>,
        MyCouponRecyclerAdapter.OnActivateCouponListener {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View footerView;
    private View endView;
    private View loadView;
    private MyCouponRecyclerAdapter adapter;
    private Unbinder unbinder;
    private String name;
    private boolean isSearch;
    private boolean isNeedRefresh;
    private int valid;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber activateSub;

    public static MyCouponListFragment newInstance(String name, boolean isSearch, int valid) {
        MyCouponListFragment fragment = new MyCouponListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putBoolean("is_search", isSearch);
        bundle.putInt("valid", valid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
            isSearch = getArguments().getBoolean("is_search");
            valid = getArguments().getInt("valid");
        }
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new MyCouponRecyclerAdapter(getContext());
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
        initViews();
        return rootView;
    }

    private void initViews() {
        if (!isSearch) {
            recyclerView.setOnRefreshListener(this);
            recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        } else {
            recyclerView.setNeedChangeSize(false);
            recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setFooterView(footerView);
        adapter.setOnItemClickListener(this);
        adapter.setOnActivateCouponListener(this);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isSearch) {
            onRefresh(null);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CouponInfo>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CouponInfo>> listHljHttpData) {
                        recyclerView.getRefreshableView()
                                .scrollToPosition(0);
                        adapter.setCoupons(listHljHttpData.getData());
                        initPagination(listHljHttpData.getPageCount());
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(recyclerView.isRefreshing() && progressBar.getVisibility() !=
                        View.VISIBLE ? null : progressBar)
                .build();
        CouponApi.getCouponListObb(name, valid, 1, HljCommon.PER_PAGE)
                .subscribe(refreshSub);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<CouponInfo>>> observable = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<CouponInfo>>>() {
                    @Override
                    public Observable<HljHttpData<List<CouponInfo>>> onNextPage(int page) {
                        return CouponApi.getCouponListObb(name, valid, page, HljCommon.PER_PAGE);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CouponInfo>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CouponInfo>> listHljHttpData) {
                        adapter.addCoupons(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void onItemClick(int position, CouponInfo couponInfo) {
        if (couponInfo != null && couponInfo.getId() > 0) {
            Intent intent = new Intent(getContext(), CreateCouponActivity.class);
            intent.putExtra("id", couponInfo.getId());
            intent.putExtra("position", position);
            intent.putExtra("couponInfo", couponInfo);
            startActivityForResult(intent, Constants.RequestCode.UPDATE_COUPON);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onActivateCoupon(final int position, final CouponInfo couponInfo) {
        if (couponInfo != null && couponInfo.getId() > 0) {
            DialogUtil.createDoubleButtonDialog(getContext(),
                    getString(couponInfo.isHidden() ? R.string.hint_deactivate_msg : R.string
                            .hint_activate_msg),
                    null,
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonUtil.unSubscribeSubs(activateSub);
                            activateSub = HljHttpSubscriber.buildSubscriber(getContext())
                                    .setOnNextListener(new SubscriberOnNextListener() {
                                        @Override
                                        public void onNext(Object o) {
                                            ToastUtil.showCustomToast(getContext(),
                                                    couponInfo.isHidden() ? R.string
                                                            .label_deactivate_success : R.string
                                                            .label_activate_success);
                                            couponInfo.setHidden(couponInfo.isHidden());
                                            adapter.notifyItemChanged(position);
                                        }
                                    })
                                    .setProgressDialog(DialogUtil.createProgressDialog(getContext
                                            ()))
                                    .build();
                            CouponApi.activateCouponObb(couponInfo.getId())
                                    .subscribe(activateSub);
                        }
                    },
                    null)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Constants.RequestCode.UPDATE_COUPON:
                    if (data == null) {
                        return;
                    }
                    int position = data.getIntExtra("position", -1);
                    CouponInfo couponInfo = data.getParcelableExtra("couponInfo");
                    if (position >= 0 && couponInfo != null && couponInfo.getId() > 0) {
                        adapter.getCoupons()
                                .set(position, couponInfo);
                        adapter.notifyItemChanged(position);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setNeedRefresh(boolean needRefresh) {
        this.isNeedRefresh = needRefresh;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isNeedRefresh) {
            isNeedRefresh = false;
            refresh();
        }
    }

    @Override
    public void refresh(Object... params) {
        if (recyclerView == null) {
            return;
        }
        if (params != null && params.length > 0) {
            name = (String) params[0];
        }
        onRefresh(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub, pageSub, activateSub);
    }
}
