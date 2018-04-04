package me.suncloud.marrymemo.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshVerticalRecyclerView;
import com.hunliji.hljcardcustomerlibrary.views.activities.AfterRollInOutActivity;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.entities.HljHttpResultZip;
import com.hunliji.hljhttplibrary.entities.HljRZField;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljhttplibrary.utils.pagination.PaginationTool;
import com.hunliji.hljhttplibrary.utils.pagination.PagingListener;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyBillListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.orders.ServiceOrdersAdapter;
import me.suncloud.marrymemo.api.orders.OrderApi;
import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.model.orders.BasicServiceOrderWrapper;
import me.suncloud.marrymemo.model.orders.ServiceOrder;
import me.suncloud.marrymemo.model.orders.ServiceOrderCountInfo;
import me.suncloud.marrymemo.model.orders.ServiceOrderIdBody;
import me.suncloud.marrymemo.task.StatusHttpPutTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.AfterConfirmReceiveActivity;
import me.suncloud.marrymemo.view.AfterPayActivity;
import me.suncloud.marrymemo.view.PayOfflineOrderListActivity;
import me.suncloud.marrymemo.view.RefundOrderListActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

/**
 * Created by werther on 16/10/11.
 * 新版本地服务订单列表（包含定制套餐列表）
 */

