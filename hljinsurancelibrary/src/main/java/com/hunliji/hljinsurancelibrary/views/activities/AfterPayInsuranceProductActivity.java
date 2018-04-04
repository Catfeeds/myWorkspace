package com.hunliji.hljinsurancelibrary.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import com.hunliji.hljcommonlibrary.HljCommon;
import com.hunliji.hljcommonlibrary.utils.SystemNotificationUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 保险支付成功页
 * Created by chen_bin on 2017/5/25 0025.
 */
public class AfterPayInsuranceProductActivity extends HljBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_after_pay_insurance_product);
        ButterKnife.bind(this);
        initNotifyDialog();
    }

    private void initNotifyDialog() {
        Dialog dialog = SystemNotificationUtil.getNotificationOpenDlgOfPrefName(this,
                HljCommon.SharedPreferencesNames.PREF_NOTICE_OPEN_DLG_PAY,
                "付款成功",
                "立即开启消息通知，及时掌握订单状态和物流信息哦~",
                R.drawable.icon_dlg_appointment);
        if (dialog != null) {
            dialog.show();
        }
    }

    @OnClick(R2.id.btn_create_policy)
    void onCreatePolicy() {
        Intent intent = new Intent(this, CreateHlbPolicyActivity.class);
        intent.putExtra("policy_detail", getIntent().getParcelableExtra("policy_detail"));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MyPolicyListActivity.class);
        intent.putExtra("backMain", true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.activity_anim_default);
        finish();
    }
}
