package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CustomSetmeal;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.AsyncBitmapDrawable;
import me.suncloud.marrymemo.task.ImageLoadTask;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ScaleMode;
import me.suncloud.marrymemo.util.Util;

public class CustomSetmealOrderConfirmActivity extends HljBaseActivity {

    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.agreement_layout)
    LinearLayout agreementLayout;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.merchant_layout)
    LinearLayout merchantLayout;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.items_layout)
    LinearLayout itemsLayout;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_gift)
    TextView tvGift;
    @BindView(R.id.privilege_layout)
    LinearLayout privilegeLayout;
    private boolean submitting;
    private CustomSetmeal customSetmeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_setmeal_order_confirm);
        ButterKnife.bind(this);
        Point point = JSONUtil.getDeviceSize(this);
        int coverWidth = Math.round(point.x * 100 / 320);
        int coverHeight = Math.round(coverWidth * 212 / 338);
        imgCover.getLayoutParams().width = coverWidth;
        imgCover.getLayoutParams().height = coverHeight;
        customSetmeal = (CustomSetmeal) getIntent().getSerializableExtra("customSetmeal");
        String path = JSONUtil.getImagePath(customSetmeal.getImgCover(), coverWidth);
        if (!JSONUtil.isEmpty(path)) {
            ImageLoadTask task = new ImageLoadTask(imgCover, null);
            task.loadImage(path,
                    coverWidth,
                    ScaleMode.WIDTH,
                    new AsyncBitmapDrawable(getResources(), R.mipmap.icon_empty_image, task));
        }
        tvMerchantName.setText(customSetmeal.getMerchant()
                .getName());
        tvTitle.setText(customSetmeal.getTitle());
        tvPrice.setText(getString(R.string.label_price4,
                Util.formatDouble2String(customSetmeal.getActualPrice())));

        // 设置订单礼
        if (!JSONUtil.isEmpty(customSetmeal.getOrderGift())) {
            privilegeLayout.setVisibility(View.VISIBLE);
            tvGift.setText("• " + getString(R.string.label_gift, customSetmeal.getOrderGift()));
        } else {
            privilegeLayout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", customSetmeal.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (submitting) {
            return;
        } else {
            submitting = true;
        }

        progressBar.setVisibility(View.VISIBLE);
        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                submitting = false;
                Intent intent = new Intent(CustomSetmealOrderConfirmActivity.this,
                        MyOrderListActivity.class);
                intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
                intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                        RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                submitting = false;
                Util.postFailToast(CustomSetmealOrderConfirmActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_submit_order,
                        network);

            }
        }).execute(Constants.getAbsUrl(Constants.HttpPath.CUSTOM_SETMEAL_ORDER_SUBMIT),
                jsonObject.toString());
    }

}
