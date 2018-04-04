package com.hunliji.hljinsurancelibrary.views.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.models.Photo;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.api.InsuranceApi;
import com.hunliji.hljinsurancelibrary.models.InsuranceProduct;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;

/**
 * 蜜月保填保单
 * Created by jinxin on 2017/8/18 0018.
 */

public class CreateMYBActivity extends HljBaseActivity {

    public final int ACTION_OUT = 1;
    public final int ACTION_IN = 2;
    public final int ACTION_RULE = 3;

    @BindView(R2.id.img_banner)
    ImageView imgBanner;
    @BindView(R2.id.edit_bride_name)
    EditText editBrideName;
    @BindView(R2.id.edit_bride_identity)
    EditText editBrideIdentity;
    @BindView(R2.id.edit_bride_phone)
    EditText editBridePhone;
    @BindView(R2.id.edit_groom_name)
    EditText editGroomName;
    @BindView(R2.id.edit_groom_identity)
    EditText editGroomIdentity;
    @BindView(R2.id.edit_groom_phone)
    EditText editGroomPhone;
    @BindView(R2.id.tv_wedding_date)
    TextView tvWeddingDate;
    @BindView(R2.id.cb_clause)
    CheckBox cbClause;
    @BindView(R2.id.tv_clause)
    TextView tvClause;
    @BindView(R2.id.btn_submit)
    Button btnSubmit;
    @BindView(R2.id.img_myb_detail)
    ImageView imgMybDetail;
    @BindView(R2.id.tv_look_myb_detail)
    TextView tvLookMybDetail;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private String id;
    private Calendar tempCalendar;
    private Calendar calendar;
    private PolicyDetail policyDetail;
    private HljHttpSubscriber saveSubscriber;
    private HljHttpSubscriber loadSubscriber;
    private SimpleDateFormat format;
    private DateTime weddingDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mi_yue_bao);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
            long weddingDateMills = getIntent().getLongExtra("weddingDate", 0L);
            if (weddingDateMills > 0) {
                weddingDate = new DateTime(weddingDateMills);
            }
        }
        calendar = Calendar.getInstance();
        format = new SimpleDateFormat(HljTimeUtils.DATE_FORMAT_SHORT);
    }

    private void initWidget() {
        imgBanner.getLayoutParams().height = CommonUtil.getDeviceSize(this).x / 2;
        tvClause.setText(getClauseSpan());
        tvWeddingDate.setText(weddingDate == null ? null : weddingDate.toString(HljTimeUtils
                .DATE_FORMAT_SHORT));
    }

    private void initLoad() {
        loadSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<PolicyDetail>() {
                    @Override
                    public void onNext(PolicyDetail policyDetail) {
                        setPolicyDetail(policyDetail);
                    }
                })
                .build();
        InsuranceApi.getMybDetail(id)
                .subscribe(loadSubscriber);
    }

    private void setPolicyDetail(PolicyDetail policyDetail) {
        this.policyDetail = policyDetail;
        if (policyDetail == null) {
            return;
        }
        int width = CommonUtil.getDeviceSize(this).x;
        int height = width / 2;
        InsuranceProduct product = policyDetail.getProduct();
        if (product != null) {
            String aboutMybImageUrl = product.getAboutMybImageUrl();
            Glide.with(this)
                    .load(ImagePath.buildPath(aboutMybImageUrl)
                            .width(width)
                            .height(height)
                            .cropPath())
                    .into(imgBanner);

            Photo guaranteesImage = product.getGuaranteesImage();
            if (guaranteesImage != null) {
                int imgDetailWidth = width - CommonUtil.dp2px(this, 32);
                float rate = guaranteesImage.getHeight() * 1.0F / guaranteesImage.getWidth();
                int imgDetailHeight = (int) (rate * imgDetailWidth);
                imgMybDetail.getLayoutParams().width = imgDetailWidth;
                imgMybDetail.getLayoutParams().height = imgDetailHeight;
                Glide.with(this)
                        .load(ImagePath.buildPath(guaranteesImage.getImagePath())
                                .width(imgDetailWidth)
                                .height(imgDetailHeight)
                                .cropPath())
                        .into(imgMybDetail);
            }
        }
        editGroomName.setText(policyDetail.getFullName());
        editGroomIdentity.setText(policyDetail.getCertiNo());
        editGroomPhone.setText(policyDetail.getPhone());
        editBrideName.setText(policyDetail.getSpouseName());
        editBrideIdentity.setText(policyDetail.getSpouseCertiNo());
        editBridePhone.setText(policyDetail.getSpousePhone());
    }

    private SpannableStringBuilder getClauseSpan() {
        String clause = getString(R.string.label_create_miyuebao_clause);
        String oneClause = "《保险条款-海外蜜月》";
        String twoClause = "《保险条款-境内蜜月》";
        String three = "《投保规则》";
        ForegroundColorSpan span1 = new ForegroundColorSpan(getResources().getColor(R.color
                .colorLink));
        ForegroundColorSpan span2 = new ForegroundColorSpan(getResources().getColor(R.color
                .colorLink));
        ForegroundColorSpan span3 = new ForegroundColorSpan(getResources().getColor(R.color
                .colorLink));
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(clause);


        spanBuilder.setSpan(span1,
                clause.indexOf(oneClause),
                clause.indexOf(oneClause) + oneClause.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spanBuilder.setSpan(span2,
                clause.indexOf(twoClause),
                clause.indexOf(twoClause) + twoClause.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spanBuilder.setSpan(span3,
                clause.indexOf(three),
                clause.indexOf(three) + three.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ClauseClickSpan clickSpan1 = new ClauseClickSpan(ACTION_OUT);
        spanBuilder.setSpan(clickSpan1,
                clause.indexOf(oneClause),
                clause.indexOf(oneClause) + oneClause.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ClauseClickSpan clickSpan2 = new ClauseClickSpan(ACTION_IN);
        spanBuilder.setSpan(clickSpan2,
                clause.indexOf(twoClause),
                clause.indexOf(twoClause) + twoClause.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ClauseClickSpan clickSpan3 = new ClauseClickSpan(ACTION_RULE);
        spanBuilder.setSpan(clickSpan3,
                clause.indexOf(three),
                clause.indexOf(three) + three.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tvClause.setMovementMethod(LinkMovementMethod.getInstance());
        return spanBuilder;
    }

    @OnClick(R2.id.btn_submit)
    void onSubmit() {
        String groomName = editGroomName.getText()
                .toString();
        if (TextUtils.isEmpty(groomName)) {
            Toast.makeText(this, "请填写正确的新郎姓名", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String groomIdentify = editGroomIdentity.getText()
                .toString();
        if (TextUtils.isEmpty(groomIdentify)) {
            Toast.makeText(this, "请填写正确的新郎身份证号", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (!CommonUtil.validIdStr(groomIdentify)) {
            Toast.makeText(this, "新郎身份证号码格式错误", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (!CommonUtil.validBirthdayStr(groomIdentify)) {
            Toast.makeText(this, "新郎身份证生日无效", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String groomPhone = editGroomPhone.getText()
                .toString();
        if (TextUtils.isEmpty(groomPhone) || groomPhone.length() != 11) {
            Toast.makeText(this, "请填写正确的新郎手机号", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String brideName = editBrideName.getText()
                .toString();
        if (TextUtils.isEmpty(brideName)) {
            Toast.makeText(this, "请填写正确的新郎姓名", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String brideIdentify = editBrideIdentity.getText()
                .toString();
        if (TextUtils.isEmpty(brideIdentify)) {
            Toast.makeText(this, "请填写正确的新郎身份证号", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (!CommonUtil.validIdStr(brideIdentify)) {
            Toast.makeText(this, "新娘身份证号码格式错误", Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (!CommonUtil.validBirthdayStr(brideIdentify)) {
            Toast.makeText(this, "新娘身份证生日无效", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        String bridePhone = editBridePhone.getText()
                .toString();
        if (TextUtils.isEmpty(bridePhone) || bridePhone.length() != 11) {
            Toast.makeText(this, "请填写正确的新郎手机号", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (TextUtils.isEmpty(groomIdentify)) {
            Toast.makeText(this, "新郎身份证号不能为空！", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!CommonUtil.validIdStr(groomIdentify)) {
            Toast.makeText(this, "新郎身份证号码格式错误", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!CommonUtil.validBirthdayStr(groomIdentify)) {
            Toast.makeText(this, "新郎身份证生日无效!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(brideIdentify)) {
            Toast.makeText(this, "新娘身份证号不能为空！", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!CommonUtil.validIdStr(brideIdentify)) {
            Toast.makeText(this, "新娘身份证号码格式错误", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (!CommonUtil.validBirthdayStr(brideIdentify)) {
            Toast.makeText(this, "新娘身份证生日无效!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }


        if (tempCalendar == null || tempCalendar.before(calendar)) {
            Toast.makeText(this, "请填写正确的婚礼日期", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (saveSubscriber != null && !saveSubscriber.isUnsubscribed()) {
            return;
        }

        saveSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        Intent intent = null;
                        if (hljHttpResult.getStatus() != null) {
                            if (hljHttpResult.getStatus()
                                    .getRetCode() == 0) {
                                //填写成功
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType
                                                .POLICY_INFO_COMPLETED_SUCCESS,
                                                null));
                                Toast.makeText(CreateMYBActivity.this, "投保成功", Toast.LENGTH_SHORT)
                                        .show();
                                intent = new Intent(CreateMYBActivity.this,
                                        MYBDetailActivity.class);
                                intent.putExtra("id", id);
                                sendRefreshRxEvent();
                            } else if (hljHttpResult.getStatus()
                                    .getRetCode() >= 3000) {
                                //填写失败
                                Toast.makeText(CreateMYBActivity.this, "投保失败", Toast.LENGTH_SHORT)
                                        .show();
                                intent = new Intent(CreateMYBActivity.this,
                                        MYBFailureActivity.class);
                                intent.putExtra("id", id);
                                sendRefreshRxEvent();
                            } else {
                                Toast.makeText(CreateMYBActivity.this,
                                        hljHttpResult.getStatus()
                                                .getMsg(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                            if (intent != null) {
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                                finish();
                            }
                        }
                    }
                })
                .build();

        Map<String, Object> params = new HashMap<>();

        params.put("id", id);
        params.put("certi_no", groomIdentify);
        params.put("full_name", groomName);
        params.put("phone", groomPhone);
        params.put("spouse_certi_no", brideIdentify);
        params.put("spouse_name", brideName);
        params.put("spouse_phone", bridePhone);

        SimpleDateFormat format = new SimpleDateFormat(HljTimeUtils.DATE_FORMAT_SHORT);
        params.put("wedding_date", format.format(tempCalendar.getTime()));

        InsuranceApi.fillMyb(params)
                .subscribe(saveSubscriber);

    }

    private void sendRefreshRxEvent(){
        //领取成功 我的钱包页面刷新
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType
                        .POLICY_WRITE,
                        null));
        //我的保单列表刷新
        RxBus.getDefault()
                .post(new RxEvent(RxEvent.RxEventType
                        .POLICY_INFO_COMPLETED_SUCCESS,
                        null));
    }

    @OnClick(R2.id.tv_look_myb_detail)
    void onContentAndRule() {
        if (policyDetail == null || policyDetail.getProduct() == null || TextUtils.isEmpty(
                policyDetail.getProduct()
                        .getDetailUrl())) {
            return;
        }
        HljWeb.startWebView(this,
                policyDetail.getProduct()
                        .getDetailUrl());
    }

    @OnClick(R2.id.tv_wedding_date)
    void onWeddingDate() {
        showWeddingDialog();
    }

    private void showWeddingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View picker = inflater.inflate(R.layout.hlj_datetime_picker, null);
        final DatePicker datePicker = (DatePicker) picker.findViewById(R.id.date_picker);

        // 不显示日历视图
        datePicker.setCalendarViewShown(false);
        // 显示当前月份
        datePicker.updateDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.setMinDate(calendar.getTimeInMillis());
        builder.setView(picker);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 选中日期并确定后,显示新的月份和刷新日历数据
                tempCalendar = Calendar.getInstance();
                tempCalendar.set(Calendar.YEAR, datePicker.getYear());
                tempCalendar.set(Calendar.MONTH, datePicker.getMonth());
                tempCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                tvWeddingDate.setText(format.format(tempCalendar.getTime()));
                dialogInterface.dismiss();
            }
        });

        builder.create()
                .show();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(saveSubscriber, loadSubscriber);
    }

    class ClauseClickSpan extends ClickableSpan {

        private int action;

        public ClauseClickSpan(int action) {
            super();
            this.action = action;
        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            //去除下划线
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(getResources().getColor(R.color.colorLink));
        }

        @Override
        public void onClick(View widget) {
            if (policyDetail == null || policyDetail.getProduct() == null) {
                return;
            }
            String webPath = null;
            InsuranceProduct product = policyDetail.getProduct();
            switch (action) {
                case ACTION_OUT:
                    webPath = product.getClauseUrlAbroad();
                    break;
                case ACTION_IN:
                    webPath = product.getClauseUrl();
                    break;
                case ACTION_RULE:
                    webPath = product.getNoticeUrl();
                    break;
                default:
                    break;
            }
            if (!TextUtils.isEmpty(webPath)) {
                HljWeb.startWebView(CreateMYBActivity.this, webPath);
            }
        }
    }
}
