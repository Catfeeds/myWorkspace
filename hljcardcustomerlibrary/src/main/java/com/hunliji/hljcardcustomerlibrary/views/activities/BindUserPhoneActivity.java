package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.JsonElement;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.SupportJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.moudles.Support;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2018/1/22.绑定手机号
 */

public class BindUserPhoneActivity extends HljBaseActivity {

    @BindView(R2.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private Dialog dialog;
    private User user;
    private HljHttpSubscriber initSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_user_phone);
        ButterKnife.bind(this);
        user = UserSession.getInstance()
                .getUser(this);
        if (user != null) {
            tvPhoneNum.setText(user.getPhone());
        }
    }


    @OnClick(R2.id.action_change_phone)
    public void onActionChangePhoneClicked() {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setProgressBar(progressBar)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            try {
                                boolean isSecurity = jsonElement.getAsJsonObject()
                                        .get("is_security")
                                        .getAsBoolean();
                                //点击更换手机号按钮，如果此设备第一次登录是3天以前（距现在大于3day*24h），
                                //点击跳转至更换手机号。否则弹窗，点击联系客服跳转至客服聊天页面：
                                if (isSecurity) {
                                    Intent intent = new Intent(BindUserPhoneActivity.this,
                                            ChangeBindPhoneActivity.class);
                                    startActivity(intent);
                                } else {
                                    showDialog();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .build();
            CustomerCardApi.checkPhoneStatusObb()
                    .subscribe(initSubscriber);
        }
    }

    private void showDialog() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        if (dialog == null) {
            dialog = DialogUtil.createDoubleButtonDialog(this,
                    "提示",
                    "为了保证您的账号安全，请使用常用登录设备进行更换手机号操作",
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
                                supportJumpService.gotoSupport(BindUserPhoneActivity.this,
                                        Support.SUPPORT_KIND_DEFAULT_ROBOT);
                            }
                        }
                    });
        }
        dialog.show();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(initSubscriber);
    }
}
