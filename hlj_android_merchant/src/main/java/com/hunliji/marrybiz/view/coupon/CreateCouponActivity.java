package com.hunliji.marrybiz.view.coupon;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.models.coupon.CouponInfo;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.coupon.CouponApi;
import com.hunliji.marrybiz.util.JSONUtil;
import com.umeng.analytics.MobclickAgent;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kankan.wheel.widget.DTPicker;
import kankan.wheel.widget.DatePickerView;

/**
 * 创建跟编辑优惠券界面
 * Created by chen_bin on 2016/10/13 0013.
 */
public class CreateCouponActivity extends HljBaseActivity {
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_value)
    EditText etValue;
    @BindView(R.id.condition_layout)
    LinearLayout conditionLayout;
    @BindView(R.id.tv_condition)
    TextView tvCondition;
    @BindView(R.id.et_money_sill)
    EditText etMoneySill;
    @BindView(R.id.tv_provide_start)
    TextView tvProvideStart;
    @BindView(R.id.tv_provide_end)
    TextView tvProvideEnd;
    @BindView(R.id.valid_start_layout)
    LinearLayout validStartLayout;
    @BindView(R.id.tv_valid_start)
    TextView tvValidStart;
    @BindView(R.id.valid_end_layout)
    LinearLayout validEndLayout;
    @BindView(R.id.tv_valid_end)
    TextView tvValidEnd;
    @BindView(R.id.et_total_count)
    EditText etTotalCount;
    private TextView tvSelectTime;
    private DatePickerView pickerView;
    private Dialog timeDialog;
    private Dialog conditionDialog;
    private Dialog exitDialog;
    private Calendar tempCalendar;
    private List<String> conditions;
    private CouponInfo couponInfo;
    private DateTimeFormatter formatter;
    private String oldTitle = "";
    private String oldValue = "";
    private String oldMoneySill = "";
    private String oldCondition = "";
    private String oldProvideStart = "";
    private String oldProvideEnd = "";
    private String oldValidStart = "";
    private String oldValidEnd = "";
    private String oldTotalCount = "";
    private boolean isFromEdu; //是否从教育页进来
    private InputMethodManager imm;
    private HljHttpSubscriber postSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_coupon);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        tempCalendar = Calendar.getInstance();
        formatter = DateTimeFormat.forPattern(getString(R.string.format_date_type4,
                Locale.getDefault()));
        conditions = new ArrayList<>();
        conditions.add(getString(R.string.label_full_coupon));
        conditions.add(getString(R.string.label_cash_coupon));
        couponInfo = getIntent().getParcelableExtra("couponInfo");
        isFromEdu = getIntent().getBooleanExtra("is_from_edu", false);
        if (couponInfo == null) {
            couponInfo = new CouponInfo();
        }
        if (couponInfo.getId() > 0) {
            setTitle(R.string.title_activity_edit_coupon);
            showCouponDetail();
        } else {
            setTitle(R.string.title_activity_create_coupon);
        }
    }

    //显示优惠信息
    private void showCouponDetail() {
        //名称
        etTitle.setText(couponInfo.getTitle());
        etTitle.setSelection(etTitle.length());
        oldTitle = etTitle.getText()
                .toString();
        //金额
        etValue.setEnabled(false);
        etValue.setHint("");
        etValue.setText(CommonUtil.formatDouble2String(couponInfo.getValue()));
        oldValue = etValue.getText()
                .toString();
        etValue.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        etValue.setText(CommonUtil.formatDouble2String(couponInfo.getValue()));
        //使用条件
        conditionLayout.setClickable(false);
        tvCondition.setHint("");
        tvCondition.setCompoundDrawables(null, null, null, null);
        tvCondition.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        tvCondition.setText(couponInfo.getType() == 1 ? conditions.get(0) : conditions.get(1));
        oldCondition = tvCondition.getText()
                .toString();
        //门槛金额
        etMoneySill.setEnabled(false);
        etMoneySill.setHint("");
        etMoneySill.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        etMoneySill.setText(CommonUtil.formatDouble2String(couponInfo.getMoneySill()));
        oldMoneySill = etMoneySill.getText()
                .toString();
        //开始领取时间
        tvProvideStart.setText(couponInfo.getProvideStart() == null ? "" : couponInfo
                .getProvideStart()
                .toString(getString(R.string.format_date_type4), Locale.getDefault()));
        oldProvideStart = tvProvideStart.getText()
                .toString();
        //停止领取时间
        tvProvideEnd.setText(couponInfo.getProvideEnd() == null ? "" : couponInfo.getProvideEnd()
                .toString(getString(R.string.format_date_type4), Locale.getDefault()));
        oldProvideEnd = tvProvideEnd.getText()
                .toString();
        //开始生效时间
        validStartLayout.setClickable(false);
        tvValidStart.setHint("");
        tvValidStart.setCompoundDrawables(null, null, null, null);
        tvValidStart.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        tvValidStart.setText(couponInfo.getValidStart() == null ? "" : couponInfo.getValidStart()
                .toString(getString(R.string.format_date_type4), Locale.getDefault()));
        oldValidStart = tvValidStart.getText()
                .toString();
        //结束生效时间
        validEndLayout.setClickable(false);
        tvValidEnd.setHint("");
        tvValidEnd.setTextColor(ContextCompat.getColor(this, R.color.colorGray));
        tvValidEnd.setCompoundDrawables(null, null, null, null);
        tvValidEnd.setText(couponInfo.getValidEnd() == null ? "" : couponInfo.getValidEnd()
                .toString(getString(R.string.format_date_type4), Locale.getDefault()));
        oldValidEnd = tvValidEnd.getText()
                .toString();
        //发行量
        etTotalCount.setText(String.valueOf(couponInfo.getTotalCount()));
        oldTotalCount = etTotalCount.getText()
                .toString();
    }

    //显示日期的Dialog
    @OnClick({R.id.provide_start_layout, R.id.provide_end_layout, R.id.valid_start_layout, R.id
            .valid_end_layout})
    public void onShowTimeDialog(View view) {
        if (timeDialog != null && timeDialog.isShowing()) {
            return;
        }
        switch (view.getId()) {
            case R.id.provide_start_layout:
                tvSelectTime = tvProvideStart;
                break;
            case R.id.provide_end_layout:
                tvSelectTime = tvProvideEnd;
                break;
            case R.id.valid_start_layout:
                tvSelectTime = tvValidStart;
                break;
            case R.id.valid_end_layout:
                tvSelectTime = tvValidEnd;
                break;
        }
        try {
            DateTime dateTime = DateTime.parse(tvSelectTime.getText()
                    .toString(), formatter);
            tempCalendar.setTimeInMillis(dateTime.getMillis());
        } catch (Exception e) {
            tempCalendar = Calendar.getInstance();
        }
        if (timeDialog == null) {
            timeDialog = new Dialog(this, R.style.BubbleDialogTheme);
            timeDialog.setContentView(R.layout.dialog_date_picker);
            timeDialog.findViewById(R.id.close)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            timeDialog.dismiss();
                        }
                    });
            pickerView = (DatePickerView) timeDialog.findViewById(R.id.picker);
            pickerView.setYearLimit(2016, 49);
            pickerView.setOnPickerDateListener(new DTPicker.OnPickerDateListener() {
                @Override
                public void onPickerDate(int year, int month, int day) {
                    if (tempCalendar == null) {
                        tempCalendar = new GregorianCalendar(year, month - 1, day);
                    } else {
                        tempCalendar.set(year, month - 1, day);
                    }
                }
            });
            pickerView.getLayoutParams().height = Math.round(getResources().getDisplayMetrics()
                    .density * (24 * 8));
            Window win = timeDialog.getWindow();
            if (win != null) {
                WindowManager.LayoutParams params = win.getAttributes();
                params.width = JSONUtil.getDeviceSize(this).x;
                win.setGravity(Gravity.BOTTOM);
                win.setWindowAnimations(R.style.dialog_anim_rise_style);
            }
        }
        pickerView.setCurrentCalender(tempCalendar);
        timeDialog.findViewById(R.id.confirm)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        timeDialog.dismiss();
                        DateTime dateTime = new DateTime(tempCalendar.getTime());
                        String time = dateTime.toString(getString(R.string.format_date_type4),
                                Locale.getDefault());
                        tvSelectTime.setText(time);
                        if (tvSelectTime == tvProvideStart) {
                            couponInfo.setProvideStart(dateTime);
                        } else if (tvSelectTime == tvProvideEnd) {
                            couponInfo.setProvideEnd(dateTime);
                        } else if (tvSelectTime == tvValidStart) {
                            couponInfo.setValidStart(dateTime);
                        } else if (tvSelectTime == tvValidEnd) {
                            couponInfo.setValidEnd(dateTime);
                        }
                    }
                });
        timeDialog.show();
    }

    //显示使用条件的Dialog
    @OnClick(R.id.condition_layout)
    public void onShowConditionDialog() {
        if (conditionDialog != null && conditionDialog.isShowing()) {
            return;
        }
        if (conditionDialog == null) {
            conditionDialog = DialogUtil.createSingleWheelPickerDialog(this,
                    conditions,
                    0,
                    new DialogUtil.OnWheelSelectedListener() {
                        @Override
                        public void onWheelSelected(int position, String str) {
                            tvCondition.setText(str);
                            if (position == 0) {
                                etMoneySill.setEnabled(true);
                            } else {
                                etMoneySill.setText("0");
                                etMoneySill.setEnabled(false);
                                etMoneySill.setTextColor(ContextCompat.getColor(CreateCouponActivity
                                        .this, R.color.colorGray));
                            }
                            couponInfo.setType(position + 1);
                        }
                    });
        }
        conditionDialog.show();
    }

    //点击提交
    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        if (etTitle.length() == 0 || etValue.length() == 0 || tvCondition.length() == 0 ||
                (couponInfo.getType() == 1 && etMoneySill.length() == 0) || tvProvideStart.length
                () == 0 || tvProvideEnd.length() == 0 || tvValidStart.length() == 0 || tvValidEnd
                .length() == 0 || etTotalCount.length() == 0) {
            ToastUtil.showToast(this, null, R.string.hint_please_enter_all_information);
            return;
        }
        if (couponInfo.getType() == 1 && Double.valueOf(etMoneySill.getText()
                .toString()) <= Double.valueOf(etValue.getText()
                .toString())) {
            ToastUtil.showToast(this, null, R.string.hint_money_sill_less_than_value);
            return;
        }
        final int count = Integer.valueOf(etTotalCount.getText()
                .toString());
        if (count == 0) {
            ToastUtil.showToast(this, null, R.string.hint_enter_total_count_min);
            return;
        }
        if (couponInfo.getId() > 0 && count < Integer.valueOf(oldTotalCount)) {
            ToastUtil.showToast(this, null, R.string.hint_edit_total_count_max);
            return;
        }
        if (count > 100000) {
            ToastUtil.showToast(this, null, R.string.hint_enter_total_count_max);
            return;
        }
        if (couponInfo.getProvideEnd()
                .isBefore(couponInfo.getProvideStart())) {
            ToastUtil.showToast(this, null, R.string.hint_provide_end_early_provide_start);
            return;
        }
        if (couponInfo.getValidEnd()
                .isBefore(couponInfo.getValidStart())) {
            ToastUtil.showToast(this, null, R.string.hint_valid_end_early_valid_end);
            return;
        }
        if (couponInfo.getValidEnd()
                .isBefore(couponInfo.getProvideEnd())) {
            ToastUtil.showToast(this, null, R.string.hint_valid_end_early_provide_end);
            return;
        }
        couponInfo.setTitle(etTitle.getText()
                .toString());
        couponInfo.setValue(Integer.valueOf(etValue.getText()
                .toString()));
        couponInfo.setMoneySill(Integer.valueOf(etMoneySill.getText()
                .toString()));
        couponInfo.setTotalCount(Integer.valueOf(etTotalCount.getText()
                .toString()));
        if (couponInfo.getId() == 0) {
            couponInfo.setHidden(true);
        }
        CommonUtil.unSubscribeSubs(postSub);
        postSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        if (couponInfo.getId() == 0) {
                            ToastUtil.showCustomToast(CreateCouponActivity.this,
                                    R.string.label_create_coupon_success);
                            setResult(RESULT_OK);
                            if (isFromEdu) {
                                RxBus.getDefault()
                                        .post(new RxEvent(RxEvent.RxEventType.CREATE_COUPON_SUCCESS,
                                                null));
                                startActivity(new Intent(CreateCouponActivity.this,
                                        MyCouponListActivity.class));
                                overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.activity_anim_default);
                                finish();
                            }
                        } else {
                            ToastUtil.showCustomToast(CreateCouponActivity.this,
                                    R.string.label_edit_coupon_success);
                            setResult(RESULT_OK, getIntent().putExtra("couponInfo", couponInfo));
                        }
                        CreateCouponActivity.super.onBackPressed();
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        CouponApi.createCouponObb(couponInfo)
                .subscribe(postSub);
    }

    @Override
    public void onBackPressed() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (TextUtils.equals(oldTitle,
                etTitle.getText()
                        .toString()) && TextUtils.equals(oldValue,
                etValue.getText()
                        .toString()) && TextUtils.equals(oldCondition,
                tvCondition.getText()
                        .toString()) && TextUtils.equals(oldMoneySill,
                etMoneySill.getText()
                        .toString()) && TextUtils.equals(oldProvideStart,
                tvProvideStart.getText()
                        .toString()) && TextUtils.equals(oldProvideEnd,
                tvProvideEnd.getText()
                        .toString()) && TextUtils.equals(oldValidStart,
                tvValidStart.getText()
                        .toString()) && TextUtils.equals(oldValidEnd,
                tvValidEnd.getText()
                        .toString()) && TextUtils.equals(oldTotalCount,
                etTotalCount.getText()
                        .toString())) {
            super.onBackPressed();
        } else {
            if (exitDialog != null && exitDialog.isShowing()) {
                return;
            }
            if (exitDialog == null) {
                exitDialog = DialogUtil.createDoubleButtonDialog(this,
                        getString(R.string.label_exit_edit2),
                        null,
                        null,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CreateCouponActivity.super.onBackPressed();
                            }
                        },
                        null);
            }
            exitDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(postSub);
    }
}