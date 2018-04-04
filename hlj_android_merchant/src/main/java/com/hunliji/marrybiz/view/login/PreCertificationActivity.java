package com.hunliji.marrybiz.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.login.LoginApi;
import com.hunliji.marrybiz.model.Certify;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.Session;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by mo_yu on 2017/8/16.实名认证选择类型
 */
public class PreCertificationActivity extends HljBaseActivity {

    @BindView(R.id.personal_certification_view)
    View personalCertificationView;
    @BindView(R.id.company_certification_view)
    View companyCertificationView;
    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;

    private Subscription rxBusEventSub;
    private HljHttpSubscriber initSubscriber;
    private Certify certify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_certification);
        ButterKnife.bind(this);
        registerRxBusEvent();
        MerchantUser merchantUser = Session.getInstance()
                .getCurrentUser(this);
        if (merchantUser != null && merchantUser.getCertify() != null && merchantUser.getCertify()
                .getStatus() > 0) {
            certify = merchantUser.getCertify();
            getCertifyInfo(merchantUser.getId());
        }
    }

    private void initCertifyView() {
        if (certify != null) {
            if (certify.getStatus() == 1) {
                // 正在审核的状态
                alertLayout.setVisibility(View.VISIBLE);
                tvAlertMsg.setText(R.string.hint_merchant_reviewing);
            } else if (certify.getStatus() == 2) {
                // 审核不通过,显示原因
                alertLayout.setVisibility(View.VISIBLE);
                tvAlertMsg.setText(TextUtils.isEmpty(certify.getFailReason()) ? "未通过审核！" :
                        "未通过审核！原因：" + certify.getFailReason());
            } else {
                alertLayout.setVisibility(View.GONE);
            }
        }
    }

    //获取实名认证信息
    private void getCertifyInfo(long merchantId) {
        if (initSubscriber == null || initSubscriber.isUnsubscribed()) {
            initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<JsonElement>() {
                        @Override
                        public void onNext(JsonElement jsonElement) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(jsonElement.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            certify = new Certify(jsonObject);
                            initCertifyView();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(this))
                    .build();

            LoginApi.getCertifyInfoObb(merchantId)
                    .subscribe(initSubscriber);
        }
    }

    @OnClick(R.id.personal_certification_view)
    public void onPersonalCertificationViewClicked() {
        Intent intent = new Intent(this, PersonalCertificationActivity.class);
        intent.putExtra(BaseCertificationActivity.ARG_CERTIFY, certify);
        startActivity(intent);
    }

    @OnClick(R.id.company_certification_view)
    public void onCompanyCertificationViewClicked() {
        Intent intent = new Intent(this, CompanyCertificationActivity.class);
        intent.putExtra(BaseCertificationActivity.ARG_CERTIFY, certify);
        startActivity(intent);
    }

    //注册RxEventBus监听
    private void registerRxBusEvent() {
        if (rxBusEventSub == null || rxBusEventSub.isUnsubscribed()) {
            rxBusEventSub = RxBus.getDefault()
                    .toObservable(RxEvent.class)
                    .subscribe(new RxBusSubscriber<RxEvent>() {
                        @Override
                        protected void onEvent(RxEvent rxEvent) {
                            switch (rxEvent.getType()) {
                                case CERTIFY_SUCCESS:
                                    finish();
                                    break;
                            }
                        }
                    });
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(rxBusEventSub, initSubscriber);
    }
}
