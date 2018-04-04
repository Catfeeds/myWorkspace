package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.views.fragments.CheckSecurityVerificationFragment;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.SupportJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.moudles.Support;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2018/1/22.更换手机号
 */

public class ChangeBindPhoneActivity extends HljBaseActivity {

    @BindView(R2.id.et_new_phone)
    EditText etNewPhone;
    @BindView(R2.id.action_submit)
    TextView actionSubmit;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    private HljHttpSubscriber submitSubscriber;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_bind_phone);
        ButterKnife.bind(this);
    }

    @OnClick(R2.id.action_submit)
    public void onActionSubmitClicked() {
        String newPhone = etNewPhone.getText()
                .toString();
        if (CommonUtil.isEmpty(newPhone) || !CommonUtil.isMobileNO(newPhone)) {
            ToastUtil.showToast(this, "请输入正确的手机号", 0);
            return;
        }
        submitNewPhone(newPhone);
    }

    private void submitNewPhone(final String newPhone) {
        if (submitSubscriber == null || submitSubscriber.isUnsubscribed()) {
            submitSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            try {
                                int bindStatues = jsonElement.getAsJsonObject()
                                        .get("bind_status")
                                        .getAsInt();
                                if (bindStatues > 0) {
                                    showAlreadyRegisteredDialog();
                                } else {
                                    showSecurityVerificationDialog(newPhone);
                                }
                            } catch (Exception ignore) {
                            }
                        }
                    })
                    .setProgressBar(progressBar)
                    .build();
            CustomerCardApi.getPhoneBindStatusObb(newPhone)
                    .subscribe(submitSubscriber);
        }
    }

    /**
     * 手机号已注册弹窗
     */
    private void showAlreadyRegisteredDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = DialogUtil.createDoubleButtonDialog(this,
                    "该手机号已被注册，请更换其他手机号或联系客服",
                    "好的",
                    "联系客服",
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SupportJumpService supportJumpService = (SupportJumpService) ARouter
                                    .getInstance()
                                    .build(RouterPath.ServicePath.GO_TO_SUPPORT)
                                    .navigation();
                            if (supportJumpService != null) {
                                supportJumpService.gotoSupport(ChangeBindPhoneActivity.this,
                                        Support.SUPPORT_KIND_DEFAULT_ROBOT);
                            }
                        }
                    });
        }
        dialog.show();
    }

    private void showSecurityVerificationDialog(String phone) {
        CheckSecurityVerificationFragment fragment = CheckSecurityVerificationFragment.newInstance(
                phone);
        fragment.show(getSupportFragmentManager(), "CheckSecurityVerificationFragment");
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(submitSubscriber);
    }
}
