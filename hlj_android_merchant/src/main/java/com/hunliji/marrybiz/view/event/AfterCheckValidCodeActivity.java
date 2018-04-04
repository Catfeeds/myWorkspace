package com.hunliji.marrybiz.view.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.event.SignUpInfo;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.event.EventApi;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 凭验证码验证结果
 * Created by chen_bin on 2016/9/6 0006.
 */
public class AfterCheckValidCodeActivity extends HljBaseActivity {

    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_real_name)
    TextView tvRealName;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private String validCode;
    private HljHttpSubscriber initSub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_check_valid_code);
        ButterKnife.bind(this);
        validCode = getIntent().getStringExtra("validCode");
        emptyView.setNetworkHint2Id(R.string.label_click_to_reload___cm);
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                getAfterCheckValidCode();
            }
        });
        getAfterCheckValidCode();
    }

    private void getAfterCheckValidCode() {
        if (initSub == null || initSub.isUnsubscribed()) {
            initSub = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<SignUpInfo>() {
                        @Override
                        public void onNext(SignUpInfo object) {
                            tvStatus.setText(object.getWinnerLimit() == 0 ? R.string
                                    .label_valid_code : R.string.label_valid_code2);
                            tvTitle.setText(TextUtils.isEmpty(object.getTitle()) ? "" : object
                                    .getTitle());
                            tvRealName.setText(TextUtils.isEmpty(object.getRealName()) ? "" :
                                    object.getRealName());
                        }
                    })
                    .setProgressBar(progressBar)
                    .setEmptyView(emptyView)
                    .setContentView(scrollView)
                    .build();
            EventApi.getSignUpInfoByCodeObb(validCode)
                    .subscribe(initSub);
        }
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
        CommonUtil.unSubscribeSubs(initSub);
    }
}