package me.suncloud.marrymemo.fragment.orders;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.hunliji.hljcommonlibrary.adapters.OnTabTextChangeListener;
import com.hunliji.hljcommonlibrary.models.orders.HotelPeriodOrder;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.InstallmentPaymentActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyBillListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.orders.HotelPeriodOrdersAdapter;
import me.suncloud.marrymemo.adpter.orders.viewholder.HotelPeriodOrderViewHolder;
import me.suncloud.marrymemo.api.orders.OrderApi;
import me.suncloud.marrymemo.view.merchant.MerchantDetailActivity;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 婚宴酒店订单列表
 * Created by chen_bin on 2018/2/28 0028.
 */
public class HotelPeriodOrdersFragment extends RefreshFragment implements PullToRefreshBase
        .OnRefreshListener<RecyclerView>, HotelPeriodOrderViewHolder.OnCancelOrderListener,
        HotelPeriodOrderViewHolder.OnDeleteOrderListener, HotelPeriodOrderViewHolder
                .OnPayListener, HotelPeriodOrderViewHolder.OnReorderListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private View footerView;
    private View endView;
    private View loadView;

    private Dialog confirmDialog;

    private HotelPeriodOrdersAdapter adapter;

    private int totalCount;

    private Subscription rxBusEventSub;
    private HljHttpSubscriber refreshSub;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber cancelSub;
    private HljHttpSubscriber deleteSub;

    private Unbinder unbinder;

    private OnTabTextChangeListener onTabTextChangeListener;

    @Override
    public void setOnTabTextChangeListener(OnTabTextChangeListener onTabTextChangeListener) {
        this.onTabTextChangeListener = onTabTextChangeListener;
    }

    public static HotelPeriodOrdersFragment newInstance() {
        Bundle args = new Bundle();
        HotelPeriodOrdersFragment fragment = new HotelPeriodOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        footerView = View.inflate(getContext(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        loadView.setVisibility(View.INVISIBLE);
        adapter = new HotelPeriodOrdersAdapter(getContext());
        adapter.setFooterView(footerView);
        adapter.setOnCancelOrderListener(this);
        adapter.setOnDeleteOrderListener(this);
        adapter.setOnPayListener(this);
        adapter.setOnReorderListener(this);
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
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        registerRxBusEvent();
        if (CommonUtil.isCollectionEmpty(adapter.getOrders())) {
            initLoad(true);
        }
    }

    private void initViews() {
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_order);
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad(true);
            }
        });
        ((SimpleItemAnimator) recyclerView.getRefreshableView()
                .getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRefreshableView()
                .addItemDecoration(new SpacesItemDecoration());
        recyclerView.getRefreshableView()
                .setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(new RxBusSubscriber<PayRxEvent>() {
                        @Override
                        protected void onEvent(PayRxEvent payRxEvent) {
                            switch (payRxEvent.getType()) {
                                case INSTALLMENT_PAY_SUCCESS:
                                    //分期支付成功
                                    Intent intent = new Intent(getContext(),
                                            MyBillListActivity.class);
                                    intent.putExtra(RouterPath.IntentPath.Customer.MyOrder
                                                    .ARG_SELECT_TAB,
                                            RouterPath.IntentPath.Customer.MyOrder.Tab
                                                    .HOTEL_PERIOD_ORDER);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initLoad(false);
    }

    private void initLoad(boolean isShowProgressBar) {
        CommonUtil.unSubscribeSubs(refreshSub);
        refreshSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<HotelPeriodOrder>>>() {

                    @Override
                    public void onNext(HljHttpData<List<HotelPeriodOrder>> listHljHttpData) {
                        adapter.setOrders(listHljHttpData.getData());
                        initPagination(listHljHttpData.getPageCount());
                        totalCount = listHljHttpData.getTotalCount();
                        if (onTabTextChangeListener != null) {
                            onTabTextChangeListener.onTabTextChange(totalCount);
                        }
                    }
                })
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setPullToRefreshBase(recyclerView)
                .setProgressBar(isShowProgressBar || progressBar.getVisibility() == View.VISIBLE
                        ? progressBar : null)
                .build();
        OrderApi.getHotelPeriodOrdersObb(1, HljCommon.PER_PAGE)
                .subscribe(refreshSub);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        initLoad(false);
    }

    private void initPagination(int pageCount) {
        CommonUtil.unSubscribeSubs(pageSub);
        Observable<HljHttpData<List<HotelPeriodOrder>>> observable = PaginationTool
                .buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<HotelPeriodOrder>>>() {
                    @Override
                    public Observable<HljHttpData<List<HotelPeriodOrder>>> onNextPage(int page) {
                        return OrderApi.getHotelPeriodOrdersObb(page, HljCommon.PER_PAGE);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable();
        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<HotelPeriodOrder>>>() {
                    @Override
                    public void onNext(HljHttpData<List<HotelPeriodOrder>> listHljHttpData) {
                        adapter.addOrders(listHljHttpData.getData());
                    }
                })
                .build();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }


    @Override
    public void onCancelOrder(final int position, final HotelPeriodOrder order) {
        if (order == null) {
            return;
        }
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        confirmDialog = DialogUtil.createDoubleButtonDialog(getContext(),
                "是否取消此订单",
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.unSubscribeSubs(cancelSub);
                        cancelSub = HljHttpSubscriber.buildSubscriber(getContext())
                                .setOnNextListener(new SubscriberOnNextListener() {
                                    @Override
                                    public void onNext(Object o) {
                                        ToastUtil.showCustomToast(getContext(), "取消订单成功");
                                        order.setStatus(HotelPeriodOrder.STATUS_ORDER_CLOSED);
                                        adapter.notifyItemChanged(position);
                                    }
                                })
                                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                                .build();
                        OrderApi.cancelHotelPeriodOrderObb(order.getId())
                                .subscribe(cancelSub);
                    }
                },
                null);
        confirmDialog.show();

    }

    @Override
    public void onDeleteOrder(final int position, final HotelPeriodOrder order) {
        if (order == null) {
            return;
        }
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        confirmDialog = DialogUtil.createDoubleButtonDialog(getContext(),
                "是否删除此订单",
                null,
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.unSubscribeSubs(deleteSub);
                        deleteSub = HljHttpSubscriber.buildSubscriber(getContext())
                                .setOnNextListener(new SubscriberOnNextListener() {
                                    @Override
                                    public void onNext(Object o) {
                                        ToastUtil.showCustomToast(getContext(), "删除订单成功");
                                        List<HotelPeriodOrder> orders = adapter.getOrders();
                                        if (CommonUtil.isCollectionEmpty(orders)) {
                                            return;
                                        }
                                        totalCount--;
                                        orders.remove(order);
                                        adapter.notifyItemRemoved(position);
                                        if (onTabTextChangeListener != null) {
                                            onTabTextChangeListener.onTabTextChange(totalCount);
                                        }
                                        if (totalCount == 0) {
                                            emptyView.showEmptyView();
                                            recyclerView.setVisibility(View.GONE);
                                        } else if (CommonUtil.isCollectionEmpty(orders)) {
                                            onRefresh(null);
                                        }
                                    }
                                })
                                .setDataNullable(true)
                                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                                .build();
                        OrderApi.deleteHotelPeriodOrderObb(order.getId())
                                .subscribe(deleteSub);
                    }
                },
                null);
        confirmDialog.show();
    }

    @Override
    public void onPay(int position, HotelPeriodOrder order) {
        if (order == null) {
            return;
        }
        JSONObject params = new JSONObject();
        try {
            params.put("order_id", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(getContext(), InstallmentPaymentActivity.class);
        intent.putExtra(InstallmentPaymentActivity.ARG_ORDER_PAY_TYPE,
                InstallmentPaymentActivity.ORDER_PAY_TYPE_HOTEL);
        intent.putExtra(InstallmentPaymentActivity.ARG_INSTALLMENT_STAGE_NUM, order.getPeriod());
        intent.putExtra(InstallmentPaymentActivity.ARG_PRICE, order.getActualMoney());
        intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PARAMS, params.toString());
        intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PATH,
                Constants.getAbsUrl(Constants.HttpPath.HOTEL_PERIOD_ORDER_PAYMENT));
        if (order.getWeddingDay() != null) {
            intent.putExtra(InstallmentPaymentActivity.ARG_WEDDING_DAY,
                    order.getWeddingDay()
                            .toString(HljTimeUtils.DATE_FORMAT_SHORT));
        }
        startActivity(intent);
    }

    @Override
    public void onReorder(int position, HotelPeriodOrder order) {
        if (order == null) {
            return;
        }
        Intent intent = new Intent(getContext(), MerchantDetailActivity.class);
        intent.putExtra(MerchantDetailActivity.ARG_ID,
                order.getMerchant()
                        .getId());
        startActivity(intent);
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        SpacesItemDecoration() {
            this.space = CommonUtil.dp2px(getContext(), 10);
        }

        @Override
        public void getItemOffsets(
                Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int top = position < adapter.getItemCount() - adapter.getFooterViewCount() ? space : 0;
            outRect.set(0, top, 0, 0);
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.getRefreshableView()
                .setAdapter(null);
        CommonUtil.unSubscribeSubs(refreshSub, pageSub, cancelSub, deleteSub);
        unbinder.unbind();
    }

}
