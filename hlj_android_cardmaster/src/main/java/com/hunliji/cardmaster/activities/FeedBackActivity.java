package com.hunliji.cardmaster.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.cardmaster.R;
import com.hunliji.cardmaster.api.CommonApi;
import com.hunliji.cardmaster.utils.CardMasterSupportUtil;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.moudles.Support;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 意见反馈
 * Created by jinxin on 2017/11/27 0027.
 */
@Route(path = RouterPath.IntentPath.Customer.FEED_BACK_ACTIVITY)
public class FeedBackActivity extends HljBaseActivity {
    private static final int MAX_COUNT = 500;//最多输入500个
    @BindView(R.id.et_feedback)
    EditText etFeedback;
    @BindView(R.id.tv_text_count)
    TextView tvTextCount;
    @BindView(R.id.et_contact)
    ClearableEditText etContact;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.tv_contact_us)
    TextView tvContactUs;
    @BindView(R.id.tv_contact_time)
    TextView tvContactTime;

    private HljHttpSubscriber feedBackSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);

        initWidget();
    }

    private void initWidget() {
        tvTextCount.setText(String.valueOf(MAX_COUNT));
        etFeedback.addTextChangedListener(new TextCountWatcher(etFeedback, tvTextCount, MAX_COUNT));
    }

    @OnClick(R.id.btn_send)
    public void onSend() {
        CommonUtil.unSubscribeSubs(feedBackSub);
        String content = etFeedback.getText()
                .toString()
                .trim();
        String phone = etContact.getText()
                .toString()
                .trim();
        if (!TextUtils.isEmpty(phone) && !CommonUtil.isMobileNO(phone)) {
            Toast.makeText(this, getString(R.string.hint_new_number_error___cm), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        feedBackSub = HljHttpSubscriber.buildSubscriber(this)
                .setProgressDialog(DialogUtil.createProgressDialog(this))
                .setDataNullable(true)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                    @Override
                    public void onNext(HljHttpResult hljHttpResult) {
                        if (hljHttpResult != null && hljHttpResult.getStatus()
                                .getRetCode() == 0) {
                            ToastUtil.showToast(FeedBackActivity.this,
                                    null,
                                    R.string.msg_feedback_ok___card);
                            onBackPressed();
                        }
                    }
                })
                .build();
        CommonApi.postFeedback(content, phone)
                .subscribe(feedBackSub);
    }

    @OnClick(R.id.tv_contact_us)
    public void onContactUs() {
        CardMasterSupportUtil.goToSupport(this, Support.SUPPORT_KIND_CARD_MASTER);
    }

    @OnTextChanged(R.id.et_feedback)
    public void onBtnEnable(CharSequence text) {
        btnSend.setEnabled(!TextUtils.isEmpty(text.toString()
                .trim()));
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(feedBackSub);
    }
}
