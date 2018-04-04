package me.suncloud.marrymemo.view.orders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.FlowLayout;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljpaymentlibrary.api.xiaoxi_installment.XiaoxiInstallmentApi;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.Installment2;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentData;
import com.hunliji.hljpaymentlibrary.models.xiaoxi_installment.InstallmentDetail;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.model.orders.ServeCustomerInfo;
import me.suncloud.marrymemo.util.ServeCustomerInfoUtil;
import rx.Subscription;

/**
 * 婚宴酒店填写订单页面
 * Created by jinxin on 2018/2/28 0028.
 */

public class HotelPeriodFillOrderActivity extends HljBaseActivity {

    public static final String ARG_MERCHANT = "merchant";
    private final double DEFAULT_PRICE = 0D;
    private final int INFO_REQUEST = 100;

    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.img_logo)
    ImageView imgLogo;
    @BindView(R.id.tv_hotel_name)
    TextView tvHotelName;
    @BindView(R.id.tv_hotel_address)
    TextView tvHotelAddress;
    @BindView(R.id.edit_money)
    EditText editMoney;
    @BindView(R.id.flow_layout)
    FlowLayout flowLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_wedding_day)
    TextView tvWeddingDay;
    @BindView(R.id.tv_name_hint)
    TextView tvNameHint;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private Merchant merchant;
    private HljHttpSubscriber initSub;
    private Installment2 installment;
    private int selectedStageNum;
    private int periodLimit;
    private int noVisibleStageNum;
    private Subscription rxPaymentBusSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_period_fill_order);
        ButterKnife.bind(this);

        initValues();
        initWidget();
        initLoad();
        registerPaymentRxBus();
    }

    private void initValues() {
        if (getIntent() != null) {
            merchant = getIntent().getParcelableExtra(ARG_MERCHANT);
        }
    }

    private void initWidget() {
        SpannableString nameSpan = new SpannableString("姓姓名名：");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color
                .transparent));
        nameSpan.setSpan(colorSpan, 1, 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvNameHint.setText(nameSpan);

        ServeCustomerInfo serveCustomerInfo = ServeCustomerInfoUtil.readServeCustomerInfo(this);
        setCustomerInfo(serveCustomerInfo);
        if (merchant != null) {
            int logoSize = CommonUtil.dp2px(this, 62);
            Glide.with(this)
                    .load(ImagePath.buildPath(merchant.getLogoPath())
                            .width(logoSize)
                            .height(logoSize)
                            .cropPath())
                    .apply(new RequestOptions().placeholder(R.mipmap.icon_empty_image))
                    .into(imgLogo);
            tvHotelName.setText(merchant.getName());
            tvHotelAddress.setText(merchant.getAddress());
        }
    }

    private void setCustomerInfo(ServeCustomerInfo info) {
        if (info != null) {
            tvName.setText(info.getCustomerName());
            tvPhone.setText(info.getCustomerPhone());
        }
    }

    private void initLoad() {
        initSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<InstallmentData>() {
                    @Override
                    public void onNext(InstallmentData installmentData) {
                        installment = installmentData.getInstallment();
                        periodLimit = installmentData.getPeriodLimit();
                        List<InstallmentDetail> details = installment.getDetails();
                        if (!CommonUtil.isCollectionEmpty(details)) {
                            selectedStageNum = details.get(0)
                                    .getStageNum();
                        }
                        setViews();
                    }
                })
                .build();
        XiaoxiInstallmentApi.getInstallmentInfo(DEFAULT_PRICE)
                .subscribe(initSub);
    }

    private void registerPaymentRxBus() {
        rxPaymentBusSub = RxBus.getDefault()
                .toObservable(PayRxEvent.class)
                .subscribe(new RxBusSubscriber<PayRxEvent>() {
                    @Override
                    protected void onEvent(PayRxEvent payRxEvent) {
                        switch (payRxEvent.getType()) {
                            case PAY_CANCEL:
                                finish();
                                break;
                        }
                    }
                });
    }

    @OnClick({R.id.layout_information, R.id.tv_wedding_day, R.id.tv_phone, R.id.tv_name})
    void onInformation() {
        Intent intent = new Intent(this, HotelOrderInfoEditActivity.class);
        ServeCustomerInfo serveCustomerInfo = ServeCustomerInfoUtil.readServeCustomerInfo(this);
        intent.putExtra(HotelOrderInfoEditActivity.ARG_INFO, serveCustomerInfo);
        startActivityForResult(intent, INFO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INFO_REQUEST && resultCode == Activity.RESULT_OK) {
            ServeCustomerInfo serveCustomerInfo = data.getParcelableExtra("info");
            setCustomerInfo(serveCustomerInfo);
            if (serveCustomerInfo != null) {
                tvWeddingDay.setText(serveCustomerInfo.getServeTime() == null ? null :
                        serveCustomerInfo.getServeTime()
                        .toString(HljCommon.DateFormat.DATE_FORMAT_SHORT));
                setTimeGapView(serveCustomerInfo.getServeTime());
            }
        }
    }

    private void setTimeGapView(DateTime time) {
        int day = getWeddingDayGap(time);
        setPeriod(day);
    }

    private void setViews() {
        setViews(-1);
    }

    private void setViews(final int lastStageNum) {
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
            }
        });
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (merchant == null) {
            Toast.makeText(this, "没有婚宴酒店商家信息", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String name = tvName.getText()
                .toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入联系人", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String phone = tvPhone.getText()
                .toString();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        String payMoney = editMoney.getText()
                .toString();
        if (TextUtils.isEmpty(payMoney)) {
            Toast.makeText(this, "请输入支付金额", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String weddingDay = tvWeddingDay.getText()
                .toString();
        if (TextUtils.isEmpty(weddingDay)) {
            Toast.makeText(this, "请选择婚礼日期", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Intent intent = new Intent(this, HotelPeriodOrderPhotoActivity.class);
        intent.putExtra(HotelPeriodOrderPhotoActivity.ARG_NAME, name);
        intent.putExtra(HotelPeriodOrderPhotoActivity.ARG_PHONE, phone);
        intent.putExtra(HotelPeriodOrderPhotoActivity.ARG_MONEY, payMoney);
        intent.putExtra(HotelPeriodOrderPhotoActivity.ARG_MERCHANT_ID, merchant.getId());
        intent.putExtra(HotelPeriodOrderPhotoActivity.ARG_STAGE_NUM, selectedStageNum);
        intent.putExtra(HotelPeriodOrderPhotoActivity.ARG_WEDDING_DAY, weddingDay);
        startActivity(intent);
    }


    private int getWeddingDayGap(DateTime time) {
        if (time == null) {
            return 0;
        }
        Calendar now = Calendar.getInstance();
        long gapMills = time.getMillis() - now.getTimeInMillis();
        int day = (int) Math.ceil(gapMills / (24 * 60 * 60 * 1000));
        return day;
    }

    private void setPeriod(int day) {
        if (day < 0) {
            return;
        }
        List<InstallmentDetail> details = installment.getDetails();
        if (CommonUtil.isCollectionEmpty(details)) {
            return;
        }

        if (day < periodLimit * 30) {
            noVisibleStageNum = details.get(details.size() - 1)
                    .getStageNum();
        } else {
            noVisibleStageNum = 0;
        }
        if (selectedStageNum == noVisibleStageNum) {
            selectedStageNum = details.get(0)
                    .getStageNum();
        }
        setViews(noVisibleStageNum);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSub, rxPaymentBusSub);
    }
}
