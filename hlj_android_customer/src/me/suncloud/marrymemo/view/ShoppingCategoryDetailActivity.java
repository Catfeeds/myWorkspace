package me.suncloud.marrymemo.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunliji.hljcommonlibrary.models.Label;
import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.activities.HljBaseNoBarActivity;
import com.hunliji.hljcommonlibrary.views.widgets.HljEmptyView;
import com.hunliji.hljcommonlibrary.views.widgets.ScrollableLayout;
import com.hunliji.hljhttplibrary.api.newsearch.NewSearchApi;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;
import com.hunliji.hljtrackerlibrary.HljTracker;
import com.hunliji.hljtrackerlibrary.TrackerSite;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.adpter.CategoryMarkFilterAdapter;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.fragment.product.ShoppingCategoryDetailFragment;
import me.suncloud.marrymemo.util.NoticeUtil;
import me.suncloud.marrymemo.util.Session;
import me.suncloud.marrymemo.util.Util;
import me.suncloud.marrymemo.view.newsearch.NewSearchActivity;
import me.suncloud.marrymemo.view.notification.MessageHomeActivity;
import me.suncloud.marrymemo.widget.TabPageIndicator;
import rx.Observable;

/**
 * 婚品分类详情页面
 * Created by jinxin on 2017/5/8 0008.
 */

