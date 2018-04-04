package me.suncloud.marrymemo.view.event;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hunliji.hljcommonlibrary.models.event.EventInfo;
import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljimagelibrary.utils.ImagePath;
import com.hunliji.hljpaymentlibrary.PayConfig;
import com.hunliji.hljpaymentlibrary.models.PayRxEvent;
import com.makeramen.rounded.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.event.EventApi;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.DataConfig;
import me.suncloud.marrymemo.util.LoginHelper;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import rx.Subscriber;

/**
 * 报名弹框，因为之前dialog中有输入法，为了体验将原来的dialog改成用activity式的dialog来实现
 * * Created by chen_bin on 2016/12/21 0021.
 */
public class SignUpActivity extends Activity {
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    @BindView(R.id.img_cover)
    RoundedImageView imgCover;
    @BindView(R.id.tv_show_time_title)
    TextView tvShowTimeTitle;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.phone_layout)
    LinearLayout phoneLayout;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.valid_layout)
    LinearLayout validLayout;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.btn_sms_certify)
    Button btnSmsCertify;
    @BindView(R.id.et_sms_code)
    EditText etSmsCode;
    @BindView(R.id.btn_voice_certify)
    Button btnVoiceCertify;
    @BindView(R.id.tv_voice_certify_hint)
    TextView tvVoiceCertifyHint;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.progressBar)
    View progressBar;
    private EditText currentEt;
    private DecimalFormat decimalFormat;
    private CertifyTimeDown timeDown;
    private EventInfo eventInfo;
    private User user;
    private HljHttpSubscriber signUpSub;
    private HljHttpSubscriber checkPhoneSub;
    private Subscriber<PayRxEvent> paySub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        eventInfo = getIntent().getParcelableExtra("eventInfo");
        decimalFormat = new DecimalFormat("00");
        user = Session.getInstance()
                .getCurrentUser(this);
        LoginHelper.getInstance()
                .setProgressBar(progressBar);
        currentEt = etPhone;
        showEventDetail();
    }

    private void showEventDetail() {
        int imageWidth = Math.round(CommonUtil.getDeviceSize(this).x * 27.0f / 32.0f);
        int imageHeight = Math.round(imageWidth / 2.0f);
        contentLayout.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().width = imageWidth;
        imgCover.getLayoutParams().height = imageHeight;
        Glide.with(this)
                .load(ImagePath.buildPath(eventInfo.getSurfaceImg())
                        .width(imageWidth)
                        .height(imageHeight)
                        .cropPath())
                .apply(new RequestOptions().dontAnimate()
                        .placeholder(R.mipmap.icon_empty_image)
                        .error(R.mipmap.icon_empty_image))
                .into(imgCover);
        tvShowTimeTitle.setText(TextUtils.isEmpty(eventInfo.getShowTimeTitle()) ? "-" : eventInfo
                .getShowTimeTitle());
        tvAddress.setText(TextUtils.isEmpty(eventInfo.getAddress()) ? "-" : eventInfo.getAddress());
        phoneLayout.setVisibility(View.VISIBLE);
        validLayout.setVisibility(View.GONE);
        tvPhone.setText(TextUtils.isEmpty(user.getPhone()) ? "-" : user.getPhone());
        btnSubmit.setText(eventInfo.getSignUpFee() > 0 ? R.string.label_sign_up_pay : R.string
                .label_confirm_sign_up);
        contentLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
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
                if (!isShowImm) {
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                            currentEt.requestFocus();
                            if (currentEt == etPhone) {
                                etSmsCode.clearFocus();
                            } else if (currentEt == etSmsCode) {
                                etPhone.clearFocus();
                            }
                        }
                    });
                }
            }
        });
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    //点击修改按钮
    @OnClick(R.id.tv_edit_phone)
    public void onEditPhone() {
        phoneLayout.setVisibility(View.GONE);
        validLayout.setVisibility(View.VISIBLE);
    }

    //获取验证码
    @OnClick(R.id.btn_sms_certify)
    public void onSmsCertify() {
        certify(LoginHelper.ACTIVITYAPPLYSMS);
    }

    //发送语音
    @OnClick(R.id.btn_voice_certify)
    public void onVoiceCertify() {
        certify(LoginHelper.ACTIVITYAPPLYVOICE);
    }

    private void certify(String flag) {
        if (etPhone.length() == 0) {
            ToastUtil.showToast(this, null, R.string.label_phone_hint);
            return;
        }
        String phone = etPhone.getText()
                .toString();
        if (!CommonUtil.isMobileNO(phone)) {
            ToastUtil.showToast(this, null, R.string.msg_wrong_phone_number);
            return;
        }
        timeDown = new CertifyTimeDown(LoginHelper.CERTIFY_TIME, LoginHelper.TIME_GAP);
        timeDown.setFlag(flag);
        LoginHelper.getInstance()
                .setTimeDown(timeDown);
        LoginHelper.getInstance()
                .getCertify(this, phone, flag, btnSmsCertify, btnVoiceCertify);
    }

    @OnTouch({R.id.et_phone, R.id.et_sms_code})
    public boolean onTouch(View view, MotionEvent event) {
        if (view instanceof EditText) {
            currentEt = (EditText) view;
        }
        return false;
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        boolean isEdit = validLayout.getVisibility() == View.VISIBLE; //判断是否在修改电话中
        final String phone = isEdit ? etPhone.getText()
                .toString() : user.getPhone();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(this, null, R.string.label_phone_hint);
            return;
        }
        if (phone.length() != 11 || !Util.isMobileNO(phone)) {
            ToastUtil.showToast(this, null, R.string.msg_wrong_phone_number);
            return;
        }
        if (!isEdit) {
            postSignUpInfo(phone, null);
        } else {
            if (TextUtils.isEmpty(etSmsCode.getText())) {
                ToastUtil.showToast(this, null, R.string.label_certify_hint);
                return;
            }
            String smsCode = etSmsCode.getText()
                    .toString();
            CommonUtil.unSubscribeSubs(checkPhoneSub);
            checkPhoneSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<String>() {
                        @Override
                        public void onNext(String code) {
                            postSignUpInfo(phone, code);
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            EventApi.checkPhoneIsRealObb(phone, smsCode)
                    .subscribe(checkPhoneSub);
        }
    }

    //提交报名参数
    private void postSignUpInfo(final String phone, String code) {
        if (eventInfo.getSignUpFee() > 0) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", eventInfo.getId());
                jsonObject.put("name", user.getRealName());
                jsonObject.put("phone", phone);
                if (!TextUtils.isEmpty(code)) {
                    jsonObject.put("sms_code", code);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (paySub == null) {
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
                    .path(Constants.HttpPath.SIGN_UP)
                    .price(eventInfo.getSignUpFee())
                    .subscriber(paySub)
                    .payAgents(payTypes, DataConfig.getPayAgents())
                    .build()
                    .pay();
        } else {
            CommonUtil.unSubscribeSubs(signUpSub);
            signUpSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<SignUpInfo>() {
                        @Override
                        public void onNext(SignUpInfo object) {
                            eventInfo.setSignUpInfo(object);
                            setResult(RESULT_OK, getIntent().putExtra("eventInfo", eventInfo));
                            onBackPressed();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();
            EventApi.signUpObb(eventInfo.getId(), user.getRealName(), phone, code)
                    .subscribe(signUpSub);
        }
    }

    private Subscriber<PayRxEvent> initSubscriber() {
        return new RxBusSubscriber<PayRxEvent>() {
            @Override
            protected void onEvent(PayRxEvent rxEvent) {
                switch (rxEvent.getType()) {
                    case PAY_SUCCESS:
                        if (eventInfo != null) {
                            eventInfo.getSignUpInfo()
                                    .setStatus(1);
                            setResult(RESULT_OK, getIntent().putExtra("eventInfo", eventInfo));
                            onBackPressed();
                        }
                        break;
                }
            }
        };
    }

    //倒计时
    private class CertifyTimeDown extends CountDownTimer {

        private String flag; //通过flag来区分

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public CertifyTimeDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String timeStr = getString(R.string.label_second1,
                    decimalFormat.format((int) (millisUntilFinished / 1000)));
            //短信
            if (flag.equalsIgnoreCase(LoginHelper.ACTIVITYAPPLYSMS)) {
                btnSmsCertify.setEnabled(false);
                btnSmsCertify.setText(timeStr);
                tvVoiceCertifyHint.setVisibility(View.GONE);
                btnVoiceCertify.setVisibility(View.GONE);
            }
            //语音
            else if (flag.equalsIgnoreCase(LoginHelper.ACTIVITYAPPLYVOICE)) {
                btnSmsCertify.setEnabled(false);
                btnVoiceCertify.setText(timeStr);
                btnVoiceCertify.setTextColor(ContextCompat.getColor(SignUpActivity.this,
                        R.color.colorGray));
                btnVoiceCertify.setEnabled(false);
                tvVoiceCertifyHint.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFinish() {
            if (flag.equalsIgnoreCase(LoginHelper.ACTIVITYAPPLYSMS)) {
                btnSmsCertify.setEnabled(true);
                btnSmsCertify.setText(R.string.label_reload);
                btnVoiceCertify.setVisibility(View.VISIBLE);
                btnVoiceCertify.setTextColor(ContextCompat.getColor(SignUpActivity.this,
                        R.color.colorPrimary));
                btnVoiceCertify.setText(R.string.label_voice_certify_unreceive);
            } else if (flag.equalsIgnoreCase(LoginHelper.ACTIVITYAPPLYVOICE)) {
                btnSmsCertify.setEnabled(true);
                btnVoiceCertify.setEnabled(true);
                btnVoiceCertify.setTextColor(ContextCompat.getColor(SignUpActivity.this,
                        R.color.colorPrimary));
                btnVoiceCertify.setText(R.string.label_voice_certify_unreceive);
                tvVoiceCertifyHint.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeDown != null) {
            timeDown.cancel();
        }
        LoginHelper.getInstance()
                .onDestroy();
        CommonUtil.unSubscribeSubs(signUpSub, checkPhoneSub, paySub);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            if (timeDown != null) {
                timeDown.cancel();
            }
            LoginHelper.getInstance()
                    .onDestroy();
            CommonUtil.unSubscribeSubs(signUpSub, checkPhoneSub, paySub);
        }
    }
}