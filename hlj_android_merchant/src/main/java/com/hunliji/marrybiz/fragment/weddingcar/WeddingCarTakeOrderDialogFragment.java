package com.hunliji.marrybiz.fragment.weddingcar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljhttplibrary.entities.HljHttpResult;
import com.hunliji.hljhttplibrary.entities.HljHttpStatus;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.api.weddingcar.WeddingCarApi;
import com.hunliji.marrybiz.view.weddingcar.WeddingCarOrderDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by jinxin on 2018/1/8 0008.
 */

public class WeddingCarTakeOrderDialogFragment extends Fragment {

    public static final String STATUS_BAR_HEIGHT = "status_bar_height";
    public static final String ORDER_ID = "order_id";

    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.action_bar)
    RelativeLayout actionBar;
    @BindView(R.id.edit_bride_address)
    EditText editBrideAddress;
    @BindView(R.id.edit_groom_address)
    EditText editGroomAddress;
    @BindView(R.id.edit_hotel_address)
    EditText editHotelAddress;
    @BindView(R.id.edit_way)
    EditText editWay;
    @BindView(R.id.edit_extra)
    EditText editExtra;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.tv_other_hint)
    TextView tvOtherHint;

    private int statusBarHeight;
    private long orderId;
    private Unbinder unbind;
    private HljHttpSubscriber takeOrderSub;

    public static WeddingCarTakeOrderDialogFragment newInstance(long orderId, int statusBarHeight) {
        WeddingCarTakeOrderDialogFragment takeOrderDialogFragment = new
                WeddingCarTakeOrderDialogFragment();
        Bundle data = new Bundle();
        data.putInt(STATUS_BAR_HEIGHT, statusBarHeight);
        data.putLong(ORDER_ID, orderId);
        takeOrderDialogFragment.setArguments(data);
        return takeOrderDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            statusBarHeight = getArguments().getInt(STATUS_BAR_HEIGHT);
            orderId = getArguments().getLong(ORDER_ID, 0L);
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_wedding_car_take_order, container, false);
        unbind = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWidget();
    }

    private void initWidget() {
        SpannableString spannableString = new SpannableString("其他其他");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getContext().getResources()
                .getColor(R.color.transparent));
        spannableString.setSpan(colorSpan, 1, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvOtherHint.setText(spannableString);
    }


    @OnClick(R.id.btn_back)
    void onBack() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        String brideAddress = editBrideAddress.getText()
                .toString()
                .trim();
        String groomAddress = editGroomAddress.getText()
                .toString()
                .trim();
        String hotelAddress = editHotelAddress.getText()
                .toString()
                .trim();
        String way = editWay.getText()
                .toString()
                .trim();
        String extra = editExtra.getText()
                .toString();

        CommonUtil.unSubscribeSubs(takeOrderSub);
        takeOrderSub = HljHttpSubscriber.buildSubscriber(getContext())
                .setProgressBar(progressBar)
                .setOnNextListener(new SubscriberOnNextListener<HljHttpResult<JsonElement>>() {
                    @Override
                    public void onNext(HljHttpResult<JsonElement> result) {
                        setHttpResult(result);
                    }
                })
                .build();
        WeddingCarApi.takeOrder(orderId, brideAddress, extra, groomAddress, hotelAddress, way)
                .subscribe(takeOrderSub);
    }

    private void setHttpResult(HljHttpResult<JsonElement> result) {
        if (result != null) {
            HljHttpStatus status = result.getStatus();
            if (status == null) {
                Toast.makeText(getContext(), "接单失败", Toast.LENGTH_SHORT)
                        .show();
            } else {
                if (status.getRetCode() == 0) {
                    Toast.makeText(getContext(), "成功接单", Toast.LENGTH_SHORT)
                            .show();
                    WeddingCarOrderDetailActivity orderDetailActivity =
                            (WeddingCarOrderDetailActivity) getActivity();
                    if (orderDetailActivity != null) {
                        orderDetailActivity.refresh();
                    }
                    onBack();
                } else {
                    Toast.makeText(getContext(), status.getMsg(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        } else {
            Toast.makeText(getContext(), "接单失败", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CommonUtil.unSubscribeSubs(takeOrderSub);
        if (unbind != null) {
            unbind.unbind();
        }
    }
}