public class ShoppingCategoryDetailActivity extends HljBaseNoBarActivity implements
        TabPageIndicator.OnTabChangeListener {

    @BindView(R.id.tab_indicator)
    TabPageIndicator tabIndicator;
    @BindView(R.id.tab_line)
    View tabLine;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.empty_view)
    HljEmptyView emptyView;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;
    @BindView(R.id.scrollable_layout)
    ScrollableLayout scrollableLayout;
    @BindView(R.id.btn_back)
    ImageButton btnBack;
    @BindView(R.id.empty_divider)
    View emptyDivider;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.msg_notice_view)
    View msgNoticeView;
    @BindView(R.id.tv_msg_count)
    TextView tvMsgCount;
    @BindView(R.id.msg_layout)
    RelativeLayout msgLayout;
    @BindView(R.id.btn_shopping_cart)
    ImageButton btnShoppingCart;
    @BindView(R.id.notice)
    View notice;
    @BindView(R.id.line_layout)
    View lineLayout;
    private long parentId;
    private long childId;
    private ShopCategory parentCategory;
    private List<ShopCategory> childCategories;
    private CategoryFragmentAdapter fragmentAdapter;
    private HljHttpSubscriber initSubscriber;
    private List<ShoppingCategoryDetailFragment> fragments;
    private ShopCategory currentChildCategory;
    private ShopCategory allCategory;
    private NoticeUtil noticeUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        parentId = getIntent().getLongExtra("parentId", 0);
        childId = getIntent().getLongExtra("childId", 0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_category_detail);
        ButterKnife.bind(this);
        setDefaultStatusBarPadding();
        initConstant();
        initWidget();
        initLoad();
    }

    private void initConstant() {
        fragments = new ArrayList<>();
        childCategories = new ArrayList<>();
        fragmentAdapter = new CategoryFragmentAdapter(getSupportFragmentManager());
        allCategory = new ShopCategory();
        allCategory.setId(CategoryMarkFilterAdapter.ALL_CATEGORY_MARK_ID);
        allCategory.setName("全部");
    }

    private void initWidget() {
        scrollableLayout.setExtraHeight(0);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabIndicator.setCurrentItem(position);
                currentChildCategory = childCategories.get(position);
            }
        });
        tabIndicator.setTabViewId(R.layout.menu_tab_widget2);
        tabIndicator.setOnTabChangeListener(this);
        tabIndicator.setPagerAdapter(fragmentAdapter);
        viewPager.setAdapter(fragmentAdapter);
        // action bar
        tvSearch.setText(R.string.hint_search_product);
        notice.setVisibility(Session.getInstance()
                .isNewCart() ? View.VISIBLE : View.GONE);
        noticeUtil = new NoticeUtil(this, tvMsgCount, msgNoticeView);
        noticeUtil.onResume();
    }

    private void initLoad() {
        String url = Constants.HttpPath.GET_SHOP_PRODUCT_CATEGORY + "?id=" + parentId;
        Observable<List<ShopCategory>> obb = ProductApi.getProductProperty(this, url, false);
        initSubscriber = HljHttpSubscriber.buildSubscriber(this)
                .setProgressBar(progressBar)
                .setEmptyView(emptyView)
                .setContentView(layoutContent)
                .setOnNextListener(new SubscriberOnNextListener<List<ShopCategory>>() {
                    @Override
                    public void onNext(List<ShopCategory> shopCategories) {
                        if (shopCategories != null && !shopCategories.isEmpty()) {
                            parentCategory = shopCategories.get(0);
                            childCategories.clear();
                            if (parentCategory != null) {
                                if (parentCategory.getChildren() != null) {
                                    childCategories.addAll(parentCategory.getChildren());
                                }
                            }

                            if (!childCategories.isEmpty()) {
                                //添加全部分类
                                childCategories.add(0, allCategory);
                            }

                            int position = 0;
                            for (int i = 0, size = childCategories.size(); i < size; i++) {
                                ShopCategory shopCategory = childCategories.get(i);
                                if (shopCategory.getId() == childId) {
                                    position = i;
                                }
                                long parentId;
                                long categoryId;
                                boolean showFilter;
                                if (shopCategory.getId() == CategoryMarkFilterAdapter
                                        .ALL_CATEGORY_MARK_ID) {
                                    parentId = parentCategory.getParentId();
                                    categoryId = parentCategory.getId();
                                    showFilter = true;
                                } else {
                                    parentId = shopCategory.getParentId();
                                    categoryId = shopCategory.getId();
                                    showFilter = false;
                                }
                                ShoppingCategoryDetailFragment fragment =
                                        ShoppingCategoryDetailFragment.newInstance(
                                        categoryId,
                                        parentId,
                                        showFilter);
                                fragments.add(fragment);
                            }

                            tabLine.setVisibility(childCategories.isEmpty() ? View.GONE : View
                                    .VISIBLE);
                            fragmentAdapter.notifyDataSetChanged();
                            tabIndicator.notifyDataSetChanged((ArrayList<? extends Label>)
                                    childCategories);
                            viewPager.setCurrentItem(position);
                            viewPager.setOffscreenPageLimit(fragments.size());
                            currentChildCategory = childCategories.get(position);
                        }
                    }
                })
                .build();
        obb.subscribe(initSubscriber);
    }

    public List<ShopCategory> getChildCategories() {
        return parentCategory.getChildren();
    }

    public void setPage(ShopCategory shopCategory) {
        if (shopCategory == null || shopCategory.getId() <= 0) {
            return;
        }
        int index = childCategories.indexOf(shopCategory);
        viewPager.setCurrentItem(index);
    }

    public void scrollToTop() {
        if (isFinishing()) {
            return;
        }
        if (scrollableLayout != null) {
            scrollableLayout.scrollToTop();
        }
    }


    @OnClick(R.id.btn_back)
    void onBack() {
        super.onBackPressed();
    }

    @OnClick(R.id.btn_shopping_cart)
    void onShoppingCard() {
        if (!Util.loginBindChecked(this)) {
            return;
        }
        if (notice != null) {
            notice.setVisibility(View.GONE);
        }
        startActivity(new Intent(this, ShoppingCartActivity.class));
    }

    //新消息点击
    @OnClick(R.id.msg_layout)
    void onNewMsg() {
        if (Util.loginBindChecked(this)) {
            startActivity(new Intent(this, MessageHomeActivity.class));
        }
    }

    @OnClick(R.id.tv_search)
    void onSearch() {
        Intent intent = new Intent(this, NewSearchActivity.class);
        intent.putExtra(NewSearchApi.ARG_SEARCH_TYPE,
                NewSearchApi.SearchType.SEARCH_TYPE_PRODUCT);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.activity_anim_default);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (noticeUtil != null) {
            noticeUtil.onResume();
        }
        notice.setVisibility(Session.getInstance()
                .isNewCart() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (noticeUtil != null) {
            noticeUtil.onPause();
        }
    }

    @Override
    protected void onFinish() {
        CommonUtil.unSubscribeSubs(initSubscriber);
        super.onFinish();
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    class CategoryFragmentAdapter extends FragmentStatePagerAdapter {

        public CategoryFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            ShopCategory shopCategory = childCategories.get(position);
            if (shopCategory != null) {
                return shopCategory.getName();
            }
            return null;
        }
    }
}
