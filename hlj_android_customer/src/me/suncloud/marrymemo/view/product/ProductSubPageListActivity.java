package me.suncloud.marrymemo.view.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.product.ProductTopic;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerSite;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.product.ProductSubPageRecyclerAdapter;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.ShoppingCartActivity;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 婚品专题列表
 * Created by chen_bin on 2016/11/26 0026.
 */
public class ProductSubPageListActivity extends HljBaseNoBarActivity implements PullToRefreshBase
        .OnRefreshListener<RecyclerView> {

    @Override
    public String pageTrackTagName() {
        return "婚品严选";
    }

    @BindView(R.id.notice)
    View shopNoticeView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private View endView;
    private View loadView;
    private ProductSubPageRecyclerAdapter adapter;
    private int limit;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_sub_page_list);
        setDefaultStatusBarPadding();
        ButterKnife.bind(this);
        limit = getIntent().getIntExtra("limit", 0);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onRefresh(null);
            }
        });
        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setOnRefreshListener(this);
        adapter = new ProductSubPageRecyclerAdapter(this);
        adapter.addFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        shopNoticeView.setVisibility(Session.getInstance()
                .isNewCart() ? View.VISIBLE : View.GONE);
        onRefresh(null);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        if (refreshSub == null || refreshSub.isUnsubscribed()) {
            refreshSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ProductTopic>>>() {
                        @Override
                        public void onNext(HljHttpData<List<ProductTopic>> listHljHttpData) {
                            List<ProductTopic> topics = listHljHttpData.getData();
                            if (limit < topics.size()) {
                                topics.addAll(topics.subList(limit, topics.size()));
                            }
                            adapter.setTopics(topics);
                            initPagination(listHljHttpData.getPageCount());
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(recyclerView)
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .build();
            ProductApi.getProductSubPagesObb(1, 10)
                    .subscribe(refreshSub);
        }
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<ProductTopic>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<ProductTopic>>>() {
                    @Override
                    public Observable<HljHttpData<List<ProductTopic>>> onNextPage(
                            int page) {
                        return ProductApi.getProductSubPagesObb(page, 10);
                    }
                })
                .setLoadView(loadView)
                .setEndView(endView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ProductTopic>>>() {
                    @Override
                    public void onNext(
                            HljHttpData<List<ProductTopic>> listHljHttpData) {
                        adapter.addTopics(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    //搜索
    @OnClick(R.id.btn_search)
    public void onSearch() {
        Intent intent = new Intent(this, NewSearchActivity.class);
        intent.putExtra(NewSearchApi.ARG_SEARCH_TYPE,
                NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    //点击跳转到购物车
    @OnClick(R.id.btn_shopping_cart)
    public void onShoppingCart() {
        if (!Util.loginBindChecked(this, Constants.Login.SHOP_CART_LOGIN)) {
            return;
        }
        new HljTracker.Builder(this).action("hit")
                .sid("C1/A1")
                .pos(3)
                .desc("购物车")
                .build()
                .add();
        if (shopNoticeView != null) {
            shopNoticeView.setVisibility(View.GONE);
        }
        startActivity(new Intent(this, ShoppingCartActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.Login.SHOP_CART_LOGIN:
                    onShoppingCart();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shopNoticeView != null) {
            shopNoticeView.setVisibility(Session.getInstance()
                    .isNewCart() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(refreshSub, pageSub);
    }
}