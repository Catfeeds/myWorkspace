package me.suncloud.marrymemo.fragment.product;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.HomePageScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.CommonApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.home.HomeFeedsFragmentCallBack;
import me.suncloud.marrymemo.adpter.product.SelectedProductRecyclerAdapter;
import me.suncloud.marrymemo.view.MainActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 婚品列表
 * Created by chen_bin on 2017/5/2 0002.
 */
public class SelectedProductListFragment extends HomePageScrollAbleFragment implements
        PullToRefreshBase.OnRefreshListener<RecyclerView> {
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;
    protected View footerView;
    protected View endView;
    protected View loadView;
    protected SelectedProductRecyclerAdapter adapter;
    private String url;
    private boolean isShowTopBtn; //回到顶部的按钮是否显示着
    private Unbinder unbinder;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    protected String propertyId;//HomeSelectedProductListFragment使用到的字段

    public static SelectedProductListFragment newInstance(String url) {
        SelectedProductListFragment fragment = new SelectedProductListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString("url");
            propertyId = getArguments().getString("propertyId");
        }
        footerView = View.inflate(getContext(), R.layout.hlj_product_no_more_footer___cv, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new SelectedProductRecyclerAdapter(getContext());
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
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setMode(PullToRefreshBase.Mode.DISABLED);
        recyclerView.getRefreshableView()
                .setPadding(0,
                        0,
                        0,
                        getContext() instanceof MainActivity ? CommonUtil.dp2px(getContext(),
                                50) : 0);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.getRefreshableView()
                .addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (recyclerView == null || recyclerView.getLayoutManager() == null) {
                            return;
                        }
                        onRecyclerViewScrolled(recyclerView, dx, dy);
                    }
                });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ShopProduct>>>() {
                        @Override
                        public void onNext(HljHttpData<List<ShopProduct>> listHljHttpData) {
                            recyclerView.getRefreshableView()
                                    .scrollToPosition(0);
                            recyclerView.getRefreshableView()
                                    .setBackgroundColor(ContextCompat.getColor(getContext(),
                                            R.color.colorWhite));
                            adapter.setProducts(listHljHttpData.getData());
                            initPagination(listHljHttpData.getPageCount());
                            onSubNext(listHljHttpData);
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            onSubError(o);
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            CommonApi.getProductsObb(url, 1, 20)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<ShopProduct>>> pageObservable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<ShopProduct>>>() {
                    @Override
                    public Observable<HljHttpData<List<ShopProduct>>> onNextPage(
                            int page) {
                        return CommonApi.getProductsObb(url, page, 20);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ShopProduct>>>() {
                    @Override
                    public void onNext(HljHttpData<List<ShopProduct>> listHljHttpData) {
                        adapter.addProducts(listHljHttpData.getData());
                    }
                })
                .build();
        pageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @OnClick(R.id.btn_scroll_top)
    public void onScrollTop() {
        recyclerView.getRefreshableView()
                .scrollToPosition(0);
    }

    //显示回到顶部的按钮
    private void showFiltrateAnimation() {
        if (isShowTopBtn) {
            return;
        }
        isShowTopBtn = true;
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_in_bottom2);
            animation.setFillBefore(true);
            btnScrollTop.startAnimation(animation);
            btnScrollTop.setVisibility(View.VISIBLE);
        }
    }

    //隐藏回到顶部的按钮
    private void hideFiltrateAnimation() {
        if (!isShowTopBtn) {
            return;
        }
        isShowTopBtn = false;
        if (btnScrollTop.getAnimation() == null || btnScrollTop.getAnimation()
                .hasEnded()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    R.anim.slide_out_bottom2);
            animation.setFillAfter(true);
            btnScrollTop.startAnimation(animation);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public View getScrollableView() {
        return recyclerView == null ? null : recyclerView.getRefreshableView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }

    protected HomeFeedsFragmentCallBack getHomeFeedsFragmentCallBack() {
        if (getParentFragment() != null && getParentFragment() instanceof
                HomeFeedsFragmentCallBack) {
            return (HomeFeedsFragmentCallBack) getParentFragment();
        } else {
            return null;
        }
    }

    protected void onRecyclerViewScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0)) < 3) {
            hideFiltrateAnimation();
        } else {
            showFiltrateAnimation();
        }
    }

    protected void onSubNext(HljHttpData<List<ShopProduct>> listHljHttpData) {

    }

    protected void onSubError(Object o) {

    }

    @Override
    public void scrollTop() {
    }
}