package com.hunliji.marrybiz.view.orders;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.NumberFormatUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.order.OrderApi;
import com.hunliji.marrybiz.model.DataConfig;
import com.hunliji.marrybiz.model.orders.MerchantOrder;
import com.hunliji.marrybiz.util.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by wangtao on 2017/12/27.
 */

public class MerchantOrderMultiPayActivity extends HljBaseActivity {

    public static final String ARG_ORDER = "order";

    @BindView(R.id.tv_order_amount)
    TextView tvOrderAmount;
    @BindView(R.id.tv_remaining_amount)
    TextView tvRemainingAmount;
    @BindView(R.id.et_payment_price)
    EditText etPaymentPrice;
    @BindView(R.id.btn_pay)
    Button btnPay;

    private MerchantOrder order;
    private Subscriber<PayRxEvent> paySub;
    private HljHttpSubscriber refreshSubscriber;
    private TextWatcher payPriceEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                TextPaint paint = etPaymentPrice.getPaint();
                if (TextUtils.isEmpty(s.toString())) {
                    paint.setFakeBoldText(false);
                } else {
                    paint.setFakeBoldText(true);
                }
                double price = 0;
                if (s.length() > 0) {
                    price = Double.parseDouble(s.toString());
                }
                double newPrice = (double) (int) (price * 100) / 100;
                if (newPrice <= 0) {
                    newPrice = 0;
                } else if (newPrice > order.getActualMoney() - order.getPaidMoney()) {
                    newPrice = order.getActualMoney() - order.getPaidMoney();
                }
                btnPay.setEnabled(newPrice > 0);
                if (newPrice != price) {
                    String priceStr = NumberFormatUtil.formatDouble2String(newPrice);
                    etPaymentPrice.removeTextChangedListener(payPriceEditWatcher);
                    int start = Math.min(etPaymentPrice.getSelectionStart(), priceStr.length());
                    etPaymentPrice.setText(priceStr);
                    etPaymentPrice.setSelection(start);
                    etPaymentPrice.addTextChangedListener(payPriceEditWatcher);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_order_multi_pay);
        ButterKnife.bind(this);

        order = getIntent().getParcelableExtra(ARG_ORDER);
        initView();
        etPaymentPrice.addTextChangedListener(payPriceEditWatcher);
    }

    private void initView(){
        tvOrderAmount.setText(getString(R.string.label_price,
                NumberFormatUtil.formatDouble2String(order.getActualMoney())));
        tvRemainingAmount.setText(getString(R.string.label_price,
                NumberFormatUtil.formatDouble2String(order.getActualMoney() - order.getPaidMoney
                        ())));
        etPaymentPrice.setText("");
    }

    @OnClick(R.id.btn_pay)
    public void onPay() {
        double price = 0;
        try {
            price = Double.parseDouble(etPaymentPrice.getText()
                    .toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (price <= 0) {
            return;
        } else if (price > order.getActualMoney() - order.getPaidMoney()) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", order.getId());
            jsonObject.put("input_money", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (CommonUtil.isUnsubscribed(paySub)) {
            paySub = initSubscriber();
        }
        ArrayList<String> payTypes = null;
        if (Session.getInstance()
                .getDataConfig(this) != null) {
            payTypes = Session.getInstance()
                    .getDataConfig(this)
                    .getPayTypes();
        }
        new PayConfig.Builder(this).params(jsonObject)
                .path(Constants.HttpPath.MERCHANT_ORDER_PAY)
                .price(price)
                .subscriber(paySub)
                .llpayMode(true)
                .payAgents(payTypes, DataConfig.getPayAgents())
                .build()
                .pay();
    }


    //支付回调事件
    private Subscriber<PayRxEvent> initSubscriber() {
        return new RxBusSubscriber<PayRxEvent>() {
            @Override
            protected void onEvent(PayRxEvent rxEvent) {
                switch (rxEvent.getType()) {
                    case PAY_SUCCESS:
                        setResult(RESULT_OK);
                        onRefreshOrder();
                        break;
                    case PAY_FAIL:
                        //支付失败
                        break;
                }
            }
        };
    }

    public void onRefreshOrder() {
        if (refreshSubscriber == null || refreshSubscriber.isUnsubscribed()) {
            refreshSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<MerchantOrder>() {
                        @Override
                        public void onNext(MerchantOrder merchantOrder) {
                            if(merchantOrder.getStatus()!=MerchantOrder.ORDER_WAIT_FOR_PAY){
                                setResult(RESULT_CANCELED);
                                RxBus.getDefault().post(new RxEvent(RxEvent.RxEventType.MERCHANT_ORDER_PAID,merchantOrder));
                                onBackPressed();
                            }else {
                                order=merchantOrder;
                                initView();
                                DialogUtil.createSingleButtonDialog(MerchantOrderMultiPayActivity.this,
                                        "支付成功",
                                        "请尽快完成剩余款项的支付，\n全部付清后订单开始生效！",
                                        "继续支付",
                                        null)
                                        .show();
                            }
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {
                            onBackPressed();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(MerchantOrderMultiPayActivity.this))
                    .build();
            OrderApi.getMerchantOrderDetailObb(order.getId())
                    .subscribe(refreshSubscriber);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(paySub,refreshSubscriber);
    }
}
