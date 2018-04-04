package com.hunliji.hljpaymentlibrary.views.activities.xiaoxi_installment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljpaymentlibrary.HljPay;
import com.hunliji.hljpaymentlibrary.R;
import com.hunliji.hljpaymentlibrary.R2;
import com.hunliji.hljpaymentlibrary.adapters.xiaoxi_installment.PreviewSchedulesAdapter;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Installment2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentDetail;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.wrappers
        .XiaoxiInstallmentPreviewSchedulesData;
import com.hunliji.hljpaymentlibrary.views.fragments.xiaoxi_installment
        .XiaoxiInstallmentConfirmVerFragment;
import com.hunliji.hljpaymentlibrary.views.widgets.xiaoxi_installment
        .XiaoxiInstallmentAgreementsDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;


public class InstallmentPaymentActivity extends HljBaseActivity implements
        XiaoxiInstallmentConfirmVerFragment.OnConfirmSuccessListener {

    public static final int ORDER_PAY_TYPE_HOTEL = 10;//婚宴酒店支付

    public static final String ARG_ORDER_PAY_TYPE = "order_pay_type";//付款类型
    public static final String ARG_INSTALLMENT_STAGE_NUM = "installment_stage_num";
    public static final String ARG_PAY_PARAMS = "pay_params";
    public static final String ARG_PAY_PATH = "pay_path";
    public static final String ARG_PRICE = "price";
    public static final String ARG_WEDDING_DAY = "wedding_day";

    @BindView(R2.id.tv_price)
    TextView tvPrice;
    @BindView(R2.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R2.id.tv_loan_money_amount)
    TextView tvLoanMoneyAmount;
    @BindView(R2.id.tv_payback_money_each)
    TextView tvPaybackMoneyEach;
    @BindView(R2.id.tv_see_detail_link)
    TextView tvSeeDetailLink;
    @BindView(R2.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R2.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R2.id.btn_confirm_installment)
    Button btnConfirmInstallment;
    @BindView(R2.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private HljPay hljPay;

    private int selectedStageNum;
    private double price;
    private String payParams;
    private String payPath;
    private HljHttpSubscriber initSub;
    private Installment2 installment;
    private Dialog dialog;
    private HljHttpSubscriber scheduleSub;
    private HashMap<Integer, XiaoxiInstallmentPreviewSchedulesData> schedulesDataMap;
    private Subscription paySub;
    private JsonObject payResult;
    private int orderPayType;//订单支付类型
    private String weddingDay;
    private int periodLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installment_payment);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    private void initValues() {
        weddingDay = getIntent().getStringExtra(ARG_WEDDING_DAY);
        orderPayType = getIntent().getIntExtra(ARG_ORDER_PAY_TYPE, 0);
        payParams = getIntent().getStringExtra(ARG_PAY_PARAMS);
        payPath = getIntent().getStringExtra(ARG_PAY_PATH);
        selectedStageNum = getIntent().getIntExtra(ARG_INSTALLMENT_STAGE_NUM, -1);
        price = getIntent().getDoubleExtra(ARG_PRICE, 0);
        schedulesDataMap = new HashMap<>();
        paySub = RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(new RxBusSubscriber<PayRxEvent>() {
                    @Override
                    protected void onEvent(PayRxEvent payRxEvent) {
                        switch (payRxEvent.getType()) {
                            case ORDER_SUBMIT_SUCCESS:
                                // 支付成功，对于分期来说只是收单成功，需要进一步确认订单
                                JsonObject jsonObject = (JsonObject) payRxEvent.getObject();
                                if (jsonObject != null) {
                                    payResult = jsonObject;
                                    onConfirmOrder();
                                }
                                break;
                            case CREDIT_NOT_ENOUGH:
                            case CREDIT_NOT_ENOUGH2:
                            case AUTHORIZE_NOT_PASSED:
                                onCreditNotEnough(payRxEvent);
                                break;
                        }
                    }
                });
    }

    private int getWeddingDayGap(Date weddingDate) {
        if (weddingDate == null) {
            return 0;
        }
        Calendar now = Calendar.getInstance();
        long gapMills = weddingDate.getTime() - now.getTimeInMillis();
        int day = (int) Math.ceil(gapMills / (24 * 60 * 60 * 1000));
        return day;
    }

    private int setPeriod(int day) {
        int noVisibleStageNum = -1;
        if (day < 0) {
            return noVisibleStageNum;
        }
        List<InstallmentDetail> details = installment.getDetails();
        if (CommonUtil.isCollectionEmpty(details)) {
            return noVisibleStageNum;
        }

        if (day < periodLimit * 30) {
            noVisibleStageNum = details.get(details.size() - 1)
                    .getStageNum();
        } else {
            noVisibleStageNum = 0;
        }
        return noVisibleStageNum;
    }

    private void setPeriodNotVisibleStageNum() {
        try {
            int noVisibleStageNum = 0;
            if (!TextUtils.isEmpty(weddingDay)) {
                SimpleDateFormat format = new SimpleDateFormat(HljCommon.DateFormat
                        .DATE_FORMAT_SHORT);
                Date weddingDate = format.parse(weddingDay);
                int day = getWeddingDayGap(weddingDate);
                noVisibleStageNum = setPeriod(day);
            }
            setViews(noVisibleStageNum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                btnConfirmInstallment.setEnabled(b);
            }
        });
        cbAgreement.setChecked(true);
        SpannableStringBuilder builder = new SpannableStringBuilder(tvAgreement.getText());
        builder.setSpan(new AgreementClickableSpan(0), 7, 16, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        builder.setSpan(new AgreementClickableSpan(1),
                16,
                tvAgreement.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvAgreement.setText(builder);
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class AgreementClickableSpan extends ClickableSpan {
        private int type;

        private AgreementClickableSpan(int type) {
            this.type = type;
        }

        @Override
        public void onClick(View widget) {
            if (type == 0) { //小犀分期相关服务协议
                new XiaoxiInstallmentAgreementsDialog(InstallmentPaymentActivity.this).show();
            } else { //51分期借款须知和风险告知书
                HljWeb.startWebView(InstallmentPaymentActivity.this,
                        XiaoxiInstallmentApi.XIAOXI_INSTALLMENT_LOAN_AND_RISK_URL);
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(InstallmentPaymentActivity.this, R.color.colorLink));
            ds.setUnderlineText(false);
        }
    }

    private void initLoad() {
        initSub = HljHttpSubscriber.buildSubscriber(this)
                .setContentView(contentLayout)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<InstallmentData>() {
                    @Override
                    public void onNext(InstallmentData installmentData) {
                        installment = installmentData.getInstallment();
                        periodLimit = installmentData.getPeriodLimit();
                        // 开始后台加载预览数据
                        if (selectedStageNum <= 0) {
                            // 如果没有预设分期选项，默认选中第一个分期选项
                            if (!CommonUtil.isCollectionEmpty(installment.getDetails())) {
                                selectedStageNum = installment.getDetails()
                                        .get(0)
                                        .getStageNum();
                            }
                        }
                        loadDetailSchedule(selectedStageNum, false);
                        setMoneyView();
                        switch (orderPayType) {
                            case ORDER_PAY_TYPE_HOTEL:
                                setPeriodNotVisibleStageNum();
                                break;
                            default:
                                setViews();
                        }
                    }
                })
                .build();
        XiaoxiInstallmentApi.getInstallmentInfo(price)
                .subscribe(initSub);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RxBus.getDefault()
                .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_CANCEL, null));
    }

    private void setViews() {
        setViews(-1);
    }

    private void setViews(int lastStageNum) {
        List<InstallmentDetail> details = installment.getDetails();
        if (CommonUtil.isCollectionEmpty(details)) {
            return;
        }
        int installmentWidth = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this,
                16 * 3)) / 2;
        flowLayout.removeAllViews();
        for (int i = 0, size = details.size(); i < size; i++) {
            InstallmentDetail detail = details.get(i);
            if (detail.getStageNum() == lastStageNum) {
                continue;
            }
            final CheckBox cbDetail = (CheckBox) LayoutInflater.from(this)
                    .inflate(com.hunliji.hljpaymentlibrary.R.layout.installment_check_box,
                            null,
                            false);
            cbDetail.setText(detail.getStageNumString());
            cbDetail.setTag(i);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(installmentWidth,
                    CommonUtil.dp2px(this, 44));
            if (detail.getStageNum() == selectedStageNum) {
                cbDetail.setChecked(true);
            }
            flowLayout.addView3(cbDetail, params);
        }

        flowLayout.setOnChildCheckedChangeListener(new FlowLayout.OnChildCheckedChangeListener() {
            @Override
            public void onCheckedChange(View childView, int index) {
                selectedStageNum = installment.getDetails()
                        .get(index)
                        .getStageNum();
                setMoneyView();
            }
        });
    }

    private void setMoneyView(){
        tvPrice.setText(getString(R.string.label_price, CommonUtil.formatDouble2String(price)));
        tvLoanMoneyAmount.setText(getString(R.string.label_price,
                CommonUtil.formatDouble2String(price)));

        setSelectedInstallmentInfo();
    }


    private void setSelectedInstallmentInfo() {
        for (InstallmentDetail detail : installment.getDetails()) {
            if (detail.getStageNum() == selectedStageNum) {
                tvPaybackMoneyEach.setText(getString(R.string.label_price,
                        CommonUtil.formatDouble2String(detail.getEachPay())));
            }
        }
    }

    @OnClick(R2.id.tv_see_detail_link)
    void onSeeDetail() {
        showSchedules();
    }

    private void loadDetailSchedule(final int stageNum, final boolean showImmediately) {

        scheduleSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(showImmediately ? DialogUtil.createProgressDialog(this) : null)
                .setOnNextListener(new SubscriberOnNextListener<XiaoxiInstallmentPreviewSchedulesData>() {
                    @Override
                    public void onNext(XiaoxiInstallmentPreviewSchedulesData schedulesData) {
                        schedulesDataMap.put(stageNum, schedulesData);
                        if (showImmediately) {
                            showSchedules();
                        }
                        for (InstallmentDetail detail : installment.getDetails()) {
                            if (!schedulesDataMap.containsKey(detail.getStageNum())) {
                                loadDetailSchedule(detail.getStageNum(), false);
                                break;
                            }
                        }
                    }
                })
                .build();
        XiaoxiInstallmentApi.previewPaybackSchedule(this, price, stageNum)
                .subscribe(scheduleSub);
    }

    private void showSchedules() {
        if (!schedulesDataMap.containsKey(selectedStageNum)) {
            loadDetailSchedule(selectedStageNum, true);
        } else {
            XiaoxiInstallmentPreviewSchedulesData schedulesData = schedulesDataMap.get(
                    selectedStageNum);
            if (dialog == null) {
                dialog = new Dialog(this, R.style.BubbleDialogTheme);
                dialog.setContentView(R.layout.dialog_installment_schedules___pay);
                final Dialog finalDialog = dialog;
                dialog.findViewById(R.id.btn_close)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finalDialog.dismiss();
                            }
                        });
                Window win = dialog.getWindow();
                WindowManager.LayoutParams params = win.getAttributes();
                params.width = CommonUtil.getDeviceSize(this).x;
                params.height = CommonUtil.getDeviceSize(this).y * 2 / 3;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
            ListView listView = dialog.findViewById(R.id.list_view);
            listView.setAdapter(new PreviewSchedulesAdapter(schedulesData.getSchedules()));
            TextView tvCapitalizedCost = dialog.findViewById(R.id.tv_capitalized_cost);
            tvCapitalizedCost.setText(getString(R.string.label_capitalized_cost___pay,
                    CommonUtil.formatDouble2StringWithTwoFloat(schedulesData.getCapitalizedCost()
                    )));
            dialog.show();
        }
    }

    @OnClick(R2.id.btn_confirm_installment)
    void onSubmit() {
        initPay().onPayXiaoxiInstallment(selectedStageNum);
    }

    private HljPay initPay() {
        if (hljPay == null) {
            hljPay = new HljPay.Builder(this).params(payParams)
                    .path(payPath)
                    .price(price)
                    .build();
        }

        return hljPay;
    }

    private void onConfirmOrder() {
        // 默认和0，认为成功
        // 小犀分期返回消息，获取其中的smsSerialCode和verify_phone，以继续支付流程
        String verifyPhone = null;
        String smsSerialNo;
        String assetOrderId;
        JSONObject payParams = null;
        try {
            payParams = new JSONObject(payResult.get("pay_params")
                    .getAsString());
            verifyPhone = payResult.get("verify_phone")
                    .getAsString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (payParams != null) {
            smsSerialNo = payParams.optString("smsSerialNo");
            assetOrderId = payParams.optString("assetOrderId");
            XiaoxiInstallmentConfirmVerFragment verificationFragment =
                    XiaoxiInstallmentConfirmVerFragment.newInstance(
                    verifyPhone,
                    smsSerialNo,
                    assetOrderId);
            verificationFragment.setOnConfirmSuccessListener(this);
            verificationFragment.show(getSupportFragmentManager(),
                    "XiaoxiInstallmentConfirmVerificationFragment");
        }
    }

    private void showHotelAuthorizeNotPassedDialog(PayRxEvent payRxEvent) {
        Dialog dialog = DialogUtil.createSingleButtonDialog(this,
                "您暂时无法分期支付",
                "好的",
                null);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showDefaultAuthorizeNotPassedDialog(final PayRxEvent payRxEvent) {
        Dialog dialog = DialogUtil.createSingleButtonDialog(this,
                "您暂时无法分期支付，请使用其他支付方式。",
                "其他支付方式",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RxBus.getDefault()
                                .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_BY_OTHERS,
                                        payRxEvent.getObject()));
                        finish();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showHotelCreditNotEnought2Dialog(PayRxEvent payRxEvent) {
        Dialog dialog = DialogUtil.createDoubleButtonDialog(this,
                "您的分期可用额度小于最低借款金额，\n" + "请先提额~",
                "取消",
                "提额",
                null,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(InstallmentPaymentActivity.this,
                                LimitAuthItemListActivity.class);
                        intent.putExtra(LimitAuthItemListActivity.ARG_IS_PAY, true);
                        startActivity(intent);
                        finish();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showDefaultCreditNotEnought2Dialog(final PayRxEvent payRxEvent) {
        Dialog dialog = DialogUtil.createDoubleButtonDialog(this,
                "您的分期可用额度小于最低借款金额，\n" + "请先提额~",
                "提额",
                "其他支付方式",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(InstallmentPaymentActivity.this,
                                LimitAuthItemListActivity.class);
                        intent.putExtra(LimitAuthItemListActivity.ARG_IS_PAY, true);
                        startActivity(intent);
                        finish();
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RxBus.getDefault()
                                .post(new PayRxEvent(PayRxEvent.RxEventType.PAY_BY_OTHERS,
                                        payRxEvent.getObject()));
                        finish();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void onCreditNotEnough(PayRxEvent payRxEvent) {
        switch (payRxEvent.getType()) {
            case CREDIT_NOT_ENOUGH:
            case AUTHORIZE_NOT_PASSED:
                // 3024, 51返回的授信额度不足，意味着： 真实额度 < amount < 显示额度
                // 无法分期支付，不显示提额按钮
                // 风控校验未通过也不能支付
                switch (orderPayType) {
                    case ORDER_PAY_TYPE_HOTEL:
                        showHotelAuthorizeNotPassedDialog(payRxEvent);
                        break;
                    default:
                        showDefaultAuthorizeNotPassedDialog(payRxEvent);
                        break;
                }
                break;
            case CREDIT_NOT_ENOUGH2:
                // 900001, 婚礼纪服务器返回的额度不足，意味着： amount > 显示额度
                // 无法分期支付，显示提额按钮
                switch (orderPayType) {
                    case ORDER_PAY_TYPE_HOTEL:
                        showHotelCreditNotEnought2Dialog(payRxEvent);
                        break;
                    default:
                        showDefaultCreditNotEnought2Dialog(payRxEvent);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, paySub);
    }

    @Override
    public void onConfirmSuccess() {
        // 确认分期支付成功
        final Dialog dialog = DialogUtil.createSingleButtonDialog(InstallmentPaymentActivity.this,
                getString(R.string.msg_xiaoxi_installment_success),
                getString(R.string.label_confirm___cm),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RxBus.getDefault()
                                .post(new PayRxEvent(PayRxEvent.RxEventType.INSTALLMENT_PAY_SUCCESS,
                                        payResult));

                        finish();
                    }
                });
        dialog.setCancelable(false);
        dialog.show();
    }
}
