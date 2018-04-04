package com.hunliji.hljnotelibrary.views.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.ClearableEditText;
import com.hunliji.hljnotelibrary.R;
import com.hunliji.hljnotelibrary.R2;
import com.hunliji.hljnotelibrary.views.fragments.OrderedListFragment;
import com.hunliji.hljnotelibrary.views.fragments.SearchNoteSpotEntityFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 选择商家、婚品
 * Created by chen_bin on 2017/6/28 0028.
 */
public class SelectNoteSpotEntityActivity extends HljBaseNoBarActivity {
    @BindView(R2.id.et_keyword)
    public ClearableEditText etKeyword;
    private OrderedListFragment orderedListFragment;
    private SearchNoteSpotEntityFragment searchNoteSpotEntityFragment;
    private Handler handler;
    private String keyword;
    private boolean isSearch; //当前是否进行了搜索

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_note_spot_entity__note);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initValues();
        initViews();
        setFragment();
    }

    private void initValues() {
        handler = new Handler();
    }

    private void initViews() {
        etKeyword.setHint(R.string.label_input_merchant_or_product___note);
    }

    private void setFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        orderedListFragment = (OrderedListFragment) fm.findFragmentByTag("OrderedListFragment");
        searchNoteSpotEntityFragment = (SearchNoteSpotEntityFragment) fm.findFragmentByTag(
                "SearchNoteSpotEntityFragment");
        if (orderedListFragment != null && !orderedListFragment.isHidden()) {
            ft.hide(orderedListFragment);
        }
        if (searchNoteSpotEntityFragment != null && !searchNoteSpotEntityFragment.isHidden()) {
            ft.hide(searchNoteSpotEntityFragment);
        }
        if (!isSearch) {
            if (orderedListFragment != null) {
                ft.show(orderedListFragment);
            } else {
                orderedListFragment = OrderedListFragment.newInstance();
                ft.add(R.id.fragment_content, orderedListFragment, "OrderedListFragment");
            }
        } else {
            if (searchNoteSpotEntityFragment != null) {
                ft.show(searchNoteSpotEntityFragment);
                searchNoteSpotEntityFragment.refresh(keyword);
            } else {
                searchNoteSpotEntityFragment = SearchNoteSpotEntityFragment.newInstance(keyword);
                ft.add(R.id.fragment_content,
                        searchNoteSpotEntityFragment,
                        "SearchNoteSpotEntityFragment");
            }
        }
        ft.commitAllowingStateLoss();
    }

    @OnClick(R2.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnTextChanged(R2.id.et_keyword)
    public void afterTextChanged(Editable s) {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 150);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isFinishing()) {
                return;
            }
            if (etKeyword.length() > 0) {
                keyword = etKeyword.getText()
                        .toString();
            }
            isSearch = etKeyword.length() > 0 || !isOrdered();
            setFragment();
        }
    };

    public void requestFocus() {
        if (etKeyword != null) {
            etKeyword.requestFocus();
        }
    }

    private boolean isOrdered() {
        return orderedListFragment != null && orderedListFragment.isOrdered();
    }

    @Override
    protected void onFinish() {
        super.onFinish();
        handler.removeCallbacks(runnable);
    }
}