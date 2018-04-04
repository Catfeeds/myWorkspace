package com.hunliji.hljinsurancelibrary.views.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.RxEvent;
import com.hunliji.hljcommonlibrary.rxbus.RxBus;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import com.hunliji.hljinsurancelibrary.R;
import com.hunliji.hljinsurancelibrary.R2;
import com.hunliji.hljinsurancelibrary.api.InsuranceApi;
import com.hunliji.hljinsurancelibrary.models.PostHlbPolicy;
import com.hunliji.hljinsurancelibrary.views.activities.PolicyDetailActivity;

/**
 * Created by mo_yu on 2017/5/25.填写婚礼保保单二次确认弹窗
 */

public class CheckPolicyFragment extends DialogFragment {

    @BindView(R2.id.tv_user_name)
    TextView tvUserName;
    @BindView(R2.id.tv_user_id_card)
    TextView tvUserIdCard;
    @BindView(R2.id.tv_user_phone)
    TextView tvUserPhone;
    @BindView(R2.id.tv_user_birthday)
    TextView tvUserBirthday;
    @BindView(R2.id.tv_user_sex)
    TextView tvUserSex;
    @BindView(R2.id.tv_spouse_name)
    TextView tvSpouseName;
    @BindView(R2.id.tv_hotel_address)
    TextView tvHotelAddress;
    @BindView(R2.id.tv_security_date)
    TextView tvSecurityDate;
    Unbinder unbinder;
    @BindView(R2.id.btn_confirm)
    Button btnConfirm;
    @BindView(R2.id.tv_hotel_name)
    TextView tvHotelName;
    @BindView(R2.id.line_layout)
    View lineLayout;
    @BindView(R2.id.btn_cancel)
    Button btnCancel;
    @BindView(R2.id.divider)
    View divider;
    private PostHlbPolicy postHlbPolicy;
    private HljHttpSubscriber submitSubscriber;

    public static CheckPolicyFragment newInstance(PostHlbPolicy postHlbPolicy) {
        Bundle args = new Bundle();
        CheckPolicyFragment fragment = new CheckPolicyFragment();
        args.putParcelable("postHlbPolicy", postHlbPolicy);
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
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        View view = inflater.inflate(R.layout.dialog_fragment_policy_info, container);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {
            postHlbPolicy = getArguments().getParcelable("postHlbPolicy");
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (postHlbPolicy != null) {
            tvUserName.setText(postHlbPolicy.getFullName());
            tvUserPhone.setText(postHlbPolicy.getPhone());
            tvHotelName.setText(postHlbPolicy.getWeddingHotel());
            tvHotelAddress.setText(postHlbPolicy.getWeddingAddress());
            tvSpouseName.setText(postHlbPolicy.getSpouseName());
            tvSecurityDate.setText(postHlbPolicy.getTransBeginDate());
            String idCard = postHlbPolicy.getCertiNo();
            tvUserIdCard.setText(idCard);
            String birthDay;
            //根据身份证号取生日和性别
            if (idCard.length() == 15) {
                birthDay = "19" + idCard.substring(6, 8) + "-" + idCard.substring(10,
                        12) + "-" + idCard.substring(12, 14);
            } else {
                birthDay = idCard.substring(6, 10) + "-" + idCard.substring(10,
                        12) + "-" + idCard.substring(12, 14);
            }
            String sex = (Integer.valueOf(idCard.substring(idCard.length() - 2,
                    idCard.length() - 1)) % 2 == 1) ? "男" : "女";
            tvUserBirthday.setText(birthDay);
            tvUserSex.setText(sex);
        }
    }

    /**
     * 提交保单
     */
    private void submitPolicy() {
        btnConfirm.setClickable(false);
        if (submitSubscriber == null || submitSubscriber.isUnsubscribed()) {
            submitSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener<HljHttpResult>() {
                        @Override
                        public void onNext(HljHttpResult result) {
                            if (result.getStatus() != null) {
                                //0表示投保成功，3000表示投保失败
                                if (result.getStatus()
                                        .getRetCode() == 0 || result.getStatus()
                                        .getRetCode() > 3000) {
                                    RxBus.getDefault()
                                            .post(new RxEvent(RxEvent.RxEventType
                                                    .POLICY_INFO_COMPLETED_SUCCESS,
                                                    null));
                                    dismiss();
                                    Intent intent = new Intent(getContext(),
                                            PolicyDetailActivity.class);
                                    intent.putExtra("id", postHlbPolicy.getId());
                                    intent.putExtra("backMain", true);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    ToastUtil.showToast(getContext(),
                                            result.getStatus()
                                                    .getMsg(),
                                            0);
                                    dismiss();
                                }
                            }
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                    .build();

            InsuranceApi.fillHlbObb(postHlbPolicy)
                    .subscribe(submitSubscriber);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(submitSubscriber);
    }

    @OnClick(R2.id.btn_cancel)
    public void onBtnCancelClicked() {
        dismiss();
    }

    @OnClick(R2.id.btn_confirm)
    public void onBtnConfirmClicked() {
        submitPolicy();
    }
}
