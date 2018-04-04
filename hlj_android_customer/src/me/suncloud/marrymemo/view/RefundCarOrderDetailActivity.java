package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hunliji.hljcarlibrary.views.activities.WeddingCarProductDetailActivity;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljkefulibrary.moudles.Support;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CarOrder;
import me.suncloud.marrymemo.model.CarSubOrder;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;

public class RefundCarOrderDetailActivity extends HljBaseActivity {

    @BindView(R.id.tv_refunded_money)
    TextView tvRefundedMoney;
    @BindView(R.id.tv_paid_money)
    TextView tvPaidMoney;
    @BindView(R.id.refund_infos_layout)
    LinearLayout refundInfosLayout;
    @BindView(R.id.items_layout)
    LinearLayout itemsLayout;
    @BindView(R.id.products_layout)
    LinearLayout productsLayout;
    @BindView(R.id.tv_order_no)
    TextView tvOrderNo;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.btn_contact_service)
    Button btnContactService;
    @BindView(R.id.ordering_info_layout)
    LinearLayout orderingInfoLayout;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private CarOrder order;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_car_order_detail);
        ButterKnife.bind(this);
        dateFormat = new SimpleDateFormat(getString(R.string.format_date_type10));

        order = (CarOrder) getIntent().getSerializableExtra("order");
        long orderId = getIntent().getLongExtra("id", 0);
        if (order != null) {
            setOrderDetail();
        } else if (orderId > 0) {
            progressBar.setVisibility(View.VISIBLE);
            new GetOrderDetailTask().execute(orderId);
        }
    }

    private class GetOrderDetailTask extends AsyncTask<Long, Object, JSONObject> {

        @Override
        protected JSONObject doInBackground(Long... params) {
            String url = Constants.getAbsUrl(String.format(Constants.HttpPath.CAR_ORDER_DETAIL,
                    params[0]));
            try {
                String json = JSONUtil.getStringFromUrl(url);
                if (JSONUtil.isEmpty(json)) {
                    return null;
                }

                return new JSONObject(json);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            progressBar.setVisibility(View.GONE);
            if (jsonObject != null) {
                ReturnStatus returnStatus = new ReturnStatus(jsonObject.optJSONObject("status"));
                if (returnStatus.getRetCode() == 0) {
                    JSONObject dataObject = jsonObject.optJSONObject("data");
                    if (dataObject != null) {
                        order = new CarOrder(dataObject);
                        setOrderDetail();
                    }
                }
            }
            super.onPostExecute(jsonObject);
        }
    }

    private void setOrderDetail() {
        if (order == null) {
            contentLayout.setVisibility(View.GONE);
        } else {
            contentLayout.setVisibility(View.VISIBLE);

            setRefundInfos();
            setCarItems();
            setOrderingInfo();
        }
    }

    private void setRefundInfos() {
        tvRefundedMoney.setText(getString(R.string.label_refund_price2,
                Util.formatDouble2String(order.getRefundedMoney())));
        tvPaidMoney.setText(getString(R.string.label_order_price5,
                Util.formatDouble2String(order.getPaidMoney())));
    }

    private void setCarItems() {
        itemsLayout.removeAllViews();
        for (final CarSubOrder subOrder : order.getSubOrders()) {
            View itemView = getLayoutInflater().inflate(R.layout.car_shop_cart_item, null);
            TextView tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ImageView imgCover = (ImageView) itemView.findViewById(R.id.img_cover);
            TextView tvSkuInfo = (TextView) itemView.findViewById(R.id.tv_sku_info);
            TextView tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            TextView tvQuantity = (TextView) itemView.findViewById(R.id.tv_quantity);

            tvTitle.setText(subOrder.getCarProduct()
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RefundCarOrderDetailActivity
                            .this, WeddingCarProductDetailActivity.class);
                    intent.putExtra(WeddingCarProductDetailActivity.ARG_ID,
                            subOrder.getCarProduct()
                                    .getId());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                }
            });
            itemsLayout.addView(itemView);
        }
    }

    private void setOrderingInfo() {
        tvOrderNo.setText(order.getOrderNo());
        tvOrderTime.setText(dateFormat.format(order.getCreatedAt()));
    }

    @OnClick(R.id.btn_contact_service)
    void onContact(View view) {
        CustomerSupportUtil.goToSupport(this, Support.SUPPORT_KIND_CAR, order);
    }

}
