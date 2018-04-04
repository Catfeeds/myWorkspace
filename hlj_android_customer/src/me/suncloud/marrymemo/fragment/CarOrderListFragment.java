package me.suncloud.marrymemo.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hunlijicalendar.ResizeAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.CarOrder;
import me.suncloud.marrymemo.model.CarSubOrder;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.AfterConfirmReceiveActivity;
import me.suncloud.marrymemo.view.AfterPayActivity;
import me.suncloud.marrymemo.view.CarOrderDetailActivity;
import me.suncloud.marrymemo.view.CarOrderPaymentActivity;
import me.suncloud.marrymemo.view.CommentCarActivity;
import me.suncloud.marrymemo.view.RefundCarOrderActivity;
import me.suncloud.marrymemo.view.RefundCarOrderDetailActivity;
import rx.Subscriber;

/**
 * Created by werther on 15/9/23.
 */
public class CarOrderListFragment extends RefreshFragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener<ListView>,
        AbsListView.OnScrollListener, ObjectBindAdapter.ViewBinder<CarOrder> {

    private static final int ITEMS_LIMIT = 10;
    private View rootView;
    private PullToRefreshListView listView;
    private int emptyHintId;
    private ObjectBindAdapter<CarOrder> adapter;
    private ArrayList<CarOrder> orders;
    private int currentPage = 0;
    private String currentUrl;
    private View footView;
    private View headView;
    private View loadView;
    private View endView;
    private boolean isEnd;
    private boolean isLoad;
    private View progressBar;
    private DisplayMetrics dm;
    private Dialog dialog;
    private TextView refundOrdersCountTv;
    private int refundOrdersCount;
    private SparseBooleanArray collapseFlags;
    private int itemViewHeight;
    private int singleAppendHeight;
    private Dialog confirmShippingDlg;
    private boolean needFresh;
    private Dialog confirmServiceDlg;
    private Subscriber<PayRxEvent> paySubscriber;

    public static CarOrderListFragment newInstance() {
        Bundle args = new Bundle();
        CarOrderListFragment fragment = new CarOrderListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dm = getResources().getDisplayMetrics();
        itemViewHeight = (int) (88.5 * dm.density);
        singleAppendHeight = (int) (36 * dm.density);

        emptyHintId = R.string.no_item;
        collapseFlags = new SparseBooleanArray();

        headView = getActivity().getLayoutInflater()
                .inflate(R.layout.order_list_head2, null, false);
        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);

        headView.findViewById(R.id.refund_orders_layout)
                .setOnClickListener(this);
        headView.findViewById(R.id.refund_orders_layout)
                .setVisibility(View.GONE);
        headView.findViewById(R.id.orders_counts_layout)
                .setVisibility(View.GONE);
        headView.findViewById(R.id.bottom_line)
                .setVisibility(View.GONE);
        refundOrdersCountTv = (TextView) headView.findViewById(R.id.tv_refund_orders_count);

        orders = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(),
                orders,
                R.layout.car_product_order_list_item);

        EventBus.getDefault()
                .register(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);
        listView = (PullToRefreshListView) rootView.findViewById(R.id.list);

        progressBar = rootView.findViewById(R.id.progressBar);

        listView.getRefreshableView()
                .addFooterView(footView);
        listView.getRefreshableView()
                .addHeaderView(headView);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(this);

        adapter.setViewBinder(this);
        listView.getRefreshableView()
                .setAdapter(adapter);
        currentUrl = Constants.getAbsUrl(Constants.HttpPath.CAR_PRODUCT_ORDER_LIST);

        if (CommonUtil.isCollectionEmpty(orders)) {
            progressBar.setVisibility(View.VISIBLE);
            currentPage = 1;

            endView.setVisibility(View.GONE);
            loadView.setVisibility(View.GONE);

            new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));

            //            new GetRefundOrdersCount().executeOnExecutor(Constants.LISTTHEADPOOL);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        if (needFresh) {
            refresh();
        }

        super.onResume();
    }

    @Override
    public void refresh(Object... params) {
        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;

        endView.setVisibility(View.GONE);
        loadView.setVisibility(View.GONE);

        new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                String.format(currentUrl, currentPage));
    }

    private void setRefundOrdersView() {
        if (refundOrdersCount > 0) {
            headView.findViewById(R.id.refund_orders_layout)
                    .setVisibility(View.VISIBLE);
            headView.findViewById(R.id.orders_counts_layout)
                    .setVisibility(View.VISIBLE);
            headView.findViewById(R.id.bottom_line)
                    .setVisibility(View.VISIBLE);
            refundOrdersCountTv.setText(getString(R.string.label_order_count2, refundOrdersCount));
        } else {
            headView.findViewById(R.id.refund_orders_layout)
                    .setVisibility(View.GONE);
            headView.findViewById(R.id.orders_counts_layout)
                    .setVisibility(View.GONE);
            headView.findViewById(R.id.bottom_line)
                    .setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refund_orders_layout:
                Intent intent = new Intent(getActivity(), RefundCarOrderActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listView.getRefreshableView()
                .setAdapter(null);
        CommonUtil.unSubscribeSubs(paySubscriber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault()
                .unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CarOrder order = (CarOrder) parent.getAdapter()
                .getItem(position);
        if (order != null) {
            if (order.getStatus() == 24) {
                Intent intent = new Intent(getActivity(), RefundCarOrderDetailActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            } else {
                Intent intent = new Intent(getActivity(), CarOrderDetailActivity.class);
                intent.putExtra("order", order);
                intent.putExtra("id", order.getId());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right,
                        R.anim.activity_anim_default);
            }
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;

            new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
            //            new GetRefundOrdersCount().executeOnExecutor(Constants.LISTTHEADPOOL);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                            String.format(currentUrl, currentPage));
                } else {
                    break;
                }
        }
    }

    @Override
    public void onScroll(
            AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void setViewValue(View view, final CarOrder carOrder, final int position) {
        if (view.getTag() == null) {
            view.setTag(new ViewHolder(view));
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvMerchantName.setText(R.string.label_car_order);
        holder.tvOrderStatus.setText(String.valueOf(carOrder.getStatusStr()));

        holder.tvRedMoney.setVisibility(View.GONE);

        switch (carOrder.getStatus()) {
            case 10:
                // 等待商家接单
                double totalPrice = carOrder.getOriginActualMoney() - carOrder.getAidMoney();
                if (totalPrice < 0) {
                    totalPrice = 0;
                }
                holder.tvTotalPrice.setText(getString(R.string.label_total_price5,
                        Util.formatDouble2StringPositive(totalPrice)));
                holder.actionsLayout.setVisibility(View.GONE);
                break;
            case 11:
                // 待付款
                double obligationPrice = carOrder.getOriginActualMoney() - carOrder.getAidMoney()
                        - carOrder.getRedPacketMoney();
                if (obligationPrice < 0) {
                    obligationPrice = 0;
                }
                holder.tvTotalPrice.setText(getString(R.string.label_total_price5,
                        Util.formatDouble2StringPositive(obligationPrice)));
                holder.actionsLayout.setVisibility(View.VISIBLE);
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.btnAction.setText(R.string.label_go_pay);
                holder.btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CarOrderPaymentActivity.class);
                        intent.putExtra("id", carOrder.getId());
                        intent.putExtra("total_price",
                                carOrder.getOriginActualMoney() - carOrder.getAidMoney());
                        intent.putExtra("pay_all_saved_money",
                                carOrder.getPayAllSavedMoneyExpect());
                        intent.putExtra("deposit_percent", carOrder.getEarnestPercent());
                        if (!JSONUtil.isEmpty(carOrder.getRedPacketNo())) {
                            intent.putExtra("red_packet_no", carOrder.getRedPacketNo());
                            intent.putExtra("red_packet_money", carOrder.getRedPacketMoney());
                        }
                        // 拼接请求红包需要的参数
                        JSONObject extraObj = new JSONObject();
                        JSONArray extraArray = new JSONArray();
                        try {
                            for (CarSubOrder item : carOrder.getSubOrders()) {
                                JSONObject itemObj = new JSONObject();
                                itemObj.put("sku_id",
                                        item.getCarSku()
                                                .getId());
                                itemObj.put("product_id",
                                        item.getCarProduct()
                                                .getId());
                                itemObj.put("quantity", item.getQuantity());

                                extraArray.put(itemObj);
                            }

                            extraObj.put("sub_items", extraArray);
                            extraObj.put("order_id", carOrder.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        intent.putExtra("extra_json_string", extraObj.toString());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                break;
            case 87:
                // 已付款
                if (carOrder.isPayAll()) {
                    // 已付全款
                    holder.tvTotalPrice.setText(getString(R.string.label_real_pay3,
                            Util.formatDouble2StringPositive(carOrder.getPaidMoney())));
                    holder.actionsLayout.setVisibility(View.VISIBLE);
                    holder.btnAction.setVisibility(View.VISIBLE);
                    holder.btnAction.setText(R.string.label_confirm_service);
                    holder.btnAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmService(carOrder, position);
                        }
                    });
                } else {
                    // 已付定金,去支付余款
                    holder.tvRedMoney.setVisibility(View.VISIBLE);
                    double redMoney = carOrder.getOriginActualMoney() - carOrder
                            .getRedPacketMoney() - carOrder.getAidMoney() - carOrder.getPaidMoney();
                    if (redMoney < 0) {
                        redMoney = 0;
                    }
                    holder.tvRedMoney.setText(getString(R.string.label_rest_to_pay2,
                            Util.formatDouble2String(redMoney)));
                    holder.tvTotalPrice.setText(getString(R.string.label_paid_money2,
                            Util.formatDouble2StringPositive(carOrder.getPaidMoney())));
                    holder.actionsLayout.setVisibility(View.VISIBLE);
                    holder.btnAction.setVisibility(View.VISIBLE);
                    holder.btnAction.setText(R.string.label_pay_rest_money3);
                    final double finalRedMoney = redMoney;
                    holder.btnAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("id", carOrder.getId());
                            } catch (JSONException ignored) {

                            }
                            if (paySubscriber == null) {
                                paySubscriber = initSubscriber();
                            }
                            ArrayList<String> payTypes = null;
                            if (Session.getInstance()
                                    .getDataConfig(v.getContext()) != null) {
                                payTypes = Session.getInstance()
                                        .getDataConfig(v.getContext())
                                        .getPayTypes();
                            }
                            new PayConfig.Builder(getActivity()).params(jsonObject)
                                    .path(Constants.HttpPath.CAR_PRODUCT_PAY_REST)
                                    .price(finalRedMoney)
                                    .subscriber(paySubscriber)
                                    .payAgents(payTypes, DataConfig.getPayAgents())
                                    .build()
                                    .pay();
                        }
                    });
                }
                break;
            case 90:
                // 交易成功,待评价
                holder.tvTotalPrice.setText(getString(R.string.label_real_pay3,
                        Util.formatDouble2StringPositive(carOrder.getPaidMoney())));
                holder.actionsLayout.setVisibility(View.VISIBLE);
                holder.btnAction.setVisibility(View.VISIBLE);
                holder.btnAction.setText(R.string.label_review2);
                holder.btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent comment = new Intent(getActivity(), CommentCarActivity.class);
                        comment.putExtra(CommentCarActivity.ARG_ORDER_ID, carOrder.getId());
                        startActivity(comment);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                });
                break;
            case 92:
                // 交易成功,已评价
                holder.tvTotalPrice.setText(getString(R.string.label_real_pay3,
                        Util.formatDouble2StringPositive(carOrder.getPaidMoney())));
                holder.actionsLayout.setVisibility(View.GONE);
                break;
            case 15:
            case 91:
            case 93:
                // 订单已关闭
                holder.tvTotalPrice.setText(getString(R.string.label_total_price5,
                        Util.formatDouble2StringPositive(carOrder.getOriginActualMoney() -
                                carOrder.getAidMoney())));
                holder.actionsLayout.setVisibility(View.GONE);
                break;
            case 24:
                // 退款成功
                holder.tvRedMoney.setVisibility(View.VISIBLE);
                holder.tvRedMoney.setText(getString(R.string.label_refunded_money2,
                        Util.formatDouble2String(carOrder.getRefundedMoney())));
                holder.tvTotalPrice.setText(getString(R.string.label_real_pay3,
                        Util.formatDouble2StringPositive(carOrder.getPaidMoney())));
                holder.actionsLayout.setVisibility(View.GONE);
                break;
            case 22:
                holder.tvRedMoney.setVisibility(View.GONE);
                holder.tvTotalPrice.setVisibility(View.GONE);
                holder.actionsLayout.setVisibility(View.GONE);
                break;
        }

        if (JSONUtil.isEmpty(carOrder.getRedPacketNo())) {
            holder.redPacketLayout.setVisibility(View.GONE);
        } else {
            holder.redPacketLayout.setVisibility(View.VISIBLE);
        }

        holder.itemsLayout.removeAllViews();
        int appendHeight = 0;
        int appendHeightAll = 0;
        for (int i = 0; i < carOrder.getSubOrders()
                .size(); i++) {
            final CarSubOrder subOrder = carOrder.getSubOrders()
                    .get(i);
            View itemView = getActivity().getLayoutInflater()
                    .inflate(R.layout.product_order_item, null);
            TextView tvProductTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ImageView imgCover = (ImageView) itemView.findViewById(R.id.img_cover);
            TextView tvSkuInfo = (TextView) itemView.findViewById(R.id.tv_sku_info);
            TextView tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            TextView tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);
            TextView tvActivityOverHint = (TextView) itemView.findViewById(R.id.tv_activity_over);

            tvProductTitle.setText(subOrder.getCarProduct()
                    .getTitle());

            String url = JSONUtil.getImagePath(subOrder.getCarProduct()
                    .getCover(), imgCover.getLayoutParams().width);
            if (!JSONUtil.isEmpty(url)) {
                imgCover.setTag(url);
                ImageLoadTask task = new ImageLoadTask(imgCover, null, 0);
                task.loadImage(url,
                        imgCover.getLayoutParams().width,
                        ScaleMode.WIDTH,
                        new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
            } else {
                imgCover.setImageBitmap(null);
            }

            tvSkuInfo.setText(getString(R.string.label_sku2,
                    subOrder.getCarSku()
                            .getSkuNames()));
            tvPrice.setText(getString(R.string.label_price,
                    CommonUtil.formatDouble2String((subOrder.getActualMoney() / subOrder
                            .getQuantity()))));
            tvQuantity.setText("x" + String.valueOf(subOrder.getQuantity()));

            holder.itemsLayout.addView(itemView);

            if (carOrder.getStatus() == 10) {
                if (subOrder.getActivityStatus() == 2) {
                    tvActivityOverHint.setVisibility(View.VISIBLE);
                    if (i < ITEMS_LIMIT) {
                        appendHeight += singleAppendHeight;
                    }

                    appendHeightAll += singleAppendHeight;
                } else {
                    tvActivityOverHint.setVisibility(View.GONE);
                }
            } else {
                tvActivityOverHint.setVisibility(View.GONE);
            }

            if (carOrder.getSubOrders()
                    .size() > ITEMS_LIMIT) {
                holder.collapseLayout.setVisibility(View.VISIBLE);
                holder.tvRestLabel.setText(getString(R.string.label_show_rest_product,
                        carOrder.getSubOrders()
                                .size() - ITEMS_LIMIT));
                final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder
                        .itemsLayout.getLayoutParams();
                if (collapseFlags.get(position)) {
                    params.height = itemViewHeight * carOrder.getSubOrders()
                            .size() + appendHeightAll;
                } else {
                    params.height = itemViewHeight * ITEMS_LIMIT + appendHeight;
                }

                holder.collapseLayout.setOnClickListener(new CollapseListener(position,
                        holder.itemsLayout,
                        carOrder.getSubOrders()
                                .size(),
                        holder.imgArrow,
                        holder.tvRestLabel,
                        appendHeight,
                        appendHeightAll));
            } else {
                holder.collapseLayout.setVisibility(View.GONE);
                final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder
                        .itemsLayout.getLayoutParams();
                params.height = itemViewHeight * carOrder.getSubOrders()
                        .size() + appendHeight;
            }
        }
    }

    private void confirmService(final CarOrder order, final int position) {
        if (order == null || !order.isPayAll() || (confirmServiceDlg != null && confirmServiceDlg
                .isShowing())) {
            return;
        }

        confirmServiceDlg = new Dialog(getActivity(), R.style.BubbleDialogTheme);
        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_confirm, null);
        TextView msgAlertTv = (TextView) v.findViewById(R.id.tv_alert_msg);
        final Button confirmBtn = (Button) v.findViewById(R.id.btn_confirm);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        msgAlertTv.setText(R.string.msg_confirm_service);
        confirmBtn.setText(R.string.label_confirm_service2);
        cancelBtn.setText(R.string.label_wrong_action);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmServiceDlg.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                postConfirmService(order, position);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmServiceDlg.dismiss();
            }
        });
        confirmServiceDlg.setContentView(v);
        Window window = confirmServiceDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(getActivity());
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        confirmServiceDlg.show();
    }

    private void postConfirmService(final CarOrder order, final int position) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", order.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new StatusHttpPostTask(getActivity(), new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject orderObject = (JSONObject) object;
                if (orderObject != null) {
                    CarOrder newOrder = new CarOrder(orderObject);
                    // 支付成功,发送刷新订单列表的消息
                    orders.set(position, newOrder);
                    adapter.notifyDataSetChanged();
                    Intent intent = new Intent(getActivity(), AfterConfirmReceiveActivity.class);
                    intent.putExtra("is_car_order", true);
                    intent.putExtra("car_order", newOrder);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_bottom, 0);
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(getActivity(),
                        returnStatus,
                        R.string.msg_fail_to_confirm_service,
                        network);
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CONFIRM_CAR_ORDER_SERVICE),
                jsonObject.toString());
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.CAR_ORDER_REFRESH_WITH_OBJECT) {
            CarOrder newOrder = null;
            try {
                newOrder = (CarOrder) event.getObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (newOrder != null) {
                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i)
                            .getId()
                            .equals(newOrder.getId())) {
                        orders.set(i, newOrder);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } else if (event.getType() == MessageEvent.EventType.CAR_ORDER_REFRESH_FLAG) {
            needFresh = true;
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'product_order_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.items_layout)
        LinearLayout itemsLayout;
        @BindView(R.id.tv_total_price)
        TextView tvTotalPrice;
        @BindView(R.id.actions_layout)
        LinearLayout actionsLayout;
        @BindView(R.id.tv_order_status)
        TextView tvOrderStatus;
        @BindView(R.id.collapse_button_layout)
        LinearLayout collapseLayout;
        @BindView(R.id.red_packet_layout)
        LinearLayout redPacketLayout;
        @BindView(R.id.tv_rest_label)
        TextView tvRestLabel;
        @BindView(R.id.img_arrow)
        ImageView imgArrow;
        @BindView(R.id.btn_action)
        Button btnAction;
        @BindView(R.id.tv_red_money)
        TextView tvRedMoney;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class CollapseListener implements View.OnClickListener {
        int position;
        View itemsLayout;
        int itemsCount;
        View imgView;
        TextView collapseLabel;
        int appendHeight;
        int appendHeightAll;

        public CollapseListener(int p, View v, int c, View v2, TextView label, int ah, int aha) {
            position = p;
            itemsLayout = v;
            itemsCount = c;
            imgView = v2;
            collapseLabel = label;
            appendHeight = ah;
            appendHeightAll = aha;
        }

        @Override
        public void onClick(View v) {
            ResizeAnimation itemsRA;
            if (collapseFlags.get(position)) {
                // 已经展开过,现在收起
                imgView.startAnimation(AnimUtil.getAnimArrowDown(getActivity()));

                itemsRA = new ResizeAnimation(itemsLayout,
                        itemViewHeight * ITEMS_LIMIT + appendHeight);
                itemsRA.setDuration(100);
                itemsRA.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        collapseFlags.put(position, false);
                        collapseLabel.setText(getString(R.string.label_show_rest_product,
                                itemsCount - ITEMS_LIMIT));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                // 没有展开过,现在展开
                imgView.startAnimation(AnimUtil.getAnimArrowUp(getActivity()));

                itemsRA = new ResizeAnimation(itemsLayout,
                        itemViewHeight * itemsCount + appendHeightAll);
                itemsRA.setDuration(100);
                itemsRA.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        collapseFlags.put(position, true);
                        collapseLabel.setText(getString(R.string.label_hide_rest_product,
                                itemsCount - ITEMS_LIMIT));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            itemsLayout.startAnimation(itemsRA);
        }
    }

    private class GetOrdersTask extends AsyncTask<String, Object, JSONObject> {
        private String url;

        public GetOrdersTask() {
            needFresh = false;
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (url.equals(String.format(currentUrl, currentPage))) {
                loadView.setVisibility(View.INVISIBLE);
                listView.onRefreshComplete();
                listView.getRefreshableView()
                        .setSelection(0);
                int size = 0;
                if (jsonObject != null) {
                    ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject
                            ("status"));
                    if (returnStatus.getRetCode() == 0) {
                        JSONObject dataObj = jsonObject.optJSONObject("data");
                        if (dataObj != null) {
                            JSONArray jsonArray = dataObj.optJSONArray("list");
                            if (jsonArray != null && jsonArray.length() > 0) {
                                if (currentPage == 1) {
                                    orders.clear();
                                }
                                size = jsonArray.length();
                                for (int i = 0; i < size; i++) {
                                    JSONObject orderObject = jsonArray.optJSONObject(i);
                                    Log.d("CarOrderListFragment", orderObject.toString());
                                    CarOrder order = new CarOrder(orderObject);
                                    orders.add(order);
                                }
                            }
                            if (onTabTextChangeListener != null) {
                                int totalCount = dataObj.optInt("total_count", 0);
                                onTabTextChangeListener.onTabTextChange(totalCount);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                if (size < 20) {
                    isEnd = true;
                    footView.findViewById(R.id.no_more_hint)
                            .setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                } else {
                    isEnd = false;
                    footView.findViewById(R.id.no_more_hint)
                            .setVisibility(View.GONE);
                    loadView.setVisibility(View.INVISIBLE);
                }

                if (orders.isEmpty() && refundOrdersCount == 0) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = rootView.findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_hint);
                    ImageView imgNetHint = (ImageView) emptyView.findViewById(R.id.img_net_hint);
                    TextView textEmptyHint = (TextView) emptyView.findViewById(R.id
                            .text_empty_hint);

                    imgEmptyHint.setVisibility(View.VISIBLE);
                    imgNetHint.setVisibility(View.VISIBLE);
                    textEmptyHint.setVisibility(View.VISIBLE);

                    if (JSONUtil.isNetworkConnected(getActivity())) {
                        imgEmptyHint.setImageResource(R.mipmap.icon_empty_order);
                        imgNetHint.setVisibility(View.GONE);
                        textEmptyHint.setText(emptyHintId);
                    } else {
                        imgEmptyHint.setVisibility(View.GONE);
                        textEmptyHint.setText(R.string.net_disconnected);
                    }
                }

                if (orders.isEmpty()) {
                    endView.setVisibility(View.GONE);
                }

                isLoad = false;
            }


            super.onPostExecute(jsonObject);
        }
    }

    private class GetRefundOrdersCount extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.REFUND_CAR_ORDERS_COUNT);
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    JSONObject dataObj = jsonObject.optJSONObject("data");
                    if (dataObj != null) {
                        refundOrdersCount = dataObj.optInt("count", 0);
                    }
                }

                setRefundOrdersView();
            }

            super.onPostExecute(jsonObject);
        }
    }

    private Subscriber<PayRxEvent> initSubscriber() {
        return new RxBusSubscriber<PayRxEvent>() {
            @Override
            protected void onEvent(PayRxEvent rxEvent) {
                switch (rxEvent.getType()) {
                    case PAY_SUCCESS:
                        needFresh = true;
                        Intent intent = new Intent(getActivity(), AfterPayActivity.class);
                        intent.putExtra(AfterPayActivity.ARG_ORDER_TYPE,
                                Constants.OrderType.WEDDING_CAR_ORDER);
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
                }
            }
        };
    }

}
