package com.hunliji.hljinsurancelibrary.views.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.HljWeb;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.CheckableLinearLayout;
import com.hunliji.hljcommonlibrary.views.widgets.NoUnderlineSpan;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnErrorListener;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.api.InsuranceApi;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;
import com.hunliji.hljinsurancelibrary.models.PostHlbPolicy;
import com.hunliji.hljinsurancelibrary.views.fragments.CheckPolicyFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by mo_yu on 2017/5/24.填写婚礼保保单
 */

public class CreateHlbPolicyActivity extends HljBaseActivity {

    @BindView(R2.id.tv_insurance_product_title)
    TextView tvInsuranceProductTitle;
    @BindView(R2.id.insurance_info_layout)
    LinearLayout insuranceInfoLayout;
    @BindView(R2.id.tv_insurance_age)
    TextView tvInsuranceAge;
    @BindView(R2.id.insurance_age_layout)
    LinearLayout insuranceAgeLayout;
    @BindView(R2.id.tv_insurance_num)
    TextView tvInsuranceNum;
    @BindView(R2.id.insurance_num_layout)
    LinearLayout insuranceNumLayout;
    @BindView(R2.id.tv_insurance_date)
    TextView tvInsuranceDate;
    @BindView(R2.id.insurance_date_layout)
    LinearLayout insuranceDateLayout;
    @BindView(R2.id.et_user_name)
    EditText etUserName;
    @BindView(R2.id.user_name_layout)
    LinearLayout userNameLayout;
    @BindView(R2.id.et_user_code)
    EditText etUserCode;
    @BindView(R2.id.user_code_layout)
    LinearLayout userCodeLayout;
    @BindView(R2.id.et_user_phone)
    EditText etUserPhone;
    @BindView(R2.id.user_phone_layout)
    LinearLayout userPhoneLayout;
    @BindView(R2.id.et_spouse_user_name)
    EditText etSpouseUserName;
    @BindView(R2.id.et_hotel_address)
    EditText etHotelAddress;
    @BindView(R2.id.et_hotel_name)
    EditText etHotelName;
    @BindView(R2.id.tv_benefit_user_name)
    TextView tvBenefitUserName;
    @BindView(R2.id.benefit_user_layout)
    LinearLayout benefitUserLayout;
    @BindView(R2.id.tv_submit_insurance_tip)
    TextView tvSubmitInsuranceTip;
    @BindView(R2.id.scroll_view)
    ScrollView scrollView;
    @BindView(R2.id.action_submit_insurance)
    TextView actionSubmitInsurance;
    @BindView(R2.id.tv_insurance_type_name)
    TextView tvInsuranceTypeName;
    @BindView(R2.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R2.id.user_mate_name_layout)
    LinearLayout userMateNameLayout;
    @BindView(R2.id.cl_insurance_tip)
    CheckableLinearLayout clInsuranceTip;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private Calendar tempCalendar;
    private Calendar insuranceCalendar;
    private SimpleDateFormat dateTitleFormat;
    private PostHlbPolicy postHlbPolicy;
    private PolicyDetail policyDetail;
    private HljHttpSubscriber subscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_policy);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        initValue();
        initView();
        initLoad();
    }

    private void initValue() {
        policyDetail = getIntent().getParcelableExtra("policy_detail");
        insuranceCalendar = Calendar.getInstance();
        dateTitleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        postHlbPolicy = new PostHlbPolicy();
    }

    private void initView() {
        setPolicyDetailView();
        clInsuranceTip.setChecked(true);
        clInsuranceTip.setOnCheckedChangeListener(new CheckableLinearLayout
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(View view, boolean checked) {
                if (checked) {
                    actionSubmitInsurance.setEnabled(true);
                } else {
                    actionSubmitInsurance.setEnabled(false);
                }
            }
        });

        rootLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                int height = getWindow().getDecorView()
                        .getHeight();
                boolean isKeyboard = (double) (bottom - top) / height < 0.8;
                if (isKeyboard) {
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollBy(0,
                                    CommonUtil.dp2px(CreateHlbPolicyActivity.this, 16));
                        }
                    });
                }
            }
        });
    }

    private void initLoad() {
        if (subscriber == null || subscriber.isUnsubscribed()) {
            Observable<PolicyDetail> observable = InsuranceApi.getPolicyDetail(policyDetail.getId());
            subscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<PolicyDetail>() {

                        @Override
                        public void onNext(PolicyDetail data) {
                            if (data != null) {
                                policyDetail = data;
                                setPolicyDetailView();
                            }
                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Object o) {

                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribe(subscriber);
        }
    }

    private void setPolicyDetailView() {
        if (policyDetail != null) {
            tvInsuranceTypeName.setText(policyDetail.getProduct()
                    .getTitle());

            String str = getString(R.string.label_submit_insurance_tip);
            int insuranceStart = str.indexOf("《保险条款》");
            SpannableString sp = new SpannableString(str);
            sp.setSpan(new NoUnderlineSpan() {
                @Override
                public void onClick(View widget) {
                    HljWeb.startWebView(CreateHlbPolicyActivity.this,
                            policyDetail.getProduct()
                                    .getClauseUrl());
                }
            }, insuranceStart, insuranceStart + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            int insuredStart = str.indexOf("《投保须知》");
            sp.setSpan(new NoUnderlineSpan() {
                @Override
                public void onClick(View widget) {
                    HljWeb.startWebView(CreateHlbPolicyActivity.this,
                            policyDetail.getProduct()
                                    .getNoticeUrl());
                }
            }, insuredStart, insuredStart + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvSubmitInsuranceTip.setText(sp);
            tvSubmitInsuranceTip.setLinkTextColor(ContextCompat.getColor(this, R.color.colorLink));
            tvSubmitInsuranceTip.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void checkPolicy() {
        hideKeyboard(null);
        if (policyDetail == null || TextUtils.isEmpty(policyDetail.getId())) {
            return;
        }
        String userName = etUserName.getText()
                .toString();
        String userPhone = etUserPhone.getText()
                .toString();
        String userIdCard = etUserCode.getText()
                .toString();
        String spouseName = etSpouseUserName.getText()
                .toString();
        String hotelName = etHotelName.getText()
                .toString();
        String hotelAddress = etHotelAddress.getText()
                .toString();
        if (tempCalendar == null) {
            ToastUtil.showToast(this, "请选择起保日期！", 0);
        } else if (TextUtils.isEmpty(userName)) {
            ToastUtil.showToast(this, "被保险人姓名不能为空！", 0);
        } else if (userName.length() < 2) {
            ToastUtil.showToast(this, "被保险人姓名不能少于两个字！", 0);
        } else if (TextUtils.isEmpty(userPhone)) {
            ToastUtil.showToast(this, "被保险人手机号不能为空！", 0);
        } else if (userPhone.length() != 11) {
            ToastUtil.showToast(this, "请填写正确的手机号码!", 0);
        } else if (TextUtils.isEmpty(userIdCard)) {
            ToastUtil.showToast(this, "被保险人身份证号不能为空！", 0);
        } else if (!CommonUtil.validIdStr(userIdCard)) {
            ToastUtil.showToast(this, "身份证号码格式错误", 0);
        } else if (!CommonUtil.validBirthdayStr(userIdCard)) {
            ToastUtil.showToast(this, "身份证生日无效!", 0);
        } else if (TextUtils.isEmpty(spouseName)) {
            ToastUtil.showToast(this, "配偶姓名不能为空！", 0);
        } else if (spouseName.length() < 2) {
            ToastUtil.showToast(this, "配偶姓名不能少于两个字！", 0);
        } else if (TextUtils.isEmpty(hotelName)) {
            ToastUtil.showToast(this, "婚宴酒店名称不能为空！", 0);
        } else if (TextUtils.isEmpty(hotelAddress)) {
            ToastUtil.showToast(this, "婚宴酒店地址不能为空！", 0);
        } else {
            postHlbPolicy.setId(policyDetail.getId());
            postHlbPolicy.setFullName(userName);
            postHlbPolicy.setPhone(userPhone);
            postHlbPolicy.setCertiNo(userIdCard);
            postHlbPolicy.setSpouseName(spouseName);
            postHlbPolicy.setWeddingHotel(hotelName);
            postHlbPolicy.setWeddingAddress(hotelAddress);
            postHlbPolicy.setTransBeginDate(dateTitleFormat.format(tempCalendar.getTime()));
            showSubmitInfo();
        }
    }

    //提交保单二次确认弹窗
    private void showSubmitInfo() {
        CheckPolicyFragment checkUserFragment = CheckPolicyFragment.newInstance(
                postHlbPolicy);
        checkUserFragment.show(getSupportFragmentManager(), "CheckPolicyFragment");
    }

    @OnClick(R2.id.action_submit_insurance)
    public void onActionSubmit() {
        checkPolicy();
    }

    @OnClick(R2.id.insurance_info_layout)
    public void onInsuranceInfo() {
        HljWeb.startWebView(this,
                policyDetail.getProduct()
                        .getDetailUrl());
    }

    /**
     * 日历选择控件,选择月份
     */
    @OnClick(R2.id.insurance_date_layout)
    public void onInsuranceDate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View picker = inflater.inflate(R.layout.hlj_datetime_picker, null);
        final DatePicker datePicker = (DatePicker) picker.findViewById(R.id.date_picker);

        // 显示当前月份
        datePicker.updateDate(insuranceCalendar.get(Calendar.YEAR),
                insuranceCalendar.get(Calendar.MONTH),
                insuranceCalendar.get(Calendar.DAY_OF_MONTH));
        long day = 24 * 60 * 60 * 1000;
        datePicker.setMinDate(insuranceCalendar.getTimeInMillis() + day);
        builder.setView(picker);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 选中日期并确定后,显示新的月份和刷新日历数据
                tempCalendar = Calendar.getInstance();
                tempCalendar.set(Calendar.YEAR, datePicker.getYear());
                tempCalendar.set(Calendar.MONTH, datePicker.getMonth());
                tempCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                tvInsuranceDate.setText(dateTitleFormat.format(tempCalendar.getTime()));
                dialogInterface.dismiss();
            }
        });

        builder.create()
                .show();
    }

    /**
     * 根据身份证号验证该用户是否满足18-75周岁要去
     *
     * @param idCard
     * @return
     */
    private boolean isAgeValid(String idCard) {
        Calendar cal1 = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        if (idCard.length() == 15) {
            cal1.set(Integer.parseInt("19" + idCard.substring(6, 8)),
                    Integer.parseInt(idCard.substring(8, 10)),
                    Integer.parseInt(idCard.substring(10, 12)));
        } else {
            cal1.set(Integer.parseInt(idCard.substring(6, 10)),
                    Integer.parseInt(idCard.substring(10, 12)),
                    Integer.parseInt(idCard.substring(12, 14)));
        }

        int m = (today.get(Calendar.MONTH)) - (cal1.get(Calendar.MONTH));
        int y = (today.get(Calendar.YEAR)) - (cal1.get(Calendar.YEAR));
        int age = (y * 12 + m) / 12;
        return age >= 18 && age <= 75;
    }
}
