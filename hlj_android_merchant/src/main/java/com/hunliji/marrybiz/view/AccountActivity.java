package com.hunliji.marrybiz.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.suncloud.hljweblibrary.views.activities.HljWebViewActivity;
import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.Constants;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.merchant.MerchantApi;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.task.OnHttpRequestListener;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.LinkUtil;
import com.hunliji.marrybiz.util.MerchantUserSyncUtil;
import com.hunliji.marrybiz.util.Session;

import rx.Subscription;

/**
 * 账号管理
 * Created by jinxin on 2016/9/13.
 */
public class AccountActivity extends HljBaseActivity implements View.OnClickListener {

    private TextView tvBindStatus;
    private TextView tvBindAction;

    private Subscription releaseSubscription;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initWidget();
    }

    private void initWidget() {
        findViewById(R.id.change_password_layout).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.wechant_bind_layout).setOnClickListener(this);
        tvBindStatus = (TextView) findViewById(R.id.tv_bind_status);
        tvBindAction = (TextView) findViewById(R.id.tv_bind_action);
        MerchantUser user = Session.getInstance()
                .getCurrentUser(this);
        TextView tvLoginPhoneNumber = (TextView) findViewById(R.id.tv_login_phone_number);
        if (!JSONUtil.isEmpty(user.getPhone())) {
            tvLoginPhoneNumber.setText(user.getPhone());
        }
        setBindStatus();
    }


    private void setBindStatus() {
        if (MerchantUserSyncUtil.getInstance()
                .isBind()) {
            tvBindAction.setText(R.string.label_action_unbind);
            tvBindAction.setTextColor(getResources().getColor(R.color.colorPrimary));
            tvBindStatus.setText(R.string.hint_is_bind);
        } else {
            tvBindAction.setText(R.string.label_action_bind);
            tvBindAction.setTextColor(getResources().getColor(R.color.green));
            tvBindStatus.setText(R.string.hint_bind_wechant);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id) {
            case R.id.change_password_layout:
                intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.wechant_bind_layout:
                if (releaseSubscription != null && !releaseSubscription.isUnsubscribed()) {
                    return;
                }
                if (progressDialog == null) {
                    progressDialog = DialogUtil.createProgressDialog(AccountActivity.this);
                }
                if (MerchantUserSyncUtil.getInstance()
                        .isBind()) {
                    DialogUtil.createDoubleButtonDialog(this,
                            "解绑微信后你将无法收到客户的私信提醒，你确定要解绑吗？",
                            "解绑",
                            null,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    releaseSubscription = MerchantApi.releaseBindWechatObb()
                                            .subscribe(HljHttpSubscriber.buildSubscriber(
                                                    AccountActivity.this)
                                                    .setProgressDialog(progressDialog)
                                                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                                                        @Override
                                                        public void onNext(
                                                                JsonElement jsonElement) {
                                                            MerchantUserSyncUtil.getInstance()
                                                                    .setBind(false);
                                                            setBindStatus();
                                                        }
                                                    })
                                                    .build());
                                }
                            },
                            null)
                            .show();

                } else {
                    progressDialog.show();
                    LinkUtil.getInstance(this)
                            .getLink(Constants.LinkNames.BIND_WECHAT_EDU,
                                    new OnHttpRequestListener() {
                                        @Override
                                        public void onRequestCompleted(Object obj) {
                                            progressDialog.dismiss();
                                            String url = (String) obj;
                                            if (!JSONUtil.isEmpty(url)) {
                                                Intent intent = new Intent(AccountActivity.this,
                                                        HljWebViewActivity.class);
                                                intent.putExtra("path", url);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.slide_in_right,
                                                        R.anim.activity_anim_default);
                                            }
                                        }

                                        @Override
                                        public void onRequestFailed(Object obj) {
                                            progressDialog.dismiss();
                                        }
                                    });
                }
                break;
            default:
                break;
        }
    }

    public void logout() {
        Intent intent = new Intent(this, PreLoginActivity.class);
        intent.putExtra("logout", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onFinish() {
        if (releaseSubscription != null && !releaseSubscription.isUnsubscribed()) {
            releaseSubscription.unsubscribe();
        }
        super.onFinish();
    }
}
