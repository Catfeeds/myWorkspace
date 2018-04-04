package com.hunliji.marrybiz.view.orders;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.order.MerchantOrderListRecyclerAdapter;
import com.hunliji.marrybiz.api.order.OrderApi;
import com.hunliji.marrybiz.model.college.CollegeItem;
import com.hunliji.marrybiz.model.orders.MerchantOrder;
import com.hunliji.marrybiz.widget.MerchantOrderFilterMenuViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by mo_yu on 2017/12/11.商家支付订单列表(cpm,店铺，转化等)
 */

public class MerchantOrderListActivity extends HljBaseActivity implements PullToRefreshBase
        .OnRefreshListener {

    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.merchant_order_filter_layout)
    RelativeLayout merchantOrderFilterLayout;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_scroll_top)
    ImageButton btnScrollTop;

    private ArrayList<MerchantOrder> merchantOrders;
    private MerchantOrderFilterMenuViewHolder merchantOrderFilterMenuViewHolder;
    private MerchantOrderListRecyclerAdapter adapter;
    private FooterViewHolder footerViewHolder;
    private LinearLayoutManager layoutManager;
    private Map<String, Object> filterMap;

    private HljHttpSubscriber refreshSubscriber;
    private HljHttpSubscriber pageSubscriber;
    private Subscription rxSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_order_list);
        ButterKnife.bind(this);
        initValue();
        initView();
        initRxSub();
        onRefresh(recyclerView);
    }

    private void initValue() {
        merchantOrders = new ArrayList<>();
    }

    private void initView() {
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_order);
        emptyView.setHintText("暂无相关订单");
        merchantOrderFilterMenuViewHolder = MerchantOrderFilterMenuViewHolder.newInstance(this,
                new MerchantOrderFilterMenuViewHolder.OnFilterResultListener() {
                    @Override
                    public void onFilterResult(Map<String, Object> filterMap) {
                        scrollToTop();
                        MerchantOrderListActivity.this.filterMap = filterMap;
                        onRefresh(recyclerView);
                    }
                });
        merchantOrderFilterLayout.removeAllViews();
        merchantOrderFilterLayout.addView(merchantOrderFilterMenuViewHolder.getRootView());

        View footerView = View.inflate(this, R.layout.hlj_foot_no_more___cm, null);
        footerViewHolder = new FooterViewHolder(footerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        adapter = new MerchantOrderListRecyclerAdapter(this);
        adapter.setList(merchantOrders);
        adapter.setFooterView(footerView);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {

                        @Override
                        public void onNext(ResultZip resultZip) {
                            if (resultZip.hljHttpData == null || CommonUtil.isCollectionEmpty(
                                    resultZip.hljHttpData.getData())) {
                                recyclerView.setVisibility(View.GONE);
                                emptyView.showEmptyView();
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.hideEmptyView();
                                merchantOrders.clear();
                                merchantOrders.addAll(resultZip.hljHttpData.getData());
                                adapter.notifyDataSetChanged();
                                initPage(resultZip.hljHttpData.getPageCount());
                            }
                            int count = CommonUtil.getAsInt(resultZip.jsonElement, "num");
                            if (count > 0) {
                                alertLayout.setVisibility(View.VISIBLE);
                                tvAlertMsg.setText(getString(R.string
                                                .format_bd_wait_pay_count_alert,
                                        String.valueOf(count)));
                            } else {
                                alertLayout.setVisibility(View.GONE);
                            }
                        }
                    })
                    .setPullToRefreshBase(recyclerView)
                    .setProgressBar(recyclerView.isRefreshing() ? null : progressBar)
                    .setContentView(recyclerView)
                    .setEmptyView(emptyView)
                    .build();
            Observable.zip(OrderApi.getMerchantOrderListObb(getUrl(1)),
                    OrderApi.getWaitPayCountObb(),
                    new Func2<HljHttpData<List<MerchantOrder>>, JsonElement, ResultZip>() {
                        @Override
                        public ResultZip call(
                                HljHttpData<List<MerchantOrder>> listHljHttpData,
                                JsonElement jsonElement) {
                            ResultZip zip = new ResultZip();
                            zip.hljHttpData = listHljHttpData;
                            zip.jsonElement = jsonElement;
                            return zip;
                        }
                    })
                    .subscribe(refreshSubscriber);
        }
    }

    private class ResultZip extends HljHttpResultZip {
        @HljRZField
        HljHttpData<List<MerchantOrder>> hljHttpData;
        @HljRZField
        JsonElement jsonElement;
    }

    private void initPage(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSubscriber);
        Observable<HljHttpData<List<MerchantOrder>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<MerchantOrder>>>() {
                    @Override
                    public Observable<HljHttpData<List<MerchantOrder>>> onNextPage(int page) {
                        return OrderApi.getMerchantOrderListObb(getUrl(page));
                    }
                })
                .setLoadView(footerViewHolder.loadView)
                .setEndView(footerViewHolder.endView)
                .build()
                .getPagingObservable();
        pageSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<MerchantOrder>>>
                        () {
                    @Override
                    public void onNext(HljHttpData<List<MerchantOrder>> listHljHttpData) {
                        merchantOrders.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSubscriber);
    }

    private String getUrl(int page) {
        StringBuilder url = new StringBuilder("p/wedding/admin/APIMerchantOrder/list");
        url.append("?page=")
                .append(page)
                .append("&per_page=20");
        if (filterMap != null) {
            for (Object o : filterMap.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                Label label = (Label) entry.getValue();
                if (label != null && !CommonUtil.isEmpty(label.getValue())) {
                    url.append("&")
                            .append(key)
                            .append("=")
                            .append(label.getValue());
                }
            }
        }
        return url.toString();
    }

    private void scrollToTop() {
        if (layoutManager.findFirstVisibleItemPosition() > 5) {
            recyclerView.getRefreshableView()
                    .scrollToPosition(5);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.getRefreshableView()
                            .smoothScrollToPosition(0);
                }
            });
        } else {
            recyclerView.getRefreshableView()
                    .smoothScrollToPosition(0);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(pageSubscriber, refreshSubscriber, rxSubscription);
        merchantOrderFilterMenuViewHolder.onDestroy();
    }

    private void initRxSub() {
        if (!CommonUtil.isUnsubscribed(rxSubscription)) {
            return;
        }
        rxSubscription = RxBus.getDefault()
                .toObservable(RxEvent.class)
                .filter(new Func1<RxEvent, Boolean>() {
                    @Override
                    public Boolean call(RxEvent rxEvent) {
                        return rxEvent.getType() == RxEvent.RxEventType.MERCHANT_ORDER_PAID;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxEvent>() {
                    @Override
                    public void call(RxEvent rxEvent) {
                        onRefresh(null);
                    }
                });
    }

    static class FooterViewHolder {
        @BindView(R.id.no_more_hint)
        TextView endView;
        @BindView(R.id.loading)
        LinearLayout loadView;

        FooterViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
