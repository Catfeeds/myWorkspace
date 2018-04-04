package com.hunliji.marrybiz.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.customer.CustomerUser;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.api.order.OrderApi;
import com.hunliji.marrybiz.model.Label;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.NewOrder;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.NewOrderDetailActivity;
import com.hunliji.marrybiz.view.OrderConfirmActivity;
import com.hunliji.marrybiz.view.chat.WSMerchantChatActivity;
import com.hunliji.marrybiz.view.orders.UploadProtocolImageActivity;
import com.hunliji.marrybiz.widget.PriceKeyboardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by luohanlin on 15/6/29.
 */
public class OnlineOrdersListFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<NewOrder>, AdapterView.OnItemClickListener, AbsListView.OnScrollListener,
        PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = OnlineOrdersListFragment.class.getSimpleName();
    public static final int STATUS_FOR_SEARCH_ORDERS = -2;
    private View rootView;
    // 这个Status并不是orders当中的status,接口设计成用status的值去获取一个类型的订单,这个类型中的订单列表中的status可能是不一样的
    // 比如说,使用status=91这个参数去获取已关闭订单列表,取到的订单数据的status可能是91,93,15等
    private int status;
    private String tabName;
    private PullToRefreshListView listView;
    private ObjectBindAdapter<NewOrder> adapter;
    private ArrayList<NewOrder> orders;
    private View progressBar;
    private int emptyHintDrawableId;
    private int emptyHintId;
    private boolean isEnd;
    private boolean isLoad;
    private int currentPage;
    private View footView;
    private View loadView;
    private View endView;
    private int imgWidth;
    private int imgHeight;
    private Dialog acceptDlg;
    private Dialog changePriceDlg;
    private DisplayMetrics dm;
    private int currentChangePosition = -1;
    private int currentRejectOrderPosition = -1;

    private ArrayList<Label> reasons;
    private Dialog rejectDlg;
    private Label selectReason;
    private OnlineOrdersFragment onLineOrdersFragment;
    private Dialog confirmDlg;
    private int margin;
    private HljHttpSubscriber confirmSub;
    private Dialog confirmRestDlg;
    private HljHttpSubscriber changePricesSub;
    private String keyword;


    public static OnlineOrdersListFragment newInstance(int status, String name) {
        OnlineOrdersListFragment fragment = new OnlineOrdersListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, status);
        args.putString(ARG_PARAM2, name);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dm = getResources().getDisplayMetrics();
        margin = Math.round(dm.density * 4);
        Point point = JSONUtil.getDeviceSize(getActivity());
        imgWidth = Math.round(point.x * 100 / 320);
        imgHeight = Math.round(imgWidth * 212 / 338);

        orders = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(), orders, R.layout.online_order_list_item);

        if (getArguments() != null) {
            status = getArguments().getInt(ARG_PARAM1);
            tabName = getArguments().getString(ARG_PARAM2);
        }

        emptyHintId = R.string.hint_no_orders;
        emptyHintDrawableId = R.mipmap.icon_empty_order;
        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more, null);
        loadView = footView.findViewById(R.id.loading);
        endView = footView.findViewById(R.id.no_more_hint);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        listView = rootView.findViewById(R.id.list);
        listView.getRefreshableView()
                .addFooterView(footView);
        adapter.setViewBinder(this);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);

        progressBar = rootView.findViewById(R.id.progressBar);
        if (status != STATUS_FOR_SEARCH_ORDERS) {
            if (orders.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                if (!isLoad) {
                    currentPage = 1;
                    endView.setVisibility(View.GONE);
                    loadView.setVisibility(View.GONE);

                    // 加载订单列表
                    new OrderListTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl());
                }
            }
        }

        if (!EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .register(this);
        }

        return rootView;
    }

    @Override
    public void refresh(Object... params) {
        if (params.length > 0) {
            keyword = (String) params[0];
        }
        progressBar.setVisibility(View.VISIBLE);
        currentPage = 1;
        orders.clear();
        footView.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();

        // 加载订单列表
        new OrderListTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl());
    }

    private String getUrl() {
        String url;
        if (status > 0) {
            url = Constants.getAbsUrl(Constants.HttpPath.GET_NEW_ORDERS) + "&status=" + String
                    .valueOf(
                    status) + "&page=%s";
            url = String.format(url, currentPage);
        } else {
            if (status == STATUS_FOR_SEARCH_ORDERS) {
                url = Constants.getAbsUrl(Constants.HttpPath.GET_SEARCH_ORDERS);
                url = String.format(url, currentPage, keyword);
            } else {
                url = Constants.getAbsUrl(Constants.HttpPath.GET_NEW_ORDERS) + "&page=%s";
                url = String.format(url, currentPage);
            }
        }

        return url;
    }

    @Override
    public void setViewValue(View view, final NewOrder order, final int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.buyerNameTv = (TextView) view.findViewById(R.id.tv_user_nick);
            holder.paidMoneyLayout = view.findViewById(R.id.money_layout);
            holder.titleTv = (TextView) view.findViewById(R.id.tv_title);
            holder.orderStatusTv = (TextView) view.findViewById(R.id.tv_order_status);
            holder.coverImg = (ImageView) view.findViewById(R.id.img_cover);
            holder.serveTimeTv = (TextView) view.findViewById(R.id.tv_serve_time);
            holder.priceTv = (TextView) view.findViewById(R.id.tv_price);
            holder.restToPayLayout = view.findViewById(R.id.rest_to_pay_layout);
            holder.restPayTv = (TextView) view.findViewById(R.id.tv_rest_to_pay);
            holder.restPayLabelTv = (TextView) view.findViewById(R.id.tv_rest_to_pay_label);
            holder.paidOrTotalMoneyTv = (TextView) view.findViewById(R.id.tv_paid_or_total_money);
            holder.paidOrTotalMoneyLabelTv = (TextView) view.findViewById(R.id
                    .tv_paid_or_total_money_label);
            holder.invalidOrderView = view.findViewById(R.id.img_invalid_order);
            holder.actionsHolderLayout = view.findViewById(R.id.order_action_layout);
            holder.btnAction1 = (Button) view.findViewById(R.id.btn_action1);
            holder.btnAction2 = (Button) view.findViewById(R.id.btn_action2);
            holder.btnAction3 = (Button) view.findViewById(R.id.btn_action3);
            holder.isInstallment = view.findViewById(R.id.ic_installment);
            holder.isIntentMoney = view.findViewById(R.id.ic_intent_money);
            holder.salesLayout = view.findViewById(R.id.sales_layout);
            holder.tvSalesText = (TextView) view.findViewById(R.id.tv_sales_text);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.coverImg
                    .getLayoutParams();
            params.width = imgWidth;
            params.height = imgHeight;

            view.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder != null) {
            // 重置颜色
            holder.restPayLabelTv.setTextColor(ContextCompat.getColor(getContext(),
                    R.color.colorGray));
            holder.buyerNameTv.setText(order.getBuyerRealName());
            holder.buyerNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactUser(order);
                }
            });
            holder.orderStatusTv.setText(order.getStatusStr());
            holder.isInstallment.setVisibility(order.isInstallment() ? View.VISIBLE : View.GONE);
            holder.isIntentMoney.setVisibility(order.getPayType() == 5 ? View.VISIBLE : View.GONE);
            String url = JSONUtil.getImagePath(order.getPrdCoverPath(), imgWidth);
            if (!JSONUtil.isEmpty(url)) {
                Glide.with(getActivity())
                        .load(url)
                        .into(holder.coverImg);
            }
            holder.titleTv.setText(order.getPrdTitle());
            holder.priceTv.setText(getString(R.string.label_price4,
                    Util.formatDouble2String(order.getActualPrice())));
            if (order.getWeddingTime() != null) {
                holder.serveTimeTv.setVisibility(View.VISIBLE);
                holder.serveTimeTv.setText(getString(R.string.label_serve_time2,
                        order.getWeddingTime()
                                .toString(getString(R.string.format_date_type8))));
            } else {
                holder.serveTimeTv.setVisibility(View.INVISIBLE);
            }
            if (order.getRule() != null && !TextUtils.isEmpty(order.getRule()
                    .getName())) {
                holder.salesLayout.setVisibility(View.VISIBLE);
                holder.tvSalesText.setText(order.getRule()
                        .getName());
            } else {
                holder.salesLayout.setVisibility(View.GONE);
            }

            setMoneyStatusView(order, holder);

            MerchantUser user = Session.getInstance()
                    .getCurrentUser(getContext());

            // 设置可操作的按钮部分
            switch (order.getStatus()) {
                case NewOrder.STATUS_WAITING_FOR_THE_PAYMENT:
                    // 等待付款
                    holder.actionsHolderLayout.setVisibility(View.VISIBLE);
                    holder.btnAction1.setVisibility(View.GONE);
                    holder.btnAction2.setVisibility(View.VISIBLE);
                    holder.btnAction2.setText("修改价格");
                    holder.btnAction3.setVisibility(View.GONE);

                    holder.btnAction2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ((order.getPayType() == NewOrder.PAY_TYPE_DEPOSIT || order
                                    .getPayType() == NewOrder.PAY_TYPE_INTENT) && order
                                    .getEarnestMoney() > 0) {
                                onChangePrice(position, PriceKeyboardView.MODE_BOTH);
                            } else {
                                onChangePrice(position, PriceKeyboardView.MODE_TOTAL);
                            }
                        }
                    });

                    if (user != null && user.getPropertyId() == Merchant.PROPERTY_WEDDING_PLAN &&
                            order.getProtocolPhotos() != null && order.getProtocolPhotos()
                            .size() < 12) {
                        // 婚礼策划可以上传协议
                        holder.btnAction3.setVisibility(View.VISIBLE);
                        holder.btnAction3.setText("上传协议");
                        holder.btnAction3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onUploadProtocols(order);
                            }
                        });
                    }
                    break;
                case NewOrder.STATUS_WAITING_FOR_ACCEPT_ORDER:
                    // 等待商家接单
                    holder.actionsHolderLayout.setVisibility(View.VISIBLE);
                    holder.btnAction3.setVisibility(View.GONE);
                    holder.btnAction2.setVisibility(View.VISIBLE);
                    holder.btnAction2.setText("拒绝接单");
                    holder.btnAction2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 拒绝服务
                            onDeclineOrder(position);
                        }
                    });
                    holder.btnAction1.setVisibility(View.VISIBLE);
                    holder.btnAction1.setText("同意接单");
                    holder.btnAction1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 接单
                            onAcceptOrder(order);
                        }
                    });
                    break;
                case NewOrder.STATUS_MERCHANT_ACCEPT_ORDER:
                    // 商家已接单，待服务
                    holder.actionsHolderLayout.setVisibility(View.GONE);
                    holder.btnAction1.setVisibility(View.GONE);
                    holder.btnAction2.setVisibility(View.GONE);
                    holder.btnAction3.setVisibility(View.GONE);

                    if (order.isFinished()) {
                        // 商家已确认
                        holder.actionsHolderLayout.setVisibility(View.GONE);
                    } else {
                        if (user != null && user.getPropertyId() == Merchant
                                .PROPERTY_WEDDING_PLAN && order.getProtocolPhotos() != null &&
                                order.getProtocolPhotos()
                                .size() < 12) {
                            // 婚礼策划可以上传协议
                            holder.actionsHolderLayout.setVisibility(View.VISIBLE);
                            holder.btnAction3.setVisibility(View.VISIBLE);
                            holder.btnAction3.setText("上传协议");
                            holder.btnAction3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onUploadProtocols(order);
                                }
                            });
                        }
                        if (order.getMoneyStatus() == NewOrder.MONEY_STATUS_PAID_ALL) {
                            // 此时商家的操作有（已交成片和确认服务两种）
                            NewOrder.MerchantAction confirmAction = null;
                            for (NewOrder.MerchantAction action : order.getMerchantActions()) {
                                if ("finish_order".equals(action.getAction()) ||
                                        "finish_order_photo".equals(
                                        action.getAction())) {
                                    confirmAction = action;
                                    break;
                                }
                            }
                            if (confirmAction != null) {
                                holder.actionsHolderLayout.setVisibility(View.VISIBLE);
                                holder.btnAction1.setVisibility(View.VISIBLE);
                                holder.btnAction1.setText(confirmAction.getActionTxt());
                                holder.btnAction1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // 确认服务
                                        confirmService(order);
                                    }
                                });
                            }
                        }
                    }
                    break;
                default:
                    // 剩下的情况：
                    // 交易成功，商家已确认服务，同样是以"待服务"状态显示
                    // 但商家没有操作
                    // 其他等等都没有操作
                    holder.actionsHolderLayout.setVisibility(View.GONE);
                    break;

            }

            // 设置是否是无效单
            if (order.isInvalid()) {
                holder.invalidOrderView.setVisibility(View.VISIBLE);
            } else {
                holder.invalidOrderView.setVisibility(View.GONE);
            }
        }
    }

    private void setMoneyStatusView(NewOrder order, ViewHolder holder) {
        switch (order.getStatus()) {
            case NewOrder.STATUS_WAITING_FOR_THE_PAYMENT:
                if (order.getPayType() == NewOrder.PAY_TYPE_INTENT || order.getPayType() ==
                        NewOrder.PAY_TYPE_DEPOSIT) {
                    holder.restToPayLayout.setVisibility(View.VISIBLE);
                    holder.restPayLabelTv.setText("总金额：");
                    holder.restPayTv.setVisibility(View.VISIBLE);
                    holder.restPayTv.setText(getString(R.string.label_price,
                            Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));

                    if (order.getPayType() == NewOrder.PAY_TYPE_INTENT) {
                        holder.paidOrTotalMoneyLabelTv.setText("意向金：");
                        holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                                Util.formatDouble2String(order.getIntentMoney())));
                    } else {
                        holder.paidOrTotalMoneyLabelTv.setText("定金：");
                        holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                                Util.formatDouble2String(order.getEarnestMoney())));
                    }
                } else {
                    holder.restToPayLayout.setVisibility(View.GONE);
                    holder.paidOrTotalMoneyLabelTv.setText("总金额：");
                    holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                            Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                }
                break;
            case NewOrder.STATUS_WAITING_FOR_ACCEPT_ORDER:
                holder.paidOrTotalMoneyLabelTv.setText("已付金额：");
                holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(order.getPaidMoney())));
                holder.restToPayLayout.setVisibility(View.VISIBLE);

                if (order.getMoneyStatus() == NewOrder.MONEY_STATUS_PAID_ALL) {
                    holder.restPayLabelTv.setText("总金额：");
                    holder.restPayTv.setText(getString(R.string.label_price,
                            Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                } else {
                    holder.restPayLabelTv.setText("还需支付：");
                    holder.restPayTv.setText(getString(R.string.label_price,
                            Util.formatDouble2StringPositive(order.getCustomerRealPayMoney() -
                                    order.getPaidMoney())));
                }
                break;
            case NewOrder.STATUS_MERCHANT_ACCEPT_ORDER:
                holder.paidOrTotalMoneyLabelTv.setText("已付金额：");
                holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(order.getPaidMoney())));
                holder.restToPayLayout.setVisibility(View.VISIBLE);

                if (order.getMoneyStatus() == NewOrder.MONEY_STATUS_PAID_ALL) {
                    if (order.isFinished()) {
                        // 商家已确认
                        holder.paidOrTotalMoneyLabelTv.setText("用户实付");
                        holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                                Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));

                        holder.restPayTv.setText(R.string.label_wait_user_confirm);
                        holder.restPayLabelTv.setVisibility(View.GONE);
                    } else {
                        holder.restPayLabelTv.setText("总金额：");
                        holder.restPayTv.setText(getString(R.string.label_price,
                                Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                    }
                } else {
                    holder.restPayLabelTv.setText("还需支付：");
                    holder.restPayTv.setText(getString(R.string.label_price,
                            Util.formatDouble2StringPositive(order.getCustomerRealPayMoney() -
                                    order.getPaidMoney())));
                }
                break;
            case NewOrder.STATUS_SERVICE_COMPLETE:
                holder.paidOrTotalMoneyLabelTv.setText("用户实付：");
                holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                holder.restToPayLayout.setVisibility(View.VISIBLE);

                holder.restPayTv.setText(R.string.label_both_confirm);
                holder.restPayLabelTv.setVisibility(View.GONE);
                break;
            case NewOrder.STATUS_REFUND_REVIEWING:
            case NewOrder.STATUS_REFUSE_REFUND:
            case NewOrder.STATUS_MERCHANT_REFUSE_ORDER:
                holder.paidOrTotalMoneyLabelTv.setText("已付金额：");
                holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(order.getPaidMoney())));
                holder.restToPayLayout.setVisibility(View.VISIBLE);

                holder.restPayLabelTv.setText("总金额：");
                holder.restPayTv.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                break;
            case NewOrder.STATUS_REFUND_SUCCESS:
                holder.paidOrTotalMoneyLabelTv.setText("已付金额：");
                holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(order.getPaidMoney())));
                holder.restToPayLayout.setVisibility(View.VISIBLE);

                holder.restPayLabelTv.setText("退款金额：");
                holder.restPayTv.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(order.getRefundInfo()
                                .getMerchantPayMoney())));
                break;
            default:
                holder.restToPayLayout.setVisibility(View.GONE);
                holder.paidOrTotalMoneyLabelTv.setText("总金额：");
                holder.paidOrTotalMoneyTv.setText(getString(R.string.label_price,
                        Util.formatDouble2StringPositive(order.getCustomerRealPayMoney())));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewOrder order = (NewOrder) parent.getAdapter()
                .getItem(position);
        if (order != null) {
            Intent intent = new Intent(getActivity(), NewOrderDetailActivity.class);
            intent.putExtra("id", order.getId());
            if (status != -1) {
                intent.putExtra("title", tabName);
            }
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    loadView.setVisibility(View.VISIBLE);
                    currentPage++;
                    new OrderListTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl());
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
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            new OrderListTask().executeOnExecutor(Constants.LISTTHEADPOOL, getUrl());
        }
    }

    private void onContactUser(NewOrder order) {
        if (order.getUserId() != 0) {
            CustomerUser user = new CustomerUser();
            user.setId(order.getUserId());
            user.setNick(order.getUserNick());
            user.setAvatar(order.getUserAvatar());
            Intent intent = new Intent(getActivity(), WSMerchantChatActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    private void onUploadProtocols(NewOrder order) {
        if (order.getProtocolPhotos() != null && order.getProtocolPhotos()
                .size() >= 12) {
            Toast.makeText(getContext(), R.string.msg_protocol_imgs_max, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Intent intent = new Intent(getContext(), UploadProtocolImageActivity.class);
            int currentSize = order.getProtocolPhotos() == null ? 0 : order.getProtocolPhotos()
                    .size();
            intent.putExtra("photos_limit", 12 - currentSize);
            intent.putExtra("order_id", order.getId());
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    private void onAcceptOrder(final NewOrder order) {
        if (acceptDlg != null && acceptDlg.isShowing()) {
            return;
        }

        acceptDlg = new Dialog(getActivity(), R.style.BubbleDialogTheme);
        final View dialogView = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_confirm_notice, null);

        TextView noticeMsg = (TextView) dialogView.findViewById(R.id.tv_notice_msg);
        Button confirm = (Button) dialogView.findViewById(R.id.btn_notice_confirm);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_notice_cancel);
        cancel.setVisibility(View.VISIBLE);
        //日程已满接单
        if (order.getCalStatus()) {
            noticeMsg.setText(getString(R.string.msg_accept_risk_order,
                    order.getWeddingTime() == null ? "" : order.getWeddingTime()
                            .toString(getString(R.string.format_date_type12))));
        } else {
            noticeMsg.setText(R.string.msg_accept_order);
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDlg.dismiss();
                acceptOrder(order);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptDlg.cancel();
            }
        });
        acceptDlg.setContentView(dialogView);
        Window window = acceptDlg.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(getActivity());
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        acceptDlg.show();
    }

    private void onDeclineOrder(final int position) {
        currentRejectOrderPosition = position;
        progressBar.setVisibility(View.VISIBLE);
        // 先获取拒绝的原因
        reasons = new ArrayList<>();
        new GetRejectReasonTask().execute();
    }

    private void showRejectReasons() {
        if (rejectDlg != null && rejectDlg.isShowing()) {
            return;
        }
        if (currentRejectOrderPosition < 0) {
            return;
        }

        selectReason = null;
        if (rejectDlg == null) {
            rejectDlg = new Dialog(getActivity(), R.style.BubbleDialogTheme);
            View v = getActivity().getLayoutInflater()
                    .inflate(R.layout.dialog_reject_reasons, null);
            v.findViewById(R.id.btn_close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rejectDlg.dismiss();
                        }
                    });
            v.findViewById(R.id.btn_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 确定拒绝服务
                            if (selectReason == null) {
                                Toast.makeText(getActivity(),
                                        getString(R.string.msg_no_reason_selected),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                rejectDlg.dismiss();
                                declineOrder();
                            }
                        }
                    });
            final ListView listView = (ListView) v.findViewById(R.id.list);

            RejectReasonAdapter rejectReasonAdapter = new RejectReasonAdapter(reasons,
                    getActivity());
            listView.setAdapter(rejectReasonAdapter);
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    listView.setItemChecked(position, true);
                    selectReason = reasons.get(position);
                }
            });

            rejectDlg.setContentView(v);

            Window win = rejectDlg.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(getActivity());
            params.width = point.x;
            params.height = point.y / 2;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }

        rejectDlg.show();
    }

    private void declineOrder() {
        NewOrder order = orders.get(currentRejectOrderPosition);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", String.valueOf(order.getId()));
            jsonObject.put("reason", String.valueOf(selectReason.getId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new NewHttpPostTask(getActivity(), new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject resultObject = (JSONObject) obj;

                    if (resultObject.optJSONObject("status") != null) {
                        int retCode = resultObject.optJSONObject("status")
                                .optInt("RetCode", -1);
                        if (retCode == 0) {
                            // 拒绝成功
                            Toast.makeText(getActivity(),
                                    R.string.msg_success_reject_order,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            JSONObject dataObject = resultObject.optJSONObject("data");
                            if (dataObject != null) {
                                NewOrder newOrder = new NewOrder(dataObject);
                                // 刷新列表
                                EventBus.getDefault()
                                        .post(new MessageEvent(7, newOrder));
                                if (getParentOrdersFragment() != null) {
                                    getParentOrdersFragment().rejectOrder();
                                }
                            }
                        } else {
                            String msg = JSONUtil.getString(resultObject.optJSONObject("status"),
                                    "msg");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                R.string.msg_fail_to_reject_order,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(getActivity(),
                            R.string.msg_fail_to_reject_order,
                            Toast.LENGTH_SHORT)
                            .show();
                }

                currentRejectOrderPosition = -1;
            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.msg_fail_to_reject_order, Toast.LENGTH_SHORT)
                        .show();
                currentRejectOrderPosition = -1;
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.REJECT_NEW_ORDER), jsonObject.toString());
    }

    private void acceptOrder(final NewOrder order) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", String.valueOf(order.getId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.VISIBLE);
        new NewHttpPostTask(getActivity(), new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject jsonObject1 = (JSONObject) obj;

                    if (jsonObject1.optJSONObject("status") != null) {
                        int retCode = jsonObject1.optJSONObject("status")
                                .optInt("RetCode", -1);
                        if (retCode == 0) {
                            Toast.makeText(getActivity(),
                                    R.string.msg_success_accept_order,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            JSONObject dataObject = jsonObject1.optJSONObject("data");
                            if (dataObject != null) {
                                NewOrder newOrder = new NewOrder(dataObject);
                                EventBus.getDefault()
                                        .post(new MessageEvent(7, newOrder));
                                if (getParentOrdersFragment() != null) {
                                    getParentOrdersFragment().acceptOrder();
                                }
                            }
                        } else {
                            String msg = JSONUtil.getString(jsonObject1.optJSONObject("status"),
                                    "msg");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                R.string.msg_fail_to_accept_order,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(getActivity(),
                            R.string.msg_fail_to_accept_order,
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.msg_fail_to_accept_order, Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.ACCEPT_NEW_ORDER), jsonObject.toString());
    }

    /**
     * 修改价格
     *
     * @param position
     * @param type     MODE_TOTAL = 0; int MODE_DEPOSIT = 1; MODE_BOTH = 2;
     */
    private void onChangePrice(int position, final int type) {
        currentChangePosition = position;
        NewOrder order = orders.get(position);
        if (changePriceDlg != null && changePriceDlg.isShowing()) {
            return;
        }

        if (changePriceDlg == null) {
            changePriceDlg = new Dialog(getActivity(), R.style.BubbleDialogTheme);
            changePriceDlg.setContentView(R.layout.dialog_change_price_new);
            changePriceDlg.findViewById(R.id.btn_key_hide)
                    .setOnClickListener(this);
            Window win = changePriceDlg.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(getActivity());
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        PriceKeyboardView keyboardView = (PriceKeyboardView) changePriceDlg.findViewById(R.id
                .keyboard);

        keyboardView.setConfirmOnClickListener(new PriceKeyboardView.ConfirmOnClickListener() {
            @Override
            public void priceConfirm(double newTotalPrice, double newDepositPrice) {
                NewOrder order = orders.get(currentChangePosition);
                if (newTotalPrice == order.getActualPrice() && newDepositPrice == order
                        .getEarnestMoney()) {
                    Toast.makeText(getActivity(),
                            getString(R.string.msg_need_new_price),
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Double actualPrice = null;
                    Double earnestMoney = null;
                    if (newTotalPrice > 0 && newTotalPrice != order.getActualPrice()) {
                        actualPrice = newTotalPrice;
                    }
                    if (newDepositPrice > 0 && newDepositPrice != order.getEarnestMoney()) {
                        earnestMoney = newDepositPrice;
                    }
                    onChangePrice(order.getId(), actualPrice, earnestMoney);
                    changePriceDlg.dismiss();
                }
            }
        });

        keyboardView.setPriceModifyMode(type);
        keyboardView.setDepositPrices(order.getEarnestMoney(), order.getOriginalEarnestMoney());
        keyboardView.setTotalPrices(order.getActualPrice(), order.getOriginalActualPrice());

        changePriceDlg.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_key_hide:
                changePriceDlg.dismiss();
                break;
        }
    }

    private void onChangePrice(
            long orderSubId, final Double actualPrice, final Double earnestMoney) {
        changePricesSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                    @Override
                    public void onNext(JsonElement jsonElement) {
                        Toast.makeText(getActivity(),
                                R.string.msg_success_change_price,
                                Toast.LENGTH_SHORT)
                                .show();
                        NewOrder newOrder = null;
                        try {
                            newOrder = new NewOrder(new JSONObject(jsonElement.toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault()
                                .post(new MessageEvent(7, newOrder));
                    }
                })
                .build();
        OrderApi.postChangeOrderPrice(orderSubId, actualPrice, earnestMoney)
                .subscribe(changePricesSub);
    }

    private void confirmReceiveRestMoney(final NewOrder order) {
        confirmSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        Toast.makeText(getContext(),
                                R.string.msg_confirm_rest_money_success,
                                Toast.LENGTH_SHORT)
                                .show();
                        refresh();
                    }
                })
                .build();
        confirmRestDlg = DialogUtil.createDoubleButtonDialog(getContext(),
                getString(R.string.txt_msg_confirm_rest_money),
                "确认已收尾款",
                "继续等待用户",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderApi.confirmRestMoney(order.getId())
                                .subscribe(confirmSub);
                    }
                },
                null);
        confirmRestDlg.show();
    }

    private void confirmService(final NewOrder order) {
        MerchantUser user = Session.getInstance()
                .getCurrentUser(getActivity());
        if (user.getPropertyId() == 6) {
            Intent intent = new Intent(getActivity(), OrderConfirmActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
            return;
        }
        if (confirmDlg != null && confirmDlg.isShowing()) {
            return;
        }

        confirmDlg = new Dialog(getActivity(), R.style.BubbleDialogTheme);
        final View dialogView = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_confirm_notice, null);

        TextView noticeMsg = (TextView) dialogView.findViewById(R.id.tv_notice_msg);
        Button confirm = (Button) dialogView.findViewById(R.id.btn_notice_confirm);
        Button cancel = (Button) dialogView.findViewById(R.id.btn_notice_cancel);
        cancel.setVisibility(View.VISIBLE);

        noticeMsg.setText(R.string.msg_confirm_service);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.dismiss();
                submitConfirmService(order);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDlg.cancel();
            }
        });

        confirmDlg.setContentView(dialogView);
        Window window = confirmDlg.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(getActivity());
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        confirmDlg.show();
    }

    private void submitConfirmService(NewOrder order) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", String.valueOf(order.getId()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.VISIBLE);
        new NewHttpPostTask(getActivity(), new OnHttpRequestListener() {
            @Override
            public void onRequestCompleted(Object obj) {
                progressBar.setVisibility(View.GONE);
                if (obj != null) {
                    JSONObject jsonObject1 = (JSONObject) obj;

                    if (jsonObject1.optJSONObject("status") != null) {
                        int retCode = jsonObject1.optJSONObject("status")
                                .optInt("RetCode", -1);
                        if (retCode == 0) {
                            Toast.makeText(getActivity(),
                                    R.string.msg_success_confirm_service2,
                                    Toast.LENGTH_SHORT)
                                    .show();
                            JSONObject dataObject = jsonObject1.optJSONObject("data");
                            if (dataObject != null) {
                                NewOrder newOrder = new NewOrder(dataObject);
                                EventBus.getDefault()
                                        .post(new MessageEvent(7, newOrder));
                            }
                        } else {
                            String msg = JSONUtil.getString(jsonObject1.optJSONObject("status"),
                                    "msg");
                            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(getActivity(),
                                R.string.msg_fail_to_confirm_service,
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(getActivity(),
                            R.string.msg_fail_to_confirm_service,
                            Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onRequestFailed(Object obj) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(),
                        R.string.msg_fail_to_confirm_service,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.POST_CONFIRM_SERVICE),
                jsonObject.toString());
    }

    private OnlineOrdersFragment getParentOrdersFragment() {
        if (onLineOrdersFragment == null && getParentFragment() != null && getParentFragment()
                instanceof OnlineOrdersFragment) {
            onLineOrdersFragment = (OnlineOrdersFragment) getParentFragment();
        }

        return onLineOrdersFragment;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault()
                .unregister(this);
        CommonUtil.unSubscribeSubs(confirmSub, changePricesSub);
        super.onDestroy();
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.SERVICE_ORDER_REFRESH_WITH_OBJECT && event
                .getObject() != null && event.getObject() instanceof NewOrder && !orders.isEmpty
                ()) {
            NewOrder order = (NewOrder) event.getObject();
            Log.d(TAG, "New event in status=" + status);
            Log.d(TAG, "New event of order: id=" + order.getId() + ", status=" + order.getStatus());
            boolean exist = false;
            int i = 0;
            for (; i < orders.size(); i++) {
                NewOrder o = orders.get(i);
                if (o.getId()
                        .equals(order.getId())) {
                    exist = true;
                    break;
                }
            }

            int x = calcStatus(status, order.getStatus());
            if (x > 0) {
                if (exist) {
                    orders.set(i, order);
                } else {
                    orders.add(0, order);
                }
            } else {
                if (exist) {
                    if (getParentOrdersFragment() != null) {
                        orders.remove(i);
                    }
                }
            }

            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 根据现在这个列表中显示的状态和新的订单信息中的status判断这个订单该怎么更新
     *
     * @param currentListStatus
     * @param newStatus
     * @return 1:添加到当前列表或者刷新,-1:从当前列表中移除
     */
    private int calcStatus(int currentListStatus, int newStatus) {
        // 当前列表是全部订单,都是更新
        if (currentListStatus == -1) {
            return 1;
        }

        // 修改价格,更新
        if (currentListStatus == 10 && newStatus == 10) {
            return 1;
        }

        // 当前列表是待接单的列表,接单操作从当前待接单列表中移除
        if (currentListStatus == 14 && newStatus != 14) {
            return -1;
        }

        // 当前列表是已接单的列表
        if (currentListStatus == 11 && newStatus == 11) {
            return 1;
        }

        // 当前列表是退款列表,可以包含的订单状态为20,23,24,15
        if (currentListStatus == 20 && (newStatus == 20 || newStatus == 23 || newStatus == 24 ||
                newStatus == 15)) {
            return 1;
        }

        // 已关闭
        if (currentListStatus == 91 && (newStatus == 91 || newStatus == 93)) {
            return 1;
        }

        // 已完成
        if (currentListStatus == 90 && (newStatus == 92 || newStatus == 90)) {
            return 1;
        }

        return 0;
    }

    private class ViewHolder {
        TextView buyerNameTv;
        TextView orderStatusTv;
        ImageView coverImg;
        TextView titleTv;
        TextView serveTimeTv;
        TextView priceTv;
        View restToPayLayout;
        TextView restPayTv;
        TextView restPayLabelTv;
        TextView paidOrTotalMoneyTv;
        TextView paidOrTotalMoneyLabelTv;
        View actionsHolderLayout;
        Button btnAction1;
        Button btnAction2;
        Button btnAction3;
        View paidMoneyLayout;
        View isInstallment;
        View isIntentMoney;
        View salesLayout;
        TextView tvSalesText;
        View invalidOrderView;
    }

    private class GetRejectReasonTask extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.getAbsUrl(Constants.HttpPath.GET_REJECT_REASON);
            try {
                String json = JSONUtil.getStringFromUrl(getActivity(), url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(jsonObject);
            if (jsonObject != null) {
                if (jsonObject.optJSONObject("status") != null && jsonObject.optJSONObject("status")
                        .optInt("RetCode", -1) == 0) {
                    // 获取成功
                    JSONArray dataArray = jsonObject.optJSONArray("data");
                    if (dataArray != null && dataArray.length() > 0) {
                        for (int i = 0; i < dataArray.length(); i++) {
                            Label reason = new Label(null);
                            reason.setId(dataArray.optJSONObject(i)
                                    .optLong("id", 0));
                            reason.setName(dataArray.optJSONObject(i)
                                    .optString("name", ""));
                            reasons.add(reason);
                        }

                        // 显示弹窗选择
                        showRejectReasons();
                    } else {
                        Toast.makeText(getActivity(),
                                getString(R.string.msg_error_server),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    // 获取失败
                    Toast.makeText(getActivity(),
                            getString(R.string.msg_error_server),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            } else {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_error_server),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public class OrderListTask extends AsyncTask<String, Integer, JSONObject> {

        String url;

        public OrderListTask() {
            isLoad = true;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            url = params[0];
            try {
                String json = JSONUtil.getStringFromUrl(getActivity(), url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                Log.e(TAG, "Result of status: " + status);
                Log.e(TAG, "List: " + json);
                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(jsonObject);
            int size = 0;
            if (url.equals(getUrl())) {
                loadView.setVisibility(View.INVISIBLE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    JSONObject statusObject = jsonObject.optJSONObject("status");
                    if (statusObject != null && statusObject.optInt("RetCode", -1) == 0) {
                        // 数据正确
                        if (jsonObject.optJSONObject("data") != null) {
                            JSONArray jsonArray = jsonObject.optJSONObject("data")
                                    .optJSONArray("list");
                            if (jsonArray != null && jsonArray.length() > 0) {
                                size = jsonArray.length();
                                if (currentPage == 1) {
                                    orders.clear();
                                }
                                for (int i = 0; i < size; i++) {
                                    JSONObject dataObject = jsonArray.optJSONObject(i);
                                    NewOrder order = new NewOrder(dataObject);
                                    orders.add(order);
                                }
                            }
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

            if (orders.isEmpty()) {
                View emptyView = listView.getRefreshableView()
                        .getEmptyView();

                if (emptyView == null) {
                    emptyView = rootView.findViewById(R.id.empty_hint_layout);
                    listView.getRefreshableView()
                            .setEmptyView(emptyView);
                }

                ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                        .img_empty_list_hint);
                TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id.empty_hint);
                imgEmptyHint.setVisibility(View.VISIBLE);
                emptyHintTextView.setVisibility(View.VISIBLE);
                if (JSONUtil.isNetworkConnected(getActivity())) {
                    imgEmptyHint.setImageResource(emptyHintDrawableId);
                    emptyHintTextView.setText(emptyHintId);
                } else {
                    imgEmptyHint.setVisibility(View.GONE);
                    emptyHintTextView.setText(R.string.net_disconnected);
                }
            }

            isLoad = false;

        }
    }

    private class RejectReasonAdapter extends BaseAdapter {

        private ArrayList<Label> mData;
        private Context mContext;

        public RejectReasonAdapter(ArrayList<Label> mData, Context mContext) {
            this.mData = mData;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData == null ? null : mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mData == null ? 0 : mData.get(position)
                    .getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.reject_reason_item_view, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.ctv = (CheckedTextView) convertView.findViewById(R.id.ctv_amount);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder != null) {
                Label reason = mData.get(position);
                holder.ctv.setText(reason.getName());
            }

            return convertView;
        }

        private class ViewHolder {
            CheckedTextView ctv;
        }
    }
}
