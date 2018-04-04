package com.hunliji.hljcardcustomerlibrary.views.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.hunliji.hljcardcustomerlibrary.R;
import com.hunliji.hljcardcustomerlibrary.R2;
import com.hunliji.hljcardcustomerlibrary.views.fragments.CardFeedBackFragment;
import com.hunliji.hljcardcustomerlibrary.views.fragments.CardHelpFragment;
import com.hunliji.hljcardlibrary.HljCard;
import com.hunliji.hljcommonlibrary.adapters.CommonPagerAdapter;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hua_rong on 2017/6/15.
 * 电子请帖帮助
 */

public class CardTutorialActivity extends HljBaseActivity implements TabPageIndicator
        .OnTabChangeListener {

    @BindView(R2.id.indicator)
    TabPageIndicator indicator;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_tutorial___card);
        ButterKnife.bind(this);
        initView();
        hideDividerView();
    }

    private void initView() {
        List<Fragment> fragmentList = new ArrayList<>();
        String[] titles = getResources().getStringArray(R.array.card_tutorial_title___card);
        List<String> titleList = Arrays.asList(titles);
        fragmentList.add(CardHelpFragment.newInstance(HljCard.getCardFaqUrl()));
        fragmentList.add(CardFeedBackFragment.newInstance());
        indicator.setTabViewId(R.layout.menu_tab_widget___card);
        CommonPagerAdapter pagerAdapter = new CommonPagerAdapter(getSupportFragmentManager(),
                fragmentList,
                titleList);
        viewPager.setAdapter(pagerAdapter);
        indicator.setPagerAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                indicator.setCurrentItem(position);
                hideKeyboard(null);
                super.onPageSelected(position);
            }
        });
        indicator.setOnTabChangeListener(this);
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }
}
