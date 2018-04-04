package me.suncloud.marrymemo.fragment.orders;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcommonlibrary.adapters.OnItemClickListener;
import com.hunliji.hljcommonlibrary.models.product.ShopProduct;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.orders.ProductOrdersAdapter;
import me.suncloud.marrymemo.api.orders.OrderApi;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.model.orders.ProductOrderRedPacketState;
import me.suncloud.marrymemo.model.orders.ProductSubOrder;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.AfterConfirmReceiveActivity;
import me.suncloud.marrymemo.view.ProductOrderDetailActivity;
import me.suncloud.marrymemo.view.product.AfterPayProductOrderActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 17/1/3.
 */

public class ProductOrdersFragment extends RefreshFragment implements
        PullToRefreshVerticalRecyclerView.OnRefreshListener, ProductOrdersAdapter
        .OnConfirmOrderListener, ProductOrdersAdapter.OnDeleteOrderListener, ProductOrdersAdapter
        .OnCancelOrderListener, OnItemClickListener, ProductOrdersAdapter.OnPayOrderListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private ArrayList<ProductOrder> orders = new ArrayList<>();
    private ProductOrdersAdapter adapter;
    private Unbinder unbinder;
    private View endView;
    private View loadView;
    private View footView;
    private HljHttpSubscriber initSub;
    private HljHttpSubscriber pageSub;
    private HljHttpSubscriber deleteSub;
    private HljHttpSubscriber confirmSub;
    private HljHttpSubscriber checkSub;
    private HljHttpSubscriber cancelSub;
    private Dialog confirmShippingDlg;
    private Dialog deleteDlg;
    private Dialog checkDlg;
    private Dialog cancelOrderDlg;
    private String productIds;
    private int totalCount;
    private RxBusSubscriber<PayRxEvent> paySubscriber;

    public static ProductOrdersFragment newInstance() {
        Bundle args = new Bundle();
        ProductOrdersFragment fragment = new ProductOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        footView = inflater.inflate(R.layout.hlj_foot_no_more___cm, null, false);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);
        initViews();

        return rootView;
    }

    @Override
    public void onResume() {
        initLoad(false);
        super.onResume();
    }

    private void initViews() {
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_order);
        adapter = new ProductOrdersAdapter(getContext(), orders);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter.setFooterView(footView);

        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);

        adapter.setOnConfirmOrderListener(this);
        adapter.setOnDeleteOrderListener(this);
        adapter.setOnCancelOrderListener(this);
        adapter.setOnItemClickListener(this);
        adapter.setOnPayOrderListener(this);

        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    Intent intent;
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            // 支付成功，跳转成功页面
                            intent = new Intent(getContext(), AfterPayProductOrderActivity.class);
                            intent.putExtra("order_type",
                                    Constants.OrderType.WEDDING_PRODUCT_ORDER);
                            intent.putExtra("product_ids", productIds);
                            refresh();
                            startActivity(intent);
                            getActivity().finish();
                            getActivity().overridePendingTransition(0, 0);
                            break;
                        case PAY_CANCEL:
                            break;
                    }
                }
            };
        }
    }

    /**
     * 初始加载或者刷新页面
     *
     * @param isPTR 是否是下拉刷新
     */
    private void initLoad(boolean isPTR) {
        if (initSub != null && !initSub.isUnsubscribed()) {
            initSub.unsubscribe();
        }
        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(isPTR ? null : progressBar)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ProductOrder>>>() {
                    @Override
                    public void onNext(HljHttpData<List<ProductOrder>> listHljHttpData) {
                        recyclerView.onRefreshComplete();
                        orders.clear();
                        orders.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                        initPagination(listHljHttpData.getPageCount());
                        totalCount = listHljHttpData.getTotalCount();
                        if (onTabTextChangeListener != null) {
                            onTabTextChangeListener.onTabTextChange(totalCount);
                        }
                    }
                })
                .build();
        OrderApi.getProductOrderList(1)
                .subscribe(initSub);
    }

    private void initPagination(int pageCount) {
        if (pageSub != null && !pageSub.isUnsubscribed()) {
            pageSub.unsubscribe();
        }

        Observable<HljHttpData<List<ProductOrder>>> pageObb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<ProductOrder>>>() {
                    @Override
                    public Observable<HljHttpData<List<ProductOrder>>> onNextPage(int page) {
                        return OrderApi.getProductOrderList(page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ProductOrder>>>() {
                    @Override
                    public void onNext(HljHttpData<List<ProductOrder>> listHljHttpData) {
                        orders.addAll(listHljHttpData.getData());
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        pageObb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
    }

    @Override
    public void refresh(Object... params) {
        initLoad(false);
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        initLoad(true);
    }


    @Override
    public void onItemClick(int position, Object object) {
        ProductOrder order = (ProductOrder) object;
        if (order != null) {
            Intent intent = new Intent(getActivity(), ProductOrderDetailActivity.class);
            intent.putExtra("id", order.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onConfirmOrder(final ProductOrder order, final int position) {
        confirmSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener<ProductOrder>() {
                    @Override
                    public void onNext(ProductOrder order) {
                        // 刷新列表
                        adapter.setItem(position, order);
                        try {
                            adapter.notifyItemChanged(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 跳转到交易成功
                        Intent intent = new Intent(getContext(), AfterConfirmReceiveActivity.class);
                        intent.putExtra("productOrder", order);
                        getContext().startActivity(intent);
                        ((Activity) getContext()).overridePendingTransition(R.anim
                                        .slide_in_from_bottom,
                                0);
                    }
                })
                .build();

        confirmShippingDlg = DialogUtil.createDoubleButtonDialog(confirmShippingDlg,
                getContext(),
                getContext().getString(R.string.msg_confirm_shipping),
                getContext().getString(R.string.label_confirm_receive),
                getContext().getString(R.string.label_wrong_action),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        confirmShippingDlg.dismiss();
                        OrderApi.confirmProductOrderShipping(order.getId())
                                .subscribe(confirmSub);
                    }
                });
        confirmShippingDlg.show();
    }

    @Override
    public void onDeleteOrder(final ProductOrder order, final int position) {
        deleteSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        // 刷新列表
                        adapter.removeItem(position);
                        adapter.notifyItemRemoved(position);
                        totalCount = totalCount - 1;
                        if (onTabTextChangeListener != null) {
                            onTabTextChangeListener.onTabTextChange(totalCount);
                        }
                    }
                })
                .build();

        deleteDlg = DialogUtil.createDoubleButtonDialog(deleteDlg,
                getContext(),
                "确定要删除订单？",
                "",
                "",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDlg.cancel();
                        OrderApi.deleteProductOrder(order.getId())
                                .subscribe(deleteSub);
                    }
                });
        deleteDlg.show();
    }

    @Override
    public void onCancel(final ProductOrder order, final int position) {
        if (cancelOrderDlg != null && cancelOrderDlg.isShowing()) {
            return;
        }
        cancelSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener<ProductOrder>() {
                    @Override
                    public void onNext(ProductOrder o) {
                        // 支付成功,发送刷新订单列表的消息
                        adapter.setItem(position, o);
                        try {
                            adapter.notifyItemChanged(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .build();
        cancelOrderDlg = DialogUtil.createDoubleButtonDialog(cancelOrderDlg,
                getContext(),
                getString(R.string.msg_cancel_order),
                getString(R.string.label_cancel_order),
                getString(R.string.label_wrong_action),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelOrderDlg.cancel();
                        OrderApi.cancelProductOrder(order.getId())
                                .subscribe(cancelSub);
                    }
                });
        cancelOrderDlg.show();
    }

    @Override
    public void onPay(final ProductOrder order, int position) {
        // 如果使用红包，付款前先确认红包是否满足原红包的满减条件
        if (!TextUtils.isEmpty(order.getRedPacketNo()) && order.getRedPacketMoney() > 0) {
            checkSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                    .setOnNextListener(new SubscriberOnNextListener<ProductOrderRedPacketState>() {
                        @Override
                        public void onNext(ProductOrderRedPacketState productOrderRedPacketState) {
                            if (productOrderRedPacketState.getRedPacketUseless() > 0) {
                                // 红包无效，弹窗提示
                                showRedPacketUselessDlg(order);
                            } else {
                                payOrder(order,
                                        order.getActualMoney() + order.getShippingFee() - order
                                                .getRedPacketMoney() - order.getAidMoney());
                            }
                        }
                    })
                    .build();
            OrderApi.checkProductOrderRedPacket(order.getId())
                    .subscribe(checkSub);
        } else {
            payOrder(order, order.getActualMoney() + order.getShippingFee() - order.getAidMoney());
        }
    }

    private void showRedPacketUselessDlg(final ProductOrder order) {
        checkDlg = DialogUtil.createDoubleButtonDialog(checkDlg,
                getContext(),
                "订单金额不满足红包满减条件，\n是否继续支付？",
                "",
                "",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkDlg.cancel();
                        // 确认不使用红包继续付款
                        payOrder(order, order.getActualMoney() + order.getShippingFee());
                    }
                });
        checkDlg.show();
    }

    private void payOrder(ProductOrder order, double money) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_ids", String.valueOf(order.getId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        if (!CommonUtil.isCollectionEmpty(order.getSubOrders())) {
            for (ProductSubOrder subOrder : order.getSubOrders()) {
                ShopProduct product = subOrder.getProduct();
                if (product != null) {
                    sb.append(product.getId())
                            .append(",");
                }
            }
            if (!TextUtils.isEmpty(sb) && sb.lastIndexOf(",") > 0) {
                sb.deleteCharAt(sb.length() - 1); //移除最后的逗号
            }
        }
        productIds = sb.toString();
        PayConfig.Builder builder = new PayConfig.Builder(getActivity());
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(getActivity());
        List<String> payTypes = dataConfig != null ? dataConfig.getPayTypes() : null;
        builder.payAgents(payTypes, DataConfig.getWalletPayAgents());
        builder.params(jsonObject)
                .path(Constants.getAbsUrl(Constants.HttpPath.PRODUCT_ORDER_PAYMENT))
                .price(money > 0 ? money : 0)
                .subscriber(paySubscriber)
                .build()
                .pay();
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
        CommonUtil.unSubscribeSubs(initSub,
                pageSub,
                confirmSub,
                deleteSub,
                paySubscriber,
                checkSub);
    }

    static class HeaderViewHolder {
        @BindView(R.id.line_layout)
        View lineLayout;
        @BindView(R.id.tv_refund_orders_count)
        TextView tvRefundOrdersCount;
        @BindView(R.id.refund_orders_layout)
        LinearLayout refundOrdersLayout;
        @BindView(R.id.orders_counts_layout)
        LinearLayout ordersCountsLayout;
        @BindView(R.id.bottom_line)
        View bottomLine;

        HeaderViewHolder(View view) {ButterKnife.bind(this, view);}
    }

}
