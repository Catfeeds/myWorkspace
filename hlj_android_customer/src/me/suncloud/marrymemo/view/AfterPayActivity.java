package me.suncloud.marrymemo.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.util.JSONUtil;

import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;

/**
 * 通用的付款完成中转页面,所有的付款结束后都跳转到这个页面,根据不同的订单类型进行不同的操作和提示
 * 普通套餐(本地服务)订单付款完成后需要显示一个抽奖的跳转按钮,只要付款金额大于零即跳转至抽奖的HTML5页面
 * 需要两个参数,1,order_type,订单类型; 2,path,如果有跳转,则跳转的web url
 */
public class AfterPayActivity extends HljBaseActivity {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.btn_go_lucky_draw)
    Button btnGoLuckyDraw;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    private int orderType; // 付款的订单类型
    private String webUrl; // 抽奖的web页面URL
    private boolean isBack;

    public final static String ARG_ORDER_TYPE = "order_type";
    public final static String ARG_PATH = "path";
    public final static String ARG_IS_BACK = "is_back";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_pay);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setOkText(R.string.label_finish);

        orderType = getIntent().getIntExtra(ARG_ORDER_TYPE, 0);
        webUrl = getIntent().getStringExtra(ARG_PATH);
        isBack = getIntent().getBooleanExtra(ARG_IS_BACK, false);
        // 目前只有普通套餐的付款完成之后又进一步操作
        if (orderType == Constants.OrderType.NOMAL_WORK_ORDER && !JSONUtil.isEmpty(webUrl)) {
            btnGoLuckyDraw.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.image_after_pay_free);
        } else {
            btnGoLuckyDraw.setVisibility(View.GONE);
            image.setImageResource(R.drawable.image_after_pay_rest);
        }

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
        if (isBack) {
            finish();
            overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_to_bottom);
            return;
        }
        Intent intent = new Intent(this, MyOrderListActivity.class);
        if (orderType == Constants.OrderType.NOMAL_WORK_ORDER || orderType == Constants.OrderType
                .CUSTOM_SETMEAL_WORK_ORDER) {
            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
        } else if (orderType == Constants.OrderType.WEDDING_PRODUCT_ORDER) {
            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                    RouterPath.IntentPath.Customer.MyOrder.Tab.PRODUCT_ORDER);
        } else if (orderType == Constants.OrderType.WEDDING_CAR_ORDER) {
            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                    RouterPath.IntentPath.Customer.MyOrder.Tab.CAR_ORDER);
        }
        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_to_bottom);
        finish();
    }

    @OnClick(R.id.btn_go_lucky_draw)
    void onLuckyDraw() {
        Intent intent = new Intent(this, HljWebViewActivity.class);
        intent.putExtra("path", webUrl);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onBackPressed() {
        onOkButtonClick();
    }
}
