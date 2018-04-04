package com.hunliji.marrybiz.view;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.NewOrder;
import com.hunliji.marrybiz.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewOrderRefundActivity extends HljBaseActivity {

    @BindView(R.id.price_return)
    TextView priceReturn;
    @BindView(R.id.price_return_time)
    TextView priceReturnTime;
    @BindView(R.id.returned_layout)
    LinearLayout returnedLayout;
    @BindView(R.id.return_checking_layout)
    LinearLayout returnCheckingLayout;
    @BindView(R.id.return_back_time)
    TextView returnBackTime;
    @BindView(R.id.return_back_reason_hint)
    TextView returnBackReasonHint;
    @BindView(R.id.return_back_reason)
    TextView returnBackReason;
    @BindView(R.id.return_back_notice)
    TextView returnBackNotice;
    @BindView(R.id.return_back_number)
    TextView returnBackNumber;

    private NewOrder order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_refund);
        ButterKnife.bind(this);

        order = (NewOrder) getIntent().getSerializableExtra("order");
        if (order != null) {
            setRefundInfo(order);
        }
    }

    private void setRefundInfo(NewOrder order) {
        returnBackTime.setText(order.getRefundInfo()
                .getCreatedDT()
                .toString(getString(R.string.format_date_type8)));
        returnBackReason.setText(order.getRefundInfo()
                .getReason());
        returnBackNotice.setText(order.getRefundInfo()
                .getDesc());
        returnBackNumber.setText(String.format(getString(R.string.label_return_back_number),
                order.getRefundInfo()
                        .getNo()));

        if (order.getStatus() == 20) {
            // 审核中
            returnedLayout.setVisibility(View.GONE);
            returnCheckingLayout.setVisibility(View.VISIBLE);
        } else if (order.getStatus() == 24) {
            // 退款成功
            returnedLayout.setVisibility(View.VISIBLE);
            returnCheckingLayout.setVisibility(View.GONE);

            priceReturn.setText(Util.formatDouble2String(order.getRefundInfo()
                    .getMerchantPayMoney()));
            priceReturnTime.setText(order.getRefundInfo()
                    .getUpdatedDT()
                    .toString(getString(R.string.format_date_type11)));
        }
    }

    public void onService(View view) {
        callUp(Uri.parse("tel:" + getString(R.string.service_phone).trim()));
    }

}
