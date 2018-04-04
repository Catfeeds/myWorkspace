package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.OfflineOrder;
import me.suncloud.marrymemo.model.OrderStatusActionsEnum;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.Util;

public class PayOfflineOrderListActivity extends HljBaseNoBarActivity implements AdapterView
        .OnItemClickListener, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener<ListView>, ObjectBindAdapter.ViewBinder<OfflineOrder> {

    private PullToRefreshListView listView;
    private int emptyHintId;
    private int currentPage = 0;
    private String currentUrl;
    private View footView;
    private View loadView;
    private View endView;
    private boolean isEnd;
    private boolean isLoad;
    private int coverWidth;
    private int coverHeight;
    private View progressBar;
    private SimpleDateFormat simpleDateFormat;
    private DisplayMetrics dm;
    private Dialog cancelDialog;
    private ObjectBindAdapter<OfflineOrder> adapter;
    private ArrayList<OfflineOrder> offlineOrders;
    private boolean backToList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_offline_orders);

        setDefaultStatusBarPadding();
        backToList = getIntent().getBooleanExtra("back_to_list", false);
        setSwipeBackEnable(!backToList);

        Point point = JSONUtil.getDeviceSize(this);
        dm = getResources().getDisplayMetrics();
        coverWidth = Math.round(point.x * 100 / 320);
        coverHeight = Math.round(coverWidth * 212 / 338);

        listView = (PullToRefreshListView) findViewById(R.id.list);
        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);
        progressBar = findViewById(R.id.progressBar);

        emptyHintId = R.string.no_item;

        offlineOrders = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, offlineOrders, R.layout.offline_order_item);

        listView.getRefreshableView()
                .addFooterView(footView);
        listView.getRefreshableView()
                .setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(this);
        listView.setOnItemClickListener(this);

        adapter.setViewBinder(this);
        listView.setAdapter(adapter);

        currentUrl = Constants.getAbsUrl(Constants.HttpPath.GET_OFFLINE_ORDERS);

        if (offlineOrders.size() == 0) {
            if (!isLoad) {
                currentPage = 0;
                endView.setVisibility(View.GONE);
                loadView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                        String.format(currentUrl, currentPage * Constants.PER_PAGE));
            }
        }
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (backToList) {
            Intent intent = new Intent(this, MyOrderListActivity.class);
            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }

    private class GetOrdersTask extends AsyncTask<String, Object, JSONObject> {

        private String url;

        public GetOrdersTask() {
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (url.equals(String.format(currentUrl, currentPage * Constants.PER_PAGE))) {
                loadView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.GONE);
                listView.onRefreshComplete();
                int size = 0;
                if (jsonObject != null) {
                    JSONObject statusObject = jsonObject.optJSONObject("status");
                    if (statusObject.optInt("RetCode", -1) == 0) {
                        JSONArray dataArray = jsonObject.optJSONArray("data");
                        if (dataArray != null && dataArray.length() > 0) {
                            if (currentPage == 0) {
                                offlineOrders.clear();
                            }
                            size = dataArray.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject dataObject = dataArray.optJSONObject(i);
                                Log.d("OrderList Data", dataObject.toString());
                                OfflineOrder offlineOrder = new OfflineOrder(dataObject);
                                offlineOrders.add(offlineOrder);
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

                if (offlineOrders.isEmpty()) {
                    View emptyView = listView.getRefreshableView()
                            .getEmptyView();
                    if (emptyView == null) {
                        emptyView = findViewById(R.id.empty_hint_layout);
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

                    if (JSONUtil.isNetworkConnected(PayOfflineOrderListActivity.this)) {
                        imgNetHint.setVisibility(View.GONE);
                        textEmptyHint.setText(emptyHintId);
                    } else {
                        imgEmptyHint.setVisibility(View.GONE);
                        textEmptyHint.setText(R.string.net_disconnected);

                    }
                }

                isLoad = false;
            }
            super.onPostExecute(jsonObject);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OfflineOrder offlineOrder = (OfflineOrder) parent.getAdapter()
                .getItem(position);
        if (offlineOrder != null) {
            Intent intent = new Intent(this, OfflineOrderDetailActivity.class);
            intent.putExtra("offline_order", offlineOrder);
            //            Intent intent = new Intent(this, AfterPayOfflineActivity.class);
            //            intent.putExtra("order_no", offlineOrder.getOrderNum());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
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
                            String.format(currentUrl, currentPage * Constants.PER_PAGE));
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
    public void setViewValue(View view, final OfflineOrder offlineOrder, int position) {
        if (view.getTag() == null) {
            ViewHolder holder = new ViewHolder();
            holder.merchantNameTv = (TextView) view.findViewById(R.id.tv_merchant_name);
            holder.orderStatusTv = (TextView) view.findViewById(R.id.tv_order_status);
            holder.priceLabelTv = (TextView) view.findViewById(R.id.tv_price_label);
            holder.priceTv = (TextView) view.findViewById(R.id.tv_price);
            holder.actionBtn = (Button) view.findViewById(R.id.btn_action);

            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        if (holder != null) {
            holder.merchantNameTv.setText(offlineOrder.getMerchantName());
            holder.orderStatusTv.setText(offlineOrder.getStatusStr());
            double price = offlineOrder.getTotalPrice() - offlineOrder.getRedPacketMoney();
            holder.priceTv.setText(Util.formatDouble2String(price < 0 ? 0 : price));
            OrderStatusActionsEnum actionsEnum = null;
            ArrayList<OrderStatusActionsEnum> actionsEnums = offlineOrder.getActionsEnums();
            for (int i = 0; i < actionsEnums.size(); i++) {
                if (actionsEnums.get(i) != OrderStatusActionsEnum.CANCEL) {
                    actionsEnum = actionsEnums.get(i);
                    break;
                }
            }
            holder.priceLabelTv.setText(R.string.label_real_pay);

            // 要有就只会有一个动作，付款
            if (actionsEnum == null) {
                holder.actionBtn.setVisibility(View.GONE);
            } else {
                holder.priceLabelTv.setText(R.string.label_consume_money);
                holder.actionBtn.setVisibility(View.VISIBLE);
                holder.actionBtn.setText(actionsEnum.action);
                final OrderStatusActionsEnum finalActionsEnum = actionsEnum;
                holder.actionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (finalActionsEnum) {
                            case ONPAY:
                                // 去付款
                                Intent intent = new Intent(PayOfflineOrderListActivity.this,
                                        PayOfflineActivity.class);
                                intent.putExtra("offline_order", offlineOrder);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                                break;
                        }
                    }
                });
            }
        }
    }

    public class ViewHolder {
        TextView merchantNameTv;
        TextView orderStatusTv;
        TextView priceLabelTv;
        TextView priceTv;
        Button actionBtn;
    }


    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 0;
            new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage * Constants.PER_PAGE));
        }
    }
}
