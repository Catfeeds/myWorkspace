package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.CarOrder;
import me.suncloud.marrymemo.model.CustomSetmealOrder;
import me.suncloud.marrymemo.model.orders.ProductOrder;
import me.suncloud.marrymemo.view.comment.CommentProductOrderActivity;
import me.suncloud.marrymemo.view.comment.CommentServiceActivity;

/**
 * 作为统一的订单确认完成之后的提示页面,可以点击评价按钮进入不同的评价页面
 */
public class AfterConfirmReceiveActivity extends HljBaseActivity {

    @BindView(R.id.btn_comment)
    Button btnComment;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private ProductOrder productOrder;
    private CarOrder carOrder;
    private CustomSetmealOrder customSetmealOrder;
    private boolean isCarOrder;
    private boolean isCustomOrder;
    private boolean isServiceOrder;
    private String serviceOrderNo;
    private long serviceOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_confirm_receive);
        ButterKnife.bind(this);

        setOkText(R.string.label_finish);

        isCarOrder = getIntent().getBooleanExtra("is_car_order", false);
        isCustomOrder = getIntent().getBooleanExtra("is_custom_order", false);
        isServiceOrder = getIntent().getBooleanExtra("is_service_order", false);
        if (isCarOrder) {
            carOrder = (CarOrder) getIntent().getSerializableExtra("car_order");
        } else if (isCustomOrder) {
            // 定制套餐订单
            customSetmealOrder = (CustomSetmealOrder) getIntent().getSerializableExtra(
                    "custom_order");
        } else if (isServiceOrder) {
            serviceOrderNo = getIntent().getStringExtra("service_order_no");
            serviceOrderId = getIntent().getLongExtra("service_order_id", 0);
        } else {
            productOrder = getIntent().getParcelableExtra("productOrder");
        }

        if (!TextUtils.isEmpty(serviceOrderNo) || productOrder != null || carOrder != null ||
                customSetmealOrder != null) {
            btnComment.setVisibility(View.VISIBLE);
        } else {
            btnComment.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_comment)
    void onComment() {
        if (isCarOrder && carOrder != null) {
            Intent comment = new Intent(this, CommentCarActivity.class);
            comment.putExtra(CommentCarActivity.ARG_ORDER_ID, carOrder.getId());
            startActivity(comment);
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        } else if (isCustomOrder && customSetmealOrder != null) {
            Intent intent = new Intent(this, CommentNewWorkActivity.class);
            intent.putExtra("is_custom_order", true);
            intent.putExtra("custom_order", (Serializable) customSetmealOrder);
            intent.putExtra("commentType", Constants.ENTRY_ITEM.WORK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            finish();
        } else if (isServiceOrder) {
            Intent intent = new Intent(this, CommentServiceActivity.class);
            intent.putExtra(CommentServiceActivity.ARG_SUB_ORDER_NO, serviceOrderNo);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
            finish();
        } else if (productOrder != null) {
            Intent intent = new Intent(this, CommentProductOrderActivity.class);
            intent.putExtra(CommentProductOrderActivity.ARG_PRODUCT_ORDER, productOrder);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @Override
    public void onOkButtonClick() {
        Intent intent = new Intent(this, MyOrderListActivity.class);
        intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN, true);
        if (isCarOrder) {
            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                    RouterPath.IntentPath.Customer.MyOrder.Tab.CAR_ORDER);
        } else if (isCustomOrder || isServiceOrder) {
            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.activity_anim_default);
        finish();
    }
}
