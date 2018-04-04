package com.hunliji.marrybiz.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.adapter.ObjectBindAdapter;
import com.hunliji.marrybiz.model.CustomSetmeal;
import com.hunliji.marrybiz.model.CustomSetmealOrder;
import com.hunliji.marrybiz.model.MessageEvent;
import com.hunliji.marrybiz.model.Status;
import com.hunliji.marrybiz.task.NewHttpPostTask;
import com.hunliji.marrybiz.task.NewHttpPutTask;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.view.CustomOrderDetailActivity;
import com.hunliji.marrybiz.view.CustomOrderEditActivity;
import com.hunliji.marrybiz.view.CustomerReturnedActivity;
import com.hunliji.marrybiz.widget.RoundProgressDialog;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Suncloud on 2016/2/1.
 */
public class CustomOrderListFragment extends RefreshFragment implements ObjectBindAdapter
        .ViewBinder<CustomSetmealOrder>, AdapterView.OnItemClickListener, AbsListView
        .OnScrollListener, PullToRefreshBase.OnRefreshListener<ListView>, View.OnClickListener {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View rootView;
    private int type;
    private String tabName;
    private PullToRefreshListView listView;
    private ObjectBindAdapter<CustomSetmealOrder> adapter;
    private ArrayList<CustomSetmealOrder> orders;
    private View progressBar;
    private boolean isEnd;
    private boolean isLoad;
    private int currentPage;
    private int lastPage;
    private View footView;
    private View loadView;
    private TextView endView;
    private int imgWidth;
    private int imgHeight;
    private String currentUrl;
    private String keyWord;
    private Dialog dialog;
    private RoundProgressDialog progressDialog;
    private int margin;


    public static CustomOrderListFragment newInstance(int type, String name) {
        CustomOrderListFragment fragment = new CustomOrderListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, type);
        args.putString(ARG_PARAM2, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        margin = Math.round(dm.density * 4);
        Point point = JSONUtil.getDeviceSize(getActivity());
        imgWidth = Math.round(point.x * 100 / 320);
        imgHeight = Math.round(imgWidth * 212 / 338);
        orders = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(getActivity(),
                orders,
                R.layout.custom_setmeal_order_list_item,
                this);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_PARAM1);
            tabName = getArguments().getString(ARG_PARAM2);
            keyWord = getArguments().getString("keyWord");
        }
        StringBuilder builder = new StringBuilder(Constants.getAbsUrl(Constants.HttpPath
                .GET_CUSTOM_ORDERS));
        if (type > 0) {
            builder.append("&type=")
                    .append(type);
        }
        if (!JSONUtil.isEmpty(keyWord)) {
            builder.append("&keyword=")
                    .append(keyWord);
        }
        currentUrl = builder.toString();
        footView = getActivity().getLayoutInflater()
                .inflate(R.layout.list_foot_no_more_2, null);
        loadView = footView.findViewById(R.id.loading);
        endView = (TextView) footView.findViewById(R.id.no_more_hint);
        endView.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        listView = (PullToRefreshListView) rootView.findViewById(R.id.list);
        listView.getRefreshableView()
                .addFooterView(footView);
        adapter.setViewBinder(this);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(this);
        listView.setOnRefreshListener(this);
        listView.setAdapter(adapter);

        progressBar = rootView.findViewById(R.id.progressBar);
        if (orders.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            if (!isLoad) {
                currentPage = lastPage = 1;
                endView.setVisibility(View.GONE);
                loadView.setVisibility(View.GONE);
                // 加载订单列表
                new OrderListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                        String.format(currentUrl, currentPage));
            }
        }

        if (!EventBus.getDefault()
                .isRegistered(this)) {
            EventBus.getDefault()
                    .register(this);
        }

        return rootView;
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
                return new JSONObject(json).optJSONObject("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (url.equals(String.format(currentUrl, currentPage))) {
                isLoad = false;
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                if (jsonObject != null) {
                    lastPage = currentPage;
                    JSONArray array = jsonObject.optJSONArray("list");
                    if (currentPage == 1) {
                        orders.clear();
                    }
                    if (array != null && array.length() > 0) {
                        for (int i = 0, size = array.length(); i < size; i++) {
                            orders.add(new CustomSetmealOrder(array.optJSONObject(i)));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    int pageCount = jsonObject.optInt("page_count", 0);
                    isEnd = pageCount <= currentPage;
                    if (isEnd) {
                        endView.setVisibility(orders.isEmpty() ? View.GONE : View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.no_more);
                    } else {
                        endView.setVisibility(View.GONE);
                        loadView.setVisibility(View.INVISIBLE);
                    }
                } else if (!orders.isEmpty()) {
                    currentPage = lastPage;
                    endView.setVisibility(View.VISIBLE);
                    loadView.setVisibility(View.GONE);
                    endView.setText(R.string.hint_net_disconnected);
                }
                if (orders.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = rootView.findViewById(R.id.empty_hint_layout);
                        listView.getRefreshableView()
                                .setEmptyView(emptyView);
                    }
                    emptyView.setVisibility(View.VISIBLE);
                    ImageView imgEmptyHint = (ImageView) emptyView.findViewById(R.id
                            .img_empty_list_hint);
                    TextView emptyHintTextView = (TextView) emptyView.findViewById(R.id.empty_hint);
                    imgEmptyHint.setVisibility(View.VISIBLE);
                    emptyHintTextView.setVisibility(View.VISIBLE);
                    if (JSONUtil.isNetworkConnected(getActivity())) {
                        imgEmptyHint.setImageResource(R.mipmap.icon_empty_order);
                        emptyHintTextView.setText(R.string.hint_no_orders);
                    } else {
                        imgEmptyHint.setVisibility(View.GONE);
                        emptyHintTextView.setText(R.string.net_disconnected);
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }


    @Override
    public void refresh(Object... params) {
        if (params.length > 0 && params[0] instanceof String && !params[0].equals(keyWord)) {
            keyWord = (String) params[0];
            StringBuilder builder = new StringBuilder(Constants.getAbsUrl(Constants.HttpPath
                    .GET_CUSTOM_ORDERS));
            if (!JSONUtil.isEmpty(keyWord)) {
                builder.append("&keyword=")
                        .append(keyWord);
            }
            currentUrl = builder.toString();
            currentPage = 1;
            orders.clear();
            endView.setVisibility(View.GONE);
            loadView.setVisibility(View.INVISIBLE);
            adapter.notifyDataSetChanged();
            listView.onRefreshComplete();
            progressBar.setVisibility(View.VISIBLE);
            LinearLayout emptyView = (LinearLayout) listView.getRefreshableView()
                    .getEmptyView();
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
            new OrderListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_more_hint:
                onScrollStateChanged(listView.getRefreshableView(),
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CustomSetmealOrder order = (CustomSetmealOrder) parent.getAdapter()
                .getItem(position);
        if (order != null && order.getId() > 0) {
            Intent intent = new Intent(getActivity(), CustomOrderDetailActivity.class);
            intent.putExtra("order", order);
            if (!JSONUtil.isEmpty(tabName) && type > 0) {
                intent.putExtra("title", tabName);
            } else {
                intent.putExtra("title", order.getStatusStr());
            }
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_right,
                    R.anim.activity_anim_default);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;
            new OrderListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                if (view.getLastVisiblePosition() >= (view.getCount() - 5) && !isEnd && !isLoad) {
                    if (JSONUtil.isNetworkConnected(getActivity())) {
                        loadView.setVisibility(View.VISIBLE);
                        endView.setVisibility(View.GONE);
                        currentPage++;
                        new OrderListTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                                String.format(currentUrl, currentPage));
                    } else {
                        endView.setVisibility(View.VISIBLE);
                        loadView.setVisibility(View.GONE);
                        endView.setText(R.string.hint_net_disconnected);
                    }
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
    public void setViewValue(View view, CustomSetmealOrder order, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            holder.imgCover.getLayoutParams().width = imgWidth;
            holder.imgCover.getLayoutParams().height = imgHeight;
            holder.icCustom.setVisibility(View.VISIBLE);
            view.setTag(holder);
        }
        holder.tvUserNick.setText(order.getBuyerName());
        holder.tvOrderStatus.setText(order.getStatusStr());
        CustomSetmeal setmeal = order.getCustomSetmeal();
        String path = null;
        if (setmeal != null) {
            holder.tvTitle.setText(setmeal.getTitle());
            path = setmeal.getCoverPath();
        }
        path = JSONUtil.getImagePath(path, imgWidth);
        if (!JSONUtil.isEmpty(path)) {
            Glide.with(getActivity())
                    .load(path)
                    .into(holder.imgCover);
        } else {
            holder.imgCover.setImageBitmap(null);
        }
        holder.tvPrice.setText(getString(R.string.label_price4,
                Util.formatDouble2String(order.getActualPrice())));
        if (order.getWeddingTime() != null) {
            holder.tvServeTime.setVisibility(View.VISIBLE);
            holder.tvServeTime.setText(getString(R.string.label_serve_time2,
                    new DateTime(order.getWeddingTime()).toString(getString(R.string
                            .format_date_type8))));
        } else {
            holder.tvServeTime.setVisibility(View.INVISIBLE);
        }

        if (order.isInvalid()) {
            holder.tvInvalidOrder.setVisibility(View.VISIBLE);
            int paddingRight = Math.round(holder.tvInvalidOrder.getPaint()
                    .measureText(holder.tvInvalidOrder.getText()
                            .toString()) + margin + holder.tvInvalidOrder.getPaddingLeft() +
                    holder.tvInvalidOrder.getPaddingRight());
            holder.tvUserNick.setPadding(0, 0, paddingRight, 0);
        } else {
            holder.tvInvalidOrder.setVisibility(View.GONE);
            holder.tvUserNick.setPadding(0, 0, 0, 0);
        }

        holder.btnAction1.setOnClickListener(new OnOrderActionClickListener(order));
        holder.btnAction2.setOnClickListener(new OnOrderActionClickListener(order));
        holder.btnAction3.setOnClickListener(new OnOrderActionClickListener(order));
        switch (order.getStatus()) {
            case 10:
                holder.customOrderActionLayout.setVisibility(View.VISIBLE);
                holder.customPaidPriceLayout.setVisibility(View.GONE);
                holder.customRestToPayLayout.setVisibility(View.GONE);
                holder.customActionsLayout.setVisibility(View.VISIBLE);
                holder.btnAction1.setVisibility(View.VISIBLE);
                holder.btnAction3.setVisibility(View.VISIBLE);
                holder.btnAction2.setVisibility(View.GONE);
                holder.btnAction1.setText(R.string.label_accept_order2);
                holder.btnAction3.setText(R.string.label_decline_order);
                break;
            case 11:
                holder.customOrderActionLayout.setVisibility(View.VISIBLE);
                holder.customPaidPriceLayout.setVisibility(View.GONE);
                holder.customRestToPayLayout.setVisibility(View.GONE);
                holder.customActionsLayout.setVisibility(View.VISIBLE);
                holder.btnAction1.setVisibility(View.GONE);
                holder.btnAction3.setVisibility(View.VISIBLE);
                holder.btnAction2.setVisibility(View.VISIBLE);
                holder.btnAction3.setText(R.string.label_edit_protocol);
                break;
            case 87:
                holder.customOrderActionLayout.setVisibility(View.VISIBLE);
                holder.customPaidPriceLayout.setVisibility(View.VISIBLE);
                holder.tvCustomPaidMoney.setText(getString(R.string.label_price,
                        Util.formatDouble2String(order.getPaidMoney())));
                if (order.isPayAll()) {
                    holder.customRestToPayLayout.setVisibility(View.GONE);
                    holder.tvCustomPaidLabel.setText(R.string.label_paid_all);
                    if (order.isFinished()) {
                        holder.customActionsLayout.setVisibility(View.GONE);
                    } else {
                        holder.customActionsLayout.setVisibility(View.VISIBLE);
                        holder.btnAction1.setVisibility(View.VISIBLE);
                        holder.btnAction2.setVisibility(View.GONE);
                        holder.btnAction3.setVisibility(View.GONE);
                        holder.btnAction1.setText(R.string.label_confirm_service);
                    }
                } else {
                    holder.customActionsLayout.setVisibility(View.GONE);
                    holder.customRestToPayLayout.setVisibility(View.VISIBLE);
                    holder.tvCustomRestToPay.setText(getString(R.string.label_price,
                            Util.formatDouble2String(Math.max(order.getActualPrice() - order
                                            .getRedPacketMoney() - order.getPaidMoney(),
                                    0))));
                    if (order.getPaidMoney() > order.getEarnestMoney()) {
                        holder.tvCustomPaidLabel.setText(R.string.label_paid2);
                    } else {
                        holder.tvCustomPaidLabel.setText(R.string.label_paid_deposit);
                    }
                }
                break;
            case 24:
            case 20:
            case 21:
            case 23:
                holder.customOrderActionLayout.setVisibility(View.VISIBLE);
                if (order.getStatus() == 24) {
                    holder.customPaidPriceLayout.setVisibility(View.VISIBLE);
                    holder.tvCustomPaidMoney.setText(getString(R.string.label_price,
                            Util.formatDouble2String(order.getRefundPayMoney() + (order.isInvalid
                                    () ? 0 : order.getRedPacketMoney()))));
                    holder.tvCustomPaidLabel.setText(R.string.label_refunded_money);
                } else {
                    holder.customPaidPriceLayout.setVisibility(View.GONE);
                }
                holder.customRestToPayLayout.setVisibility(View.GONE);
                holder.customActionsLayout.setVisibility(View.VISIBLE);
                holder.btnAction1.setVisibility(View.GONE);
                holder.btnAction2.setVisibility(View.GONE);
                holder.btnAction3.setVisibility(View.VISIBLE);
                holder.btnAction3.setText(R.string.label_order_refunded_view);
                break;
            case 90:
            case 92:
                holder.customOrderActionLayout.setVisibility(View.VISIBLE);
                holder.customPaidPriceLayout.setVisibility(View.VISIBLE);
                holder.customRestToPayLayout.setVisibility(View.GONE);
                holder.tvCustomPaidMoney.setText(getString(R.string.label_price,
                        Util.formatDouble2String(order.getPaidMoney())));
                holder.tvCustomPaidLabel.setText(R.string.label_paid_all);
                if (!order.isFinished()) {
                    holder.customActionsLayout.setVisibility(View.VISIBLE);
                    holder.btnAction1.setVisibility(View.VISIBLE);
                    holder.btnAction2.setVisibility(View.GONE);
                    holder.btnAction3.setVisibility(View.GONE);
                    holder.btnAction1.setText(R.string.label_accomplish_order);
                } else {
                    holder.customActionsLayout.setVisibility(View.GONE);
                }
                break;
            default:
                holder.customOrderActionLayout.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault()
                .unregister(this);
        super.onDestroy();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'online_order_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.tv_invalid_order)
        TextView tvInvalidOrder;
        @BindView(R.id.tv_user_nick)
        TextView tvUserNick;
        @BindView(R.id.tv_order_status)
        TextView tvOrderStatus;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.ic_custom)
        ImageView icCustom;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_serve_time)
        TextView tvServeTime;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_custom_rest_to_pay)
        TextView tvCustomRestToPay;
        @BindView(R.id.custom_rest_to_pay_layout)
        LinearLayout customRestToPayLayout;
        @BindView(R.id.tv_custom_paid_label)
        TextView tvCustomPaidLabel;
        @BindView(R.id.tv_custom_paid_money)
        TextView tvCustomPaidMoney;
        @BindView(R.id.custom_paid_price_layout)
        LinearLayout customPaidPriceLayout;
        @BindView(R.id.btn_action3)
        Button btnAction3;
        @BindView(R.id.btn_action2)
        Button btnAction2;
        @BindView(R.id.btn_action1)
        Button btnAction1;
        @BindView(R.id.custom_actions_layout)
        LinearLayout customActionsLayout;
        @BindView(R.id.custom_order_action_layout)
        RelativeLayout customOrderActionLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.CUSTOM_ORDER_REFRESH_WITH_OBJECT && event
                .getObject() != null && event.getObject() instanceof CustomSetmealOrder &&
                !orders.isEmpty()) {
            CustomSetmealOrder order = (CustomSetmealOrder) event.getObject();
            boolean exist = false;
            int i = 0;
            for (; i < orders.size(); i++) {
                CustomSetmealOrder o = orders.get(i);
                if (o.getId()
                        .equals(order.getId())) {
                    exist = true;
                    break;
                }
            }

            int x = calcStatus(type, order.getStatus());
            if (x > 0) {
                if (exist) {
                    orders.set(i, order);
                } else {
                    orders.add(0, order);
                }
            } else if (exist) {
                orders.remove(i);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private int calcStatus(int currentListStatus, int newStatus) {
        if (currentListStatus > 0 && currentListStatus != newStatus) {
            // 不同分类下状态,移除
            switch (currentListStatus) {
                case 90:
                case 92:
                    //完成分类
                    if (newStatus != 90 && newStatus != 92) {
                        return -1;
                    }
                    break;
                case 93:
                case 91:
                    //关闭分类
                    if (newStatus != 93 && newStatus != 91) {
                        return -1;
                    }
                    break;
                case 20:
                case 21:
                case 23:
                case 24:
                    //退款分类
                    if (newStatus != 20 && newStatus != 21 && newStatus != 23 && newStatus != 24) {
                        return -1;
                    }
                    break;
                default:
                    return -1;
            }
        }
        return 1;
    }

    private class OnOrderActionClickListener implements View.OnClickListener {

        private CustomSetmealOrder order;

        private OnOrderActionClickListener(CustomSetmealOrder order) {
            this.order = order;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_action1:
                    if (order.getStatus() == 10) {
                        Intent intent = new Intent(getActivity(), CustomOrderEditActivity.class);
                        intent.putExtra("order", order);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else if (order.getStatus() == 90 || order.getStatus() == 92) {
                        showConfirmDialog(order, 1);
                    } else if (order.getStatus() == 87) {
                        showConfirmDialog(order, 2);
                    }
                    break;
                case R.id.btn_action2:
                    if (order.getStatus() == 11) {
                        Intent intent = new Intent(getActivity(), CustomOrderEditActivity.class);
                        intent.putExtra("order", order);
                        intent.putExtra("isEdit", true);
                        intent.putExtra("editType", 1);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                    break;
                case R.id.btn_action3:
                    if (order.getStatus() == 10) {
                        showConfirmDialog(order, 0);
                    } else if (order.getStatus() == 11) {
                        Intent intent = new Intent(getActivity(), CustomOrderEditActivity.class);
                        intent.putExtra("order", order);
                        intent.putExtra("isEdit", true);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    } else if (order.getStatus() == 24 || order.getStatus() == 20 || order
                            .getStatus() == 21 || order.getStatus() == 23) {
                        //                        Intent intent = new Intent(getActivity(), order
                        // .getAuthorizationStatusObb() == 24 ? CustomerReturnedActivity.class :
                        // CustomerReturningActivity.class);
                        Intent intent = new Intent(getActivity(), CustomerReturnedActivity.class);
                        intent.putExtra("order", order);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                    break;
            }
        }
    }

    //type 0：拒绝接单 1：确认完成 2：待服务确认完成
    private void showConfirmDialog(final CustomSetmealOrder order, final int type) {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = new Dialog(getActivity(), R.style.BubbleDialogTheme);
            dialog.setContentView(R.layout.dialog_confirm_notice);
            dialog.findViewById(R.id.btn_notice_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
            Window window = dialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = JSONUtil.getDeviceSize(getActivity());
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        TextView noticeTitle = (TextView) dialog.findViewById(R.id.tv_msg_title);
        TextView noticeMsg = (TextView) dialog.findViewById(R.id.tv_notice_msg);
        dialog.findViewById(R.id.btn_notice_confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (progressDialog == null) {
                            progressDialog = JSONUtil.getRoundProgress(getActivity());
                        }
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        progressDialog.onLoadComplate();
                        if (type == 0) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id", order.getId());
                                jsonObject.put("type ", 2);
                                new NewHttpPostTask(getActivity(),
                                        new OrderHttpRequestListener(R.string.label_declined_order,
                                                R.string.msg_fail_to_reject_order),
                                        progressDialog).execute(Constants.getAbsUrl(Constants
                                                .HttpPath.CUSTOM_ORDER_RECEIVING),
                                        jsonObject.toString());
                            } catch (JSONException ignored) {

                            }
                        } else if (type == 1 || type == 2) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("order_id", order.getId());
                                new NewHttpPutTask(getActivity(),
                                        new OrderHttpRequestListener(R.string
                                                .msg_success_confirm_service,
                                                R.string.msg_fail_to_confirm_service),
                                        progressDialog).execute(Constants.getAbsUrl(Constants
                                                .HttpPath.CUSTOM_ORDER_CONFIRM),
                                        jsonObject.toString());
                            } catch (JSONException ignored) {

                            }
                        }
                    }
                });
        if (type == 0) {
            noticeTitle.setVisibility(View.VISIBLE);
            noticeTitle.setText(R.string.label_decline_order);
            noticeMsg.setText(R.string.hint_decline_order);
        } else if (type == 1) {
            noticeTitle.setVisibility(View.GONE);
            noticeMsg.setText(R.string.hint_confirm_order);
        } else if (type == 2) {
            noticeTitle.setVisibility(View.VISIBLE);
            noticeTitle.setText(R.string.hint_confirm_order);
            noticeMsg.setText(R.string.hint_confirm_order2);
        }
        dialog.show();
    }

    private class OrderHttpRequestListener implements OnHttpRequestListener {

        private int completedId;
        private int failedId;

        private OrderHttpRequestListener(int completedId, int failedId) {
            this.completedId = completedId;
            this.failedId = failedId;
        }

        @Override
        public void onRequestCompleted(Object obj) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            JSONObject object = (JSONObject) obj;
            Status status = null;
            if (object != null && object.optJSONObject("status") != null) {
                status = new Status(object.optJSONObject("status"));
            }
            if (status != null && status.getRetCode() == 0) {
                if (object.optJSONObject("data") != null) {
                    EventBus.getDefault()
                            .post(new MessageEvent(8,
                                    new CustomSetmealOrder(object.optJSONObject("data"))));
                }
                Util.showToast(getActivity(), null, completedId);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.onComplate();
                }
            } else {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Util.showToast(getActivity(),
                        status == null ? null : status.getErrorMsg(),
                        failedId);
            }
        }

        @Override
        public void onRequestFailed(Object obj) {
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            Util.showToast(getActivity(), null, failedId);
        }
    }
}
