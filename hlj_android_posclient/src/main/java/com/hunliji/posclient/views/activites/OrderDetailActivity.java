package com.hunliji.posclient.views.activites;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.posclient.Constants;
import com.hunliji.posclient.R;
import com.hunliji.posclient.api.order.OrderApi;
import com.hunliji.posclient.models.MerchantOrder;
import com.hunliji.posclient.models.relam.PosPayResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;

/**
 * 订单详情
 * Created by chen_bin on 2018/1/15 0015.
 */
public class OrderDetailActivity extends HljBaseActivity {

    @BindView(R.id.tv_trade_no)
    TextView tvTradeNo;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.tv_actual_money)
    TextView tvActualMoney;
    @BindView(R.id.tv_unpaid_amount)
    TextView tvUnpaidAmount;
    @BindView(R.id.et_amount)
    ClearableEditText etAmount;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private Realm realm;
    private Dialog retryDialog;

    private MerchantOrder order;
    private PosPayResult payResult; //pos机支付回调model
    private PosPayResult realmPayResult; //realm 对象

    private String posCode;

    private HljHttpSubscriber initSub;
    private HljHttpSubscriber confirmPaySub;
    private HljHttpSubscriber submitSub;

    public final static String ARG_POS_CODE = "pos_code"; //二维码

    private final static String ARG_REASON = "reason";
    private final static String ARG_TRANS_NAME = "transName";
    private final static String ARG_TRACE_NO = "traceNo"; //流水号
    private final static String ARG_AMOUNT = "amount"; //金额
    private final static String ARG_REFERENCE_NO = "referenceNo"; //参考号
    private final static String ARG_CARD_NO = "cardNo"; //卡号

    private final static String PACKET_NAME = "cn.fengfu.zhejiang.fengfupay";
    private final static String CLASS_NAME = "cn.fengfu.zhejiang.fengfupay.unionpay.MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        initValues();
        initViews();
        initLoad(true);
    }

    private void initValues() {
        posCode = getIntent().getStringExtra(ARG_POS_CODE);
        realm = Realm.getDefaultInstance();
    }

    private void initViews() {
        hideBackButton();
        hideDividerView();
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setOnEmptyClickListener(new HljEmptyView.OnEmptyClickListener() {
            @Override
            public void onEmptyClickListener() {
                initLoad(true);
            }
        });
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                initLoad(true);
            }
        });
        scrollView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    final int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getWindow().getDecorView()
                        .getHeight();
                boolean isShowImm = (double) (bottom - top) / height < 0.8;
                if (isShowImm) {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            }
        });
    }

    private void initLoad(boolean isInit) {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<MerchantOrder>() {
                        @Override
                        public void onNext(MerchantOrder order) {
                            setMerchantOrder(order);
                            if (order.getActualMoney() - order.getPaidMoney() == 0) { //订单全部支付完成
                                onBackPressed();
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .setProgressBar(isInit ? progressBar : null)
                    .setProgressDialog(isInit ? null : getProgressDialog())
                    .build();
            OrderApi.getOrderDetailObb(posCode)
                    .subscribe(initSub);
        }
    }

    private void setMerchantOrder(MerchantOrder order) {
        this.order = order;
        tvTradeNo.setText(getString(R.string.label_trade_no, order.getTradeNo()));
        tvMerchantName.setText(order.getMerchantName());
        tvActualMoney.setText(getString(R.string.label_price___cm,
                CommonUtil.formatDouble2StringWithTwoFloat(order.getActualMoney())));
        tvUnpaidAmount.setText(getString(R.string.label_price___cm,
                CommonUtil.formatDouble2StringWithTwoFloat(order.getActualMoney() - order
                        .getPaidMoney())));
    }

    @OnTextChanged(R.id.et_amount)
    void onAfterTextChanged(Editable s) {
        String temp = s.toString();
        int posDot = temp.indexOf(".");
        if (posDot < 0) {
            return;
        }
        if (temp.length() - posDot - 1 > 2) {
            s.delete(posDot + 3, posDot + 4);
        }
    }

    @OnClick(R.id.tv_input_all_amount)
    void onInputAllAmount() {
        if (order == null) {
            return;
        }
        final double unpaidAmount = order.getActualMoney() - order.getPaidMoney();
        etAmount.setText(CommonUtil.formatDouble2StringWithTwoFloat(unpaidAmount));
        etAmount.setSelection(etAmount.length());
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (order == null) {
            return;
        }
        String amountStr = etAmount.getText()
                .toString();
        if (CommonUtil.isEmpty(amountStr) || TextUtils.equals(".", amountStr)) {
            ToastUtil.showToast(this, "本次支付金额不能为空！", 0, 5000);
            return;
        }
        double amount = Double.valueOf(amountStr);
        if (amount == 0) {
            ToastUtil.showToast(this, "本次支付金额不能为0！", 0, 5000);
            return;
        }
        final double unpaidAmount = order.getActualMoney() - order.getPaidMoney();
        if (amount > unpaidAmount) {
            ToastUtil.showToast(this, "本次支付金额不得大于还需支付的金额！", 0, 5000);
            return;
        }
        confirmPay(amount);
    }

    private void confirmPay(final double amount) {
        CommonUtil.unSubscribeSubs(confirmPaySub);
        confirmPaySub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<PosPayResult>() {
                    @Override
                    public void onNext(PosPayResult result) {
                        payResult = result;
                        if (CommonUtil.isEmpty(payResult.getOutTradeNo())) {
                            ToastUtil.showToast(OrderDetailActivity.this, "outTradeNo is null", 0);
                            return;
                        }
                        if (CommonUtil.isEmpty(payResult.getNotifyUrl())) {
                            ToastUtil.showToast(OrderDetailActivity.this, "notifyUrl is null", 0);
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(PACKET_NAME, CLASS_NAME));
                        intent.putExtra(ARG_TRANS_NAME, "消费");
                        //银联格式000000001000
                        String posAmount;
                        // TODO: 2018/4/2 wangtao 测试代码
                        if (isTestServer()) {
                            posAmount = "000000000001";
                        } else {
                            posAmount = String.format("%012d", (long) (amount * 100));
                        }
                        intent.putExtra(ARG_AMOUNT, posAmount);
                        startActivityForResult(intent, Constants.RequestCode.UNIONPAY);
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        OrderApi.confirmPayObb(posCode, amount)
                .subscribe(confirmPaySub);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.UNIONPAY && data != null) {
            switch (resultCode) {
                case RESULT_CANCELED:
                    String reason = data.getStringExtra(ARG_REASON);
                    if (!CommonUtil.isEmpty(reason)) {
                        ToastUtil.showToast(this, reason, 0);
                    }
                    break;
                case RESULT_OK:
                    // 在请求confirmPayObb中已经设置了amount，为了确保正确性，将pos返回的amount再设置一遍。
                    // TODO: 2018/4/2 wangtao 测试代码 
                    if (!isTestServer()) {
                        try {
                            payResult.setAmount(CommonUtil.formatDouble2String(Double.valueOf(data.getStringExtra(

                                    ARG_AMOUNT)) / 100.00d));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    payResult.setTraceNo(data.getStringExtra(ARG_TRACE_NO));
                    payResult.setReferenceNo(data.getStringExtra(ARG_REFERENCE_NO));
                    payResult.setCardNo(data.getStringExtra(ARG_CARD_NO));

                    realm.beginTransaction();
                    realmPayResult = realm.copyToRealmOrUpdate(payResult);
                    realm.commitTransaction();

                    submitPosPayResult();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void submitPosPayResult() {
        CommonUtil.unSubscribeSubs(submitSub);
        submitSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object aBoolean) {
                        ToastUtil.showCustomToast(OrderDetailActivity.this, "支付成功");
                        realm.beginTransaction();
                        realmPayResult.deleteFromRealm();
                        realm.commitTransaction();
                        initLoad(false);
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Object o) {
                        showRetryDialog();
                    }
                })
                .setDataNullable(true)
                .build();
        OrderApi.submitPosPayResultObb(payResult)
                .subscribe(submitSub);
    }

    /**
     * pos机支付回调到婚礼纪服务器失败，弹框提示重试
     */
    private void showRetryDialog() {
        if (retryDialog != null && retryDialog.isShowing()) {
            return;
        }
        if (retryDialog == null) {
            retryDialog = DialogUtil.createSingleButtonDialog(this,
                    "支付失败，请重试",
                    null,
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submitPosPayResult();
                        }
                    });
            retryDialog.setCancelable(false);
            retryDialog.setCanceledOnTouchOutside(false);
        }
        retryDialog.show();
    }

    private Dialog getProgressDialog() {
        Dialog progressDialog = DialogUtil.createProgressDialog(this, R.style.BubbleDialogTheme);
        Window win = progressDialog.getWindow();
        if (win != null) {
            win.setDimAmount(0);
        }
        return progressDialog;
    }

    private boolean isTestServer() {
        return Constants.HOST.contains("dev.hunliji.com");
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, confirmPaySub, submitSub);
        realm.close();
    }

}