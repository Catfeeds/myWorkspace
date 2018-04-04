package me.suncloud.marrymemo.fragment;

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

import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.CustomCommonApi;

import com.hunliji.hljcommonlibrary.utils.TextCountWatcher;
import com.hunliji.hljkefulibrary.moudles.Support;

import me.suncloud.marrymemo.util.CustomerSupportUtil;
import me.suncloud.marrymemo.widget.ClearableEditText;
import rx.Subscription;

/**
 * Created by wangtao on 2017/4/26.
 */

public class FeedBackFragment extends RefreshFragment {

    @BindView(R.id.et_feedback)
    EditText etFeedback;
    @BindView(R.id.tv_text_count)
    TextView tvTextCount;
    @BindView(R.id.et_contact)
    ClearableEditText etContact;
    @BindView(R.id.btn_send)
    Button btnSend;
    private Unbinder unbinder;
    private Subscription postSubscription;

    private final int MAX_COUNT = 500;//最多输入500个

    public static Fragment newInstance() {
        return new FeedBackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        tvTextCount.setText(String.valueOf(MAX_COUNT));
        etFeedback.addTextChangedListener(new TextCountWatcher(etFeedback, tvTextCount, MAX_COUNT));
        return rootView;
    }

    @OnClick(R.id.btn_send)
    public void onSend() {
        CommonUtil.unSubscribeSubs(postSubscription);
        String content = etFeedback.getText()
                .toString()
                .trim();
        String phone = etContact.getText()
                .toString()
                .trim();
        if (!TextUtils.isEmpty(phone) && !CommonUtil.isMobileNO(phone)) {
            Toast.makeText(getContext(),
                    getString(R.string.hint_new_number_error___cm),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        postSubscription = CustomCommonApi.postFeedback(content, phone)
                .subscribe(HljHttpSubscriber.buildSubscriber(getContext())
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
                        .build());
    }

    @OnClick(R.id.tv_contact_us)
    public void onContactUs() {
        CustomerSupportUtil.goToSupport(getContext(), Support.SUPPORT_KIND_DEFAULT_ROBOT);
    }

    @OnTextChanged(R.id.et_feedback)
    public void onBtnEnable(CharSequence text) {
        btnSend.setEnabled(!TextUtils.isEmpty(text.toString()
                .trim()));
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        CommonUtil.unSubscribeSubs(postSubscription);
    }
}
