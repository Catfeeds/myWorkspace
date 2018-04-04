package me.suncloud.marrymemo.view.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hunliji.hljcommonlibrary.modules.helper.RouterPath;
import com.hunliji.hljcommonlibrary.utils.SPUtils;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.merchant.FindMerchantFragment;
import me.suncloud.marrymemo.fragment.merchant.FindMerchantHomeFragment;
import me.suncloud.marrymemo.model.City;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;

/**
 * Created by hua_rong on 2018/3/13
 * 找商家
 */
@Route(path = RouterPath.IntentPath.Customer.MerchantListActivityPath.MERCHANT_LIST_ACTIVITY)
public class MerchantListActivity extends HljBaseNoBarActivity {

    @BindView(R.id.msg_notice)
    View msgNotice;
    @BindView(R.id.msg_count)
    TextView msgCount;
    private FindMerchantFragment fragment;
    private NoticeUtil noticeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_list);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initView();
    }

    private void initView() {
        noticeUtil = new NoticeUtil(this, msgCount, msgNotice);
        noticeUtil.onResume();
        long propertyId = getIntent().getLongExtra(RouterPath.IntentPath.Customer
                        .MerchantListActivityPath.ARG_PROPERTY_ID,
                SPUtils.getLong(this,
                        RouterPath.IntentPath.Customer.MerchantListActivityPath.ARG_PROPERTY_ID,
                        0));
        City city = (City) getIntent().getSerializableExtra(FindMerchantHomeFragment.ARG_CITY);
        fragment = FindMerchantFragment.newInstance(city, propertyId);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_content, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
        if (fragment != null) {
            fragment.hideMenu();
        }
    }

    @OnClick(R.id.tv_search)
    void onSearch() {
        if (fragment != null && fragment.hideMenu()) {
            return;
        }
        Intent intent = new Intent(this, NewSearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.msg_layout)
    void onMsgLayout() {
        if (fragment != null && fragment.hideMenu()) {
            return;
        }
        if (Util.loginBindChecked(this)) {
            Intent intent = new Intent(this, MessageHomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
        }
    }

    @OnClick(R.id.btn_back)
    @Override
    public void onBackPressed() {
        if (fragment != null && fragment.hideMenu()) {
            return;
        }
        super.onBackPressed();
    }
}
