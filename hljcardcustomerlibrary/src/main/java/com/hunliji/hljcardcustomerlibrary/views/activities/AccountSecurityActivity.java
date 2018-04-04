package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.authorization.UserSession;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2018/1/22.请帖账号安全（换绑手机）
 */

public class AccountSecurityActivity extends HljBaseActivity {

    public static final int EDIT_REAL_NAME = 1;

    @BindView(R2.id.tv_real_name)
    TextView tvRealName;
    @BindView(R2.id.tv_bind_card_phone)
    TextView tvBindCardPhone;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        ButterKnife.bind(this);
        user = UserSession.getInstance()
                .getUser(this);
        tvBindCardPhone.setText(String.valueOf(user.getPhone()));
        tvRealName.setText(user.getRealName());
    }

    @OnClick(R2.id.real_name_layout)
    public void onRealNameLayoutClicked() {
        Intent intent = new Intent(this, EditRealNameActivity.class);
        startActivityForResult(intent, EDIT_REAL_NAME);
    }

    @OnClick(R2.id.bind_phone_layout)
    public void onBindPhoneLayoutClicked() {
        Intent intent = new Intent();
        intent.setClass(this, BindUserPhoneActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_REAL_NAME:
                    if (data != null && data.getBooleanExtra("refresh", false)) {
                        Intent intent = getIntent();
                        intent.putExtra("refresh", true);
                        setResult(RESULT_OK, intent);
                        User user = UserSession.getInstance()
                                .getUser(this);
                        if (!CommonUtil.isEmpty(user.getRealName())) {
                            tvRealName.setText(user.getRealName());
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
