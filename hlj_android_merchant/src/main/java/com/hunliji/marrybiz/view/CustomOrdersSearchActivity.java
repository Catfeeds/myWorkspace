package com.hunliji.marrybiz.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.CustomOrderListFragment;
import com.hunliji.marrybiz.util.JSONUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Suncloud on 2016/2/2.
 */
public class CustomOrdersSearchActivity extends HljBaseNoBarActivity implements TextView
        .OnEditorActionListener, TextWatcher, View.OnClickListener {

    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_clear)
    ImageView btnClear;

    private InputMethodManager imm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_orders_search);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        etSearch.addTextChangedListener(this);
        etSearch.setOnEditorActionListener(this);
        btnClear.setOnClickListener(this);
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_to_bottom);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                etSearch.setText("");
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (etSearch.getText()
                    .toString()
                    .length() > 0) {
                String keyWord = etSearch.getText()
                        .toString();
                performSearch(keyWord);
            }
            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (etSearch.getText()
                .toString()
                .length() > 0) {
            btnClear.setVisibility(View.VISIBLE);
        } else {
            btnClear.setVisibility(View.GONE);
        }
    }


    private void performSearch(String keyWord) {
        if (JSONUtil.isEmpty(keyWord)) {
            return;
        }
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        CustomOrderListFragment customOrderListFragment = (CustomOrderListFragment)
                getSupportFragmentManager().findFragmentByTag(
                "customOrderListFragment");
        if (customOrderListFragment != null) {
            customOrderListFragment.refresh(keyWord);
        } else {
            customOrderListFragment = (CustomOrderListFragment) Fragment.instantiate(this,
                    CustomOrderListFragment.class.getName());
            Bundle args = new Bundle();
            args.putString("keyWord", keyWord);
            customOrderListFragment.setArguments(args);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.fragment, customOrderListFragment, "customOrderListFragment");
            ft.commit();
        }

    }
}
