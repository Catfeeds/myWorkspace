package com.hunliji.hljinsurancelibrary.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.api.InsuranceApi;
import com.hunliji.hljinsurancelibrary.models.MyPolicy;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;
import com.hunliji.hljinsurancelibrary.views.fragments.PolicyFailureFragment;
import com.hunliji.hljinsurancelibrary.views.fragments.PolicySuccessFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by hua_rong on 2017/5/24.
 * 保单详情
 */

public class PolicyDetailActivity extends HljBaseActivity {
    @BindView(R2.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R2.id.fragment_content)
    FrameLayout fragmentContent;
    private HljHttpSubscriber subscriber;
    private String id;
    private boolean backMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        backMain = getIntent().getBooleanExtra("backMain", false);
        setSwipeBackEnable(!backMain);
        setContentView(R.layout.activity_policy_detail);
        ButterKnife.bind(this);
        setTitle("");
        emptyView.setNetworkErrorClickListener(new HljEmptyView.OnNetworkErrorClickListener() {
            @Override
            public void onNetworkErrorClickListener() {
                onLoad();
            }
        });
        onLoad();
    }

    private void onLoad() {
        if (subscriber == null || subscriber.isUnsubscribed()) {
            Observable<PolicyDetail> observable = InsuranceApi.getPolicyDetail(id);
            subscriber = HljHttpSubscriber.buildSubscriber(this)
                    .setOnNextListener(new SubscriberOnNextListener<PolicyDetail>() {

                        @Override
                        public void onNext(PolicyDetail insuranceDetail) {
                            if (insuranceDetail != null) {
                                initView(insuranceDetail);
                            }
                        }
                    })
                    .setEmptyView(emptyView)
                    .setProgressBar(progressBar)
                    .build();
            observable.subscribe(subscriber);
        }
    }

    //0未支付 1未提交保单 2待生效 3保障中 4已终止 5投保失败 6已退
    private void initView(PolicyDetail insuranceDetail) {
        int status = insuranceDetail.getStatus();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (status == MyPolicy.STATUS_FAILED || status == MyPolicy.STATUS_REFUND) {
            setTitle(getString(R.string.title_activity_after_insurance));
            transaction.add(R.id.fragment_content,
                    PolicyFailureFragment.newInstance(insuranceDetail),
                    "PolicyFailureFragment");
        } else if (status == MyPolicy.STATUS_TO_BE_EFFECTIVE || status == MyPolicy.STATUS_PROTECT
                || status == MyPolicy.STATUS_FINISHED) {
            setTitle(getString(R.string.title_activity_policy_detail));
            transaction.add(R.id.fragment_content,
                    PolicySuccessFragment.newInstance(insuranceDetail),
                    "PolicySuccessFragment");
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (!backMain) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent(this, MyPolicyListActivity.class);
            intent.putExtra("backMain", true);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.activity_anim_default);
        }
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(subscriber);
    }
}
