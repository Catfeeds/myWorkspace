package com.hunliji.marrybiz.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.market.CompeteAttentionFragment;
import com.hunliji.marrybiz.fragment.market.MarketTransformFragment;
import com.hunliji.marrybiz.fragment.market.SameBusinessRankFragment;
import com.hunliji.marrybiz.fragment.market.ShopDataFragment;
import com.hunliji.marrybiz.util.JSONUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 生意罗盘activity
 * Created by jinxin on 2016/6/17.
 */
@Deprecated
// TODO: 2018/3/15 生意罗盘废弃删除
public class BusinessCompassActivity extends HljBaseActivity implements TabHost
        .OnTabChangeListener {

    @BindView(android.R.id.tabhost)
    TabHost tabhost;
    private static final String SHOP_DATA_FRAGMENT = "shop_data_fragment";
    private static final String SAME_BUSINESS_RANK_FRAGMENT = "same_business_rank_fragment";
    private static final String COMPETE_ATTENTION_FRAGMENT = "compete_attention_fragment";
    private static final String MARKET_TRANSFORM_FRAGMENT = "market_transform_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_compass_activity);
        ButterKnife.bind(this);
        tabsInit();
        setOkButton(R.drawable.icon_tab_add);
        hideOkButton();
    }

    public void tabsInit() {

        View tab0 = View.inflate(this, R.layout.tab_view, null);
        ImageView tab0Image = (ImageView) tab0.findViewById(R.id.icon);
        tab0Image.setImageResource(R.drawable.sl_ic_market_transform);
        TextView tab0Text = (TextView) tab0.findViewById(R.id.text);
        tab0Text.setText(getString(R.string.label_market_transform));

        View tab1 = View.inflate(this, R.layout.tab_view, null);
        ImageView tab1Image = (ImageView) tab1.findViewById(R.id.icon);
        tab1Image.setImageResource(R.drawable.sl_ic_shop_data);
        TextView tab1Text = (TextView) tab1.findViewById(R.id.text);
        tab1Text.setText(getString(R.string.label_shop_data));

        View tab2 = View.inflate(this, R.layout.tab_view, null);
        ImageView tab2Image = (ImageView) tab2.findViewById(R.id.icon);
        tab2Image.setImageResource(R.drawable.sl_ic_business_rank);
        TextView tab2Text = (TextView) tab2.findViewById(R.id.text);
        tab2Text.setText(getString(R.string.label_same_business_rank));

        View tab3 = View.inflate(this, R.layout.tab_view, null);
        ImageView tab3Image = (ImageView) tab3.findViewById(R.id.icon);
        tab3Image.setImageResource(R.drawable.sl_ic_compete_attention);
        TextView tab3Text = (TextView) tab3.findViewById(R.id.text);
        tab3Text.setText(getString(R.string.label_compete_attention));

        tabhost.setOnTabChangedListener(this);
        tabhost.setup();
        tabhost.addTab(tabhost.newTabSpec(MARKET_TRANSFORM_FRAGMENT)
                .setIndicator(tab0)
                .setContent(R.id.tab0));
        tabhost.addTab(tabhost.newTabSpec(SHOP_DATA_FRAGMENT)
                .setIndicator(tab1)
                .setContent(R.id.tab1));
        tabhost.addTab(tabhost.newTabSpec(SAME_BUSINESS_RANK_FRAGMENT)
                .setIndicator(tab2)
                .setContent(R.id.tab2));
        tabhost.addTab(tabhost.newTabSpec(COMPETE_ATTENTION_FRAGMENT)
                .setIndicator(tab3)
                .setContent(R.id.tab3));
    }

    @Override
    public void onTabChanged(String tabId) {
        if (!JSONUtil.isEmpty(tabId)) {
            switch (tabId) {
                case MARKET_TRANSFORM_FRAGMENT:
                    selectChange(0);
                    break;
                case SHOP_DATA_FRAGMENT:
                    selectChange(1);
                    break;
                case SAME_BUSINESS_RANK_FRAGMENT:
                    selectChange(2);
                    break;
                case COMPETE_ATTENTION_FRAGMENT:
                    selectChange(3);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onOkButtonClick() {
        Intent intent = new Intent(this, CompeteAttentionSearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }


    public void selectChange(int position) {
        FragmentManager fm = getSupportFragmentManager();
        MarketTransformFragment marketTransformFragment = (MarketTransformFragment) fm
                .findFragmentByTag(
                MARKET_TRANSFORM_FRAGMENT);
        ShopDataFragment shopDataFragment = (ShopDataFragment) fm.findFragmentByTag(
                SHOP_DATA_FRAGMENT);
        SameBusinessRankFragment samebusinessrankfragment = (SameBusinessRankFragment) fm
                .findFragmentByTag(
                SAME_BUSINESS_RANK_FRAGMENT);
        CompeteAttentionFragment competeattentionfragment = (CompeteAttentionFragment) fm
                .findFragmentByTag(
                COMPETE_ATTENTION_FRAGMENT);

        FragmentTransaction ft = fm.beginTransaction();
        if (marketTransformFragment != null && !marketTransformFragment.isHidden())
            ft.hide(marketTransformFragment);
        if (shopDataFragment != null && !shopDataFragment.isHidden())
            ft.hide(shopDataFragment);
        if (samebusinessrankfragment != null && !samebusinessrankfragment.isHidden())
            ft.hide(samebusinessrankfragment);
        if (competeattentionfragment != null && !competeattentionfragment.isHidden())
            ft.hide(competeattentionfragment);

        switch (position) {
            case 0:
                if (marketTransformFragment == null) {
                    ft.add(R.id.realtabcontent,
                            MarketTransformFragment.newInstance(),
                            MARKET_TRANSFORM_FRAGMENT);
                } else {
                    ft.show(marketTransformFragment);
                }
                hideOkButton();
                break;
            case 1:
                if (shopDataFragment == null) {
                    ft.add(R.id.realtabcontent, ShopDataFragment.newInstance(), SHOP_DATA_FRAGMENT);
                } else {
                    ft.show(shopDataFragment);
                }
                hideOkButton();
                break;
            case 2:
                if (samebusinessrankfragment == null) {
                    ft.add(R.id.realtabcontent,
                            SameBusinessRankFragment.newInstance(),
                            SAME_BUSINESS_RANK_FRAGMENT);
                } else {
                    ft.show(samebusinessrankfragment);
                }
                hideOkButton();
                break;
            case 3:
                if (competeattentionfragment == null) {
                    ft.add(R.id.realtabcontent,
                            CompeteAttentionFragment.newInstance(),
                            COMPETE_ATTENTION_FRAGMENT);
                } else {
                    ft.show(competeattentionfragment);
                }
                showOkButton();
                break;
            default:
                break;
        }
        ft.commitAllowingStateLoss();
    }
}
