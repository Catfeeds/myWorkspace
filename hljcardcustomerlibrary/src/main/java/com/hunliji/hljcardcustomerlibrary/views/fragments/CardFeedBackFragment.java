package com.hunliji.hljcardcustomerlibrary.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.hunliji.hljcardcustomerlibrary.api.CustomerCardApi;
import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.modules.services.SupportJumpService;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljkefulibrary.moudles.Support;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import rx.Observable;


public class CardFeedBackFragment extends RefreshFragment {

    @BindView(R2.id.et_feedback)
    EditText etFeedback;
    @BindView(R2.id.tv_text_count)
    TextView tvTextCount;
    @BindView(R2.id.et_contact)
    ClearableEditText etContact;
    @BindView(R2.id.btn_send)
    Button btnSend;
    private Unbinder unbinder;
    private HljHttpSubscriber subscriber;

    private static final int MAX_COUNT = 500;

    public static Fragment newInstance() {
        return new CardFeedBackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback___card, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        tvTextCount.setText(String.valueOf(MAX_COUNT));
        etFeedback.addTextChangedListener(new TextCountWatcher(etFeedback, tvTextCount, MAX_COUNT));
        return rootView;
    }

    @OnClick(R2.id.btn_send)
    public void onSend() {
        if (subscriber == null || subscriber.isUnsubscribed()) {
            String content = etFeedback.getText()
                    .toString()
                    .trim();
            String contact = etContact.getText()
                    .toString()
                    .trim();
            if (!TextUtils.isEmpty(contact) && !CommonUtil.isMobileNO(contact)) {
                Toast.makeText(getContext(),
                        getString(R.string.hint_new_number_error___cm),
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            Observable<HljHttpResult> observable = CustomerCardApi.postFeedback(content, contact);
            subscriber = HljHttpSubscriber.buildSubscriber(getContext())
                    .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                    .setDataNullable(true)
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            if (getActivity() != null) {
                                ToastUtil.showToast(getContext(),
                                        null,
                                        R.string.msg_feedback_ok___card);
                                getActivity().onBackPressed();
                            }
                        }
                    })
                    .build();
            observable.subscribe(subscriber);
        }
    }

    @OnTextChanged(R2.id.et_feedback)
    public void onBtnEnable(CharSequence text) {
        btnSend.setEnabled(!TextUtils.isEmpty(text.toString()
                .trim()));
    }


    @OnClick(R2.id.tv_contact_us)
    public void onContactUs() {
        SupportJumpService supportJumpService = (SupportJumpService) ARouter.getInstance()
                .build(RouterPath.ServicePath.GO_TO_SUPPORT)
                .navigation(getContext());
        if (supportJumpService != null) {
            supportJumpService.gotoSupport(getContext(), Support.SUPPORT_KIND_DEFAULT_ROBOT);
        }
    }


    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(subscriber);
    }
}
