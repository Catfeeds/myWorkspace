package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.orders.ProductOrder;

public class SelectRefundTypeActivity extends HljBaseActivity {

    private long id;
    private ProductOrder order;
    private boolean isApplyAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_refund_type);
        ButterKnife.bind(this);
        order = getIntent().getParcelableExtra("order");
        isApplyAgain = getIntent().getBooleanExtra("apply_again", false);

        id = getIntent().getLongExtra("id", 0);
    }


    @OnClick(R.id.refund_and_return_layout)
    void onRefundAndReturn() {
        Intent intent = new Intent(this, ProductRefundApplyActivity.class);
        intent.putExtra("order", order);
        intent.putExtra("type", 2);
        intent.putExtra("id", id);
        intent.putExtra("apply_again", isApplyAgain);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        if (isApplyAgain) {
            finish();
        }
    }

    @OnClick(R.id.refund_only_layout)
    void onRefundOnly() {
        Intent intent = new Intent(this, ProductRefundApplyActivity.class);
        intent.putExtra("order", order);
        intent.putExtra("type", 1);
        intent.putExtra("id", id);
        intent.putExtra("apply_again", isApplyAgain);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        if (isApplyAgain) {
            finish();
        }
    }
}