public class ServiceOrdersFragment extends RefreshFragment implements ServiceOrdersAdapter
        .OnCancelOrderListener, ServiceOrdersAdapter.OnDeleteOrderListener, ServiceOrdersAdapter
        .OnConfirmOrderListener, PullToRefreshVerticalRecyclerView.OnRefreshListener,
        ServiceOrdersAdapter.OnCancelCSOrderListener, ServiceOrdersAdapter
                .OnConfirmCSOrderListener, ServiceOrdersAdapter.OnDelayConfirmListener {

    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.recycler_view)
    PullToRefreshVerticalRecyclerView recyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private Unbinder unbinder;
    private HljHttpSubscriber initSub;
    private ServiceOrdersAdapter adapter;
    private ArrayList<BasicServiceOrderWrapper> orderWrappers = new ArrayList<>();
    private RxBusSubscriber<PayRxEvent> paySubscriber;
    private HljHttpSubscriber deleteSub;
    private HljHttpSubscriber cancelSub;
    private HljHttpSubscriber confirmSub;
    private Dialog cancelDlg;
    private Dialog deleteDlg;
    private Dialog confirmDlg;
    private View headView;
    private HeadViewHolder headViewHolder;
    private HljHttpSubscriber pageSub;
    private View footerView;
    private View endView;
    private View loadView;
    private Dialog cancelCSODlg;
    private Dialog confirmCSODlg;
    private Dialog delayDlg;
    private int totalCount;
    private HljHttpSubscriber delayConfirmSub;

    public static ServiceOrdersFragment newInstance() {
        Bundle args = new Bundle();
        ServiceOrdersFragment fragment = new ServiceOrdersFragment();
        fragment.setArguments(args);
        return fragment;
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
        headView = inflater.inflate(R.layout.order_list_head, null, false);
        headViewHolder = new HeadViewHolder(headView);
        footerView = View.inflate(getActivity(), R.layout.hlj_foot_no_more___cm, null);
        endView = footerView.findViewById(R.id.no_more_hint);
        loadView = footerView.findViewById(R.id.loading);
        initViews();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CommonUtil.isCollectionEmpty(orderWrappers)) {
            initObservables(false);
        }
    }

    @Override
    public void onResume() {
        initObservables(false);
        super.onResume();
    }

    private void initViews() {
        emptyView.setEmptyDrawableId(R.mipmap.icon_empty_order);
        adapter = new ServiceOrdersAdapter(getContext(), orderWrappers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter.setHeaderView(headView);
        adapter.setFooterView(footerView);

        recyclerView.getRefreshableView()
                .setLayoutManager(layoutManager);
        recyclerView.getRefreshableView()
                .setAdapter(adapter);
        recyclerView.setOnRefreshListener(this);

        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    Intent intent;
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            // 支付成功，跳转成功页面
                            intent = new Intent(getContext(), AfterPayActivity.class);
                            intent.putExtra(AfterPayActivity.ARG_ORDER_TYPE,
                                    Constants.OrderType.NOMAL_WORK_ORDER);
                            if (rxEvent.getObject() != null && rxEvent.getObject() instanceof
                                    JsonObject) {
                                JsonObject orderResult = (JsonObject) rxEvent.getObject();
                                try {
                                    if (orderResult.get("free_order_link") != null) {
                                        intent.putExtra(AfterPayActivity.ARG_PATH,
                                                orderResult.get("free_order_link")
                                                        .getAsString());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            intent.putExtra(AfterPayActivity.ARG_IS_BACK, true);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0, 0);
                            break;
                        case PAY_CANCEL:
                            break;
                        case INSTALLMENT_PAY_SUCCESS:
                            //分期支付成功
                            intent = new Intent(getContext(), MyBillListActivity.class);
                            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
                            startActivity(intent);
                            getActivity().overridePendingTransition(0, 0);
                            break;
                    }
                }
            };
        }
        adapter.setPaySubscriber(paySubscriber);
        adapter.setOnCancelOrderListener(this);
        adapter.setOnDeleteOrderListener(this);
        adapter.setOnConfirmOrderListener(this);
        adapter.setOnCancelCSOrderListener(this);
        adapter.setOnConfirmCSOrderListener(this);
        adapter.setOnDelayConfirmListener(this);
    }

    /**
     * 初始加载或者刷新页面
     *
     * @param isPTR 是否是下拉刷新
     */
    private void initObservables(boolean isPTR) {
        Observable<HljHttpData<List<ServiceOrder>>> soObb = OrderApi.getServiceOrderList(1);
        Observable<ServiceOrderCountInfo> socObb = OrderApi.getServiceOrderCountInfo();
        Observable<List<CustomSetmealOrder>> csoObb = OrderApi.getCustomSetmealOrders();
        Observable<ResultZip> observable = Observable.zip(soObb,
                csoObb,
                socObb,
                new Func3<HljHttpData<List<ServiceOrder>>, List<CustomSetmealOrder>,
                        ServiceOrderCountInfo, ResultZip>() {
                    @Override
                    public ResultZip call(
                            HljHttpData<List<ServiceOrder>> listHljHttpData,
                            List<CustomSetmealOrder> customSetmealOrders,
                            ServiceOrderCountInfo serviceOrderCountInfo) {
                        ResultZip zip = new ResultZip();
                        zip.serviceOrderCountInfo = serviceOrderCountInfo;
                        zip.customSetmealOrders = customSetmealOrders;
                        zip.serviceOrdersData = listHljHttpData;
                        return zip;
                    }
                });

        if (initSub != null && !initSub.isUnsubscribed()) {
            initSub.unsubscribe();
        }

        initSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(isPTR ? null : progressBar)
                .setEmptyView(emptyView)
                .setContentView(recyclerView)
                .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                    @Override
                    public void onNext(ResultZip resultZip) {
                        recyclerView.onRefreshComplete();
                        ArrayList<ServiceOrder> serviceOrders = new ArrayList<>();
                        serviceOrders.addAll(resultZip.serviceOrdersData.getData());
                        orderWrappers.clear();
                        for (CustomSetmealOrder customSetmealOrder : resultZip
                                .customSetmealOrders) {
                            BasicServiceOrderWrapper wrapper = new BasicServiceOrderWrapper();
                            wrapper.setOrder(customSetmealOrder);
                            wrapper.setType(1);
                            orderWrappers.add(wrapper);
                        }
                        for (ServiceOrder order : serviceOrders) {
                            BasicServiceOrderWrapper wrapper = new BasicServiceOrderWrapper();
                            wrapper.setOrder(order);
                            wrapper.setType(2);
                            orderWrappers.add(wrapper);
                        }
                        adapter.notifyDataSetChanged();
                        initPagination(resultZip.serviceOrdersData.getPageCount());
                        setOrdersCountInfo(resultZip.serviceOrderCountInfo);
                        totalCount = resultZip.serviceOrdersData.getTotalCount();
                        if (onTabTextChangeListener != null) {
                            onTabTextChangeListener.onTabTextChange(totalCount);
                        }
                    }
                })
                .build();

        observable.subscribe(initSub);
    }

    private void setOrdersCountInfo(ServiceOrderCountInfo info) {
        if (info == null) {
            return;
        }
        if (info.getRefundCount() + info.getOfflineOrderCount() > 0) {
            headViewHolder.ordersCountsLayout.setVisibility(View.VISIBLE);
        } else {
            headViewHolder.ordersCountsLayout.setVisibility(View.GONE);
        }
        if (info.getRefundCount() > 0) {
            headViewHolder.refundOrdersLayout.setVisibility(View.VISIBLE);
            headViewHolder.tvRefundOrdersCount.setText(getString(R.string.label_order_count2,
                    info.getRefundCount()));
        } else {
            headViewHolder.refundOrdersLayout.setVisibility(View.GONE);
        }
        if (info.getOfflineOrderCount() > 0) {
            headViewHolder.payOfflineLayout.setVisibility(View.VISIBLE);
            headViewHolder.tvPayOfflineOrdersCount.setText(getString(R.string.label_order_count2,
                    info.getOfflineOrderCount()));
        } else {
            headViewHolder.payOfflineLayout.setVisibility(View.GONE);
        }

        headViewHolder.payOfflineLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到线下付款订单列表
                Intent intent = new Intent(getActivity(), PayOfflineOrderListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        });
        headViewHolder.refundOrdersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RefundOrderListActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        });
    }

    private void initPagination(int pageCount) {
        if (pageSub != null && !pageSub.isUnsubscribed()) {
            pageSub.unsubscribe();
        }

        Observable<HljHttpData<List<ServiceOrder>>> pageOb = PaginationTool.buildPagingObservable(
                recyclerView.getRefreshableView(),
                pageCount,
                new PagingListener<HljHttpData<List<ServiceOrder>>>() {
                    @Override
                    public Observable<HljHttpData<List<ServiceOrder>>> onNextPage(int page) {
                        return OrderApi.getServiceOrderList(page);
                    }
                })
                .setEndView(endView)
                .setLoadView(loadView)
                .build()
                .getPagingObservable()
                .observeOn(AndroidSchedulers.mainThread());

        pageSub = HljHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<ServiceOrder>>>() {
                    @Override
                    public void onNext(HljHttpData<List<ServiceOrder>> listHljHttpData) {
                        ArrayList<ServiceOrder> serviceOrders = new ArrayList<>();
                        serviceOrders.addAll(listHljHttpData.getData());

                        for (ServiceOrder order : serviceOrders) {
                            BasicServiceOrderWrapper wrapper = new BasicServiceOrderWrapper();
                            wrapper.setOrder(order);
                            wrapper.setType(2);
                            orderWrappers.add(wrapper);
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        pageOb.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageSub);
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
                paySubscriber,
                deleteSub,
                cancelSub,
                confirmSub,
                pageSub,
                delayConfirmSub);
    }

    @Override
    public void refresh(Object... params) {
        initObservables(false);
    }

    @Override
    public void onCancel(final ServiceOrder serviceOrder, final int position) {
        cancelSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener<ServiceOrder>() {
                    @Override
                    public void onNext(ServiceOrder serviceOrder) {
                        // 刷新列表
                        adapter.getItem(position)
                                .setOrder(serviceOrder);
                        adapter.notifyItemChanged(position);
                    }
                })
                .build();

        cancelDlg = DialogUtil.createDoubleButtonDialog(getContext(),
                "确定要取消订单？",
                "",
                "",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDlg.cancel();
                        ServiceOrderIdBody body = new ServiceOrderIdBody();
                        body.setOrderId(serviceOrder.getId());
                        OrderApi.cancelServiceOrder(body)
                                .subscribe(cancelSub);
                    }
                },
                null);
        cancelDlg.show();
    }

    @Override
    public void onDelete(final ServiceOrder serviceOrder, final int position) {
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

        deleteDlg = DialogUtil.createDoubleButtonDialog(getContext(),
                "确定要删除订单？",
                "",
                "",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDlg.cancel();
                        OrderApi.deleteServiceOrder(serviceOrder.getId())
                                .subscribe(deleteSub);
                    }
                },
                null);
        deleteDlg.show();
    }

    @Override
    public void onConfirm(final ServiceOrder serviceOrder, final int position) {
        confirmSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener<ServiceOrder>() {
                    @Override
                    public void onNext(ServiceOrder serviceOrder) {
                        // 刷新列表
                        adapter.getItem(position)
                                .setOrder(serviceOrder);
                        adapter.notifyDataSetChanged();
                        // 跳转到交易成功
                        Intent intent = new Intent(getContext(), AfterConfirmReceiveActivity.class);
                        intent.putExtra("service_order_id",
                                serviceOrder.getOrderSub()
                                        .getId());
                        intent.putExtra("service_order_no",
                                serviceOrder.getOrderSub()
                                        .getOrderNo());
                        intent.putExtra("is_service_order", true);
                        startActivityForResult(intent, Constants.RequestCode.ORDER_CONFIRM);
                        getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, 0);
                    }
                })
                .build();

        confirmDlg = DialogUtil.createDoubleButtonDialog(getContext(),
                "请在服务完成之后进行确认，确认后商家将收到此订单所有服务费",
                "服务已完成",
                "我点错了",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDlg.cancel();
                        ServiceOrderIdBody body = new ServiceOrderIdBody();
                        body.setOrderId(serviceOrder.getId());
                        OrderApi.confirmServiceOrder(body)
                                .subscribe(confirmSub);
                    }
                },
                null);
        confirmDlg.show();
    }

    @Override
    public void onCancelCSOrder(final CustomSetmealOrder order, final int position) {
        cancelCSODlg = DialogUtil.createDoubleButtonDialog(getContext(),
                "确定要取消订单？",
                "",
                "",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDlg.cancel();
                        progressBar.setVisibility(View.VISIBLE);
                        cancelCustomOrder(position, order);
                    }
                },
                null);
        cancelCSODlg.show();
    }

    @Override
    public void onConfirmCSOrder(final CustomSetmealOrder order, final int position) {
        confirmCSODlg = DialogUtil.createDoubleButtonDialog(getContext(),
                "请在服务完成之后进行确认，确认后商家将收到此订单所有服务费",
                "服务已完成",
                "我点错了",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmCSODlg.cancel();
                        progressBar.setVisibility(View.VISIBLE);
                        postConfirmCustomService(order);
                    }
                },
                null);
        confirmCSODlg.show();
    }


    @Override
    public void onDelayConfirm(final ServiceOrder serviceOrder, final int position) {
        delayConfirmSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener<ServiceOrder>() {
                    @Override
                    public void onNext(ServiceOrder order) {// 刷新列表
                        Toast.makeText(getContext(),
                                R.string.msg_success_to_delay,
                                Toast.LENGTH_SHORT)
                                .show();
                        adapter.getItem(position)
                                .setOrder(order);
                        adapter.notifyDataSetChanged();
                    }
                })
                .build();
        delayDlg = DialogUtil.createDoubleButtonDialog(getContext(),
                "是否延期确认服务？",
                "申请延期可延长自动确认服务时间15天\n" + "申请后7天可再次申请",
                "我要延期",
                "我点错了",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delayDlg.cancel();
                        ServiceOrderIdBody body = new ServiceOrderIdBody();
                        body.setOrderId(serviceOrder.getId());
                        OrderApi.delayConfirmServiceOrder(body)
                                .subscribe(delayConfirmSub);
                    }
                },
                null);
        delayDlg.show();
    }

    private void cancelCustomOrder(final int position, final CustomSetmealOrder order) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new StatusHttpPutTask(getActivity(), new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject orderObject = (JSONObject) object;
                if (orderObject != null) {
                    CustomSetmealOrder newOrder = new CustomSetmealOrder(orderObject);
                    adapter.getItem(position)
                            .setOrder(newOrder);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(getActivity(),
                        returnStatus,
                        R.string.msg_fail_to_cancel_order,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_CANCEL),
                jsonObject.toString());
    }

    private void postConfirmCustomService(CustomSetmealOrder order) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new StatusHttpPutTask(getActivity(), new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject orderObject = (JSONObject) object;
                if (orderObject != null) {
                    CustomSetmealOrder order = new CustomSetmealOrder(orderObject);
                    // 确认服务,发送刷新订单列表的消息
                    initObservables(false);
                    Intent intent = new Intent(getActivity(), AfterConfirmReceiveActivity.class);
                    intent.putExtra("custom_order", (Serializable) order);
                    intent.putExtra("is_custom_order", true);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, 0);
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(getActivity(),
                        returnStatus,
                        R.string.msg_fail_to_confirm_shipping,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_CONFIRM_SERVICE),
                jsonObject.toString());
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        initObservables(true);
    }

    static class HeadViewHolder {
        @BindView(R.id.tv_refund_orders_count)
        TextView tvRefundOrdersCount;
        @BindView(R.id.refund_orders_layout)
        LinearLayout refundOrdersLayout;
        @BindView(R.id.tv_pay_offline_orders_count)
        TextView tvPayOfflineOrdersCount;
        @BindView(R.id.pay_offline_layout)
        LinearLayout payOfflineLayout;
        @BindView(R.id.tv_history_orders_count)
        TextView tvHistoryOrdersCount;
        @BindView(R.id.history_orders_layout)
        LinearLayout historyOrdersLayout;
        @BindView(R.id.orders_counts_layout)
        LinearLayout ordersCountsLayout;

        HeadViewHolder(View view) {ButterKnife.bind(this, view);}
    }


    private static class ResultZip extends HljHttpResultZip {
        @HljRZField
        HljHttpData<List<ServiceOrder>> serviceOrdersData;
        @HljRZField
        List<CustomSetmealOrder> customSetmealOrders;
        @HljRZField
        ServiceOrderCountInfo serviceOrderCountInfo;
    }

}
