package me.suncloud.marrymemo.view.orders;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpData;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImageUtil;
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
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.orders.CouponsListAdapter;
import me.suncloud.marrymemo.adpter.orders.RedPacketsListAdapter;
import me.suncloud.marrymemo.api.orders.OrderApi;
import me.suncloud.marrymemo.model.Rule;
import me.suncloud.marrymemo.model.Work;
import me.suncloud.marrymemo.model.orders.ServeCustomerInfo;
import me.suncloud.marrymemo.model.orders.ServiceOrder;
import me.suncloud.marrymemo.model.orders.ServiceOrderSubmitBody;
import me.suncloud.marrymemo.model.orders.ServiceOrderSubmitResponse;
import me.suncloud.marrymemo.model.wallet.CouponRecord;

import com.hunliji.hljcardcustomerlibrary.models.RedPacket;

import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.DialogUtil;
import me.suncloud.marrymemo.util.ServeCustomerInfoUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.AfterPayActivity;
import me.suncloud.marrymemo.view.MyOrderListActivity;
import me.suncloud.marrymemo.view.OrderInfoEditActivity;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutButton;
import me.suncloud.marrymemo.widget.CheckableLinearLayoutGroup;
import me.suncloud.marrymemo.widget.FlowLayout;
import rx.Subscriber;

/**
 * 本地服务订单填写订单页面
 * 用户在本地服务套餐详情页面点击立即购买按钮后进入这个页面进行订单信息的填写从而确认下单
 * 本页面需要填写并确认的信息包括:客户信息(联系信息),支付方式(定金还是全款),红包、优惠券选择
 */
