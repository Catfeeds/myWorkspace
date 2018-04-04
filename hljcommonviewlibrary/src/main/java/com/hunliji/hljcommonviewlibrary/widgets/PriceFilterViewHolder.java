package com.hunliji.hljcommonviewlibrary.widgets;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hunliji.hljcommonviewlibrary.R;
import com.hunliji.hljcommonviewlibrary.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 套餐筛选
 * Created by mo_yu on 2017/8/2 0007.
 */

public class PriceFilterViewHolder {


    @BindView(R2.id.et_price_min)
    EditText etPriceMin;
    @BindView(R2.id.et_price_max)
    EditText etPriceMax;
    @BindView(R2.id.menu_info_layout)
    LinearLayout menuInfoLayout;
    @BindView(R2.id.menu_bg_layout)
    RelativeLayout menuBgLayout;
    @BindView(R2.id.root_layout)
    View rootLayout;
    @BindView(R2.id.edit_confirm_view)
    RelativeLayout editConfirmView;
    @BindView(R2.id.action_layout)
    LinearLayout actionLayout;
    private Context mContext;
    private View rootView;
    private OnConfirmListener onConfirmListener;
    private InputMethodManager imm;
    private boolean isShow;

    public static PriceFilterViewHolder newInstance(
            Context context, OnConfirmListener listener) {
        View view = View.inflate(context, R.layout.service_price_filter_view___cv, null);
        PriceFilterViewHolder holder = new PriceFilterViewHolder(context, view, listener);
        holder.init();
        return holder;
    }

    public PriceFilterViewHolder(Context context, View view, OnConfirmListener listener) {
        this.mContext = context;
        this.rootView = view;
        this.onConfirmListener = listener;
        ButterKnife.bind(this, view);
    }

    public void init() {
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        rootLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View v,
                    int left,
                    int top,
                    int right,
                    int bottom,
                    int oldLeft,
                    int oldTop,
                    int oldRight,
                    int oldBottom) {
                if (bottom == 0 || oldBottom == 0 || bottom == oldBottom) {
                    return;
                }
                Activity activity = (Activity) mContext;
                int height = activity.getWindow()
                        .getDecorView()
                        .getHeight();
                boolean immIsShow = (double) (bottom - top) / height < 0.8;
                if (immIsShow) {
                    editConfirmView.setVisibility(View.VISIBLE);
                    actionLayout.setVisibility(View.GONE);
                } else {
                    editConfirmView.setVisibility(View.GONE);
                    actionLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void hideSoftMethod() {
        if (etPriceMin.hasFocus()) {
            imm.hideSoftInputFromWindow(etPriceMin.getWindowToken(), 0);
        }
        if (etPriceMax.hasFocus()) {
            imm.hideSoftInputFromWindow(etPriceMax.getWindowToken(), 0);
        }
    }

    public void resetFilter(double minPrice,double maxPrice) {
        etPriceMin.setText(minPrice != -1 ? String.valueOf(minPrice) : null);
        etPriceMax.setText(maxPrice != -1 ? String.valueOf(maxPrice) : null);
    }

    public boolean isShow() {
        return isShow;
    }

    public View getRootView() {
        return rootView;
    }

    public void showPriceFilterView() {
        showMenuAnimation();
    }

    @OnClick(R2.id.tv_confirm)
    public void onPriceConfirm() {
        hideSoftMethod();
    }

    @OnClick(R2.id.btn_confirm)
    public void onConfirm() {
        if (onConfirmListener != null) {
            hideSoftMethod();
            String priceMin = etPriceMin.getText()
                    .toString();
            String priceMax = etPriceMax.getText()
                    .toString();
            double min = 0d;
            double max = 0d;
            if (!TextUtils.isEmpty(priceMin)) {
                min = Double.parseDouble(priceMin);
            }
            if (!TextUtils.isEmpty(priceMax)) {
                max = Double.parseDouble(priceMax);
            }
            if ((min > max) && (!TextUtils.isEmpty(priceMin) && !TextUtils.isEmpty(priceMax))) {
                etPriceMax.setText(priceMin);
                etPriceMin.setText(priceMax);
            }
            String minPriceStr = etPriceMin.getText()
                    .toString();
            String maxPriceStr = etPriceMax.getText()
                    .toString();
            if (!TextUtils.isEmpty(minPriceStr)) {
                min = Double.parseDouble(minPriceStr);
            } else {
                min = -1;
            }
            if (!TextUtils.isEmpty(maxPriceStr)) {
                max = Double.parseDouble(maxPriceStr);
            } else {
                max = -1;
            }
            hideMenuAnimation();
            onConfirmListener.onConfirm(min, max);
        }
    }

    @OnClick(R2.id.menu_bg_layout)
    public void onMenuBgLayout() {
        hideMenuAnimation();
    }

    @OnClick(R2.id.btn_reset)
    public void onReset() {
        etPriceMin.setText(null);
        etPriceMax.setText(null);
    }

    public interface OnConfirmListener {
        void onConfirm(
                double priceMin, double priceMax);
    }


    private void showMenuAnimation() {
        if (isShow) {
            return;
        }
        isShow = true;
        menuBgLayout.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_up);
        animation.setDuration(250);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (menuBgLayout != null) {
                    menuBgLayout.setBackgroundResource(R.color.transparent_black);
                }
            }
        });
        menuInfoLayout.startAnimation(animation);
    }

    public void hideMenuAnimation() {
        if (!isShow) {
            return;
        }
        hideSoftMethod();
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isShow = false;
                menuBgLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        menuInfoLayout.startAnimation(animation);
    }

}
