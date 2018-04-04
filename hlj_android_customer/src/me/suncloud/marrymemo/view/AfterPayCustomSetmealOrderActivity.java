package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.ReturnStatus;
import me.suncloud.marrymemo.task.StatusHttpPostTask;
import me.suncloud.marrymemo.task.StatusRequestListener;
import me.suncloud.marrymemo.util.JSONUtil;
import me.suncloud.marrymemo.util.ShareUtil;

import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;

import me.suncloud.marrymemo.util.Util;

/**
 * 作为统一的支付完成之后的提示页面,删除之前的分享的红包或者免单机会等等无用操作
 * 用于定制套餐订单
 */
public class AfterPayCustomSetmealOrderActivity extends HljBaseActivity {

    @BindView(R.id.tv_get_free_order)
    TextView tvGetFreeOrder;
    @BindView(R.id.img_after_pay)
    ImageView imgAfterPay;
    @BindView(R.id.btn_share)
    Button btnShare;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Dialog dialog;
    private ShareUtil shareUtil;
    private JSONObject shareObject;
    private boolean isCarOrder;
    private boolean isDeposit;
    private JSONArray jsonArray;
    private boolean isServiceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_pay_custom_setmel_order);
        ButterKnife.bind(this);

        setOkText(R.string.label_finish);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        isCarOrder = getIntent().getBooleanExtra("is_car_order", false);
        isDeposit = getIntent().getBooleanExtra("is_deposit", false);
        isServiceOrder = getIntent().getBooleanExtra("is_service_order", false);
        initNotifyDialog();
    }

    private void initNotifyDialog() {
        Dialog dialog = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(this,
                HljCommon.SharedPreferencesNames.PREF_NOTICE_OPEN_DLG_PAY,
                "付款成功",
                "立即开启消息通知，及时掌握订单状态和物流信息哦~",
                R.drawable.icon_dlg_appointment);
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    public void onOkButtonClick() {
        Intent intent = new Intent(this, MyOrderListActivity.class);
        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
        if (isCarOrder) {
            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                    RouterPath.IntentPath.Customer.MyOrder.Tab.CAR_ORDER);
        } else if (isServiceOrder) {
            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.activity_anim_default);
        finish();
    }

    @Override
    public void onBackPressed() {
        onOkButtonClick();
    }


    @OnClick(R.id.btn_share)
    void onShare() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (shareUtil == null) {
            return;
        }

        dialog = new Dialog(this, R.style.BubbleDialogTheme);
        View v = getLayoutInflater().inflate(R.layout.dialog_share_to_penyou, null);
        v.findViewById(R.id.msg_extra_layout)
                .setVisibility(View.VISIBLE);
        Button cancelBtn = (Button) v.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        v.findViewById(R.id.msg_tag_layout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 分享到朋友圈
                        shareUtil.shareToPengYou();
                        dialog.dismiss();
                    }
                });

        dialog.setContentView(v);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        Point point = JSONUtil.getDeviceSize(this);
        params.width = Math.round(point.x * 27 / 32);
        window.setAttributes(params);
        dialog.show();
    }

    private void postFreeOrder() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (jsonArray != null) {
                jsonObject.put("ids", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = Constants.getAbsUrl(Constants.HttpPath.POST_FREE_PRODUCT_ORDER);
        if (isCarOrder) {
            url = Constants.getAbsUrl(Constants.HttpPath.POST_FREE_CAR_ORDER);
        }

        new StatusHttpPostTask(this, new StatusRequestListener() {
            @Override
            public void onRequestCompleted(Object object, ReturnStatus returnStatus) {
                progressBar.setVisibility(View.GONE);
                JSONObject dataObject = (JSONObject) object;
                if (dataObject != null) {
                    String msg = dataObject.optString("msg");
                    Util.showToast(msg, AfterPayCustomSetmealOrderActivity.this);

                    Intent intent = new Intent(AfterPayCustomSetmealOrderActivity.this,
                            MyOrderListActivity.class);
                    intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
                    if (isCarOrder) {
                        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                                RouterPath.IntentPath.Customer.MyOrder.Tab.CAR_ORDER);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.activity_anim_default);
                    finish();
                }
            }

            @Override
            public void onRequestFailed(ReturnStatus returnStatus, boolean network) {
                progressBar.setVisibility(View.GONE);
                Util.postFailToast(AfterPayCustomSetmealOrderActivity.this,
                        returnStatus,
                        R.string.msg_fail_to_get_free_order,
                        network);
            }
        }).execute(url, jsonObject.toString());
    }
}
