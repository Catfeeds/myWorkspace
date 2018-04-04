package me.suncloud.marrymemo.fragment.product;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hunliji.hljcommonlibrary.models.product.ShopCategory;
import com.hunliji.hljcommonlibrary.utils.CommonUtil;
import com.hunliji.hljcommonlibrary.views.fragments.ScrollAbleFragment;
import com.hunliji.hljcommonlibrary.views.widgets.TabPageIndicator;
import com.hunliji.hljhttplibrary.utils.HljHttpSubscriber;
import com.hunliji.hljhttplibrary.utils.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.suncloud.marrymemo.Constants;
import me.suncloud.marrymemo.R;
import me.suncloud.marrymemo.api.product.ProductApi;
import me.suncloud.marrymemo.view.merchant.WeddingDressChannelActivity;
import rx.Observable;

/**
 * Created by mo_yu on 2017/8/4.全国优选，婚纱礼服婚品列表
 */

public class WeddingDressProductHomeFragment extends ScrollAbleFragment implements
        TabPageIndicator.OnTabChangeListener {

    @BindView(R.id.tab_indicator)
    TabPageIndicator tabIndicator;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    Unbinder unbinder;

    private long parentId;
    private ShopCategory allCategory;
    private ArrayList<ShopCategory> categories;
    private ArrayList<ShopCategory> childCategories;
    private SparseArray<WeddingDressProductListFragment> fragments;
    private SectionsPagerAdapter pagerAdapter;

    private HljHttpSubscriber initSubscriber;

    public static WeddingDressProductHomeFragment newInstance(int parentId) {
        Bundle args = new Bundle();
        WeddingDressProductHomeFragment fragment = new WeddingDressProductHomeFragment();
        args.putLong("parent_id", parentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments = new SparseArray<>();
        categories = new ArrayList<>();
        childCategories = new ArrayList<>();
        allCategory = new ShopCategory();
        allCategory.setId(1);
        allCategory.setName("全部");
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wedding_dress_product_home,
                container,
                false);
        unbinder = ButterKnife.bind(this, rootView);
        if (getArguments() != null) {
            parentId = getArguments().getLong("parent_id", 1);
        }
        tabIndicator.setTabViewId(R.layout.menu_tab_view_short_line___cm);
        initLoad();
        return rootView;
    }

    private void initLoad() {
        String url = Constants.HttpPath.GET_SHOP_PRODUCT_CATEGORY + "?id=" + parentId;
        Observable<List<ShopCategory>> obb = ProductApi.getProductProperty(getContext(),
                url,
                false);
        initSubscriber = HljHttpSubscriber.buildSubscriber(getContext())
                .setOnNextListener(new SubscriberOnNextListener<List<ShopCategory>>() {
                    @Override
                    public void onNext(List<ShopCategory> shopCategories) {
                        if (!CommonUtil.isCollectionEmpty(shopCategories)) {
                            ShopCategory parentCategory = shopCategories.get(0);
                            childCategories.clear();
                            if (parentCategory.getChildren() != null) {
                                childCategories.addAll(parentCategory.getChildren());
                            }
                            categories.clear();
                            categories.addAll(childCategories);
                            if (!categories.isEmpty()) {
                                //添加全部分类
                                categories.add(0, allCategory);
                            }
                            initViewPager();
                        }
                    }
                })
                .build();
        obb.subscribe(initSubscriber);
    }

    private void initViewPager() {
        pagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabIndicator.setPagerAdapter(pagerAdapter);
        tabIndicator.setOnTabChangeListener(this);
        viewPager.setOffscreenPageLimit(categories.size() - 1);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tabIndicator.setCurrentItem(position);
                refreshFilterView();
            }
        });
    }

    @Override
    public void onTabChanged(int position) {
        viewPager.setCurrentItem(position);
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            WeddingDressProductListFragment fragment = fragments.get(position);
            if (fragment == null) {
                fragment = WeddingDressProductListFragment.newInstance(categories.get(position)
                        .getId());
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categories.get(position)
                    .getName();
        }
    }

    public ArrayList<ShopCategory> getChildCategories() {
        return childCategories;
    }

    @Override
    public void refresh(Object... params) {

    }

    @Override
    public View getScrollableView() {
        return null;
    }

    public View getFilterView() {
        if (fragments != null && fragments.size() > 0 && viewPager != null) {
            return fragments.get(viewPager.getCurrentItem())
                    .getFilterView();
        }
        return null;
    }

    public boolean isShowFilterView() {
        if (fragments != null && fragments.size() > 0 && viewPager != null) {
            return fragments.get(viewPager.getCurrentItem())
                    .isShowFilterView();
        }
        return false;
    }

    public void hideFilterView() {
        if (fragments != null && fragments.size() > 0 && viewPager != null) {
            fragments.get(viewPager.getCurrentItem())
                    .hideFilterView();
        }
    }

    public void refreshFilterView() {
        if (getActivity() instanceof WeddingDressChannelActivity) {
            WeddingDressChannelActivity weddingDressChannelActivity =
                    (WeddingDressChannelActivity) getActivity();
            weddingDressChannelActivity.refreshBottomFilterView(getFilterView());
        }
    }

    public void setCurrentItem(long categoryId) {
        if (viewPager != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categoryId == categories.get(i)
                        .getId()) {
                    final int finalI = i;
                    viewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(finalI);
                        }
                    }, 250);
                    return;
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(initSubscriber);
    }
}
