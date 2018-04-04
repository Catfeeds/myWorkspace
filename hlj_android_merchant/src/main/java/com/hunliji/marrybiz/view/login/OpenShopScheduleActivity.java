package com.hunliji.marrybiz.view.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.rxbus.RxBusSubscriber;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.view.shop.EditShopActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/8/16.开店进度
 */

public class OpenShopScheduleActivity extends HljBaseActivity {

    @BindView(R.id.tv_alert_msg)
    TextView tvAlertMsg;
    @BindView(R.id.alert_layout)
    LinearLayout alertLayout;
    @BindView(R.id.img_shop_info_state)
    ImageView imgShopInfoState;
    @BindView(R.id.tv_shop_info_state)
    TextView tvShopInfoState;
    @BindView(R.id.img_certification_state)
    ImageView imgCertificationState;
    @BindView(R.id.tv_certification_state)
    TextView tvCertificationState;
    @BindView(R.id.shop_info_view)
    LinearLayout shopInfoView;
    @BindView(R.id.certification_view)
    LinearLayout certificationView;
    @BindView(R.id.tv_shop_info_tip)
    TextView tvShopInfoTip;
    @BindView(R.id.tv_certification_tip)
    TextView tvCertificationTip;

    private MerchantUser merchantUser;
    private RxBusSubscriber<MerchantUser> userInfoUpdateSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_shop_schedule);
        ButterKnife.bind(this);
        initValue();
        setOpenScheduleView();
        registerUserInfoUpdate();
    }

    /**
     * 注册用户信息更新监听,任何时候有用户信息更新回调都会受到更新事件,进而更新界面
     */
    private void registerUserInfoUpdate() {
        if (userInfoUpdateSub == null || userInfoUpdateSub.isUnsubscribed()) {
            userInfoUpdateSub = new RxBusSubscriber<MerchantUser>() {
                @Override
                protected void onEvent(MerchantUser user) {
                    merchantUser = user;
                    setOpenScheduleView();
                }
            };
            RxBus.getDefault()
                    .toObservable(MerchantUser.class)
                    .subscribe(userInfoUpdateSub);
        }
    }

    private void initValue() {
        merchantUser = Session.getInstance()
                .getCurrentUser(this);
    }

    private void setOpenScheduleView() {
        if (merchantUser != null) {
            //店铺信息(第一次)
            if (merchantUser.getExamine() == -1) {
                imgShopInfoState.setImageResource(R.mipmap.icon_unsubmit_44_44);
                tvShopInfoState.setText("未提交");
                tvShopInfoState.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                tvShopInfoTip.setVisibility(View.VISIBLE);
                tvShopInfoTip.setText("立即提交");
            } else if (merchantUser.getExamine() == 0 && merchantUser.getStatus() == 0) {
                imgShopInfoState.setImageResource(R.mipmap.icon_audit_in_44_44);
                tvShopInfoState.setText("审核中");
                tvShopInfoState.setTextColor(Color.parseColor("#5dc3fc"));
                tvShopInfoTip.setText("修改");
            } else if (merchantUser.getExamine() == 0 && merchantUser.getStatus() == 1) {
                imgShopInfoState.setImageResource(R.mipmap.icon_audit_fail_44_44);
                tvShopInfoState.setText("审核未通过");
                tvShopInfoState.setTextColor(Color.parseColor("#ff6060"));
                tvShopInfoTip.setVisibility(View.VISIBLE);
                tvShopInfoTip.setText("修改");
            } else if (merchantUser.getExamine() == 1) {
                imgShopInfoState.setImageResource(R.mipmap.icon_audit_complete_44_44);
                tvShopInfoState.setText("审核通过");
                tvShopInfoState.setTextColor(ContextCompat.getColor(this, R.color.colorSuccess));
                tvShopInfoTip.setVisibility(View.GONE);
            }

            //实名认证
            switch (merchantUser.getCertifyStatus()) {
                case 0:
                    imgCertificationState.setImageResource(R.mipmap.icon_unsubmit_44_44);
                    tvCertificationState.setText("未提交");
                    tvCertificationState.setTextColor(ContextCompat.getColor(this,
                            R.color.colorAccent));
                    tvCertificationTip.setVisibility(View.VISIBLE);
                    tvCertificationTip.setText("立即提交");
                    break;
                case 1:
                    imgCertificationState.setImageResource(R.mipmap.icon_audit_in_44_44);
                    tvCertificationState.setText("审核中");
                    tvCertificationState.setTextColor(Color.parseColor("#5dc3fc"));
                    tvCertificationTip.setVisibility(View.VISIBLE);
                    tvCertificationTip.setText("修改");
                    break;
                case 2:
                    imgCertificationState.setImageResource(R.mipmap.icon_audit_fail_44_44);
                    tvCertificationState.setText("审核未通过");
                    tvCertificationState.setTextColor(Color.parseColor("#ff6060"));
                    tvCertificationTip.setVisibility(View.VISIBLE);
                    tvCertificationTip.setText("修改");
                    break;
                case 3:
                    imgCertificationState.setImageResource(R.mipmap.icon_audit_complete_44_44);
                    tvCertificationState.setText("审核通过");
                    tvCertificationState.setTextColor(ContextCompat.getColor(this,
                            R.color.colorSuccess));
                    tvCertificationTip.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @OnClick(R.id.alert_layout)
    public void onAlertLayoutClicked() {}

    @OnClick(R.id.shop_info_view)
    public void onShopInfoViewClicked() {
        if (merchantUser.getExamine() != 1) {
            Intent intent = new Intent(OpenShopScheduleActivity.this, EditShopActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.certification_view)
    public void onCertificationViewClicked() {
        if (merchantUser.getExamine() == -1) {
            DialogUtil.createDoubleButtonDialog(this,
                    getString(R.string.label_please_commit_shop_info),
                    "前去提交",
                    null,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(OpenShopScheduleActivity.this,
                                    EditShopActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.activity_anim_default);
                        }
                    },
                    null)
                    .show();
            return;
        }
        if (merchantUser.getCertifyStatus() != 3) {
            Intent intent = new Intent();
            if (merchantUser.isIndividualMerchant()) {
                intent.setClass(this, PreCertificationActivity.class);
            } else {
                intent.setClass(this, CompanyCertificationActivity.class);
            }
            startActivity(intent);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(userInfoUpdateSub);
    }
}
