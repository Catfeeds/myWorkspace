package com.hunliji.marrybiz.view.orders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.fragments.RefreshFragment;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.OnlineOrdersListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by luohanlin on 2017/10/19.
 */

public class ServiceOrderSearchActivity extends HljBaseNoBarActivity {

    public static final String FRAGMENT_TAG_ORDERS = "orders_fragment";

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.btn_clear)
    ImageView btnClear;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.content_layout)
    FrameLayout contentLayout;

    private String type;
    private String keyWord;

    RefreshFragment fragment;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order_search);
        ButterKnife.bind(this);

        setDefaultStatusBarPadding();
        initValues();
        initViews();
    }

    private void initValues() {
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initViews() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (etSearch.getText()
                            .toString()
                            .length() > 0) {
                        keyWord = etSearch.getText()
                                .toString();
                        performSearch();
                    }
                    return true;
                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
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
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
            }
        });

        setFragment();
    }

    private void setFragment() {
        fragment = OnlineOrdersListFragment.newInstance(OnlineOrdersListFragment
                        .STATUS_FOR_SEARCH_ORDERS,
                "");
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.content_layout, fragment, FRAGMENT_TAG_ORDERS);
        ft.commit();
    }

    private void performSearch() {
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (fragment != null) {
            fragment.refresh(keyWord);
        }
    }

    @OnClick(R.id.tv_cancel)
    void onCancel() {
        onBackPressed();
        overridePendingTransition(R.anim.activity_anim_default, R.anim.slide_out_to_bottom);
    }
}
