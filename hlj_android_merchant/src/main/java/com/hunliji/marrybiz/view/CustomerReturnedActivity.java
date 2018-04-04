package com.hunliji.marrybiz.view;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.CustomSetmealOrder;
import com.hunliji.marrybiz.util.Util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 定制套餐退款
 * Created by jinxin on 2016/2/2.
 */
public class CustomerReturnedActivity extends HljBaseActivity {
    @BindView(R.id.return_back_time)
    TextView returnBackTime;
    @BindView(R.id.return_back_reason)
    TextView returnBackReason;
    @BindView(R.id.return_back_notice)
    TextView returnBackNotice;
    @BindView(R.id.return_back_number)
    TextView returnBackNumber;
    @BindView(R.id.price_return)
    TextView priceReturn;
    @BindView(R.id.price_return_time)
    TextView priceReturnTime;

    private CustomSetmealOrder order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        order = (CustomSetmealOrder) getIntent().getSerializableExtra("order");
        setContentView(R.layout.activity_custom_return);
        ButterKnife.bind(this);
        if (order.getStatus() == 24) {
            findViewById(R.id.custom_returned_layout).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.return_checking_layout).setVisibility(View.VISIBLE);
        }
        hideOkButton();
        if (order != null) {
            if (order.getStatus() == 24) {
                initReturnedInfo(order);
            } else {
                initReturningInfo(order);
            }
        }
    }

    private void initReturningInfo(CustomSetmealOrder order) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG,
                Locale.getDefault());
        returnBackTime.setText(order.getRefundCreateAt() == null ? "" : simpleDateFormat.format(
                order.getRefundCreateAt()));
        returnBackReason.setText(order.getRefundReasonName());
        returnBackNotice.setText(order.getRefundDesc());
        returnBackNumber.setText(String.format(getString(R.string.label_return_back_number),
                order.getRefundOrderNo()));
    }

    private void initReturnedInfo(CustomSetmealOrder order) {
        priceReturn.setText(Util.formatDouble2String(order.getRefundPayMoney() + (order.isInvalid
                () ? 0 : order.getRedPacketMoney())));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_LONG,
                Locale.getDefault());
        returnBackTime.setText(order.getRefundCreateAt() == null ? "" : simpleDateFormat.format(
                order.getRefundCreateAt()));
        priceReturnTime.setText(order.getRefundUpdateAt() == null ? "" : simpleDateFormat.format(
                order.getRefundUpdateAt()));
        returnBackReason.setText(order.getRefundReasonName());
        returnBackNotice.setText(order.getRefundDesc());
        returnBackNumber.setText(String.format(getString(R.string.label_return_back_number),
                order.getRefundOrderNo()));
    }

    public void onService(View view) {
        callUp(Uri.parse("tel:" + getString(R.string.service_phone).trim()));
    }

    @Override
    protected void onFinish() {
        super.onFinish();
    }
}