package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.ObjectBindAdapter;
import me.suncloud.marrymemo.model.MessageEvent;
import me.suncloud.marrymemo.model.NewOrder;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;

/**
 * Created by Suncloud on 2015/6/12.
 */
public class RefundOrderListActivity extends HljBaseActivity implements ObjectBindAdapter
        .ViewBinder<NewOrder>, AdapterView.OnItemClickListener {

    private ArrayList<NewOrder> orders;
    private ObjectBindAdapter<NewOrder> adapter;
    private View progressView;
    private PullToRefreshListView listView;
    private int coverWidth;
    private int coverHeight;
    private boolean needRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault()
                .register(this);

        orders = new ArrayList<>();
        adapter = new ObjectBindAdapter<>(this, orders, R.layout.refund_order_list_item, this);
        Point point = JSONUtil.getDeviceSize(this);
        coverWidth = Math.round(point.x * 5 / 16);
        coverHeight = Math.round(coverWidth * 5 / 8);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        progressView = findViewById(R.id.progressBar);
        listView = (PullToRefreshListView) findViewById(R.id.list_view);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        new GetRefundOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewOrder order = (NewOrder) parent.getAdapter()
                .getItem(position);
        if (order != null) {
            if (order.isCustomOrderRefund()) {
                Intent intent = new Intent(this, CustomRefundOrderActivity.class);
                intent.putExtra("id", order.getId());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            } else {
                Intent intent = new Intent(this, OrderRefundDetailActivity.class);
                intent.putExtra("order", order);
                startActivityForResult(intent, Constants.RequestCode.CANCEL_REFUND);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null && requestCode == Constants.RequestCode
                .CANCEL_REFUND) {
            NewOrder order = (NewOrder) data.getSerializableExtra("order");
            if (order != null && !orders.isEmpty()) {
                int size = orders.size();
                for (int i = 0; i < size; i++) {
                    NewOrder newOrder = orders.get(i);
                    if (newOrder.getId()
                            .equals(order.getId())) {
                        orders.set(i, order);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setViewValue(View view, NewOrder newOrder, int position) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.iconInstallment = (ImageView) view.findViewById(R.id.img_installment);
            holder.priceTv = (TextView) view.findViewById(R.id.tv_price);
            holder.titleTv = (TextView) view.findViewById(R.id.tv_title);
            holder.refundPriceTv = (TextView) view.findViewById(R.id.tv_refund_price);
            holder.orderStatusTv = (TextView) view.findViewById(R.id.tv_order_status);
            holder.merchantNameTv = (TextView) view.findViewById(R.id.tv_merchant_name);
            holder.coverView = (ImageView) view.findViewById(R.id.img_cover);
            holder.hintView = (ImageView) view.findViewById(R.id.img_custom);
            holder.imgIntentMoney = (ImageView) view.findViewById(R.id.img_intent_money);
            holder.coverView.getLayoutParams().width = coverWidth;
            holder.coverView.getLayoutParams().height = coverHeight;
            view.findViewById(R.id.info_layout)
                    .getLayoutParams().height = coverHeight;
            view.setTag(holder);
        }

        if (newOrder.isCustomOrderRefund()) {
            holder.hintView.setVisibility(View.VISIBLE);
            holder.iconInstallment.setVisibility(View.GONE);
        } else {
            holder.iconInstallment.setVisibility(newOrder.isInstallment() ? View.VISIBLE : View
                    .GONE);
            holder.hintView.setVisibility(View.GONE);
        }
        holder.imgIntentMoney.setVisibility(newOrder.isIntentPay() ? View.VISIBLE : View.GONE);

        if (newOrder.getStatus() == 24) {
            holder.refundPriceTv.setVisibility(View.VISIBLE);
            holder.refundPriceTv.setText(getString(R.string.label_refund_price,
                    Util.formatDouble2String(newOrder.getRefundPrice())));
        } else {
            holder.refundPriceTv.setVisibility(View.GONE);
        }
        holder.titleTv.setText(newOrder.getTitle());
        holder.priceTv.setText(getString(R.string.label_order_price4,
                Util.formatDouble2String(newOrder.getPaidMoney())));
        holder.orderStatusTv.setText(newOrder.getStatusStr());
        holder.merchantNameTv.setText(newOrder.getMerchantName());
        String path = JSONUtil.getImagePath(newOrder.getCoverPath(), coverWidth);
        if (!JSONUtil.isEmpty(path)) {
            ImageLoadTask task = new ImageLoadTask(holder.coverView);
            holder.coverView.setTag(path);
            task.loadImage(path,
                    coverWidth,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        } else {
            holder.coverView.setTag(null);
            holder.coverView.setImageBitmap(null);
        }

    }

    private class ViewHolder {
        TextView priceTv;
        TextView titleTv;
        TextView refundPriceTv;
        TextView orderStatusTv;
        TextView merchantNameTv;
        ImageView coverView;
        ImageView hintView;
        ImageView iconInstallment;
        ImageView imgIntentMoney;
    }

    private class GetRefundOrdersTask extends AsyncTask<Object, Object, JSONArray> {

        @Override
        protected JSONArray doInBackground(Object... params) {
            try {
                String json = JSONUtil.getStringFromUrl(Constants.getAbsUrl(Constants.HttpPath
                        .GET_REFUND_ORDERS_V2));
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }
                return new JSONObject(json).optJSONArray("data");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            progressView.setVisibility(View.GONE);
            if (jsonArray != null && jsonArray.length() > 0) {
                orders.clear();
                int size = jsonArray.length();
                for (int i = 0; i < size; i++) {
                    JSONObject orderObj = jsonArray.optJSONObject(i);
                    // 通过meal_type判断这个退款单是普通套餐还是定制套餐,1=定制套餐,2=普通套餐,默认是2
                    int mealType = orderObj.optInt("meal_type", 2);
                    if (mealType == 1) {
                        NewOrder order = new NewOrder(orderObj, true);
                        orders.add(order);
                    } else {
                        NewOrder order = new NewOrder(jsonArray.optJSONObject(i));
                        orders.add(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            super.onPostExecute(jsonArray);
        }
    }

    @Override
    protected void onResume() {
        if (needRefresh) {
            progressView.setVisibility(View.VISIBLE);

            new GetRefundOrdersTask().executeOnExecutor(Constants.LISTTHEADPOOL);
        }
        super.onResume();
    }

    @Override
    protected void onFinish() {
        EventBus.getDefault()
                .unregister(this);
        super.onFinish();
    }

    /**
     * 退款单信息有改变,进行刷新
     *
     * @param event
     */
    public void onEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.EventType.CUSTOM_REFUND_ORDER_FLAG) {
            needRefresh = true;
        }
    }
}
