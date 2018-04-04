package me.suncloud.marrymemo.fragment.merchant;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.hunliji.hljcommonlibrary.models.Merchant;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.utils.DialogUtil;
import com.hunliji.hljcommonlibrary.utils.ToastUtil;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.merchant.MerchantApi;
import me.suncloud.marrymemo.model.PostScheduleDateBody;
import me.suncloud.marrymemo.model.User;
import me.suncloud.marrymemo.util.Session;

/**
 * 订阅酒店接受最新优惠信息
 * Created by chen_bin on 2017/10/18 0018.
 */
public class SubscribeHotelFragment extends DialogFragment {

    @BindView(R.id.et_phone)
    EditText etPhone;

    private Merchant merchant;
    private Unbinder unbinder;
    private HljHttpSubscriber submitSub;

    public static SubscribeHotelFragment newInstance(Merchant merchant) {
        SubscribeHotelFragment fragment = new SubscribeHotelFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("merchant", merchant);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BubbleDialogFragment);
        if (getArguments() != null) {
            merchant = getArguments().getParcelable("merchant");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_subscribe_hotel,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            getDialog().setCanceledOnTouchOutside(true);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            WindowManager.LayoutParams params = window.getAttributes();
            Point point = CommonUtil.getDeviceSize(getContext());
            params.width = Math.round(point.x * 27 / 32);
            window.setAttributes(params);
        }
        initValues();
    }

    private void initValues() {
        User user = Session.getInstance()
                .getCurrentUser(getContext());
        etPhone.setText(user.getPhone());
        etPhone.setSelection(etPhone.length());
    }

    @OnClick(R.id.btn_close)
    void onClose() {
        dismiss();
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        if (merchant == null || merchant.getId() == 0) {
            return;
        }
        String phone = etPhone.getText()
                .toString();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(getContext(), null, R.string.label_phone_hint);
            return;
        }
        if (!CommonUtil.isMobileNO(phone)) {
            ToastUtil.showToast(getContext(), null, R.string.hint_new_number_error);
            return;
        }
        CommonUtil.unSubscribeSubs(submitSub);
        if (submitSub == null || submitSub.isUnsubscribed()) {
            submitSub = HljHttpSubscriber.buildSubscriber(getContext())
                    .setOnNextListener(new SubscriberOnNextListener() {
                        @Override
                        public void onNext(Object o) {
                            ToastUtil.showCustomToast(getContext(),
                                    R.string.msg_subscribe_hotel_success);
                            dismiss();
                        }
                    })
                    .setProgressDialog(DialogUtil.createProgressDialog(getContext()))
                    .build();
            PostScheduleDateBody body = new PostScheduleDateBody();
            body.setMerchantId(merchant.getId());
            body.setPhone(phone);
            MerchantApi.submitHotelScheduleObb(body)
                    .subscribe(submitSub);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        CommonUtil.unSubscribeSubs(submitSub);
    }
}