public class ServiceOrderConfirmActivity extends HljBaseActivity implements
        CheckableLinearLayoutGroup.OnCheckedChangeListener {

    @BindView(R.id.tv_serve_customer)
    TextView tvServeCustomer;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_serve_time)
    TextView tvServeTime;
    @BindView(R.id.serve_time_layout)
    LinearLayout serveTimeLayout;
    @BindView(R.id.tv_serve_addr)
    TextView tvServeAddr;
    @BindView(R.id.info_layout)
    LinearLayout infoLayout;
    @BindView(R.id.img_cover)
    ImageView imgCover;
    @BindView(R.id.img_installment)
    ImageView imgInstallment;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @BindView(R.id.tv_work_price)
    TextView tvWorkPrice;
    @BindView(R.id.work_item_layout)
    LinearLayout workItemLayout;
    @BindView(R.id.tv_sales_text)
    TextView tvSalesText;
    @BindView(R.id.sales_layout)
    LinearLayout salesLayout;
    @BindView(R.id.tv_available_coupons_count)
    TextView tvAvailableCouponsCount;
    @BindView(R.id.tv_coupon_saved_money)
    TextView tvCouponSavedMoney;
    @BindView(R.id.red_packet_coupon_layout)
    View redPacketCouponLayout;
    @BindView(R.id.tv_available_red_packets_count)
    TextView tvAvailableRedPacketsCount;
    @BindView(R.id.tv_red_packet_saved_money)
    TextView tvRedPacketSavedMoney;
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
    @BindView(R.id.tv_work_price2)
    TextView tvWorkPrice2;
    @BindView(R.id.tv_pay_all_saved_money)
    TextView tvPayAllSavedMoney;
    @BindView(R.id.tv_coupon_money)
    TextView tvCouponMoney;
    @BindView(R.id.tv_red_packet_money)
    TextView tvRedPacketMoney;
    @BindView(R.id.tv_total_money_label)
    TextView tvTotalMoneyLabel;
    @BindView(R.id.tv_total_money)
    TextView tvTotalMoney;
    @BindView(R.id.et_leave_memo)
    EditText etLeaveMemo;
    @BindView(R.id.tv_need_pay_money)
    TextView tvNeedPayMoney;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @BindView(R.id.tv_pay_deposit_money)
    TextView tvPayDepositMoney;
    @BindView(R.id.pay_deposit_money_layout)
    LinearLayout payDepositMoneyLayout;
    @BindView(R.id.pay_all_saved_money_layout)
    LinearLayout payAllSavedMoneyLayout;
    @BindView(R.id.coupon_layout)
    LinearLayout couponLayout;
    @BindView(R.id.red_packet_layout)
    LinearLayout redPacketLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.coupon_money_layout)
    LinearLayout couponMoneyLayout;
    @BindView(R.id.red_packet_money_layout)
    LinearLayout redPacketMoneyLayout;
    @BindView(R.id.tv_saved_money_hint)
    TextView tvSavedMoneyHint;
    @BindView(R.id.tv_pay_intent_money)
    TextView tvPayIntentMoney;
    @BindView(R.id.pay_intent_money_layout)
    LinearLayout payIntentMoneyLayout;
    @BindView(R.id.img_intent_money)
    ImageView imgIntentMoney;
    @BindView(R.id.tv_pay_all_saved_hint)
    TextView tvPayAllSavedHint;
    @BindView(R.id.tv_installment_hint)
    TextView tvInstallmentHint;
    @BindView(R.id.xiaoxi_installment_layout)
    LinearLayout xiaoxiInstallmentLayout;
    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;

    private ServeCustomerInfo customerInfo;
    private Work work;
    private int coverWidth;
    private int installmentWidth;
    private boolean isNeedWeddingTime; // 是否必要设置婚期,根据套餐所属商家经营范围来判断

    private double workPrice;
    private double earnestMoney;
    private int payAllSavedMoney;
    private double redPacketMoney;
    private double couponMoney;
    private double needPayMoney;
    private String disableDepositReason;
    private int disableDepositType;
    private boolean isIntentPayNow; // 套餐当前是不是满足意向金支付

    private ArrayList<RedPacket> redPackets = new ArrayList<>();
    private ArrayList<CouponRecord> couponRecords = new ArrayList<>();

    private HljHttpSubscriber redPacketSub;
    private HljHttpSubscriber couponSub;

    private Dialog redPacketDialog;
    private Dialog couponListDialog;
    private RedPacketsListAdapter redPacketsAdapter;
    private RedPacket selectedRedPacket;
    private RedPacket pendingRedPacket;
    private CouponsListAdapter couponsAdapter;
    private CouponRecord selectedCoupon;
    private CouponRecord pendingCoupon;
    private Dialog backDlg;
    private HljHttpSubscriber submitSub;
    private Dialog confirmDialog;
    private Subscriber<PayRxEvent> paySubscriber;
    private Handler handler = new Handler();

    //    自动提交订单
    private Runnable submitRunnable = new Runnable() {
        @Override
        public void run() {
            if (confirmDialog != null && confirmDialog.isShowing()) {
                confirmDialog.dismiss();
                onPostOrder();
            }
        }
    };
    private int checkedInstallmentStageNum = -1;

    public static final String ARG_WORK = "work";
    public static final String ARG_INSTALLMENT_STAGE_NUM = "installment_stage_num";
    private HljHttpSubscriber installmentSub;
    private int payType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order_confirm);
        ButterKnife.bind(this);

        initValues();
        initViews();
        initLoad();
    }

    @Override
    protected void onResume() {
        setCustomerInfoView();
        setPrices();
        super.onResume();
    }

    private void initValues() {
        work = (Work) getIntent().getSerializableExtra(ARG_WORK);
        checkedInstallmentStageNum = getIntent().getIntExtra(ARG_INSTALLMENT_STAGE_NUM, -1);
        isIntentPayNow = work.isIntentPayNow();
        if (isIntentPayNow || work.getPropertyId() == 6) {
            // 婚纱摄影不需要设置婚期，意向金不需要，其他都需要
            isNeedWeddingTime = false;
        } else {
            isNeedWeddingTime = true;
        }

        coverWidth = CommonUtil.dp2px(this, 160);
        installmentWidth = (CommonUtil.getDeviceSize(this).x - CommonUtil.dp2px(this, 16 * 3)) / 2;
    }

    private void initViews() {
        setWorkInfoView();
        checkGroupPayType.setOnCheckedChangeListener(this);
    }

    private void initLoad() {
        getCouponList();
    }

    private void getCouponList() {
        couponSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<CouponRecord>>>() {
                    @Override
                    public void onNext(HljHttpData<List<CouponRecord>> listHljHttpData) {
                        couponRecords.clear();
                        if (listHljHttpData.getData() != null) {
                            couponRecords.addAll(listHljHttpData.getData());
                        }
                        if (couponRecords.size() > 0) {
                            couponLayout.setVisibility(View.VISIBLE);
                            // 取第一个默认选取
                            selectedCoupon = couponRecords.get(0);
                            tvCouponSavedMoney.setText("省" + Util.formatDouble2String
                                    (selectedCoupon.getValue()) + "元");
                            tvCouponSavedMoney.setTextColor(ContextCompat.getColor(
                                    ServiceOrderConfirmActivity.this,
                                    R.color.colorPrimary));

                            tvAvailableCouponsCount.setText(couponRecords.size() + "个可用");
                            tvAvailableCouponsCount.setVisibility(View.VISIBLE);

                            // 添加一个不使用的空券
                            CouponRecord useNoneCoupon = new CouponRecord();
                            useNoneCoupon.setId(-1);
                            couponRecords.add(useNoneCoupon);
                        } else {
                            couponLayout.setVisibility(View.GONE);
                        }

                        if (redPackets.isEmpty() && couponRecords.isEmpty()) {
                            redPacketCouponLayout.setVisibility(View.GONE);
                        } else {
                            redPacketCouponLayout.setVisibility(View.VISIBLE);
                        }

                        // 加载完优惠券后才能加载红包列表
                        getRedPacketList();
                    }
                })
                .setDataNullable(true)
                .build();
        OrderApi.getAvailableCouponList(work.getMerchantId(), workPrice)
                .subscribe(couponSub);
    }

    private void getRedPacketList() {
        redPacketSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpData<List<RedPacket>>>() {
                    @Override
                    public void onNext(HljHttpData<List<RedPacket>> listHljHttpData) {
                        redPackets.clear();
                        if (listHljHttpData.getData() != null) {
                            redPackets.addAll(listHljHttpData.getData());
                        }
                        // 默认不选择红包
                        selectedRedPacket = null;
                        tvRedPacketSavedMoney.setText("未使用");
                        tvRedPacketSavedMoney.setTextColor(ContextCompat.getColor(
                                ServiceOrderConfirmActivity.this,
                                R.color.colorGray));

                        if (redPackets.size() > 0) {
                            redPacketLayout.setVisibility(View.VISIBLE);
                            tvAvailableRedPacketsCount.setText(redPackets.size() + "个可用");
                            RedPacket useNoneRedPacket = new RedPacket();
                            useNoneRedPacket.setId(-1);
                            redPackets.add(useNoneRedPacket);
                            tvAvailableRedPacketsCount.setVisibility(View.VISIBLE);
                        } else {
                            redPacketLayout.setVisibility(View.GONE);
                        }

                        if (redPackets.isEmpty() && couponRecords.isEmpty()) {
                            redPacketCouponLayout.setVisibility(View.GONE);
                        } else {
                            redPacketCouponLayout.setVisibility(View.VISIBLE);
                        }

                        // 加载完优惠券和红包，因为有默认选择，所以需要设置价格
                        setPrices();
                    }
                })
                .setDataNullable(true)
                .build();
        OrderApi.getAvailableRedPacketList(work.getId(),
                (selectedCoupon == null || selectedCoupon.getId() == -1) ? 0 : selectedCoupon
                        .getId())
                .subscribe(redPacketSub);
    }

    @OnClick(R.id.red_packet_layout)
    void showRedPackets() {
        if (redPacketDialog != null && redPacketDialog.isShowing()) {
            return;
        }
        if (redPacketsAdapter == null) {
            redPacketsAdapter = new RedPacketsListAdapter(redPackets);
        }
        redPacketDialog = DialogUtil.createRedPacketDialog(redPacketDialog,
                this,
                redPacketsAdapter,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        pendingRedPacket = (RedPacket) parent.getAdapter()
                                .getItem(position);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        redPacketDialog.cancel();
                        if (pendingRedPacket != null) {
                            selectedRedPacket = pendingRedPacket;
                        }
                        setPrices();
                        if (selectedRedPacket == null || selectedRedPacket.getId() < 0) {
                            tvRedPacketSavedMoney.setText("未使用");
                            tvRedPacketSavedMoney.setTextColor(ContextCompat.getColor(
                                    ServiceOrderConfirmActivity.this,
                                    R.color.colorGray));
                            return;
                        }
                        tvRedPacketSavedMoney.setText("省" + Util.formatDouble2String(
                                selectedRedPacket.getAmount()) + "元");
                        tvRedPacketSavedMoney.setTextColor(ContextCompat.getColor(
                                ServiceOrderConfirmActivity.this,
                                R.color.colorPrimary));
                    }
                },
                redPackets.indexOf(selectedRedPacket));
        redPacketDialog.show();
        redPacketsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.coupon_layout)
    void showCouponList() {
        if (couponListDialog != null && couponListDialog.isShowing()) {
            return;
        }
        if (couponsAdapter == null) {
            couponsAdapter = new CouponsListAdapter(couponRecords);
        }
        couponListDialog = DialogUtil.createdCouponListDialog(couponListDialog,
                this,
                couponsAdapter,
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent, View view, int position, long id) {
                        pendingCoupon = (CouponRecord) parent.getAdapter()
                                .getItem(position);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        couponListDialog.cancel();
                        CouponRecord oldSelectedCoupon = selectedCoupon;
                        if (pendingCoupon != null) {
                            selectedCoupon = pendingCoupon;
                        }
                        setPrices();
                        // 选择不同的优惠券后需要重新刷新红包列表
                        if (oldSelectedCoupon == null || selectedCoupon == null ||
                                oldSelectedCoupon.getId() != selectedCoupon.getId()) {
                            getRedPacketList();
                        }

                        if (selectedCoupon == null || selectedCoupon.getId() < 0) {
                            tvCouponSavedMoney.setText("未使用");
                            tvCouponSavedMoney.setTextColor(ContextCompat.getColor(
                                    ServiceOrderConfirmActivity.this,
                                    R.color.colorGray));
                            return;
                        }
                        tvCouponSavedMoney.setText("省" + Util.formatDouble2String(selectedCoupon
                                .getValue()) + "元");
                        tvCouponSavedMoney.setTextColor(ContextCompat.getColor(
                                ServiceOrderConfirmActivity.this,
                                R.color.colorPrimary));
                    }
                },
                couponRecords.indexOf(selectedCoupon));
        couponListDialog.show();
        couponsAdapter.notifyDataSetChanged();
    }


    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (TextUtils.isEmpty(customerInfo.getCustomerName())) {
            Toast.makeText(this, getString(R.string.msg_name_empty2), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(customerInfo.getCustomerPhone())) {
            Toast.makeText(this, getString(R.string.msg_phone_empty2), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (isNeedWeddingTime) {
            if (customerInfo.getServeTime() == null) {
                Toast.makeText(this, getString(R.string.msg_time_empty2), Toast.LENGTH_SHORT)
                        .show();
                return;
            } else {
                if (customerInfo.getServeTime()
                        .isBeforeNow()) {
                    // 当前填写的这个时间不能使用
                    Toast.makeText(this, getString(R.string.msg_wrong_time), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
        }

        showConfirmDialog();
        //        goSubmitOrder();
    }

    private void goSubmitOrder() {
        ServiceOrderSubmitBody body = new ServiceOrderSubmitBody();
        body.setSetMealId(work.getId());
        body.setBuyerName(customerInfo.getCustomerName());
        body.setBuyerPhone(customerInfo.getCustomerPhone());
        body.setMessage(etLeaveMemo.getText()
                .toString());
        body.setPayType(isIntentPayNow ? 5 : (checkGroupPayType.getCheckedRadioButtonId() == R.id
                .check_btn_pay_deposit ? 2 : 1));
        body.setRedPacketNo((selectedRedPacket != null && selectedRedPacket.getId() != -1) ?
                selectedRedPacket.getTicketNo() : "");
        body.setUserCouponId((selectedCoupon != null && selectedCoupon.getId() != -1) ?
                selectedCoupon.getId() : 0);
        body.setWeddingTime(isNeedWeddingTime ? customerInfo.getServeTime() : null);

        Intent intent = new Intent(this, OrderInfoConfirmActivity.class);
        intent.putExtra("merchant_city",
                work.getMerchant()
                        .getCityName());
        intent.putExtra("submit_data", body);
        intent.putExtra("need_pay_money", needPayMoney);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onBackPressed() {
        if (backDlg == null) {
            backDlg = com.hunliji.hljcommonlibrary.utils.DialogUtil.createDoubleButtonDialog(this,
                    "好不容易选择好的商品，\n确认放弃吗？",
                    "我点错了",
                    "放弃",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 点错了
                            backDlg.cancel();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 放弃
                            ServiceOrderConfirmActivity.super.onBackPressed();
                            backDlg.cancel();
                        }
                    });
        }
        backDlg.show();
    }

    /**
     * 显示服务信息确认弹窗，5秒钟倒计时不确认则自动确认提交
     */
    private void showConfirmDialog() {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            return;
        }
        if (confirmDialog == null) {
            confirmDialog = new Dialog(this, R.style.bubble_dialog_fragment);
            confirmDialog.setContentView(R.layout.dialog_order_confirm);
            confirmDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    handler.postDelayed(submitRunnable, 5000);
                }
            });
            confirmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(submitRunnable);
                }
            });
            confirmDialog.findViewById(R.id.btn_confirm)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                            onPostOrder();
                        }
                    });
            confirmDialog.findViewById(R.id.btn_cancel)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialog.dismiss();
                            onEditInfo();
                        }
                    });
            confirmDialog.findViewById(R.id.merchant_city_layout)
                    .setVisibility(View.VISIBLE);
            ((TextView) confirmDialog.findViewById(R.id.tv_city)).setText(work.getMerchant()
                    .getCityName());
        }

        if (isNeedWeddingTime) {
            confirmDialog.findViewById(R.id.serve_time_layout)
                    .setVisibility(View.VISIBLE);
            ((TextView) confirmDialog.findViewById(R.id.tv_serve_time)).setText(customerInfo
                    .getServeTime()
                    .toString("yyyy-MM-dd"));
        } else {
            confirmDialog.findViewById(R.id.serve_time_layout)
                    .setVisibility(View.GONE);
        }
        ((TextView) confirmDialog.findViewById(R.id.tv_serve_customer)).setText(customerInfo
                .getCustomerName());
        ((TextView) confirmDialog.findViewById(R.id.tv_phone)).setText(customerInfo
                .getCustomerPhone());

        confirmDialog.show();
    }


    private void onPostOrder() {
        payType = isIntentPayNow ? PayConfig.PAY_TYPE_INTENT : (checkGroupPayType
                .getCheckedRadioButtonId() == R.id.check_btn_pay_deposit ? PayConfig
                .PAY_TYPE_DEPOSIT : PayConfig.PAY_TYPE_PAY_ALL);
        ServiceOrderSubmitBody body = new ServiceOrderSubmitBody();
        body.setSetMealId(work.getId());
        body.setBuyerName(customerInfo.getCustomerName());
        body.setBuyerPhone(customerInfo.getCustomerPhone());
        body.setMessage(etLeaveMemo.getText()
                .toString());
        body.setPayType(payType);
        body.setRedPacketNo((selectedRedPacket != null && selectedRedPacket.getId() != -1) ?
                selectedRedPacket.getTicketNo() : "");
        body.setUserCouponId((selectedCoupon != null && selectedCoupon.getId() != -1) ?
                selectedCoupon.getId() : 0);
        body.setWeddingTime(isNeedWeddingTime ? customerInfo.getServeTime() : null);
        submitSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setOnNextListener(new SubscriberOnNextListener<ServiceOrderSubmitResponse>() {
                    @Override
                    public void onNext(
                            ServiceOrderSubmitResponse serviceOrderSubmitResponse) {
                        // 提交成功，直接付款
                        afterOrderSubmitted(serviceOrderSubmitResponse.getOrder());
                    }
                })
                .build();
        OrderApi.submitServiceOrder(body)
                .subscribe(submitSub);
    }

    /**
     * 生成订单之后操作
     *
     * @param serviceOrder
     */
    private void afterOrderSubmitted(final ServiceOrder serviceOrder) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", serviceOrder.getId());
            // 初次付款，付定金或者付全款
            jsonObject.put("pay_type", payType);
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
                            intent = new Intent(ServiceOrderConfirmActivity.this,
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
                            intent = new Intent(ServiceOrderConfirmActivity.this,
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
                            // 支付失败，跳转订单列表
                            intent = new Intent(ServiceOrderConfirmActivity.this,
                                    MyOrderListActivity.class);
                            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_BACK_MAIN,
                                    true);
                            intent.putExtra(RouterPath.IntentPath.Customer.MyOrder.ARG_SELECT_TAB,
                                    RouterPath.IntentPath.Customer.MyOrder.Tab.SERVICE_ORDER);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                            break;
                        case HAD_AUTHORIZED:
                        case LIMIT_CONTINUE_PAY:
                            // 授信成功
                            intent = new Intent(ServiceOrderConfirmActivity.this,
                                    InstallmentPaymentActivity.class);
                            intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PARAMS,
                                    jsonObject.toString());
                            intent.putExtra(InstallmentPaymentActivity.ARG_PAY_PATH,
                                    Constants.getAbsUrl(Constants.HttpPath.SERVICE_ORDER_PAYMENT));
                            intent.putExtra(InstallmentPaymentActivity.ARG_PRICE, needPayMoney);
                            intent.putExtra(InstallmentPaymentActivity.ARG_INSTALLMENT_STAGE_NUM,
                                    checkedInstallmentStageNum);
                            startActivity(intent);
                            break;
                        case PAY_BY_OTHERS:
                            // 直接跳转付款模块
                            goPayOrder(jsonObject, serviceOrder);
                            break;
                    }
                }
            };
        }

        if (flowLayout.getCheckedIndex() >= 0) {
            checkedInstallmentStageNum = work.getInstallment()
                    .getDetails()
                    .get(flowLayout.getCheckedIndex())
                    .getStageNum();
        } else {
            checkedInstallmentStageNum = -1;
        }

        if (checkedInstallmentStageNum > 0) {
            // 直接验证分期
            XiaoxiInstallmentAuthorization.getInstance()
                    .onAuthorization(this, true);
            RxBus.getDefault()
                    .toObservable(PayRxEvent.class)
                    .subscribe(paySubscriber);
        } else {
            // 直接跳转付款模块
            goPayOrder(jsonObject, serviceOrder);
        }
    }


    /**
     * 跳转付款
     */
    private void goPayOrder(JSONObject jsonObject, ServiceOrder serviceOrder) {
        PayConfig.Builder builder = new PayConfig.Builder(this);
        DataConfig dataConfig = Session.getInstance()
                .getDataConfig(this);
        List<String> payAgents = DataConfig.getServicePayAgents();
        if (!work.isInstallment() || payType == PayConfig.PAY_TYPE_DEPOSIT || payType ==
                PayConfig.PAY_TYPE_INTENT) {
            // 移除分期支付
            payAgents.remove(PayAgent.XIAO_XI_PAY);
        }
        builder.payAgents(dataConfig != null ? dataConfig.getPayTypes() : null, payAgents);
        builder.params(jsonObject)
                .path(Constants.getAbsUrl(Constants.HttpPath.SERVICE_ORDER_PAYMENT))
                .price(needPayMoney)
                .subscriber(paySubscriber)
                .build()
                .pay();
    }

    private void setCustomerInfoView() {
        customerInfo = ServeCustomerInfoUtil.readServeCustomerInfo(this);

        if (!TextUtils.isEmpty(customerInfo.getCustomerName())) {
            tvServeCustomer.setText(customerInfo.getCustomerName());
        }
        if (!TextUtils.isEmpty(customerInfo.getCustomerPhone())) {
            tvPhone.setText(customerInfo.getCustomerPhone());
        }
        if (isNeedWeddingTime) {
            serveTimeLayout.setVisibility(View.VISIBLE);
            if (customerInfo.getServeTime() != null) {
                tvServeTime.setText(customerInfo.getServeTime()
                        .toString("yyyy-MM-dd"));
            }
        } else {
            serveTimeLayout.setVisibility(View.GONE);
        }
        setPrices();
    }

    private void setWorkInfoView() {
        Glide.with(this)
                .load(ImageUtil.getImagePath(work.getCoverPath(), coverWidth))
                .into(imgCover);
        tvTitle.setText(work.getTitle());
        tvMerchantName.setText(work.getMerchantName());
        imgIntentMoney.setVisibility(isIntentPayNow ? View.VISIBLE : View.GONE);

        // 活动与否
        Date nowDate = new Date();
        if (work.getRule() != null && work.getRule()
                .getId() > 0 && (work.getRule()
                .getEnd_time() == null || work.getRule()
                .getEnd_time()
                .after(nowDate))) {
            // 有活动信息，并且尚未结束
            if (work.getRule()
                    .getStart_time() == null || work.getRule()
                    .getStart_time()
                    .before(nowDate)) {
                // 活动本身没有设置开始时间，或者有设置并且已经开始
                // 只有这种情况下，活动才是正确的，需要显示活动信息
                workPrice = work.getSale_price();
                earnestMoney = work.getSaleEarnestMoney();
                salesLayout.setVisibility(View.VISIBLE);
                tvSalesText.setText(work.getRule()
                        .getName());
            } else {
                // 活动有设置时间，并且尚未开始
                workPrice = work.getPrice();
                earnestMoney = work.getEarnestMoney();
                salesLayout.setVisibility(View.GONE);
            }
        } else {
            // 没有活动信息，或者有但是活动已经过期
            workPrice = work.getPrice();
            earnestMoney = work.getEarnestMoney();
            salesLayout.setVisibility(View.GONE);
        }

        tvWorkPrice.setText("￥" + Util.formatDouble2String(workPrice));
    }

    private void setPrices() {
        // 价格明细
        tvWorkPrice2.setText("￥" + Util.formatDouble2String(workPrice));

        // 优惠券红包
        if (selectedCoupon != null && selectedCoupon.getId() > 0) {
            couponMoneyLayout.setVisibility(View.VISIBLE);
            couponMoney = selectedCoupon.getValue();
            tvCouponMoney.setText("-￥" + Util.formatDouble2String(selectedCoupon.getValue()));
        } else {
            couponMoney = 0;
            couponMoneyLayout.setVisibility(View.GONE);
        }
        if (selectedRedPacket != null && selectedRedPacket.getId() > 0) {
            redPacketMoneyLayout.setVisibility(View.VISIBLE);
            redPacketMoney = selectedRedPacket.getAmount();
            tvRedPacketMoney.setText("-￥" + Util.formatDouble2String(selectedRedPacket.getAmount
                    ()));
        } else {
            redPacketMoney = 0;
            redPacketMoneyLayout.setVisibility(View.GONE);
        }
        // 底部优惠扣减提示
        if (redPacketMoney > 0 && couponMoney > 0) {
            tvSavedMoneyHint.setVisibility(View.VISIBLE);
            tvSavedMoneyHint.setText("优惠券和红包优惠将在支付余款时扣减哦~");
        } else if (redPacketMoney > 0) {
            tvSavedMoneyHint.setVisibility(View.VISIBLE);
            tvSavedMoneyHint.setText("红包优惠将在支付余款时扣减哦~");
        } else if (couponMoney > 0) {
            tvSavedMoneyHint.setVisibility(View.VISIBLE);
            tvSavedMoneyHint.setText("优惠券将在支付余款时扣减哦~");
        } else {
            tvSavedMoneyHint.setVisibility(View.GONE);
        }

        // 如果是意向金支付，不需要显示定金/全款选择视图和价格计算
        if (isIntentPayNow) {
            checkGroupPayType.setVisibility(View.GONE);
            // 显示本次意向金支付
            payIntentMoneyLayout.setVisibility(View.VISIBLE);
            tvPayIntentMoney.setText("-￥" + Util.formatDouble2String(work.getIntentPrice()));

            payAllSavedMoneyLayout.setVisibility(View.GONE);
            tvTotalMoneyLabel.setText("后续需支付：");
            double nextTimePayMoney = workPrice - work.getIntentPrice() - couponMoney -
                    redPacketMoney;
            needPayMoney = work.getIntentPrice();
            tvTotalMoney.setText("￥" + Util.formatDouble2StringPositive(nextTimePayMoney));
            if (getPayAllSavedMoney() > 0) {
                tvPayAllSavedHint.setVisibility(View.VISIBLE);
                tvPayAllSavedHint.setText(getString(R.string.label_pay_all_saved_hint,
                        CommonUtil.formatDouble2String(getPayAllSavedMoney())));
            } else if (!TextUtils.isEmpty(work.getPayAllGift())) {
                tvPayAllSavedHint.setVisibility(View.VISIBLE);
                tvPayAllSavedHint.setText(getString(R.string.label_pay_all_saved_hint2,
                        work.getPayAllGift()));
            } else {
                tvPayAllSavedHint.setVisibility(View.GONE);
            }
        } else {
            checkGroupPayType.setVisibility(View.VISIBLE);
            payIntentMoneyLayout.setVisibility(View.GONE);

            // 付款方式对应价格信息
            payAllSavedMoney = getPayAllSavedMoney();
            if (payAllSavedMoney > 0) {
                tvPayAllSaved.setVisibility(View.VISIBLE);
                tvPayAllSaved.setText(getString(R.string.label_discount_amount2,
                        Util.formatDouble2String(payAllSavedMoney)));
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
                        String.valueOf(earnestMoney)));
            } else {
                disableDepositLayout.setVisibility(View.VISIBLE);
                checkBtnPayDeposit.setVisibility(View.GONE);
                checkBtnPayAll.setChecked(true);
                tvDisableReason.setText(disableDepositReason);
                disableDepositLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showToast(ServiceOrderConfirmActivity.this,
                                null,
                                disableDepositType == 2 ? R.string.msg_disable_deposit_1 : R
                                        .string.msg_disable_deposit_2);
                    }
                });

            }
            if (checkGroupPayType.getCheckedRadioButtonId() == R.id.check_btn_pay_all) {
                // 选择了支付全款
                payDepositMoneyLayout.setVisibility(View.GONE);
                if (payAllSavedMoney > 0) {
                    payAllSavedMoneyLayout.setVisibility(View.VISIBLE);
                    tvPayAllSavedMoney.setText("￥" + Util.formatDouble2String(payAllSavedMoney));
                } else {
                    payAllSavedMoneyLayout.setVisibility(View.GONE);
                }

                tvTotalMoneyLabel.setText("全款金额：");
                needPayMoney = workPrice - payAllSavedMoney - couponMoney - redPacketMoney;
                tvTotalMoney.setText("￥" + Util.formatDouble2StringPositive(needPayMoney));
            } else {
                // 选择了支付定金
                payAllSavedMoneyLayout.setVisibility(View.GONE);
                payDepositMoneyLayout.setVisibility(View.VISIBLE);
                tvPayDepositMoney.setText("-￥" + Util.formatDouble2String(earnestMoney));

                tvTotalMoneyLabel.setText("后续需支付：");
                double nextTimePayMoney = workPrice - earnestMoney - couponMoney - redPacketMoney;
                needPayMoney = earnestMoney;
                tvTotalMoney.setText("￥" + Util.formatDouble2StringPositive(nextTimePayMoney));
            }
        }

        tvNeedPayMoney.setText(Util.formatDouble2StringPositive(needPayMoney));
        loadInstallmentDetails();
    }

    /**
     * 分期信息
     */
    private void loadInstallmentDetails() {
        if (isIntentPayNow || checkGroupPayType.getCheckedRadioButtonId() == R.id
                .check_btn_pay_deposit || !work.isInstallment()) {
            // 意向金支付或定金支付，或者不支持分期
            xiaoxiInstallmentLayout.setVisibility(View.GONE);
            return;
        }

        installmentSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(com.hunliji.hljcommonlibrary.utils.DialogUtil
                        .createProgressDialog(
                        this))
                .setOnNextListener(new SubscriberOnNextListener<InstallmentData>() {
                    @Override
                    public void onNext(InstallmentData installmentData) {
                        Installment2 installment = installmentData.getInstallment();
                        xiaoxiInstallmentLayout.setVisibility(View.VISIBLE);

                        ArrayList<InstallmentDetail> details = installment.getDetails();
                        flowLayout.removeAllViews();
                        for (InstallmentDetail detail : details) {
                            final CheckBox cbDetail = (CheckBox) LayoutInflater.from(
                                    ServiceOrderConfirmActivity.this)
                                    .inflate(R.layout.installment_check_box, null, false);
                            cbDetail.setText(detail.getShowText());
                            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                                    installmentWidth,
                                    CommonUtil.dp2px(ServiceOrderConfirmActivity.this, 44));
                            flowLayout.addView2(cbDetail, params);
                            if (detail.getStageNum() == checkedInstallmentStageNum) {
                                cbDetail.setChecked(true);
                            }
                        }
                    }
                })
                .build();
        XiaoxiInstallmentApi.getInstallmentInfo(needPayMoney)
                .subscribe(installmentSub);
    }

    /**
     * 判断是否允许定金支付
     *
     * @return
     */
    private boolean isAllowDeposit() {
        if (work.isAllowEarnest()) {
            // 如果有wedding time的话，则需要判断是否在这之前的三十天之前，否则也不支持定金付款
            if (!isNeedWeddingTime || customerInfo.getServeTime() == null) {
                return true;
            } else {
                DateTime dateTime = new DateTime();
                dateTime = dateTime.plusDays(30);
                if (dateTime.isBefore(customerInfo.getServeTime())) {
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

    /**
     * 根据是否是活动计算全款优惠金额
     *
     * @return
     */
    private int getPayAllSavedMoney() {
        Date date = new Date();
        Rule rule = work.getRule();
        if (rule != null && rule.getId() > 0 && ((rule.getEnd_time() == null || rule.getEnd_time()
                .after(date)) && rule.getStart_time() == null || rule.getStart_time()
                .before(date))) {
            return Math.round(work.getSalePayAllPercent() * work.getSale_price());
        } else {
            return Math.round(work.getPayAllPercent() * work.getPrice());
        }
    }

    @OnClick(R.id.info_layout)
    void onEditInfo() {
        Intent intent = new Intent(this, OrderInfoEditActivity.class);
        intent.putExtra("info", customerInfo);
        intent.putExtra("is_need_wedding_time", isNeedWeddingTime);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onCheckedChanged(CheckableLinearLayoutGroup group, int checkedId) {
        setPrices();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(couponSub, redPacketSub, installmentSub, paySubscriber);
    }
}
