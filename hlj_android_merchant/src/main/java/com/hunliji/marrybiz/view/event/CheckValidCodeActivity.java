package com.hunliji.marrybiz.view.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.event.EventApi;
import com.hunliji.marrybiz.util.Session;
import com.hunliji.marrybiz.util.Util;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 中奖码验证
 * Created by chen_bin on 2016/9/5 0005.
 */
public class CheckValidCodeActivity extends HljBaseActivity {
    @BindView(R.id.et_valid_code)
    EditText etValidCode;
    private long merchantId;
    private InputMethodManager imm;
    private HljHttpSubscriber checkValidCodeSub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_valid_code);
        ButterKnife.bind(this);
        this.merchantId = Session.getInstance()
                .getCurrentUser(this)
                .getMerchantId();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        if (etValidCode.length() == 0) {
            Util.showToast(this, null, R.string.hint_please_enter_valid_code);
            return;
        }
        final String validCode = etValidCode.getText()
                .toString();
        CommonUtil.unSubscribeSubs(checkValidCodeSub);
        checkValidCodeSub = HljHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        Intent intent = new Intent(CheckValidCodeActivity.this,
                                AfterCheckValidCodeActivity.class);
                        intent.putExtra("validCode", validCode);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.activity_anim_default);
                    }
                })
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .build();
        EventApi.checkInObb(merchantId, validCode)
                .subscribe(checkValidCodeSub);
    }

    @Override
    public void onBackPressed() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(checkValidCodeSub);
    }
}