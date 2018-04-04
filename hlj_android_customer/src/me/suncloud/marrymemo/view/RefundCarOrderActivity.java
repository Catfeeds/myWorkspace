package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hunlijicalendar.ResizeAnimation;

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
import me.suncloud.marrymemo.model.CarOrder;
import me.suncloud.marrymemo.model.CarSubOrder;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.AnimUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;

public class RefundCarOrderActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<CarOrder>, AbsListView.OnScrollListener, PullToRefreshBase
        .OnRefreshListener<ListView>, AdapterView.OnItemClickListener {

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
    private ObjectBindAdapter<CarOrder> adapter;
    private ArrayList<CarOrder> orders;
    private boolean isEnd;
    private boolean isLoad;
    private String currentUrl;
    private int currentPage;
    private final static int ITEMS_LIMIT = 10;
    private int singleAppendHeight;
    private SparseBooleanArray collapseFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_car_order);
        ButterKnife.bind(this);

        dm = getResources().getDisplayMetrics();
        itemViewHeight = (int) (88.5 * dm.density);
        singleAppendHeight = (int) (36 * dm.density);
        collapseFlags = new SparseBooleanArray();

        emptyHintId = R.string.no_item;
        emptyHintDrawableId = R.drawable.icon_common_empty;

        footView = getLayoutInflater().inflate(R.layout.list_foot_no_more_2, null);
        endView = footView.findViewById(R.id.no_more_hint);
        loadView = footView.findViewById(R.id.loading);

        orders = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, orders, R.layout.car_product_order_list_item);
        adapter.setViewBinder(this);

        listView.getRefreshableView()
                .addFooterView(footView);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setOnScrollListener(this);
        listView.setAdapter(adapter);

        currentUrl = Constants.getAbsUrl(Constants.HttpPath.REFUND_CAR_ORDER_LIST);
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
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (!isLoad) {
            currentPage = 1;

            new GetOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL,
                    String.format(currentUrl, currentPage));
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
    public void setViewValue(View view, final CarOrder carOrder, int position) {
        if (view.getTag() == null) {
            view.setTag(new ViewHolder(view));
        }

        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.tvMerchantName.setText(R.string.label_car_order);
        holder.tvOrderStatus.setText(String.valueOf(carOrder.getStatusStr()));

        holder.tvRedMoney.setVisibility(View.VISIBLE);
        holder.tvRedMoney.setText(getString(R.string.label_order_price4,
                Util.formatDouble2String(carOrder.getPaidMoney())));
        holder.tvRedMoney.setTextColor(getResources().getColor(R.color.colorGray));

        holder.tvTotalPrice.setText(getString(R.string.label_refunded_money2,
                Util.formatDouble2String(carOrder.getRefundedMoney())));
        holder.tvTotalPrice.setTextColor(getResources().getColor(R.color.colorPrimary));

        holder.actionsLayout.setVisibility(View.GONE);

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
            View itemView = getLayoutInflater().inflate(R.layout.product_order_item, null);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CarOrder order = (CarOrder) parent.getAdapter()
                .getItem(position);
        if (order != null) {
            Intent intent = new Intent(this, RefundCarOrderDetailActivity.class);
            intent.putExtra("order", order);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
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
                imgView.startAnimation(AnimUtil.getAnimArrowDown(RefundCarOrderActivity.this));

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
                imgView.startAnimation(AnimUtil.getAnimArrowUp(RefundCarOrderActivity.this));

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
                                    CarOrder order = new CarOrder(orderObject);
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

                    if (JSONUtil.isNetworkConnected(RefundCarOrderActivity.this)) {
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
}
