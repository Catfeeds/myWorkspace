package com.hunliji.marrybiz.view.coupon;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.coupon.MyCouponListFragment;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;

/**
 * 优惠券搜索
 * Created by chen_bin on 2016/10/13 0013.
 */
public class CouponSearchActivity extends HljBaseNoBarActivity {
    @BindView(R.id.et_search)
    AutoCompleteTextView etSearch;
    @BindView(R.id.btn_clear)
    ImageView btnClear;
    private MyCouponListFragment fragment;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_search);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragment = (MyCouponListFragment) fm.findFragmentByTag("MyCouponListFragment");
        if (fragment == null) {
            fragment = MyCouponListFragment.newInstance(null, true, 0);
            ft.add(R.id.fragment_content, fragment, "MyCouponListFragment");
        }
        ft.show(fragment);
        ft.commitAllowingStateLoss();
    }


    @OnEditorAction(R.id.et_search)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String key = etSearch.getText()
                    .toString()
                    .trim();
            if (TextUtils.isEmpty(key)) {
                return true;
            }
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            if (fragment != null) {
                fragment.refresh(key);
            }
            return true;
        }
        return false;
    }

    @OnTextChanged(R.id.et_search)
    public void afterTextChanged(Editable s) {
        if (s != null && s.length() > 0) {
            btnClear.setVisibility(View.VISIBLE);
        } else {
            btnClear.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_clear)
    public void onClear() {
        etSearch.setText("");
    }

    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}