package me.suncloud.marrymemo.view.orders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.models.Work;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.PayAgent;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Installment2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentDetail;
import com.hunliji.hljpaymentlibrary.utils.xiaoxi_installment.XiaoxiInstallmentAuthorization;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.InstallmentPaymentActivity;
import com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment.MyBillListActivity;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutGroup;
import me.suncloud.marrymemo.widget.FlowLayout;

public class ServiceOrderPaymentActivity extends HljBaseActivity implements
        CheckableLinearLayoutGroup.OnCheckedChangeListener {

    @BindView(R.id.tv_pay_all_saved)
    CheckedTextView tvPayAllSaved;
    @BindView(R.id.check_btn_pay_all)
    CheckableLinearLayoutButton checkBtnPayAll;
    @BindView(R.id.tv_deposit_need)
    CheckedTextView tvDepositNeed;
    @BindView(R.id.check_btn_pay_deposit)
    CheckableLinearLayoutButton checkBtnPayDeposit;
    @BindView(R.id.tv_disable_reason)
    TextView tvDisableReason;
    @BindView(R.id.disable_deposit_layout)
    LinearLayout disableDepositLayout;
    @BindView(R.id.check_group_pay_type)
    CheckableLinearLayoutGroup checkGroupPayType;
    @BindView(R.id.tv_pay_method)
    TextView tvPayMethod;
    @BindView(R.id.pay_method_layout)
    LinearLayout payMethodLayout;
    @BindView(R.id.tv_intent_money)
    TextView tvIntentMoney;
    @BindView(R.id.intent_money_layout)
    LinearLayout intentMoneyLayout;
    @BindView(R.id.tv_need_pay_money)
    TextView tvNeedPayMoney;
    @BindView(R.id.btn_confirm_pay)
    Button btnConfirmPay;
    ServiceOrder serviceOrder;
    @BindView(R.id.tv_need_pay_all_money)
    TextView tvNeedPayAllMoney;
    @BindView(R.id.tv_pay_all_saved_money)
    TextView tvPayAllSavedMoney;
    @BindView(R.id.pay_all_saved_money_layout)
    LinearLayout payAllSavedMoneyLayout;
    @BindView(R.id.tv_coupon_money)
    TextView tvCouponMoney;
    @BindView(R.id.coupon_money_layout)
    LinearLayout couponMoneyLayout;
    @BindView(R.id.tv_red_packet_money)
    TextView tvRedPacketMoney;
    @BindView(R.id.red_packet_money_layout)
    LinearLayout redPacketMoneyLayout;
    @BindView(R.id.tv_need_pay_all_money_label)
    TextView tvNeedPayAllMoneyLabel;
    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R.id.tv_installment_hint)
    TextView tvInstallmentHint;
    @BindView(R.id.xiaoxi_installment_layout)
    LinearLayout xiaoxiInstallmentLayout;

    private int disableDepositType;
    private String disableDepositReason;
    private double payAllSavedMoney;
    private double needPay;
    private RxBusSubscriber<PayRxEvent> paySubscriber;
    private int payType;
    private HljHttpSubscriber installmentSub;
    private int checkedInstallmentStageNum = -1;
    private Installment2 installment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order_payment);
        ButterKnife.bind(this);

        initValues();
        initViews();
    }

    private void initValues() {
        serviceOrder = getIntent().getParcelableExtra("order");
    }

    private void initViews() {
        checkGroupPayType.check(R.id.check_btn_pay_all);
        checkGroupPayType.setOnCheckedChangeListener(this);
        setPrices();
    }

    private void setPrices() {
        tvIntentMoney.setText("-" + getString(R.string.label_price,
                CommonUtil.formatDouble2String(serviceOrder.getOrderSub()
                        .getPaidMoney())));
        if (serviceOrder.getOrderSub()
                .getRedPacketMoney() > 0) {
            redPacketMoneyLayout.setVisibility(View.VISIBLE);
            tvRedPacketMoney.setText("-￥" + CommonUtil.formatDouble2String(serviceOrder
                    .getOrderSub()
                    .getRedPacketMoney()));
        } else {
            redPacketMoneyLayout.setVisibility(View.GONE);
        }
        if (serviceOrder.getOrderSub()
                .getAidMoney() > 0) {
            couponMoneyLayout.setVisibility(View.VISIBLE);
            tvCouponMoney.setText("-" + getString(R.string.label_price,
                    CommonUtil.formatDouble2String(serviceOrder.getOrderSub()
                            .getAidMoney())));
        } else {
            couponMoneyLayout.setVisibility(View.GONE);
        }
        Work work = serviceOrder.getOrderSub()
                .getWork();
        // 付款方式对应价格信息
        payAllSavedMoney = serviceOrder.getOrderSub()
                .getPayAllSavedMoney();
        if (payAllSavedMoney > 0) {
            tvPayAllSaved.setVisibility(View.VISIBLE);
            tvPayAllSaved.setText(getString(R.string.label_discount_amount2,
                    CommonUtil.formatDouble2String(payAllSavedMoney)));
        } else if (!TextUtils.isEmpty(work.getPayAllGift())) {
            tvPayAllSaved.setText("送  " + work.getPayAllGift());
        } else {
            tvPayAllSaved.setVisibility(View.INVISIBLE);
        }

        // 设置是否允许定金支付
        if (isAllowDeposit()) {
            disableDepositLayout.setVisibility(View.GONE);
            checkBtnPayDeposit.setVisibility(View.VISIBLE);
            tvDepositNeed.setText(getString(R.string.label_deposit_need3,
                    String.valueOf(serviceOrder.getOrderSub()
                            .getEarnestMoney())));
        } else {
            disableDepositLayout.setVisibility(View.VISIBLE);
            checkBtnPayDeposit.setVisibility(View.GONE);
            checkBtnPayAll.setChecked(true);
            tvDisableReason.setText(disableDepositReason);
            disableDepositLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ServiceOrderPaymentActivity.this,
                            disableDepositType == 2 ? R.string.msg_disable_deposit_1 : R.string
                                    .msg_disable_deposit_2,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

        if (checkGroupPayType.getCheckedRadioButtonId() == R.id.check_btn_pay_all) {
            // 选择了支付全款
            tvPayMethod.setText("全款支付");
            tvNeedPayAllMoneyLabel.setText("订单金额：");
            tvNeedPayAllMoney.setText(getString(R.string.label_price,
                    CommonUtil.formatDouble2String(serviceOrder.getOrderSub()
                            .getActualPrice())));
            if (payAllSavedMoney > 0) {
                payAllSavedMoneyLayout.setVisibility(View.VISIBLE);
                tvPayAllSavedMoney.setText("-" + getString(R.string.label_price,
                        CommonUtil.formatDouble2String(payAllSavedMoney)));
            } else {
                payAllSavedMoneyLayout.setVisibility(View.GONE);
            }
            needPay = serviceOrder.getOrderSub()
                    .getActualPrice() - payAllSavedMoney - serviceOrder.getOrderSub()
                    .getAidMoney() - serviceOrder.getOrderSub()
                    .getRedPacketMoney() - serviceOrder.getOrderSub()
                    .getPaidMoney();
            payType = PayConfig.PAY_TYPE_PAY_ALL;
        } else {
            // 选择了支付定金
            tvPayMethod.setText("定金支付");
            tvNeedPayAllMoneyLabel.setText("定金金额：");
            tvNeedPayAllMoney.setText(getString(R.string.label_price,
                    CommonUtil.formatDouble2String(serviceOrder.getOrderSub()
                            .getEarnestMoney())));
            redPacketMoneyLayout.setVisibility(View.GONE);
            couponMoneyLayout.setVisibility(View.GONE);
            payAllSavedMoneyLayout.setVisibility(View.GONE);
            needPay = serviceOrder.getOrderSub()
                    .getEarnestMoney() - serviceOrder.getOrderSub()
                    .getPaidMoney();
            payType = PayConfig.PAY_TYPE_DEPOSIT;
        }
        tvNeedPayMoney.setText(getString(R.string.label_price,
                CommonUtil.formatDouble2StringPositive(needPay)));
        loadInstallmentDetails();
    }

    /**
     * 分期信息
     */
    private void loadInstallmentDetails() {
        if (checkGroupPayType.getCheckedRadioButtonId() == R.id.check_btn_pay_deposit ||
                (!serviceOrder.getOrderSub()
                .getWork()
                .isInstallment())) {
            // 意向金支付或定金支付，或者不支持分期
            xiaoxiInstallmentLayout.setVisibility(View.GONE);
            return;
        }

        final int installmentWidth = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this,
                16 * 3)) / 2;
        installmentSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(com.hunliji.hljcommonlibrary.utils.DialogUtil
                        .createProgressDialog(
                        this))
                .setOnNextListener(new SubscriberOnNextListener<InstallmentData>() {
                    @Override
                    public void onNext(InstallmentData installmentData) {
                        installment = installmentData.getInstallment();
                        xiaoxiInstallmentLayout.setVisibility(View.VISIBLE);

                        ArrayList<InstallmentDetail> details = installment.getDetails();
                        flowLayout.removeAllViews();
                        for (InstallmentDetail detail : details) {
                            final CheckBox cbDetail = (CheckBox) LayoutInflater.from(
                                    ServiceOrderPaymentActivity.this)
                                    .inflate(R.layout.installment_check_box, null, false);
                            cbDetail.setText(detail.getShowText());
                            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                                    installmentWidth,
                                    CommonUtil.dp2px(ServiceOrderPaymentActivity.this, 44));
                            flowLayout.addView2(cbDetail, params);
                            if (detail.getStageNum() == checkedInstallmentStageNum) {
                                cbDetail.setChecked(true);
                            }
                        }
                        flowLayout.setCheckedChild(flowLayout.getChildCount());
                    }
                })
                .build();
        XiaoxiInstallmentApi.getInstallmentInfo(needPay)
                .subscribe(installmentSub);
    }

    /**
     * 判断是否允许定金支付
     *
     * @return
     */
    private boolean isAllowDeposit() {
        boolean isNeedWeddingTime = serviceOrder.getOrderSub()
                .getMerchant()
                .getPropertyId() != Merchant.PROPERTY_WEDDING_DRESS_PHOTO;
        if (serviceOrder.getOrderSub()
                .getWork()
                .isAllowEarnest() && serviceOrder.getOrderSub()
                .getEarnestMoney() > 0) {
            // 如果有wedding time的话，则需要判断是否在这之前的三十天之前，否则也不支持定金付款
            if (!isNeedWeddingTime || serviceOrder.getWeddingTime() == null) {
                return true;
            } else {
                DateTime dateTime = new DateTime();
                dateTime = dateTime.plusDays(30);
                if (dateTime.isBefore(serviceOrder.getWeddingTime())) {
                    return true;
                } else {
                    disableDepositType = 2;
                    disableDepositReason = "距服务时间少于30天";
                    return false;
                }
            }
        } else {
            disableDepositType = 1;
            disableDepositReason = "该套餐不支持定金支付";
            return false;
        }
    }

    @OnClick(R.id.btn_confirm_pay)
    void onPay() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", serviceOrder.getId());
            // 初次付款，付定金或者付全款
            jsonObject.put("pay_type",
                    checkGroupPayType.getCheckedRadioButtonId() == R.id.check_btn_pay_deposit ?
                            PayConfig.PAY_TYPE_DEPOSIT : PayConfig.PAY_TYPE_PAY_ALL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (paySubscriber == null) {
            paySubscriber = new RxBusSubscriber<PayRxEvent>() {
                @Override
                protected void onEvent(PayRxEvent rxEvent) {
                    Intent intent;
                    switch (rxEvent.getType()) {
                        case PAY_SUCCESS:
                            // 支付成功，跳转成功页面
                            intent = new Intent(ServiceOrderPaymentActivity.this,
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
                        case INSTALLMENT_PAY_SUCCESS:
                            // 分期支付成功
                            intent = new Intent(ServiceOrderPaymentActivity.this,
                                    MyBillListActivity.class);
                            intent.putExtra(MyBillListActivity.ARG_IS_BACK_ORDER_LIST, true);
                            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                            break;
                        case PAY_CANCEL:
                        case AUTHORIZE_CANCEL:
                        case INIT_LIMIT_CLOSE:
                        case INCREASE_LIMIT_CLOSE:
                            break;
                        case HAD_AUTHORIZED:
                        case LIMIT_CONTINUE_PAY:
                            // 授信成功
                            intent = new Intent(ServiceOrderPaymentActivity.this,
                                    InstallmentPaymentActivity.class);
                            intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PARAMS,
                                    jsonObject.toString());
                            intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PATH,
                                    Constants.getAbsUrl(Constants.HttpPath.SERVICE_ORDER_PAYMENT));
                            intent.putExtra(InstallmentPaymentActivity.ARG_PRICE, needPay);
                            intent.putExtra(InstallmentPaymentActivity.ARG_INSTALLMENT_STAGE_NUM,
                                    checkedInstallmentStageNum);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            break;
                        case PAY_BY_OTHERS:
                            // 直接跳转付款模块
                            goPayOrder(jsonObject);
                            break;
                    }
                }
            };
        }

        if (flowLayout.getCheckedIndex() >= 0) {
            checkedInstallmentStageNum = installment.getDetails()
                    .get(flowLayout.getCheckedIndex())
                    .getStageNum();
        }
        if (checkedInstallmentStageNum > 0) {
            // 直接验证分期
            XiaoxiInstallmentAuthorization.getInstance()
                    .onAuthorization(this, true);
            RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(paySubscriber);
        } else {
            goPayOrder(jsonObject);
        }
    }

    private void goPayOrder(JSONObject jsonObject) {
        PayConfig.Builder builder = new PayConfig.Builder(this);
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        List<String> payAgents = DataConfig.getServicePayAgents();
        if (!serviceOrder.getOrderSub()
                .getWork()
                .isInstallment() || payType == PayConfig.PAY_TYPE_DEPOSIT || payType == PayConfig
                .PAY_TYPE_INTENT) {
            // 移除分期支付
            payAgents.remove(PayAgent.XIAO_XI_PAY);
        }
        builder.payAgents(dataConfig != null ? dataConfig.getPayTypes() : null, payAgents);
        builder.params(jsonObject)
                .path(Constants.getAbsUrl(Constants.HttpPath.SERVICE_ORDER_PAYMENT))
                .price(needPay > 0 ? needPay : 0)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }

    private double getPayAllSavedMoney() {
        Work work = serviceOrder.getOrderSub()
                .getWork();
        // 快照中已经存储了下单时计算过的活动与否的价格
        return work.getPayAllPercent() * work.getShowPrice();
    }

    private double getShowEarnestMoney() {
        Work work = serviceOrder.getOrderSub()
                .getWork();
        // 快照中已经存储了下单时计算过的活动与否的价格
        return work.getEarnestMoney();
    }

    @Override
    public void onCheckedChanged(CheckableLinearLayoutGroup group, int checkedId) {
        setPrices();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(paySubscriber, installmentSub);
    }
}
