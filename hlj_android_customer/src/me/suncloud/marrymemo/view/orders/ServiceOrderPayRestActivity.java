package me.suncloud.marrymemo.view.orders;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljpaymentlibrary.PayAgent;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyBillListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.orders.ServiceOrder;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.view.AfterPayActivity;

public class ServiceOrderPayRestActivity extends HljBaseActivity {

    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.tv_order_price)
    TextView tvOrderPrice;
    @BindView(R.id.tv_rest_to_pay)
    TextView tvRestToPay;
    @BindView(R.id.et_pay_money)
    EditText etPayMoney;

    private ServiceOrder order;
    private double maxRestMoney;
    private RxBusSubscriber<PayRxEvent> paySubscriber;
    private Dialog hintDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order_pay_rest);
        ButterKnife.bind(this);

        initValues();
        initViews();
    }

    private void initValues() {
        order = getIntent().getParcelableExtra("order");
    }

    private void initViews() {
        tvOrderPrice.setText(getString(R.string.label_price,
                String.valueOf(CommonUtil.formatDouble2String(order.getOrderSub()
                        .getActualPrice()))));
        maxRestMoney = order.getOrderSub()
                .getActualPrice() - order.getOrderSub()
                .getAidMoney() - order.getOrderSub()
                .getRedPacketMoney() - order.getOrderSub()
                .getPaidMoney();

        tvRestToPay.setText(getString(R.string.label_price,
                String.valueOf(CommonUtil.formatDouble2String(maxRestMoney))));
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (etPayMoney.length() <= 0) {
            // 没有输入金额
            Toast.makeText(this, R.string.msg_empty_pay_money, Toast.LENGTH_SHORT)
                    .show();
            return;
        } else {
            double editPayMoney = Double.valueOf(etPayMoney.getText()
                    .toString());
            if (editPayMoney < 1) {
                // 输入金额小于1或者大于余款
                Toast.makeText(this, R.string.msg_invalid_pay_money, Toast.LENGTH_SHORT)
                        .show();
                return;
            } else if (editPayMoney > maxRestMoney) {
                Toast.makeText(this, R.string.msg_invalid_pay_money2, Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            // 支付
            onPay(editPayMoney);
        }
    }

    private void showWrongAmountDlg() {
        if (hintDlg == null) {
            hintDlg = DialogUtil.createDoubleButtonDialog(this,
                    "本次支付金额大于还需支付金额，\n确认继续支付？",
                    "重新输入",
                    "继续支付",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 重新输入
                            hintDlg.cancel();
                            etPayMoney.requestFocus();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 继续支付
                            hintDlg.cancel();
                            double editPayMoney = Double.valueOf(etPayMoney.getText()
                                    .toString());
                            onPay(editPayMoney);
                        }
                    });
        }
        hintDlg.show();
    }

    private void onPay(double amount) {
        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    Intent intent;
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            // 支付成功，跳转成功页面
                            intent = new Intent(ServiceOrderPayRestActivity.this,
                                    AfterPayActivity.class);
                            intent.putExtra(AfterPayActivity.ARG_ORDER_TYPE,
                                    Constants.OrderType.NOMAL_WORK_ORDER);
                            if (rxEvent.getObject() != null && rxEvent.getObject() instanceof
                                    JsonObject) {
                                JsonObject orderResult = (JsonObject) rxEvent.getObject();
                                try {
                                    if (orderResult.get("free_order_link") != null) {
                                        intent.putExtra(AfterPayActivity.ARG_PATH,
                                                orderResult.get("free_order_link")
                                                        .getAsString());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                            break;
                        case PAY_CANCEL:
                            break;
                        case INSTALLMENT_PAY_SUCCESS:
                            // 分期支付成功
                            intent = new Intent(ServiceOrderPayRestActivity.this,
                                    MyBillListActivity.class);
                            intent.putExtra(MyBillListActivity.ARG_IS_BACK_ORDER_LIST, true);
                            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                            break;
                    }
                }
            };
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", order.getId());
            jsonObject.put("input_money", amount);
            jsonObject.put("pay_type", PayConfig.PAY_TYPE_REST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        PayConfig.Builder builder = new PayConfig.Builder(this);
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        List<String> payAgents = DataConfig.getServicePayAgents();
        if (!order.getOrderSub()
                .getWork()
                .isInstallment()) {
            // 移除分期支付
            payAgents.remove(PayAgent.XIAO_XI_PAY);
        }
        builder.payAgents(dataConfig != null ? dataConfig.getPayTypes() : null, payAgents);
        builder.params(jsonObject)
                .path(Constants.getAbsUrl(Constants.HttpPath.SERVICE_ORDER_PAYMENT))
                .price(amount > 0 ? amount : 0)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(paySubscriber);
    }
}
