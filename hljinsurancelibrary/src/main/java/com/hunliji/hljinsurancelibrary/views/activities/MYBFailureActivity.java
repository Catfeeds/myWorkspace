package com.hunliji.hljinsurancelibrary.views.activities;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.api.InsuranceApi;
import com.hunliji.hljinsurancelibrary.models.InsuranceProduct;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;

/**
 * 蜜月保投保失败
 * Created by jinxin on 2017/8/18 0018.
 */

public class MYBFailureActivity extends HljBaseActivity {

    @BindView(R2.id.tv_insurance_failed_info)
    TextView tvInsuranceFailedInfo;
    @BindView(R2.id.tv_insurance_failed_tip)
    TextView tvInsuranceFailedTip;
    @BindView(R2.id.action_buy_again)
    TextView actionBuyAgain;
    @BindView(R2.id.tv_insurance_product_title)
    TextView tvInsuranceProductTitle;
    @BindView(R2.id.line_layout)
    View lineLayout;
    @BindView(R2.id.tv_insurance_count)
    TextView tvInsuranceCount;
    @BindView(R2.id.tv_insurance_price)
    TextView tvInsurancePrice;
    @BindView(R2.id.tv_insurance_date)
    TextView tvInsuranceDate;
    @BindView(R2.id.progress_bar)
    ProgressBar progressBar;

    private PolicyDetail policyDetail;
    private HljHttpSubscriber loadSubscriber;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_policy_failure);
        ButterKnife.bind(this);

        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        if (getIntent() != null) {
            id = getIntent().getStringExtra("id");
        }
    }

    private void initWidget() {
        actionBuyAgain.setVisibility(View.GONE);
    }

    private void initLoad() {
        loadSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<PolicyDetail>() {
                    @Override
                    public void onNext(PolicyDetail policyDetail) {
                        setPolicyDetail(policyDetail);
                    }
                })
                .build();
        InsuranceApi.getMybDetail(id)
                .subscribe(loadSubscriber);
    }

    private void setPolicyDetail(PolicyDetail policyDetail) {
        if (policyDetail == null) {
            return;
        }
        this.policyDetail = policyDetail;
        InsuranceProduct product = this.policyDetail.getProduct();
        if (product == null) {
            return;
        }
        if (!TextUtils.isEmpty(policyDetail.getReason())) {
            tvInsuranceFailedInfo.setVisibility(View.VISIBLE);
            tvInsuranceFailedInfo.setText(getString(R.string.label_refund_declined_reason___cm,
                    policyDetail.getReason()));
        } else {
            tvInsuranceFailedInfo.setVisibility(View.GONE);
        }
        tvInsuranceFailedTip.setText(getString(R.string.label_MYB_insurance_failure_tip,
                String.valueOf(policyDetail.getPrice())));
        String count = getString(R.string.label_insurance_num2, policyDetail.getNum());
        String rawGiverName = policyDetail.getGiverName();
        if (!TextUtils.isEmpty(rawGiverName)) {
            String giverName = count + "(" + rawGiverName + "赠)";
            SpannableStringBuilder ssb = new SpannableStringBuilder(giverName);
            ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color
                    .colorLink));
            ssb.setSpan(span, count.length()+1, giverName.length() -2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            tvInsuranceCount.setText(ssb);
        }else{
            tvInsuranceCount.setText(count);
        }
        tvInsurancePrice.setText(getString(R.string.label_price9___cv,
                String.valueOf(policyDetail.getPrice())));
        tvInsuranceDate.setText(policyDetail.getCreatedAt()
                .toString(HljTimeUtils.DATE_FORMAT_LONG));
        tvInsuranceProductTitle.setText(getString(R.string.label_insurance_type,
                product.getTitle()));
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        CommonUtil.unSubscribeSubs(loadSubscriber);
    }
}
