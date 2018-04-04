package com.hunliji.marrybiz.view.chat;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hunliji.hljchatlibrary.views.fragments.WSChatFragment;
import com.hunliji.hljcommonlibrary.models.User;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.marrybiz.R;
import com.hunliji.marrybiz.fragment.chat.MerchantWSChatFragment;
import com.hunliji.marrybiz.model.MerchantUser;
import com.hunliji.marrybiz.model.customer.MerchantCustomer;
import com.hunliji.marrybiz.util.Session;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Suncloud on 2016/10/19.
 */

public class WSMerchantChatActivity extends HljBaseNoBarActivity implements WSChatFragment
        .OnChatFragmentStateListener, MerchantWSChatFragment.MerchantChatFragmentListener {

    private final static String FRAGMENT_TAG_WS_CHAT = "fragment_tag_chat";

    @BindView(R.id.content_layout)
    FrameLayout contentLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.btn_customer)
    ImageButton btnCustomer;

    private MerchantUser currentUser;

    private MerchantWSChatFragment wsChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wsmerchant_chat);
        ButterKnife.bind(this);

        initValues();
        initViews();
    }

    private void initValues() {
        currentUser = Session.getInstance()
                .getCurrentUser();
        User sessionUser = getIntent().getParcelableExtra(RouterPath.IntentPath.Customer
                .BaseWsChat.ARG_USER);
        String channelId = getIntent().getStringExtra(MerchantWSChatFragment.ARG_CHANNEL_ID);
        int source = getIntent().getIntExtra(MerchantWSChatFragment.ARG_SOURCE, 1);
        long userId = getIntent().getLongExtra(RouterPath.IntentPath.Customer.BaseWsChat
                        .ARG_USER_ID,
                0);

        wsChatFragment = MerchantWSChatFragment.newInstance(sessionUser, channelId, source, userId);
        wsChatFragment.setChatFragmentStateListener(this);
        wsChatFragment.setMerchantChatFragmentListener(this);
    }

    private void initViews() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.content_layout, wsChatFragment, FRAGMENT_TAG_WS_CHAT);
        ft.commit();
        setDefaultStatusBarPadding();
    }

    @Override
    public void onSessionUserInfo(User sessionUser) {

    }

    @Override
    public void onStateChange(String state) {
        tvTitle.setText(state);
        tvTitle.requestLayout();
    }

    @Override
    public void onMerchantCustomerChange(MerchantCustomer merchantCustomer) {
        if (merchantCustomer != null) {
            btnCustomer.setVisibility(View.VISIBLE);
        } else {
            btnCustomer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCityChange(String cityName) {
        if (currentUser == null || currentUser.getShopType() != com.hunliji.hljcommonlibrary
                .models.merchant.MerchantUser.SHOP_TYPE_CAR) {
            tvCity.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(cityName)) {
            tvCity.setVisibility(View.GONE);
        } else {
            tvCity.setText(cityName);
            tvCity.setVisibility(View.VISIBLE);
        }
    }

    @Override
    @OnClick(R.id.btn_back)
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_customer)
    public void onBtnCustomerClicked() {
        if (wsChatFragment != null) {
            wsChatFragment.onEditCustomer();
        }
    }
}
