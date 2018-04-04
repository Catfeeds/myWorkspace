package me.suncloud.marrymemo.view.product;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerSite;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.fragment.product.ProductListFragment;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.ShoppingCartActivity;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;

public class WeeklyProductsActivity extends HljBaseNoBarActivity {

    @Override
    public String pageTrackTagName() {
        return "每周上新";
    }

    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_search)
    ImageButton btnSearch;
    @BindView(R.id.btn_shopping_cart)
    ImageButton btnShoppingCart;
    @BindView(R.id.notice)
    View notice;
    @BindView(R.id.action_layout)
    RelativeLayout actionLayout;
    @BindView(R.id.action_holder_layout)
    LinearLayout actionHolderLayout;
    @BindView(R.id.content_layout)
    FrameLayout contentLayout;
    private ProductListFragment fragment;
    private static final String FRAGMENT_TAG_PRODUCT_LIST = "product_list_fragment";
    private static final String URL_WEEKLY_UPDATES_PRODUCTS = "p/wedding/index" + "" + "" + "" +
            ".php/Home/APISubPageShop/week_new_product";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_products);
        ButterKnife.bind(this);
        setActionBarPadding(actionHolderLayout);
        initViews();
    }

    private void initViews() {
        notice.setVisibility(Session.getInstance()
                .isNewCart() ? View.VISIBLE : View.GONE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        fragment = ProductListFragment.newInstance(URL_WEEKLY_UPDATES_PRODUCTS);
        fragment.setRefreshable(true);
        fragment.setShowProgressBar(true);

        ft.add(R.id.content_layout, fragment, FRAGMENT_TAG_PRODUCT_LIST);
        ft.commit();
    }

    @OnClick(R.id.btn_back)
    void onBackClick() {
        onBackPressed();
    }

    @OnClick(R.id.btn_search)
    void onSearch() {
        Intent intent = new Intent(this, NewSearchActivity.class);
        intent.putExtra(NewSearchApi.ARG_SEARCH_TYPE,
                NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @OnClick(R.id.btn_shopping_cart)
    void onShoppingCart() {
        if (!Util.loginBindChecked(this)) {
            return;
        }
        if (notice != null) {
            notice.setVisibility(View.GONE);
        }
        startActivity(new Intent(this, ShoppingCartActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        notice.setVisibility(Session.getInstance()
                .isNewCart() ? View.VISIBLE : View.GONE);
    }
}
