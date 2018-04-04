package com.hunliji.hljcommonviewlibrary.views.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 获取短信验证码
 * Created by chen_bin on 2017/8/17 0017.
 */
public abstract class BaseGetSmsCodeFragment extends DialogFragment {
    @BindView(R2.id.tv_alert_title)
    public TextView tvAlertTitle;
    @BindView(R2.id.tv_alert_msg)
    public TextView tvAlertMsg;
    @BindView(R2.id.tv_alert_tip)
    public TextView tvAlertTip;
    @BindView(R2.id.et_sms_code)
    public EditText etSmsCode;
    @BindView(R2.id.btn_get_sms_code)
    public Button btnGetSmsCode;
    private Unbinder unbinder;
    private long MILLIS_IN_FUTURE = 60 * 1000;//验证码60s倒计时时间
    private long COUNT_DOWN_INTERVAL = 1000;//倒计时 时间间隔

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BubbleDialogFragment);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_base_get_sms_code___cv,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        tvAlertTitle.getPaint()
                .setFakeBoldText(true);
        initView();
        return rootView;
    }

    public void initView() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String alertTitle = getAlertTitle();
        if (TextUtils.isEmpty(alertTitle)) {
            tvAlertTitle.setVisibility(View.GONE);
        } else {
            tvAlertTitle.setVisibility(View.VISIBLE);
            tvAlertTitle.setText(alertTitle);
        }
        tvAlertMsg.setText(getAlertMsg());
    }

    @OnClick({R2.id.btn_get_sms_code, R2.id.btn_cancel, R2.id.btn_confirm})
    public void onClick(View view) {
        if (view.getId() == R.id.btn_get_sms_code) {
            onGetSmsCode();
        } else if (view.getId() == R.id.btn_cancel) {
            dismiss();
        } else if (view.getId() == R.id.btn_confirm) {
            onConfirm();
        }
    }

    protected CountDownTimer timer = new CountDownTimer(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL) {

        @Override
        public void onTick(long millisUntilFinished) {
            btnGetSmsCode.setEnabled(false);
            btnGetSmsCode.setText(String.format("%02d", (int) (millisUntilFinished / 1000)) + "S");
        }

        @Override
        public void onFinish() {
            btnGetSmsCode.setEnabled(true);
            btnGetSmsCode.setText(R.string.label_regain___cv);
        }
    };

    //修改倒计时时间，正常为60秒
    protected void changeMillisInFuture(long millis) {
        try {
            Class clazz = Class.forName("android.os.CountDownTimer");
            Field field = clazz.getDeclaredField("mMillisInFuture");
            field.setAccessible(true);
            field.set(timer, millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract String getAlertTitle();

    protected abstract String getAlertMsg();

    protected abstract void onGetSmsCode();

    protected abstract void onConfirm();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}