package com.hunliji.marrybiz.view.experience;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.experience.AdvListFragment;
import com.hunliji.marrybiz.model.experience.AdvDetail;
import com.hunliji.marrybiz.widget.ClearableEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mo_yu on 2017/12/19.搜索体验店数据
 */

public class SearchAdvShopActivity extends HljBaseNoBarActivity {

    @BindView(R.id.et_keyword)
    ClearableEditText etKeyword;
    @BindView(R.id.keyword_layout)
    LinearLayout keywordLayout;
    @BindView(R.id.search_layout)
    LinearLayout searchLayout;
    @BindView(R.id.top_bar)
    FrameLayout topBar;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;

    AdvListFragment fragment;
    String keyword;
    int advType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_experience_shop);
        ButterKnife.bind(this);
        initValue();
        initView();
        setDefaultStatusBarPadding();
    }

    private void initValue() {
        advType = getIntent().getIntExtra(AdvListActivity.ARG_ADV_TYPE, 0);
    }

    private void initView() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = AdvListFragment.newInstance(AdvDetail.ORDER_SEARCH, keyword, null, advType);
        transaction.add(R.id.fragment_content, fragment, "ExperienceShopListFragment");
        transaction.commitAllowingStateLoss();
        etKeyword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        etKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH && etKeyword.getText()
                        .length() > 0) {
                    keyword = etKeyword.getText()
                            .toString();
                    onSearch();
                    return false;
                } else {
                    return true;
                }
            }
        });
    }

    private void onSearch() {
        fragment.refresh(keyword);
        hideKeyboard(null);
    }

    @OnClick(R.id.btn_cancel)
    public void onBtnCancel() {
        onBackPressed();
    }
}
