package com.hunliji.hljinsurancelibrary.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.utils.HljTimeUtils;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.models.PolicyDetail;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by mo_yu on 2017/5/24.投保失败页
 */

public class PolicyFailureFragment extends RefreshFragment {


    @BindView(R2.id.tv_insurance_failed_info)
    TextView tvInsuranceFailedInfo;
    @BindView(R2.id.tv_insurance_failed_tip)
    TextView tvInsuranceFailedTip;
    @BindView(R2.id.action_buy_again)
    TextView actionBuyAgain;
    @BindView(R2.id.tv_insurance_product_title)
    TextView tvInsuranceType;
    @BindView(R2.id.line_layout)
    View lineLayout;
    @BindView(R2.id.tv_insurance_count)
    TextView tvInsuranceCount;
    @BindView(R2.id.tv_insurance_price)
    TextView tvInsurancePrice;
    @BindView(R2.id.tv_insurance_date)
    TextView tvInsuranceDate;
    Unbinder unbinder;
    private PolicyDetail policyDetail;
    private static final String ARG_POLICY_DETAIL = "policy_detail";

    public static PolicyFailureFragment newInstance(PolicyDetail policyDetail) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_POLICY_DETAIL, policyDetail);
        PolicyFailureFragment fragment = new PolicyFailureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_policy_failure, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            policyDetail = getArguments().getParcelable(ARG_POLICY_DETAIL);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (policyDetail != null) {
            if (!TextUtils.isEmpty(policyDetail.getReason())) {
                tvInsuranceFailedInfo.setVisibility(View.VISIBLE);
                tvInsuranceFailedInfo.setText(getString(R.string.label_refund_declined_reason___cm,
                        policyDetail.getReason()));
            } else {
                tvInsuranceFailedInfo.setVisibility(View.GONE);
            }
            tvInsuranceFailedTip.setText(getString(R.string.label_insurance_failure_tip,
                    String.valueOf(policyDetail.getPrice())));
            tvInsuranceCount.setText(getString(R.string.label_insurance_num2,
                    policyDetail.getNum()));
            tvInsurancePrice.setText(getString(R.string.label_price9___cv,
                    policyDetail.getPrice()));
            tvInsuranceDate.setText(policyDetail.getCreatedAt()
                    .toString(HljTimeUtils.DATE_FORMAT_LONG));
            tvInsuranceType.setText(getString(R.string.label_insurance_type,
                    policyDetail.getProduct()
                            .getTitle()));
        }
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick(R2.id.action_buy_again)
    public void onActionBuyAgain() {
//        HljWeb.startWebView(getActivity(),
//                policyDetail.getProduct()
//                        .getDetailUrl());
    }
}
