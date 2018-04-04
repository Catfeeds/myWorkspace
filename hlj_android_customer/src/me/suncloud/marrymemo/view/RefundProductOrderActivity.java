package me.suncloud.marrymemo.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.RefundProductOrder;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;

public class RefundProductOrderActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<RefundProductOrder>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener<ListView> {

    private int currentPage;

    @BindView(R.id.list)
    PullToRefreshListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private DisplayMetrics dm;
    private int itemViewHeight;
    private int emptyHintId;
    private int emptyHintDrawableId;
    private View footView;
    private View endView;
    private View loadView;
    private ObjectBindAdapter<RefundProductOrder> adapter;
    private ArrayList<RefundProductOrder> orders;
    private boolean isEnd;
    private boolean isLoad;
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_product_order);
        ButterKnife.bind(this);
        dm = getResources().getDisplayMetrics();
        itemViewHeight = (int) (88.5 * dm.density);

        emptyHintId = R.string.no_item;
        emptyHintDrawableId = R.drawable.icon_common_empty;

        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);

        orders = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, orders, R.layout.refund_product_order_list_item);
        adapter.setViewBinder(this);

        listView.getRefreshableView()
                .addFooterView(footView);
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);

        currentUrl = Constants.getAbsUrl(Constants.HttpPath.REFUND_PRODUCT_ORDER_LIST);
        if (orders.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            currentPage = 1;

            endView.setVisibility(View.GONE);
            loadView.setVisibility(View.GONE);

            new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
        }
    }

    @Override
    public void setViewValue(View view, RefundProductOrder order, int position) {
        if (view.getTag() == null) {
            view.setTag(new ViewHolder(view));
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvMerchantName.setText(order.getMerchant()
                .getName());
        holder.tvOrderStatus.setText(order.getSubOrder()
                .getRefundStatusStr());

        holder.tvTitle.setText(order.getSubOrder()
                .getProduct()
                .getTitle());
        String url = JSONUtil.getImagePath2(order.getSubOrder()
                .getProduct()
                .getPhoto(), holder.imgCover.getLayoutParams().width);
        if (!JSONUtil.isEmpty(url)) {
            holder.imgCover.setTag(url);
            ImageLoadTask task = new ImageLoadTask(holder.imgCover, null, 0);
            task.loadImage(url,
                    holder.imgCover.getLayoutParams().width,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            holder.imgCover.setImageBitmap(null);
        }

        holder.tvOrderPrice.setText(getString(R.string.label_order_price4,
                Util.formatDouble2String(order.getSubOrder()
                        .getActualMoney())));
        holder.tvRefundMoney.setText(getString(R.string.label_refunded_money2,
                Util.formatDouble2String(order.getRefundPayMoney())));
        holder.tvSkuInfo.setText(getString(R.string.label_sku2,
                order.getSubOrder()
                        .getSku()
                        .getName()));
        holder.tvPrice.setText(getString(R.string.label_price,
                Util.formatDouble2String(order.getSubOrder()
                        .getActualMoney() / order.getSubOrder()
                        .getQuantity())));
        holder.tvQuantity.setText("x" + String.valueOf(order.getSubOrder()
                .getQuantity()));
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
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;

            new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
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
                                    RefundProductOrder order = new RefundProductOrder(orderObject);
                                    orders.add(order);
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

                    if (JSONUtil.isNetworkConnected(RefundProductOrderActivity.this)) {
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


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file
     * 'refund_product_order_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github
     *         .com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @BindView(R.id.tv_order_status)
        TextView tvOrderStatus;
        @BindView(R.id.img_cover)
        ImageView imgCover;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_sku_info)
        TextView tvSkuInfo;
        @BindView(R.id.tv_refund_info)
        TextView tvRefundInfo;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_quantity)
        TextView tvQuantity;
        @BindView(R.id.tv_activity_over)
        TextView tvActivityOver;
        @BindView(R.id.items_layout)
        LinearLayout itemsLayout;
        @BindView(R.id.tv_order_price)
        TextView tvOrderPrice;
        @BindView(R.id.tv_refund_money)
        TextView tvRefundMoney;

        ViewHolder(View view) {ButterKnife.bind(this, view);}
    }
}
