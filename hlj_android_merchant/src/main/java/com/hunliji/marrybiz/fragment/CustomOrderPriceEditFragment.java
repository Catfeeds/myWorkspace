package com.hunliji.marrybiz.fragment;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.model.CustomSetmealOrder;
import com.hunliji.marrybiz.util.JSONUtil;
import com.hunliji.marrybiz.util.Util;
import com.hunliji.marrybiz.widget.PriceKeyboardView;
import com.hunliji.marrybiz.widget.ShSwitchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Suncloud on 2016/2/3.
 */
public class CustomOrderPriceEditFragment extends RefreshFragment implements ShSwitchView
        .OnSwitchStateChangeListener {

    @BindView(R.id.actual_price)
    TextView actualPrice;
    @BindView(R.id.switch_default)
    ShSwitchView switchDefault;
    @BindView(R.id.earnest_money)
    TextView earnestMoney;
    @BindView(R.id.earnest_money_layout)
    RelativeLayout earnestMoneyLayout;

    private double price;
    private double originalPrice;
    private double earnestPrice;
    private Dialog changePriceDlg;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            CustomSetmealOrder order = (CustomSetmealOrder) getArguments().getSerializable("order");
            if (order != null) {
                originalPrice = price = order.getActualPrice();
                earnestPrice = order.getEarnestMoney();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_order_price_edit, container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        switchDefault.setOnSwitchStateChangeListener(this);
        actualPrice.setText(Util.formatDouble2String(price));
        if (earnestPrice > 0 && earnestPrice < price) {
            switchDefault.setOn(true);
        } else {
            earnestPrice = (int) (price / 5);
        }
        earnestMoney.setText(Util.formatDouble2String(earnestPrice));
        return rootView;
    }

    @OnClick({R.id.actual_price_layout, R.id.earnest_money_layout})
    public void onPriceEdit(final View view) {
        if (changePriceDlg != null && changePriceDlg.isShowing()) {
            return;
        }
        if (changePriceDlg == null) {
            changePriceDlg = new Dialog(getActivity(), R.style.BubbleDialogTheme);
            changePriceDlg.setContentView(R.layout.dialog_change_price_new);
            changePriceDlg.findViewById(R.id.btn_key_hide).setOnClickListener(new View
                    .OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePriceDlg.dismiss();
                }
            });
            Window win = changePriceDlg.getWindow();
            WindowManager.LayoutParams params = win.getAttributes();
            Point point = JSONUtil.getDeviceSize(getActivity());
            params.width = point.x;
            win.setGravity(Gravity.BOTTOM);
            win.setWindowAnimations(R.style.dialog_anim_rise_style);
        }
        PriceKeyboardView keyboardView = (PriceKeyboardView) changePriceDlg.findViewById(R.id
                .keyboard);
        keyboardView.setDotEnabled(false);
        keyboardView.setConfirmOnClickListener(new PriceKeyboardView.ConfirmOnClickListener() {
            @Override
            public void priceConfirm(double newTotalPrice, double newDepositPrice) {
                switch (view.getId()) {
                    case R.id.actual_price_layout:
                        if (newTotalPrice == price) {
                            Toast.makeText(getActivity(), getString(R.string.msg_need_new_price),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            price = newTotalPrice;
                            if(!switchDefault.isOn()) {
                                earnestPrice = (int) (price / 5);
                                earnestMoney.setText(Util.formatDouble2String(earnestPrice));
                            }
                            actualPrice.setText(Util.formatDouble2String(newTotalPrice));
                            changePriceDlg.dismiss();
                        }
                        break;
                    case R.id.earnest_money_layout:
                        if (newTotalPrice == earnestPrice) {
                            Toast.makeText(getActivity(), getString(R.string.msg_need_new_price),
                                    Toast.LENGTH_SHORT).show();
                        } else if (newTotalPrice >= price) {
                            Toast.makeText(getActivity(), getString(R.string
                                    .err_earnest_price_edit), Toast.LENGTH_SHORT).show();
                        } else {
                            earnestPrice = newTotalPrice;
                            earnestMoney.setText(Util.formatDouble2String(earnestPrice));
                            changePriceDlg.dismiss();
                        }
                        break;
                }
            }
        });
        switch (view.getId()) {
            case R.id.actual_price_layout:
                keyboardView.setPriceModifyMode(PriceKeyboardView.MODE_TOTAL);
                keyboardView.setTotalPrices(price, originalPrice);
                keyboardView.setNewTotalPriceLabel(R.string.label_order_price4);
                keyboardView.setOldTotalPriceLabel(R.string.label_order_old_price);
                break;
            case R.id.earnest_money_layout:
                keyboardView.setPriceModifyMode(PriceKeyboardView.MODE_DEPOSIT);
                keyboardView.setDepositPrices(earnestPrice, price);
                keyboardView.setNewDepositPriceLabel(R.string.label_order_earnest_price);
                keyboardView.setOldDepositPriceLabel(R.string.label_order_price5);
                break;
        }
        changePriceDlg.show();
    }


    @Override
    public void refresh(Object... params) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public boolean setOrder(CustomSetmealOrder order) {
        if (switchDefault.isOn() && price <= earnestPrice) {
            Util.showToast(getActivity(), null, R.string.err_earnest_price_edit);
        } else if (order != null) {
            order.setActualPrice(price);
            order.setEarnestMoney(switchDefault.isOn() ? earnestPrice : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onSwitchStateChange(boolean isOn) {
        earnestMoneyLayout.setVisibility(isOn ? View.VISIBLE : View.GONE);
    }
}
